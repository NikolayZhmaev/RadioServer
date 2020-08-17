package net.thumbtack.school.concert.serviceClasses;

// сервисный клас для выполнения действий с песнями.

import com.google.gson.Gson;
import net.thumbtack.school.concert.Song;
import net.thumbtack.school.concert.User;
import net.thumbtack.school.concert.dao.SongDao;
import net.thumbtack.school.concert.daoimpl.SongDaoImpl;
import net.thumbtack.school.concert.dto.request.*;
import net.thumbtack.school.concert.Comment;
import net.thumbtack.school.concert.dto.request.*;
import net.thumbtack.school.concert.dto.response.SongDtoResponse;
import net.thumbtack.school.concert.errors.ServiceErrorCode;
import net.thumbtack.school.concert.errors.ServiceException;

import java.util.List;

public class SongService {

    private Gson gson = new Gson();
    private SongDao songDao;
    private Comment editComment;

    public SongService(SongDaoImpl songDaoImpl) {
        setSongDao(songDaoImpl);
    }

    /* сервисный метод по проверке передаваемых параметров и, в случае их корректности, регистрации песни, для участия
        в конкурсе. В случае передачи неверных данных возвращает Json с соответствующей ServiceException */
    public String songRegister(String requestJsonString) {

        RegisterSongDtoRequest dtoRegisterRequest = gson.fromJson(requestJsonString, RegisterSongDtoRequest.class);
        // запускаем проверки
        try {
            checkAuthor(dtoRegisterRequest.getAuthorToken());
            checkTitel(dtoRegisterRequest.getTitle());
            checkComposer(dtoRegisterRequest.getComposer());
            checkPoet(dtoRegisterRequest.getPoet());
            checkArtist(dtoRegisterRequest.getArtist());
            checkTime(dtoRegisterRequest.getTime());
            containsSong(dtoRegisterRequest.getTitle(), dtoRegisterRequest.getArtist()); // убедимся, что такой песни нет в базе данных
            // после выполнения проверок, регистрируем песню, добавляем ее в базу
            Song song = new Song(dtoRegisterRequest.getAuthorId(), dtoRegisterRequest.getAuthorToken(),
                    dtoRegisterRequest.getTitle(), dtoRegisterRequest.getComposer(),
                    dtoRegisterRequest.getPoet(), dtoRegisterRequest.getArtist(), dtoRegisterRequest.getTime());
            SongDtoResponse dtoResponse = new SongDtoResponse(getSongDao().insertSong(song, dtoRegisterRequest.getAuthorId()));
            return gson.toJson(dtoResponse);
        } catch (ServiceException e) {
            SongDtoResponse dtoResponse = new SongDtoResponse(e.getMessage());
            return gson.toJson(dtoResponse);
        }
    }

    /* напишем метод оценки песен. Условия, согласно задания, следующие:
    - оценка находится в диапазоне от 1 до 5
    - участник может ставить оценку
    - автор публикации оценку менять не может
    - оценка принимается только от валидного токена
    - пользователь может ставить только одну оценку, потом ее можно изменять
    для реализации этих условий, нам необходимо привязать каждую оценку к id. Поэтому во входном запросе передаем
    токен, id, название песни, оценку
     */
    public String addRatingSong(String requestJsonString) {
        AddRatingSongDtoRequest dtoAddRatingRequest = gson.fromJson(requestJsonString, AddRatingSongDtoRequest.class);
        try {
            Song song = checkSong(dtoAddRatingRequest.getTitle(), dtoAddRatingRequest.getArtist());
            checkAuthorSong(song, dtoAddRatingRequest.getId());
            checkAuthor(dtoAddRatingRequest.getToken());
            checkRatingWas(song, dtoAddRatingRequest.getId());
            SongDtoResponse dtoResponse = new SongDtoResponse(song.setRatings(dtoAddRatingRequest.getId(), dtoAddRatingRequest.getRating()));
            return gson.toJson(dtoResponse);
        } catch (ServiceException e) {
            SongDtoResponse dtoResponse = new SongDtoResponse(e.getMessage());
            return gson.toJson(dtoResponse);
        }
    }

    public String addLikeComment(String requestJsonString) {
        AddCommentSongDtoRequest dtoAddCommentRequest = gson.fromJson(requestJsonString, AddCommentSongDtoRequest.class);
        try {
            Song song = checkSong(dtoAddCommentRequest.getTitle(), dtoAddCommentRequest.getArtist());
            checkAuthor(dtoAddCommentRequest.getToken());
            // автор комментария лайкает его автоматически при создании и больше не может
            checkCommentAuthorWas(song, dtoAddCommentRequest.getId(), dtoAddCommentRequest.getComment());
            SongDtoResponse dtoResponse = new SongDtoResponse(song.likeComment(getEditComment(), dtoAddCommentRequest.getId()));
            return gson.toJson(dtoResponse);
        } catch (ServiceException e) {
            SongDtoResponse dtoResponse = new SongDtoResponse(e.getMessage());
            return gson.toJson(dtoResponse);
        }
    }

    public String removeLikeComment(String requestJsonString) {
        AddCommentSongDtoRequest dtoAddCommentRequest = gson.fromJson(requestJsonString, AddCommentSongDtoRequest.class);
        try {
            Song song = checkSong(dtoAddCommentRequest.getTitle(), dtoAddCommentRequest.getArtist());
            checkAuthor(dtoAddCommentRequest.getToken());
            // автор комментария лайкает его автоматически и больше не может его убрать
            checkCommentAuthorWas(song, dtoAddCommentRequest.getId(), dtoAddCommentRequest.getComment());
            checkAuthorLikeComment(dtoAddCommentRequest.getId());
            SongDtoResponse dtoResponse = new SongDtoResponse(song.removeLikeComment(getEditComment(), dtoAddCommentRequest.getId()));
            return gson.toJson(dtoResponse);
        } catch (ServiceException e) {
            SongDtoResponse dtoResponse = new SongDtoResponse(e.getMessage());
            return gson.toJson(dtoResponse);
        }
    }

    /* Напишем метод добавления коментария к песне. Условия, согласно задания, следующие:
     - комментарий представляет собой одну текстовую строку.
     - радиослушатель, сделавший комментарий, считается его автором.
     - радиослушатели могут присоединяться к комментариям, сделанным ранее другими радиослушателями.
     - если радиослушатель покидает сервер, то этот механизм применяется ко всем его комментариям, в том
       числе и тем, к которым никто не присоединился.
     - радиослушатели, присоединившиеся к комментарию, вправе отказаться от своего присоединения, но не могут
       изменять текст комментария.
     */
    public String addComment(String requestJsonString) {
        AddCommentSongDtoRequest dtoAddCommentRequest = gson.fromJson(requestJsonString, AddCommentSongDtoRequest.class);
        try {
            Song song = checkSong(dtoAddCommentRequest.getTitle(), dtoAddCommentRequest.getArtist());
            checkAuthor(dtoAddCommentRequest.getToken());
            checkCommentText(dtoAddCommentRequest.getComment());
            Comment comment = new Comment(dtoAddCommentRequest.getComment(), dtoAddCommentRequest.getId(),
                    dtoAddCommentRequest.getToken());
            SongDtoResponse dtoResponse = new SongDtoResponse(song.addComments(comment));
            return gson.toJson(dtoResponse);
        } catch (ServiceException e) {
            SongDtoResponse dtoResponse = new SongDtoResponse(e.getMessage());
            return gson.toJson(dtoResponse);
        }
    }

    /* Напишем метод удаления комментария:
     - автор комментария вправе удалить его в любой момент, если к этому комментарию еще никто не присоединился.
     - если кто-то присоединился к коментарию, то автором комментария становится сообщество радиослушателей,
       а лайк автора удаляется.
    */
    public String removeComment(String reguestJsonString) {
        AddCommentSongDtoRequest dtoAddCommentRequest = gson.fromJson(reguestJsonString, AddCommentSongDtoRequest.class);
        try {
            Song song = checkSong(dtoAddCommentRequest.getTitle(), dtoAddCommentRequest.getArtist());
            checkAuthor(dtoAddCommentRequest.getToken());
            checkCommentAuthor(song, dtoAddCommentRequest.getId(), dtoAddCommentRequest.getComment()); // комментарий может удалить только автор
            SongDtoResponse dtoResponse = new SongDtoResponse(song.removeComment(getEditComment(), dtoAddCommentRequest.getId()));
            return gson.toJson(dtoResponse);
        } catch (ServiceException e) {
            SongDtoResponse dtoResponse = new SongDtoResponse(e.getMessage());
            return gson.toJson(dtoResponse);
        }
    }

    /* Напишем метод изменения комментария:
    - автор комментария вправе изменить его в любой момент. Если на момент изменения к этому комментарию
      еще никто не присоединился, старый текст комментария просто заменяется на новый.
    - если же к этому комментарию кто-то успел присоединиться, старый вариант комментария остается без
      изменений, новый вариант добавляется к списку комментариев для этой песни, а автором старого комментария
      считается сообщество радиослушателей.
    */
    public String changeComment(String reguestJsonString) {
        ChangeCommentSongDtoRequest changeCommentSongDtoRequest = gson.fromJson(reguestJsonString, ChangeCommentSongDtoRequest.class);
        try {
            Song song = checkSong(changeCommentSongDtoRequest.getTitle(), changeCommentSongDtoRequest.getArtist());
            checkAuthor(changeCommentSongDtoRequest.getToken());
            checkCommentAuthor(song, changeCommentSongDtoRequest.getId(), changeCommentSongDtoRequest.getComment());
            SongDtoResponse dtoResponse = new SongDtoResponse(song.changeComment(getEditComment(),
                    new Comment(changeCommentSongDtoRequest.getCommentNew(), changeCommentSongDtoRequest.getId(),
                            changeCommentSongDtoRequest.getToken())));
            return gson.toJson(dtoResponse);
        } catch (ServiceException e) {
            SongDtoResponse dtoResponse = new SongDtoResponse(e.getMessage());
            return gson.toJson(dtoResponse);
        }
    }

    /*в этом методе будем изменять оценку. Проверка такая же как и в предыдущем методе. С небольшим изменеием*/
    public String changeRatingSong(String requestJsonString) {
        AddRatingSongDtoRequest dtoAddRatingRequest = gson.fromJson(requestJsonString, AddRatingSongDtoRequest.class);
        try {
            Song song = checkSong(dtoAddRatingRequest.getTitle(), dtoAddRatingRequest.getArtist());
            checkAuthorSong(song, dtoAddRatingRequest.getId());
            checkAuthor(dtoAddRatingRequest.getToken());
            checkRating(song, dtoAddRatingRequest.getId());
            SongDtoResponse dtoResponse = new SongDtoResponse(song.changeRating(dtoAddRatingRequest.getId(), dtoAddRatingRequest.getRating()));
            return gson.toJson(dtoResponse);
        } catch (ServiceException e) {
            SongDtoResponse dtoResponse = new SongDtoResponse(e.getMessage());
            return gson.toJson(dtoResponse);
        }
    }

    /*Метод удамения композиции. Согласно техзаданию, это возможно если на момент отмены, предложение не получило
     никаких оценок от других радиослушателей. Если же к этому моменту имеются другие оценки этого предложения, то
     удаляется лишь оценка этого предложения, сделанная его автором, а само предложение не удаляется, а автором
     предложения считается сообщество радиослушателей.*/
    public String removeSong(String requestJsonString) {
        RemoveSongDtoRequest dtoRemoveRequest = gson.fromJson(requestJsonString, RemoveSongDtoRequest.class);
        try {
            Song song = checkSong(dtoRemoveRequest.getTitle(), dtoRemoveRequest.getArtist());
            checkAuthorSongWas(song, dtoRemoveRequest.getId()); // удалить публикацию может только автор
            checkAuthor(dtoRemoveRequest.getToken());
     /* далее проверяем оценки песни, если оценок нет (кроме авторской), то удаляем песню из списка, если оценки
        есть удаляем оценку автора */
            if (song.getRating() == 5) {
                SongDtoResponse dtoResponse = new SongDtoResponse(getSongDao().removeSong(song));
                return gson.toJson(dtoResponse);
            } else {
                SongDtoResponse dtoResponse = new SongDtoResponse(song.removeRating(song.getAuthorId(), 5));
                return gson.toJson(dtoResponse);
            }
        } catch (ServiceException e) {
            SongDtoResponse dtoResponse = new SongDtoResponse(e.getMessage());
            return gson.toJson(dtoResponse);
        }
    }

    // метод для получения всех предложений одного пользователя
    public String getAllUserSongs(String requestJsonString) {
        // В запросе две строки. Обработаем запрос и "выдерним" из него эти строки.
        String[] userSongs = gson.fromJson(requestJsonString, String[].class);
        try {
            checkAuthor(userSongs[1]);
            SongDtoResponse dtoResponse = new SongDtoResponse(gson.toJson(getSongDao().allUserSongs(userSongs[0])));
            return gson.toJson(dtoResponse);
        } catch (ServiceException e) {
            SongDtoResponse dtoResponse = new SongDtoResponse(e.getMessage());
            return gson.toJson(dtoResponse);
        }
    }

    // метод формирования пробной программы концерта, согласно тех. задания
    public String concert(String requestJsonString) {
        String token = gson.fromJson(requestJsonString, String.class);
        try {
            checkAuthor(token);
            SongDtoResponse dtoResponse = new SongDtoResponse(gson.toJson(getSongDao().concert()));
            return gson.toJson(dtoResponse);
        } catch (ServiceException e) {
            SongDtoResponse dtoResponse = new SongDtoResponse(e.getMessage());
            return gson.toJson(dtoResponse);
        }
    }

    //проверяем есть ли такая песня в базе, и в случае ее нахождения, записываем ее в наше поле
    private Song checkSong(String title, String artist) throws ServiceException {
        Song song = getSongDao().searchSongInDB(title, artist);
        if (song == null) {
            throw new ServiceException(ServiceErrorCode.SONG_NOT_FOUND);
        }
        return song;
    }

    /* метод проверки, нет ли такой песни в нашей базе данных, совпадения будем искать по нескольким полям, так как
   разные песни могут иметь одинаковые названия. */
    private void containsSong(String title, String artist) throws ServiceException {
        if (getSongDao().containsSong(title, artist)) {
            throw new ServiceException(ServiceErrorCode.SONG_REGISTERED);
        }
    }

    // проверяем является ли участник автором комментария. И записываем комментарий в наше поле.
    private void checkCommentAuthor(Song song, String id, String commentTxt) throws ServiceException {
        setEditComment(song.checkComment(id));
        if (getEditComment() == null || !getEditComment().getComment().equals(commentTxt)) {
            throw new ServiceException(ServiceErrorCode.COMMENT_WRONG_AUTHOR_);
        }
    }

    //метод обратный предыдущему
    private void checkCommentAuthorWas(Song song, String id, String commentTxt) throws ServiceException {
        setEditComment(song.searchComment(commentTxt));
        if (getEditComment().getAuthorId().equals(id)) {
            throw new ServiceException(ServiceErrorCode.COMMENT_WRONG_AUTHOR_);
        }
    }

    // метод проверки ставил ли участник лайк к коментарию
    private void checkAuthorLikeComment(String id) throws ServiceException {
        if (!getEditComment().getUsersId().contains(id)) {
            throw new ServiceException(ServiceErrorCode.USER_NOT_PUT_LIKE);
        }
    }

    // проверяем не является ли участник автором песни (публикации)
    private void checkAuthorSong(Song song, String id) throws ServiceException {
        if (song.getAuthorId().equals(id)) {
            throw new ServiceException(ServiceErrorCode.INCORRECT_ACTION);
        }
    }

    // метод обратный предыдущему
    private void checkAuthorSongWas(Song song, String id) throws ServiceException {
        if (!song.getAuthorId().equals(id)) {
            throw new ServiceException(ServiceErrorCode.INCORRECT_ACTION);
        }
    }

    // проверим не оценивал ли этот участник ранее эту песню
    private void checkRatingWas(Song song, String id) throws ServiceException {
        if (song.checkRaiting(id)) {
            throw new ServiceException(ServiceErrorCode.INCORRECT_ACTION);
        }
    }

    // метод обратный предудущему
    private void checkRating(Song song, String id) throws ServiceException {
        if (!song.checkRaiting(id)) {
            throw new ServiceException(ServiceErrorCode.INCORRECT_ACTION);
        }
    }

    /* напишем метод проверки комментария:
     - поле не должно быть пустым
     - не более 100 символов (согласно условиям одна строка)*/
    private void checkCommentText(String comment) throws ServiceException {
        if (comment.length() < 2 || comment.length() > 100) {
            throw new ServiceException(ServiceErrorCode.WRONG_COMMENT);
        }
    }


    /* напишем метод проверки автора публикации по следующим критериям:
    - token должен быть действующим и автор активен*/
    private User checkAuthor(String token) throws ServiceException {
        User user = getSongDao().searchUserByToken(token);
        if (user == null) {
            throw new ServiceException(ServiceErrorCode.THE_TOKEN_NOT_VALID);
        }
        if (user.getStatus() != StatusUser.ACTIVE) {
            throw new ServiceException(ServiceErrorCode.WRONG_STATUS);
        }
        return user;
    }

    /* напишем метод проверки названия песни по следующим критериям:
    - название не менее трех букв (русского или латинского алфавита)
    - должно начинаться с большой буквы, не иметь цифр
    - поле не должно быть пустым
    Так как у разных песен бывают одинаковые названия, то сравнивать только по названию нет смысла, реализуем метод
    equals.
     */
    private void checkTitel(String titel) throws ServiceException {
        if (!titel.matches("[А-ЯA-Zа-яa-z\\s*]{3,}")) {
            throw new ServiceException(ServiceErrorCode.SONG_WRONG_TITLE);
        }
    }

    /* метод по проверки артиста. Требования такие:
    - поле не должно быть пустым, не должно быть цифр
    - начинается с заглавной буквы
    - не менее трех букв (русского или латинского алфавита)
    */
    private void checkArtist(String artist) throws ServiceException {
        if (!artist.matches("[А-ЯA-Zа-яa-z\\s*\\.]{3,}")) {
            throw new ServiceException(ServiceErrorCode.SONG_WRONG_ARTIST);
        }
    }

    /* Поле time должно соответствовать следующим требованиям
 - время (указанное в секундах) должно находиться с пределах 60сек до 600сек (трэки более 10 мин редкость и их не
 будем брать в рассчет (это не радиоформат)
  */
    private void checkTime(int time) throws ServiceException {
        if (time < 60 || time > 600) {
            throw new ServiceException(ServiceErrorCode.SONG_WRONG_TIME);
        }
    }

    /* Метод проверки поля композитор. Согласно задания формируем требования:
    - не менее одного композитора
    - для каждого указанного те же требования, что и для поля артист (будем использовать тот же метод)
     */
    private void checkComposer(List<String> composer) throws ServiceException {
        if (composer.size() < 1) {
            throw new ServiceException(ServiceErrorCode.SONG_WRONG_COMPOSER);
        }
        for (String comp : composer) {
            try {
                checkArtist(comp);
            } catch (ServiceException e) {
                throw new ServiceException(ServiceErrorCode.SONG_WRONG_COMPOSER);
            }
        }
    }

    // аналогичная проверка для авторов слов. Поэтому используем уже написанный метод
    private void checkPoet(List<String> poet) throws ServiceException {
        try {
            checkComposer(poet);
        } catch (ServiceException e) {
            throw new ServiceException(ServiceErrorCode.SONG_WRONG_POET);
        }
    }

    public SongDao getSongDao() {
        return songDao;
    }

    public void setSongDao(SongDao songDao) {
        this.songDao = songDao;
    }

    private Comment getEditComment() {
        return editComment;
    }

    private void setEditComment(Comment editComment) {
        this.editComment = editComment;
    }
}
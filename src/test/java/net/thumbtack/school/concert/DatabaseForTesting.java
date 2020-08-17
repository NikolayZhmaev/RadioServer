package net.thumbtack.school.concert;

import com.google.gson.Gson;
import net.thumbtack.school.concert.dto.request.AddCommentSongDtoRequest;
import net.thumbtack.school.concert.dto.request.AddRatingSongDtoRequest;
import net.thumbtack.school.concert.dto.request.RegisterSongDtoRequest;
import net.thumbtack.school.concert.dto.request.UserDataLoginDtoRequest;
import net.thumbtack.school.concert.errors.ServiceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// это будет класс создающий тестовую базу данных, для дальнейшей работы с ней
public class DatabaseForTesting {
    private Server server = new Server();
    private Gson gson = new Gson();
    private AddRatingSongDtoRequest addRatingDto;
    private AddCommentSongDtoRequest addCommentDto;
    // для удобства создадим перечень песен для добавления
    private List<String> composer = new ArrayList<String>();
    private List<String> poet = new ArrayList<String>();

    public DatabaseForTesting() {

    }

    public Server newDataBase() throws IOException, ServiceException {

        this.server.startServer(null);
        userFactory("Николай", "Жмаев", "Zhmaev", "Zh1111");
        String token = server.getDataBase().searchUserByLogin("Zhmaev").getToken();
        String authorId = server.getDataBase().searchUserByLogin("Zhmaev").getId();

        // добавим в базу еще несколько песен (композиторов и поэтов оставим тех же) и запишем состояние базы данных в файл
        songFactory(authorId, token, "Беспечный Ангел", "В. Кипелов", 256);
        songFactory(authorId, token, "Там высоко", "В. Кипелов", 337);
        songFactory(authorId, token, "Осколок льда", "В. Кипелов", 325);
        songFactory(authorId, token, "Дезертир", "В. Кипелов", 389);
        songFactory(authorId, token, "Улица роз", "В. Кипелов", 356);
        songFactory(authorId, token, "The Phantom Of The Opera", "Т. Турунен", 248);
        songFactory(authorId, token, "Let It Rain", "С. Ли", 276);
        songFactory(authorId, token, "One Life One Soul", "С. Ли", 238);
        songFactory(authorId, token, "Королевна", "Хелависа", 319);
        songFactory(authorId, token, "Вереск", "Хелависа", 251);
        songFactory(authorId, token, "Оборотень", "Хелависа", 351);
        songFactory(authorId, token, "Змей", "Хелависа", 352);
        songFactory(authorId, token, "The Show Must Go On", "Ф. Меркьюри", 271);
        songFactory(authorId, token, "We Will Rock You", "Ф. Меркьюри", 221);
        songFactory(authorId, token, "I Want to Break Free", "Ф. Меркьюри", 210);
        songFactory(authorId, token, "Bohemian Rhapsody", "Ф. Меркьюри", 353);

        // для дальнейшей тестировки нам потребуется добавить в базу какое-то количество пользователей
        userFactory("Дарья", "Решетникова", "Reshetnikova", "Zh1111");
        userFactory("Евгений", "Молодцов", "Molodcov", "Zh1111");
        userFactory("Валерий", "Синельников", "Sinelnikov", "Zh1111");
        userFactory("Анна", "Нуянзина", "Nuyanzina", "Zh1111");
        userFactory("Влад", "Шатов", "Shatov", "Zh1111");
        userFactory("Алексей", "Федоренко", "Fedorenco", "Zh1111");
        userFactory("Екатерина", "Шишлова", "Shishlova", "Zh1111");
        userFactory("Мария", "Юдина", "Udina", "Zh1111");
        userFactory("Светлана", "Карпенкова", "Karpenkova", "Zh1111");
        userFactory("Максим", "Доронин", "Doronin", "Zh1111");
        userFactory("Елена", "Дубова", "Dubova", "Zh1111");
        userFactory("Екатерина", "Черногородова", "Chernogorod", "Zh1111");
        String loginUser = gson.toJson(new UserDataLoginDtoRequest("Zhmaev", "Zh1111"));
        server.loginUser(loginUser);

        // для дальнейшей проверки выполним оценку песен участниками

        server.addRatingSong(ratingFactory("Reshetnikova", "Осколок льда", "В. Кипелов", 4));
        server.addRatingSong(ratingFactory("Reshetnikova", "Дезертир", "В. Кипелов", 4));
        server.addRatingSong(ratingFactory("Reshetnikova", "The Phantom Of The Opera", "Т. Турунен", 5));
        server.addRatingSong(ratingFactory("Reshetnikova", "Let It Rain", "С. Ли", 5));
        server.addRatingSong(ratingFactory("Reshetnikova", "One Life One Soul", "С. Ли", 3));
        server.addRatingSong(ratingFactory("Reshetnikova", "Королевна", "Хелависа", 2));
        server.addRatingSong(ratingFactory("Reshetnikova", "Вереск", "Хелависа", 4));
        server.addRatingSong(ratingFactory("Reshetnikova", "Оборотень", "Хелависа", 3));
        server.addRatingSong(ratingFactory("Reshetnikova", "Змей", "Хелависа", 3));
        server.addRatingSong(ratingFactory("Reshetnikova", "The Show Must Go On", "Ф. Меркьюри", 4));
        server.addRatingSong(ratingFactory("Reshetnikova", "We Will Rock You", "Ф. Меркьюри", 5));
        server.addRatingSong(ratingFactory("Reshetnikova", "I Want to Break Free", "Ф. Меркьюри", 5));
        server.addRatingSong(ratingFactory("Reshetnikova", "Bohemian Rhapsody", "Ф. Меркьюри", 5));

        server.addRatingSong(ratingFactory("Shishlova", "Осколок льда", "В. Кипелов", 5));
        server.addRatingSong(ratingFactory("Shishlova", "Дезертир", "В. Кипелов", 2));
        server.addRatingSong(ratingFactory("Shishlova", "Улица роз", "В. Кипелов", 3));
        server.addRatingSong(ratingFactory("Shishlova", "The Phantom Of The Opera", "Т. Турунен", 5));
        server.addRatingSong(ratingFactory("Shishlova", "Let It Rain", "С. Ли", 5));
        server.addRatingSong(ratingFactory("Shishlova", "One Life One Soul", "С. Ли", 5));
        server.addRatingSong(ratingFactory("Shishlova", "Королевна", "Хелависа", 5));
        server.addRatingSong(ratingFactory("Shishlova", "Вереск", "Хелависа", 4));
        server.addRatingSong(ratingFactory("Shishlova", "Оборотень", "Хелависа", 3));
        server.addRatingSong(ratingFactory("Shishlova", "Змей", "Хелависа", 2));
        server.addRatingSong(ratingFactory("Shishlova", "The Show Must Go On", "Ф. Меркьюри", 5));
        server.addRatingSong(ratingFactory("Shishlova", "We Will Rock You", "Ф. Меркьюри", 5));
        server.addRatingSong(ratingFactory("Shishlova", "I Want to Break Free", "Ф. Меркьюри", 5));
        server.addRatingSong(ratingFactory("Shishlova", "Bohemian Rhapsody", "Ф. Меркьюри", 5));

        server.addRatingSong(ratingFactory("Nuyanzina", "Осколок льда", "В. Кипелов", 5));
        server.addRatingSong(ratingFactory("Nuyanzina", "Дезертир", "В. Кипелов", 5));
        server.addRatingSong(ratingFactory("Nuyanzina", "Улица роз", "В. Кипелов", 5));
        server.addRatingSong(ratingFactory("Nuyanzina", "The Phantom Of The Opera", "Т. Турунен", 5));
        server.addRatingSong(ratingFactory("Nuyanzina", "Let It Rain", "С. Ли", 5));
        server.addRatingSong(ratingFactory("Nuyanzina", "One Life One Soul", "С. Ли", 5));
        server.addRatingSong(ratingFactory("Nuyanzina", "Королевна", "Хелависа", 5));
        server.addRatingSong(ratingFactory("Nuyanzina", "Вереск", "Хелависа", 5));
        server.addRatingSong(ratingFactory("Nuyanzina", "Оборотень", "Хелависа", 3));
        server.addRatingSong(ratingFactory("Nuyanzina", "Змей", "Хелависа", 3));
        server.addRatingSong(ratingFactory("Nuyanzina", "The Show Must Go On", "Ф. Меркьюри", 5));
        server.addRatingSong(ratingFactory("Nuyanzina", "We Will Rock You", "Ф. Меркьюри", 5));
        server.addRatingSong(ratingFactory("Nuyanzina", "I Want to Break Free", "Ф. Меркьюри", 5));
        server.addRatingSong(ratingFactory("Nuyanzina", "Bohemian Rhapsody","Ф. Меркьюри", 5));

        server.addRatingSong(ratingFactory("Doronin", "Осколок льда", "В. Кипелов", 5));
        server.addRatingSong(ratingFactory("Doronin", "Дезертир", "В. Кипелов", 2));
        server.addRatingSong(ratingFactory("Doronin", "Улица роз", "В. Кипелов", 2));
        server.addRatingSong(ratingFactory("Doronin", "The Phantom Of The Opera", "Т. Турунен", 3));
        server.addRatingSong(ratingFactory("Doronin", "Let It Rain", "С. Ли", 3));
        server.addRatingSong(ratingFactory("Doronin", "One Life One Soul", "С. Ли", 5));
        server.addRatingSong(ratingFactory("Doronin", "Королевна", "Хелависа", 4));
        server.addRatingSong(ratingFactory("Doronin", "Вереск", "Хелависа", 3));
        server.addRatingSong(ratingFactory("Doronin", "Оборотень", "Хелависа", 2));
        server.addRatingSong(ratingFactory("Doronin", "Змей", "Хелависа", 2));
        server.addRatingSong(ratingFactory("Doronin", "The Show Must Go On", "Ф. Меркьюри", 4));
        server.addRatingSong(ratingFactory("Doronin", "We Will Rock You", "Ф. Меркьюри", 5));
        server.addRatingSong(ratingFactory("Doronin", "I Want to Break Free", "Ф. Меркьюри", 4));
        server.addRatingSong(ratingFactory("Doronin", "Bohemian Rhapsody", "Ф. Меркьюри", 5));

        server.addRatingSong(ratingFactory("Molodcov", "Осколок льда", "В. Кипелов", 3));
        server.addRatingSong(ratingFactory("Molodcov", "Дезертир", "В. Кипелов", 3));
        server.addRatingSong(ratingFactory("Molodcov", "Улица роз", "В. Кипелов", 4));
        server.addRatingSong(ratingFactory("Molodcov", "The Phantom Of The Opera", "Т. Турунен", 4));
        server.addRatingSong(ratingFactory("Molodcov", "Let It Rain", "С. Ли", 3));
        server.addRatingSong(ratingFactory("Molodcov", "One Life One Soul", "С. Ли", 5));
        server.addRatingSong(ratingFactory("Molodcov", "Королевна", "Хелависа", 2));
        server.addRatingSong(ratingFactory("Molodcov", "Вереск", "Хелависа", 2));
        server.addRatingSong(ratingFactory("Molodcov", "Оборотень", "Хелависа", 4));
        server.addRatingSong(ratingFactory("Molodcov", "Змей", "Хелависа", 4));
        server.addRatingSong(ratingFactory("Molodcov", "The Show Must Go On", "Ф. Меркьюри", 5));
        server.addRatingSong(ratingFactory("Molodcov", "We Will Rock You", "Ф. Меркьюри", 4));
        server.addRatingSong(ratingFactory("Molodcov", "I Want to Break Free", "Ф. Меркьюри", 2));
        server.addRatingSong(ratingFactory("Molodcov", "Bohemian Rhapsody", "Ф. Меркьюри", 5));

        //добавим к одной из песен несколько комментариев
        server.addComment(commentFactory("Nuyanzina", "Королевна", "Хелависа", "Очень крутая песня!!!"));
        server.addComment(commentFactory("Shishlova", "Королевна", "Хелависа", "Да, песня очень сказочная"));
        server.addComment(commentFactory("Doronin", "Королевна", "Хелависа", "А мне больше Дезертир нравится"));

        //пусть пользователи присоединятся к комментам
        server.addLikeComment(commentFactory("Shatov", "Королевна", "Хелависа", "Очень крутая песня!!!"));
        server.addLikeComment(commentFactory("Shatov", "Королевна","Хелависа", "Да, песня очень сказочная"));
        server.addLikeComment(commentFactory("Dubova", "Королевна","Хелависа", "Да, песня очень сказочная"));

        return server;
    }

    private void setComposer(String composer) {
        this.composer.add(composer);
    }

    private void setPoet(String poet) {
        this.poet.add(poet);
    }

    //создадим фабрику по добавлению песен. для простоты композиторы и поэты будут одни и теже.
    private String songFactory(String authorId, String token, String title, String artist, int time) {
        setComposer("Б. Хэй");
        setComposer("Дж. Коойман");
        setPoet("М.А. Пушкина");
        RegisterSongDtoRequest song = new RegisterSongDtoRequest(authorId, token, title, composer, poet,
                artist, time);
        return server.addSong(songToJson(song));
    }

    //создадим метод по формированию запроса по добавлению оценки
    private String ratingFactory(String login, String title, String artist, int rating) {
        String token = server.getDataBase().searchUserByLogin(login).getToken();
        String id = server.getDataBase().searchUserByLogin(login).getId();
        addRatingDto = new AddRatingSongDtoRequest(id, token, title, artist, rating);
        return gson.toJson(addRatingDto);
    }

    // метод преобразования пользователя к Json строке
    public String userToJson(User user) { //vs: метод используется только в тестах, значит, ему место в тестах
        Gson gson = new Gson();
        return gson.toJson(user);
    }

    //метод для преобразования песни к Json строке
    public String songToJson(RegisterSongDtoRequest song) {
        Gson gson = new Gson();
        return gson.toJson(song);
    }

    // создадим шаблон по добавлению участников
    private void userFactory(String firstName, String lastName, String login, String password) throws ServiceException {
        User user = new User(firstName, lastName, login, password);
        server.registerUser(userToJson(user));
    }

    private String commentFactory(String login, String title, String artist, String comment) {
        String id = server.getDataBase().searchUserByLogin(login).getId();
        String token = server.getDataBase().searchUserByLogin(login).getToken();
        addCommentDto = new AddCommentSongDtoRequest(id, token, title, artist, comment);
        return gson.toJson(addCommentDto);
    }
}
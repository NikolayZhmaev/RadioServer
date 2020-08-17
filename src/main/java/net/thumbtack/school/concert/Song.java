package net.thumbtack.school.concert;

import java.io.Serializable;
import java.util.*;

// создадим объект песня. Карточка нашей песни, со всеми параметрами
public class Song implements Serializable {

    private String authorId;
    private String authorToken;
    private String title;
    private List<String> composer;
    private List<String> poet;
    private String artist;
    private int time;
    private Map<String, Integer> ratings = new HashMap<String, Integer>(); // поле для хранеия всех оценок
    private int rating; // поле хранящее сумму балов песни.
    private float averageRating; // поле хранящее средний балл
    private List<Comment> comments = new ArrayList<Comment>(); // поле для хранения комментариев

    public Map<String, Integer> getRatings() {
        return ratings;
    }

    protected List<Comment> getComments() {
        return comments;
    }

    // метод добавления коментария к песне
    public String addComments(Comment comment) {
        this.comments.add(comment);
        // после добавления комментария, будем возвращать ответ в виде id
        return comment.getAuthorId();
    }

    // метод поиска комментария по автору
    public Comment checkComment(String id) {
        for (Comment comment : comments) {
            if (comment.getAuthorId().equals(id)) {
                return comment;
            }
        }
        return null;
    }

    // поиск комментария по тексту
    public Comment searchComment(String commentTxt) {
        for (Comment comment : comments) {
            if (comment.getComment().equals(commentTxt)) {
                return comment;
            }
        }
        return null;
    }

    public String likeComment(Comment thisComment, String id) {
        for (Comment comment : comments) {
            if (comment == thisComment) {
                comment.addLike(id);
            }
        }
        return id;
    }

    public String removeLikeComment(Comment thisComment, String id) {
        for (Comment comment : comments) {
            if (comment == thisComment) {
                comment.removeLike(id);
            }
        }
        return id;
    }

    // метод удаления комментария
    public String removeComment(Comment comment, String id) {
        if (comment.getLike() == 1) {
            comments.remove(comment);
            return id;
        }
        comment.setAuthorId("000"); // пусть по умолчанию id и токен сообщества 000
        comment.setAuthorToken("000");
        comment.removeLike(id); // удаляем лайк бывшего автора
        //vs: не надо возвращать бесполезные сообщения, если действие производилось вообще над другой сущностью (комментарий). void бы подошел лучше. Относится к огромному числу методов по всему коду
        return null; // в случае отказа в удалении комментрия вернется null
    }

    // метод изменения коммнтария
    public String changeComment(Comment comment, Comment commentNew) {
        if (comment.getLike() == 1) {
            comments.remove(comment); // реализуем через удаление
            comments.add(commentNew);
            return comment.getAuthorId();
        }
        comment.setAuthorId("000");
        comments.add(commentNew);
        return null; // в случае отказа в изменении комментрия вернется null
    }

    public String setRatings(String id, Integer rating) {
        this.ratings.put(id, rating);
        // после добавления оценки добавляем ее к общему рейтингу
        setRating();
        // в качестве ответа будем передавать строку с названием и новым рейтингом
        return id;
    }

    // метод проверки оценивал ли пользователь ранее песню
    public boolean checkRaiting(String id) {
        return ratings.containsKey(id);
    }

    // метод удаления оценки
    public String removeRating(String id, int rating) {
        if (getAuthorId().equals(id)) {
            setAuthorId("Community of radio listeners");
            setAuthorToken("000"); // пусть по умолчанию токен сообщества 000
        }
        ratings.remove(id, rating); // после удаления пересчитываем рейтинг
        setRating();
        // в качестве ответа на успешное действие будем передавать id
        return id;
        //vs: представьте, что у вашего сервера есть UI. Как вы думаете, насколько удобно будет на UI парсить строку произвольного формата?
        // ответ должен быть в формате json, это общеизвестный формат, для работы с которым есть куча библиотек под разные языки. Ваш формат намного хуже в парсинге и поддержке.
        //относится ко всем методам, где в итоге Server возвращает не json
    }

    // метод изменеия оценки участника
    public String changeRating(String id, int rating) {
        ratings.put(id, rating); // после изменения оценки пересчитываем рейтинг
        setRating();
        // в качестве ответа на успешное действие будем передавать id
        return id;
    }

    public int getRating() {
        return rating;
    }

    public float getAverageRating() {
        return averageRating;
    }

    private void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    private void setRating() {
        this.rating = 0;
        for (int value : ratings.values()) {
            rating += value;
        }
        // после этого считаем средний балл
        setAverageRating(rating / ratings.size());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getComposer() {
        return composer;
    }

    private void setComposer(List<String> composer) {
        this.composer = new ArrayList<String>(composer);
    }

    public List<String> getPoet() {
        return poet;
    }

    private void setPoet(List<String> poet) {
        this.poet = new ArrayList<>(poet);
    }

    public String getArtist() {
        return artist;
    }

    private void setArtist(String artist) {
        this.artist = artist;
    }

    public int getTime() {
        return time;
    }

    private void setTime(int time) {
        this.time = time;
    }

    public String getAuthorId() {
        return authorId;
    }

    private void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorToken() {
        return authorToken;
    }

    private void setAuthorToken(String authortoken) {
        this.authorToken = authortoken;
    }

    public Song(String authorId, String authorToken, String title, List<String> composer, List<String> poet, String artist, int time) {
        setAuthorId(authorId);
        setAuthorToken(authorToken);
        setTitle(title);
        setComposer(composer);
        setPoet(poet);
        setArtist(artist);
        setTime(time);
        setRatings(authorId, 5);
        setRating();
    }

    @Override
    public String toString() {
        return "Название: " + title +
                "композитор(ы): " + composer +
                "автор(ы) слов: " + poet +
                "исполнитель: " + artist +
                "участник: " + authorId +
                "комментарии: " + comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(title, song.title) &&
                Objects.equals(composer, song.composer) &&
                Objects.equals(poet, song.poet) &&
                Objects.equals(artist, song.artist);
    }

    @Override
    public int hashCode() {

        return Objects.hash(title, composer, poet, artist);
    }
}

package net.thumbtack.school.concert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Comment {
    // создадим объект комментарий.
    private String comment; // поле для хранения текста комментария
    private int like = 0; // поле для присоединяния к комментарию (лайки), изначально 0
    private String authorId; // поле для хранеия Id автора
    private String authorToken; // поле для хранения токена автора
    private List<String> usersId = new ArrayList<String>(); // поле для хранения Id всех кто присоединился


    public Comment(String comment, String authorId, String authorToken) {
        setComment(comment);
        addLike(authorId); // при создании комментария, будем считать что его автор сразу ставит ему лайк
        setAuthorId(authorId);
        setAuthorToken(authorToken);
    }

    public String getComment() {
        return comment;
    }

    private void setComment(String comment) {
        this.comment = comment;
    }

    protected int getLike() {
        return like;
    }

    // метод добавления лайка к коментарию
    protected void addLike(String userId) {
        // добавим пользователя к списку лайкнувших
        getUsersId().add(userId);
        // теперь можно добавить лайк
        this.like = ++like;
    }

    // метод удаления лайка у комментария
    protected void removeLike(String userId) {
        // при удалении лайка, необходимо удалить пользователя поставившего его, из поля
        getUsersId().remove(userId);
        // теперь можно удалить сам лайк
        this.like = --like;
    }

    public String getAuthorId() {
        return authorId;
    }

    protected void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorToken() {
        return authorToken;
    }

    protected void setAuthorToken(String authorToken) {
        this.authorToken = authorToken;
    }

    public List<String> getUsersId() {
        return usersId;
    }

    public void setUsersId(String userId) {
        this.usersId.add(userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment1 = (Comment) o;
        return like == comment1.like &&
                Objects.equals(comment, comment1.comment) &&
                Objects.equals(authorId, comment1.authorId) &&
                Objects.equals(authorToken, comment1.authorToken) &&
                Objects.equals(usersId, comment1.usersId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(comment, like, authorId, authorToken, usersId);
    }
}

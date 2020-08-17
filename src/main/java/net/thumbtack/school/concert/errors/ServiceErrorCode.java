package net.thumbtack.school.concert.errors;

public enum ServiceErrorCode {

    USER_WRONG_FIRSTNAME("First name is not valid"), // ошибка при передаче неверного имени
    USER_WRONG_LASTNAME("Last name is not valid"), // ошибка при передаче неверной фамилии
    USER_WRONG_LOGIN("Login is not valid"), // ошибка при передаче неверного логина
    USER_WRONG_PASSWORD("Password is not valid"), // ошибка при передаче неверного пароля
    USER_WRONG_STATUS("Wrong status"), // ошибка при передаче неверного статуса
    USER_REMOVED("User removed"), // ошибка входа после удаления
    THE_USER_WAS("The user was"), // ошибка при повторной регистрации
    THE_LOGIN_WAS("The login was"), // ошибка при регистрации занятого логина
    USER_NOT_FOUND("User not found"), // ошибка, если пользователь в базе не найден
    THE_TOKEN_NOT_VALID("The token not valid"), // ошибка, токена
    WRONG_STATUS("Wrong status"), // ошибка если статус пользователя не верный
    WRONG_SERVER_STATUS("Wrong server status"), // ошибка запуска, работающего сервера
    WRONG_COMMENT("Wrong comment"), // передан не верный комментарий
    COMMENT_WRONG_AUTHOR_("Author comment is not valid"), //пользователь не является автором комментария
    SONG_WRONG_AUTHOR("Author song is not valid"), // ошибка при передаче неверного автора
    USER_NOT_PUT_LIKE("The user did not put a like"), // ошибка если пользователь не ставил лайк
    SONG_WRONG_TITLE("Title is not valid"), //  ошибка при передаче неверного названия
    SONG_WRONG_COMPOSER("Composer is not valid"), // ошибка при передаче неверного композитора
    SONG_WRONG_POET("Poet is not valid"), // ошибка при передаче неверного поэта
    SONG_WRONG_ARTIST("Artist is not valid"), // ошибка при передаче неверного исполнителя
    SONG_WRONG_TIME("Time is not valid"), // ошибка при передаче неверного времени
    SONG_NOT_FOUND("The song is not found"), // ошибка при отсутствии песни в базе.
    SONG_REGISTERED("Song registered in basel"), // ошибка при повторном добавлении песни в базу
    INCORRECT_ACTION("Incorrect action"); // выполнение недопустимого действия

    private String errorString;

    public String getErrorString() {
        return errorString;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }

    ServiceErrorCode(String errorString) {
        setErrorString(errorString);
    }
}
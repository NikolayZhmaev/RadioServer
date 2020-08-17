package net.thumbtack.school.concert;// класс участника, нечто вроде карточки с его данными.

import net.thumbtack.school.concert.errors.ServiceErrorCode;
import net.thumbtack.school.concert.errors.ServiceException;
import net.thumbtack.school.concert.serviceClasses.StatusUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User implements Serializable {
    private String firstName;
    private String lastName;
    private StatusUser status; // поле для хранения статуса учасника
    private String login;
    private String password;
    private String token; // токен учасника
    private String id; // идентификатор участника к нему будем привязывать все постоянно хранящиеся данные
    private List<Song> allUserSongs = new ArrayList<Song>();


    public String getId() {
        return id;
    }

    //воспользуемся тем же методом, что генерирует токен
    private void setId() {
        this.id = UUID.randomUUID().toString();
    }

    public String getFirstName() {
        return firstName;
    }

    private void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    private void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public StatusUser getStatus() {
        return status;
    }

    protected void setStatus(StatusUser status) throws ServiceException {
        checkStatus(status);
        this.status = status;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    private void setToken() {
        this.token = UUID.randomUUID().toString();
    }

    public User(String firstName, String lastName, String login, String password) {
        setFirstName(firstName);
        setLastName(lastName);
        this.status = StatusUser.ACTIVE; // при регистрации пользователя устанавливаем статус - активный
        setLogin(login);
        setPassword(password);
        setToken();
        setId();
    }

    // создадим метод для получения полного имени. Возможно, надобится далее
    public String getFullName() {
        return getFirstName().concat(" " + getLastName());
    }

    // напишем метод для проверки статуса
    private void checkStatus(StatusUser status) throws ServiceException {
        // если участник удалил страницу, подключение невозможно
        if (this.status == StatusUser.REMOVED) {
            throw new ServiceException(ServiceErrorCode.USER_REMOVED);
        }
        // ну и подключение к активному аккаунту также не возможно, как и выход из неактивного. Проверим еще и на null
        if (this.status == status || status == null) {
            throw new ServiceException(ServiceErrorCode.USER_WRONG_STATUS);
        }
    }

    // метод при повторном входе участника
    public String loginUser(String password) throws ServiceException {
        // проверим пароль на валидность.
        if (!getPassword().equals(password)) {
            throw new ServiceException(ServiceErrorCode.USER_WRONG_PASSWORD);
        }
        // подключаем участника, присваеваем новый token
        setStatus(StatusUser.ACTIVE);
        setToken();
        return getToken();
    }

    // метод выхода участника. после выхода возвращает статус INACTIVE
    public String logoutUser() throws ServiceException {
        setStatus(StatusUser.INACTIVE);
        return getToken().concat(" " + getStatus().toString());
    }

    // метод удаления участника
    public String removeUser() throws ServiceException {
        setStatus(StatusUser.REMOVED);
        return getToken().concat(" " + getStatus().toString());
    }

    public List<Song> getAllUserSongs() {
        return allUserSongs;
    }

    public void setAllUserSongs(Song userSong) {
        this.allUserSongs.add(userSong);
    }
}

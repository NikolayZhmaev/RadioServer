package net.thumbtack.school.concert;

import net.thumbtack.school.concert.daoimpl.SongDaoImpl;
import net.thumbtack.school.concert.daoimpl.UserDaoImpl;
import net.thumbtack.school.concert.errors.ServiceErrorCode;
import net.thumbtack.school.concert.errors.ServiceException;
import net.thumbtack.school.concert.serviceClasses.SongService;
import net.thumbtack.school.concert.serviceClasses.StatusServer;
import net.thumbtack.school.concert.serviceClasses.UserService;

import java.io.*;

public class Server {
    private StatusServer status = StatusServer.INACTIVE; // хранящее статус сервера (по умолчанию, выключен)
    private UserService userService;
    private SongService songService;
    private DataBase dataBase;

    public Server() {
    }

    public DataBase getDataBase() {
        return dataBase;
    }

    public void setDataBase(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    public SongService getSongService() {
        return songService;
    }

    public void setSongService(SongService songService) {
        this.songService = songService;
    }

    // метод производит всю необходимую инициализацию и запускает сервер.
    public void startServer(String savedDataFileName) throws IOException, ServiceException {
        checkStatus(StatusServer.ACTIVE);
        setStatus(StatusServer.ACTIVE);
        if (savedDataFileName == null) {
            setDataBase(new DataBase());
            userService = new UserService(new UserDaoImpl(getDataBase()));
            songService = new SongService(new SongDaoImpl(getDataBase()));
        } else {
            try (ObjectInputStream objInpStr = new ObjectInputStream(new FileInputStream(savedDataFileName))) {
                setDataBase((DataBase) objInpStr.readObject());
                userService = new UserService(new UserDaoImpl(getDataBase()));
                songService = new SongService(new SongDaoImpl(getDataBase()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    // Останавливает сервер и записывает все его содержимое в файл сохранения с именем savedDataFileName
    public void stopServer(String savedDataFileName) throws IOException, ServiceException {
        checkStatus(StatusServer.INACTIVE);
        setStatus(StatusServer.INACTIVE);
        dataBase.allUsersLogout();
        if (savedDataFileName != null) {
            try (ObjectOutputStream objOutStr = new ObjectOutputStream(new FileOutputStream(savedDataFileName))) {
                objOutStr.writeObject(dataBase);
            }
        }
        setDataBase(new DataBase());
    }

    // метод для регистрации радиослушателя.
    public String registerUser(String requestJsonString) throws ServiceException {
       checkStatus(StatusServer.INACTIVE);
       return getUserService().userRegister(requestJsonString);
    }

    // метод для входа радиослушателя
    public String loginUser(String requestJsonString) throws ServiceException {
       checkStatus(StatusServer.INACTIVE);
       return getUserService().userLogin(requestJsonString);
    }

    // метод для выхода радиослушателя. По всех видимости, сюда будет передоваться строка с логином
    public String logoutUser(String requestJsonString) {
      return   getUserService().userLogout(requestJsonString);
    }

    // метод для удаления радиослушателя. В качестве параметра метод будет принимать логин
    public String removeUser(String requestJsonString) {
       return getUserService().userRemove(requestJsonString);
    }

    /* метод для добавления песни участником. В этом случае, в запросе должны быть указаны все необходимые (согласно
    техзадания) требования, и, очевидно, token и id, для идентификации участника */
    public String addSong(String requestJsonString) {
       return getSongService().songRegister(requestJsonString);
    }

    // метод добавления оценки песни (передаются токен, id, название песни, оценка)
    public String addRatingSong(String requestJsonString) {
       return getSongService().addRatingSong(requestJsonString);
    }

    // метод изменения оценки (передаются id, токен, название песни, новая оценка)
    public String changeRatingSong(String requestJsonString) {
        return getSongService().changeRatingSong(requestJsonString);
    }

    // метод удаления песни (передаются id, токен, название песни)
    public String removeSong(String requestJsonString) {
       return getSongService().removeSong(requestJsonString);
    }

    // метод для получения участником всех своих предложений (передаются id, токен)
    public String getAllUserSongs(String requestJsonString) {
        return getSongService().getAllUserSongs(requestJsonString);
    }

    // метод формирования пробной программы концерта. Передается токен
    public String concert(String requestJsonString) {
       return getSongService().concert(requestJsonString);
    }

    // метод добавления комментария к песне. В передаваемой строке будет id, token, название песни и комментарий.
    public String addComment(String requestJsonString) {
        return getSongService().addComment(requestJsonString);
    }

    // метод удаления комментария. В передаваемой строке будет id, token, название песни и комментарий.
    public String removeComment(String requestJsonString) {
       return getSongService().removeComment(requestJsonString);
    }

    //метод изменения комментария (автором). В передаваемой строке будет id, token, название песни и комментарий.
    public String changeComment(String requestJsonString) {
       return getSongService().changeComment(requestJsonString);
    }

    //метод добавления лайка к коментарию. В передаваемой строке будет id, token, название песни и комментарий.
    public String addLikeComment(String requestJsonString) {
        return getSongService().addLikeComment(requestJsonString);
    }

    //метод добавления лайка к коментарию. В передаваемой строке будет id, token, название песни и комментарий.
    public String removeLikeComment(String requestJsonString) {
       return getSongService().removeLikeComment(requestJsonString);
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public StatusServer getStatus() {
        return status;
    }

    public void setStatus(StatusServer status) {
        this.status = status;
    }

    // напишем метод для проверки статуса
    public void checkStatus(StatusServer status) throws ServiceException {
        // если сервер уже запущен или остановлен, попытка повторного запуска или остановки приведет к ошибке
        if (getStatus() == status) {
            throw new ServiceException(ServiceErrorCode.WRONG_SERVER_STATUS);
        }
    }
}

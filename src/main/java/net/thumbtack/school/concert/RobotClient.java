package net.thumbtack.school.concert;

import com.google.gson.Gson;
import net.thumbtack.school.concert.dto.response.SongDtoResponse;
import net.thumbtack.school.concert.dto.response.UserDtoResponse;
import net.thumbtack.school.concert.errors.ServiceException;

import java.util.ArrayList;
import java.util.List;

public class RobotClient {

    private Server server;
    private User user;
    private int numberOfSongs; // поле хранящее количество песен (необходимый результат)
    Gson gson = new Gson();

/* Этот клиент будет:
  - регистрировать переданного ему юзера на сервере,
  - запрашивать список песен, участвующих в концерте (на основании рейтинга),
  - удалять юзера,
  - возвращать количество участвующих песен
  */

   // так как это робот клиент, сделаем его более универсальным, дадим ему возможность работать с тем сервером, который ему передадут

    public RobotClient(Server server, User user) {
        setServer(server);
        setUser(user);
    }

    public int robotClientWorking() throws ServiceException {
        String token = registerUser();
        setNumberOfSongs(concert(token));
        removeUser();
        return numberOfSongs;
    }

    //метод регистрации юзера на сервере и получении его токена
    public String registerUser () throws ServiceException {
        String response = server.registerUser(gson.toJson(user));
        UserDtoResponse userDtoResponse = gson.fromJson(response, UserDtoResponse.class);
        return userDtoResponse.getResponse();
    }

    //метод удаления юзера с сервера
    public String removeUser() {
       return server.removeUser(user.getLogin());
    }


    //метод запроса программы концерта и возврата количества участвующих в ней песен
    public int concert (String token) {
        SongDtoResponse dtoResponse = gson.fromJson(server.concert(token), SongDtoResponse.class);
        List<Song> songs = gson.fromJson(dtoResponse.getTitle(), ArrayList.class);
        return songs.size();
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getNumberOfSongs() {
        return numberOfSongs;
    }

    public void setNumberOfSongs(int numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }
}



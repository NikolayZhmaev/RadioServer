package net.thumbtack.school.concert;// создадим класс - наша база данных

import net.thumbtack.school.concert.errors.ServiceException;
import net.thumbtack.school.concert.serviceClasses.StatusUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class DataBase implements Serializable {

    // создадим поле, в котором будут храниться все зарегистрированные пользователи

    HashMap<String, User> users = new HashMap<String, User>();

    // создадим поле для хранения участвующих песен (по id авторов)
    //vs: вижу, что в методах поиска по songs поиск ведется по title. думаю, было бы эффективней использовать HashMap, где ключ будет title, а значение - Song.
    //в таком случае не надо будет делать перебор списка (линейная сложность), вместо этого получение по ключу (константная сложность)
    private List<Song> songs = new ArrayList<Song>();

    // поле хранящее программу концерта
    private List<Song> concert = new ArrayList<Song>();

    public DataBase() {
    }

    public DataBase(HashMap<String, User> users, List<User> removeUsers, List<Song> songs, List<Song> concert) {
        setUsers(users);
        setSongs(songs);
        setConcert(concert);
    }

    public List<Song> getConcert() {
        return concert;
    }

    public void setConcert(List<Song> concert) {
        this.concert = concert;
    }

    private void setSongInConcert(Song song) {
        this.concert.add(song);
    }

    protected List<Song> getSongs() {
        return songs;
    }

    private void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    // метод добавления песни в базу
    public String setSong(Song song, String userId) {
        this.songs.add(song); // добавим песню в базу
        searchUserById(userId).setAllUserSongs(song);//а также добавим в лк учатника
        return song.getTitle(); /* в задании нет конкретики, поэтому если песня добавилась в базу ответ будет ввиде
                                   названия песни*/
    }

    public String setRemoveUser(User removeUser) {
        try {
            removeUser.removeUser();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        // в соответствии с задание подчистим оценки и песни с базы
        List<Song> songs = allUserSongs(removeUser.getId());
        for (Song song : songs) {
            if (song.getRating() == 5) {
                removeSong(song);
            }
            removeRatingSong(song, removeUser.getId(), 5);
        }
        return removeUser.getId(); // ответ будет таким
    }


    public void setUsers(HashMap<String, User> users) {
        this.users = users;
    }

    public HashMap<String, User> getUsers() {
        return users;
    }


    public String setUser(User user) {
        this.users.put(user.getLogin(), user);
        return user.getToken();
    }

    public boolean searchUserByFullName(String fullName) {
        for (User user : users.values()) {
            if (user.getFullName().equals(fullName)) {
                return true;
            }
        }
        return false;
    }

    public User searchUserByLogin(String login) {
        return users.get(login);
    }

    public User searchUserById(String userId) {
        for (User user : users.values()) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    public User searchUserByToken(String token) {
        for (User user : users.values()) {
            if (user.getToken().equals(token)) {
                return user;
            }
        }
        return null;
    }

    public Song searchSongInDB(String title, String artist) {
        for (Song song : songs) {
            if (song.getTitle().equals(title)) {
                if (song.getArtist().equals(artist)) {
                    return song;
                }
            }
        }
        return null;
    }

    public boolean containsSong(String title, String artist) {
        for (Song song : songs) {
            if (song.getTitle().equals(title)) {
                if (song.getArtist().equals(artist)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String removeSong(Song song) {
        songs.remove(song);
        return song.getTitle(); // вернем результат в виде такой строки
    }

    public List<Song> allUserSongs(String userId) {
        return searchUserById(userId).getAllUserSongs(); //vs: не было бы удобнее у юзера сделать поле List<Song>? таким образом все свелось бы к поиску нужного юзера
    }

    public void removeRatingSong(Song song, String id, int rating) {
        for (Song songBase : songs) {
            if (songBase.equals(song)) {
                songBase.removeRating(id, rating);
                break;
            }
        }
    }

    //при отключении сервера все пользователи отключаются
    protected void allUsersLogout() throws ServiceException {
        for (User user : users.values()) {
            if (user.getStatus() == StatusUser.ACTIVE) {
                user.setStatus(StatusUser.INACTIVE);
            }
        }
    }

    //метод обратный предыдущему (для удобства тестирования)
    protected void allUsersLogin() throws ServiceException {
        for (User user : users.values()) {
            if (user.getStatus() == StatusUser.INACTIVE) {
                user.setStatus(StatusUser.ACTIVE);
            }
        }
    }

    public List<Song> concert() {
        //для начала выполним сортировку всех песен в базе.
        songs.sort(new Comparator<Song>() {
            @Override
            public int compare(Song o1, Song o2) {
                if (o1.getRating() == o2.getRating()) return 0;
                else if (o1.getRating() > o2.getRating()) return -1;
                else return 1;
            }
        });

        /*теперь сформируем программу концерта следующим образом:
        - суммарная продолжительность концерта не превышает 60 минут
        - между каждыми двумя песнями делается пауза продолжительностью в 10 секунд
        - если очередная песня не может быть добавлена в концерт, потому что будет превышено время концерта,
          эта песня пропускается, и делается попытка добавить следующую по популярности песню и т.д
        */
        int concertTime = 0; // поле для хранения времени понцерта
        for (Song song : songs) {
            if (song.getTime() + concertTime <= 3600) {
                setSongInConcert(song);
                concertTime += song.getTime() + 10;
            }
        }
        return concert;
    }
}

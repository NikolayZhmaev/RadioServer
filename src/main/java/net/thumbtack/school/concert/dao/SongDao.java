package net.thumbtack.school.concert.dao;

import net.thumbtack.school.concert.Song;
import net.thumbtack.school.concert.User;

import java.util.List;

public interface SongDao {


    String insertSong(Song song, String userId); // регистрирует песню в базе

    Song searchSongInDB(String title, String artist); // поиск песни в базе по названию

    boolean containsSong(String title, String artist); // проверяет, есть ли в базе такая песня

    List<Song> allUserSongs(String userId); // возвращает список всех песен участника

    String removeSong(Song song); // удаление песни из базы

    User searchUserByToken(String token); // ищет участника по токену

    User searchUserById(String userId); // ищет участника по id

    List<Song> concert(); // метод формирования концерта
}
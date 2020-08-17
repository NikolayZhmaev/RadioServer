package net.thumbtack.school.concert.daoimpl;

import net.thumbtack.school.concert.DataBase;
import net.thumbtack.school.concert.Song;
import net.thumbtack.school.concert.User;
import net.thumbtack.school.concert.dao.SongDao;

import java.util.List;

public class SongDaoImpl implements SongDao {

    DataBase dataBase;

    public SongDaoImpl(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    public String insertSong(Song song, String userId) {
        return dataBase.setSong(song, userId);
    }

    @Override
    public Song searchSongInDB(String title, String artist) {
        return dataBase.searchSongInDB(title, artist);
    }

    @Override
    public boolean containsSong(String title, String artist) {
        return dataBase.containsSong(title, artist);
    }

    @Override
    public List<Song> allUserSongs(String userId) {
        return dataBase.allUserSongs(userId);
    }

    @Override
    public String removeSong(Song song) {
        return dataBase.removeSong(song);
    }

    @Override
    public User searchUserByToken(String token) {
        return dataBase.searchUserByToken(token);
    }

    @Override
    public User searchUserById(String userId) {
        return dataBase.searchUserById(userId);
    }


    @Override
    public List<Song> concert() {
        return dataBase.concert();
    }
}
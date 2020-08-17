package net.thumbtack.school.concert.dto.request;

import net.thumbtack.school.concert.Song;

import java.util.List;

public class RegisterSongDtoRequest extends Song {
    public RegisterSongDtoRequest(String authorId, String authorToken, String title, List<String> composer, List<String> poet, String artist, int time) {
        super(authorId, authorToken, title, composer, poet, artist, time);
    }
}
package net.thumbtack.school.concert.dto.request;

public class RemoveSongDtoRequest {
    private String id, token, title, artist;

    public RemoveSongDtoRequest(String id, String token, String title, String artist) {
        setId(id);
        setToken(token);
        setTitle(title);
        setArtist(artist);
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
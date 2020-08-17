package net.thumbtack.school.concert.dto.request;

public class AddRatingSongDtoRequest {
    private String id, token, title, artist;
    private int rating;

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

    public int getRating() {
        return rating;
    }

    private void setRating(int rating) {
        this.rating = rating;
    }

    public AddRatingSongDtoRequest(String id, String token, String title, String artist, int rating) {
        setId(id);
        setToken(token);
        setTitle(title);
        setArtist(artist);
        setRating(rating);
    }
}
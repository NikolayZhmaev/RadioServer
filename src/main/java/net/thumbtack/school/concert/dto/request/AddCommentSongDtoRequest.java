package net.thumbtack.school.concert.dto.request;

public class AddCommentSongDtoRequest {
    private String id, token, title, artist, comment;

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

    public String getComment() {
        return comment;
    }

    private void setComment(String comment) {
        this.comment = comment;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public AddCommentSongDtoRequest(String id, String token, String title, String artist, String comment) {
        setId(id);
        setToken(token);
        setTitle(title);
        setArtist(artist);
        setComment(comment);
    }
}
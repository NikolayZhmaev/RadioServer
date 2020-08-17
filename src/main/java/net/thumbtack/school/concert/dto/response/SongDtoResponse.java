package net.thumbtack.school.concert.dto.response;

public class SongDtoResponse {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SongDtoResponse(String title) {
        setTitle(title);
    }
}
package net.thumbtack.school.concert.dto.request;

public class UserDataLoginDtoRequest {
    private String login, password;


    public UserDataLoginDtoRequest(String login, String password) {
        setLogin(login);
        setPassword(password);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
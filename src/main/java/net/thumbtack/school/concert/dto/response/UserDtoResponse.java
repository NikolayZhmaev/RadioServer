package net.thumbtack.school.concert.dto.response;

/*создадим класс ответа на регистрацию пользователя и любые его действия. В нем же будет только одно поле, response,
 его и будем возвращать, как результат действия участника на сервере*/

public class UserDtoResponse {
    private String response;

    public String getResponse() {
        return response;
    }

    private void setResponse(String response) {
        this.response = response;
    }

    public UserDtoResponse(String token) {
        setResponse(token);
    }
}
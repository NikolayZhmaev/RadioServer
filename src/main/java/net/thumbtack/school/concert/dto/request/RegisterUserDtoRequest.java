package net.thumbtack.school.concert.dto.request;

//создадим класс запроса на регистрацию пользователя. В нем же будем осуществлять проверку передаваемых параметров

import net.thumbtack.school.concert.User;

public class RegisterUserDtoRequest extends User {

    public RegisterUserDtoRequest(String firstName, String lastName, String login, String password) {
        super(firstName, lastName, login, password);
    }
}
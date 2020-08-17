package net.thumbtack.school.concert.dao;

import net.thumbtack.school.concert.User;

public interface UserDao {

    String insertUser(User user); // регистрирует участника в базе

    boolean searchUserByFullName(String fullName); // ищет есть ли такой зарегистрированный участник

    User searchUserByLogin(String login); // ищет есть ли такой логин в базе

    User searchUserByToken(String token); // ищет участника по токену

    String remove(User user); // удаляет участника из базы
}
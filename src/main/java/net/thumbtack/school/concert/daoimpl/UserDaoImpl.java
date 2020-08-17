package net.thumbtack.school.concert.daoimpl;

import net.thumbtack.school.concert.DataBase;
import net.thumbtack.school.concert.User;
import net.thumbtack.school.concert.dao.UserDao;

public class UserDaoImpl implements UserDao {

    DataBase dataBase;

    public UserDaoImpl(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    public String insertUser(User user) {
        return dataBase.setUser(user);
    }

    @Override
    public boolean searchUserByFullName(String fullName) {
        return dataBase.searchUserByFullName(fullName);
    }

    @Override
    public User searchUserByLogin(String login) {
        return dataBase.searchUserByLogin(login);
    }

    @Override
    public User searchUserByToken(String token) {
        return null;
    }

    @Override
    public String remove(User user) {
        return dataBase.setRemoveUser(user);
    }
}
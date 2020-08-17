package net.thumbtack.school.concert;

import com.google.gson.Gson;
import net.thumbtack.school.concert.daoimpl.UserDaoImpl;
import net.thumbtack.school.concert.dto.request.UserDataLoginDtoRequest;
import net.thumbtack.school.concert.dto.response.UserDtoResponse;
import net.thumbtack.school.concert.errors.ServiceException;
import net.thumbtack.school.concert.serviceClasses.StatusUser;
import net.thumbtack.school.concert.serviceClasses.UserService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class TestRegisterAndRemoveUsers {

    private Server server = new Server();
    private Gson gson = new Gson();
    private UserDtoResponse dtoResponse;

    @Rule
    public final TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

    //напишем метод для формирования json строки
    private String response(String response) {
        dtoResponse = new UserDtoResponse(response);
        return gson.toJson(dtoResponse);
    }

    // метод преобразования пользователя к Json строке
    public String userToJson(User user) {
        Gson gson = new Gson();
        return gson.toJson(user);
    }

    // запустить сервер и добавим пользователя (информацию сохраним в файл)
    @Test
    public void testRegisterUser() throws IOException, ServiceException {
        User user = new User("Николай", "Жмаев", "Zhmaev", "Zh1111");
        server.startServer(null);
        server.registerUser(userToJson(user));
        assertEquals(server.getDataBase().getUsers().size(), 1);
        assertEquals(server.getDataBase().searchUserByLogin("Zhmaev").getStatus(), StatusUser.ACTIVE);
        server.stopServer("text.txt");
    }

    // прочитаем информацию из файла и попробуем удалить пользователя
    @Test
    public void testRemoveUser() throws IOException, ServiceException {
        server.startServer("text.txt");
        server.getDataBase().allUsersLogin();
        server.removeUser("Zhmaev");
        assertEquals(server.getDataBase().getUsers().size(), 1);
        assertEquals(server.getDataBase().searchUserByLogin("Zhmaev").getStatus(), StatusUser.REMOVED);
        server.stopServer("text.txt");
    }


    // добавим пользователя, осуществим выход и вход на сервер
    @Test
    public void testLogoutUser() throws IOException, ServiceException {
        User user = new User("Алексей", "Иванов", "Ivanov", "I22222");
        server.startServer(null);
        server.registerUser(userToJson(user));
        server.logoutUser("Ivanov");
        assertEquals(server.getDataBase().searchUserByLogin("Ivanov").getStatus(), StatusUser.INACTIVE);
        server.stopServer("text.txt");
    }

    @Test
    public void testLoginUser() throws IOException, ServiceException {
        server.startServer("text.txt");
        String loginUser = gson.toJson(new UserDataLoginDtoRequest("Ivanov", "I22222"));
        server.loginUser(loginUser);
        assertEquals(server.getDataBase().searchUserByLogin("Ivanov").getStatus(), StatusUser.ACTIVE);
        server.stopServer(null);
    }

    // пробуем дважды зарегистрировать пользователя
    @Test
    public void testRegisterUserTwice() throws IOException, ServiceException {
        server.startServer(null);
        User user = new User("Сергей", "Петров", "Petrov", "P33333");
        server.registerUser(userToJson(user));
        User user1 = new User("Сергей", "Петров", "Petrov", "P33333");
        //vs: как упоминал в другом месте, сервер должен отвечать в формате json.
        assertEquals(server.registerUser(userToJson(user1)), response("The user was"));
        assertEquals(server.getDataBase().getUsers().size(), 1);
        server.stopServer(null);
    }


    /* проверим некорректные данные при регистрации и входе пользователя:
     - недопустимая фамилия,
     - недопустимый логин,
     - логин который уже занят,
     - недопустимый пароль,
     - неверный пароль при входе.
     Здесь же параллельно проверяется поиск пользователей в базе по имени и логину.
     */

    // недопустимое имя
    @Test
    public void testIncorrectFirstName() throws IOException, ServiceException {
        server.startServer(null);
        User user = new User("C", "Петров", "Petrov", "P33333");
        assertEquals(server.registerUser(userToJson(user)), response("First name is not valid"));
        assertEquals(server.getDataBase().getUsers().size(), 0);
        server.stopServer(null);
    }

    //недопустимый логин
    @Test
    public void testIncorrectLogin() throws IOException, ServiceException {
        server.startServer(null);
        User user = new User("Иванов", "Иван", "Иванов", "P33333");
        assertEquals(server.registerUser(userToJson(user)), response("Login is not valid"));
        assertEquals(server.getDataBase().getUsers().size(), 0);
        server.stopServer(null);
    }

    // недопустимый пароль
    @Test
    public void testIncorrectPassword() throws IOException, ServiceException {
        server.startServer(null);
        User user = new User("Андрей", "Сидоров", "Sidorov", "P4 444");
        assertEquals(server.registerUser(userToJson(user)), response("Password is not valid"));
        assertEquals(server.getDataBase().getUsers().size(), 0);
    }

    // неверный пароль при входе
    @Test
    public void testIncorrectData() throws IOException, ServiceException {
        server.startServer(null);
        User user = new User("Николай", "Жмаев", "Zhmaev", "Zh11111");
        User user2 = new User("Анна", "Жмаева", "AnnaZhmaeva", "Zh22222");
        server.registerUser(userToJson(user));
        server.registerUser(userToJson(user2));
        assertEquals(server.getDataBase().getUsers().size(), 2);
        server.logoutUser("Zhmaev");
        assertEquals(server.getDataBase().searchUserByLogin("Zhmaev").getStatus(), StatusUser.INACTIVE);
        String loginUser = gson.toJson(new UserDataLoginDtoRequest("Zhmaev", "I22222"));
        assertEquals(server.loginUser(loginUser), response("Password is not valid"));
        server.stopServer(null);
    }

    // занятый логин
    @Test
    public void testIncorrectDataUser() throws IOException, ServiceException {
        server.startServer(null);
        User user = new User("Сергей", "Петров", "Petrov", "P33333");
        dtoResponse = gson.fromJson(server.registerUser(userToJson(user)), UserDtoResponse.class);
        assertEquals(server.getDataBase().getUsers().size(), 1);
        User user2 = new User("Андрей", "Петров", "Petrov", "P4444");
        assertEquals(server.registerUser(userToJson(user2)), response("The login was"));
        assertEquals(server.getDataBase().getUsers().size(), 1);
    }

    //ДАЛЕЕ ТЕСТЫ С MOCKITO!!!!!!!!!!!!!!!!!!!!!

    //проверим выход участника
    @Test
    public void testLogoutUserMock() {
        UserDaoImpl mockUserDaoImpl = mock(UserDaoImpl.class);
        User user = new User("", "", "", "");
        when(mockUserDaoImpl.searchUserByLogin(any(String.class))).thenReturn(user);
        UserService userService = new UserService(mockUserDaoImpl);
        String jsonresponse = userService.userLogout("Ivanov");
        assertTrue(jsonresponse.contains("INACTIVE"));
    }

    //тоже что и предыдущий тест, но с помощью mock
    @Test
    public void testLoginUserMock() {
        UserDaoImpl mockUserDaoImpl = mock(UserDaoImpl.class);
        User user = new User("", "", "", "");
        when(mockUserDaoImpl.searchUserByLogin(any(String.class))).thenReturn(user);
        UserService userService = new UserService(mockUserDaoImpl);
        String jsonresponse = userService.userLogout("Ivanov");
        assertTrue(jsonresponse.contains("INACTIVE"));

        String loginUser = gson.toJson(new UserDataLoginDtoRequest("Petrov", ""));
        jsonresponse = userService.userLogin(loginUser);
        assertTrue(jsonresponse.contains("-"));
    }

    //попробуем залогиниться, не выходя
    @Test
    public void testLoginUserTwiceMock() {
        UserDaoImpl mockUserDaoImpl = mock(UserDaoImpl.class);
        User user = new User("", "", "", "");
        when(mockUserDaoImpl.searchUserByLogin(any(String.class))).thenReturn(user);
        UserService userService = new UserService(mockUserDaoImpl);

        String loginUser = gson.toJson(new UserDataLoginDtoRequest("Petrov", ""));
        String jsonresponse = userService.userLogin(loginUser);
        assertTrue(jsonresponse.contains("Wrong status"));
    }

    // Проверим, что метод (searchUserByLogin) вызывается как минимум один раз
    @Test
    public void testLogInVerifyMock() throws Exception {
        UserDaoImpl mockUserDaoImpl = mock(UserDaoImpl.class);
        UserService userService = new UserService(mockUserDaoImpl);
        String loginUser = gson.toJson(new UserDataLoginDtoRequest("Petrov", "12345687"));
        userService.userLogin(loginUser);
        verify(mockUserDaoImpl, atLeastOnce()).searchUserByLogin("Petrov");
    }

    //попробуем удалить юзера (вызов у юзера метода remove)
    @Test
    public void testRemoveUserMock() {
        UserDaoImpl mockUserDaoImpl = mock(UserDaoImpl.class);
        User user = new User("", "", "", "");
        when(mockUserDaoImpl.searchUserByLogin(any(String.class))).thenReturn(user);
        UserService userService = new UserService(mockUserDaoImpl);
        when(mockUserDaoImpl.remove(any())).thenReturn("REMOVED");
        String jsonresponse = userService.userRemove("Ivanov");
        assertTrue(jsonresponse.contains("REMOVED"));
    }

    //проверка вызова метода с нужными параметрами
    @Test
    public void testLogInCallsSearchUserByLoginMock() throws Exception {
        UserDaoImpl mockUserDaoImpl = mock(UserDaoImpl.class);
        UserService userService = new UserService(mockUserDaoImpl);
        String loginUser = gson.toJson(new UserDataLoginDtoRequest("Petrov", "12345687"));
        userService.userLogin(loginUser);
        verify(mockUserDaoImpl).searchUserByLogin("Petrov");
    }

    // Проверим, что userLogin каждый раз генерирует новый токен
    @Test
    public void testLogInSetTokensMock() throws Exception {
        UserDaoImpl mockUserDaoImpl = mock(UserDaoImpl.class);
        UserService userService = new UserService(mockUserDaoImpl);
        User user = new User("Сергей", "Петров", "Petrov", "P33333");
        user.setStatus(StatusUser.INACTIVE);
        when(mockUserDaoImpl.searchUserByLogin(any(String.class))).thenReturn(user);
        String loginUser = gson.toJson(new UserDataLoginDtoRequest("Petrov", "P33333"));

        String token1 = userService.userLogin(loginUser);
        user.setStatus(StatusUser.INACTIVE);
        String token2 = userService.userLogin(loginUser);
        assertNotEquals(token1, token2);
    }
}
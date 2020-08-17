package net.thumbtack.school.concert.serviceClasses;

// сервисный класс для выполнения действий по добавлению, удалению, входу и выходу участников.

import com.google.gson.Gson;
import net.thumbtack.school.concert.User;
import net.thumbtack.school.concert.dao.UserDao;
import net.thumbtack.school.concert.daoimpl.UserDaoImpl;
import net.thumbtack.school.concert.dto.request.RegisterUserDtoRequest;
import net.thumbtack.school.concert.dto.request.UserDataLoginDtoRequest;
import net.thumbtack.school.concert.dto.response.UserDtoResponse;
import net.thumbtack.school.concert.errors.ServiceErrorCode;
import net.thumbtack.school.concert.errors.ServiceException;

// класс для проверки переданных параметров, при регистрации пользователя, и создания этого пользователя
public class UserService {
    /*vs: плохая идея хранить состояние, не являющееся общим для всего класса (user, request/response), в качестве поля. В случае двух параллельных
     * пользователей один запрос будет переписывать поля второго и в результате ваша система будет возвращать что попало. Тоже самое для всех остальных сервисов
     */

    //vs: 2 плохих практики с dao: 1. Используете конкретный класс вместо интерфейса, теряя гибкость
    // 2. Ответственность по созданию экземпляра dao не должна принадлежать классу сервиса. Сервис должен только принимать dao как параметр конструктора
    // это позволит сделать код более гибким, не привязанным к реализации и более простому юнит-тестированию
    private UserDao userDao;
    private Gson gson = new Gson();

    public UserService(UserDaoImpl userDaoImpl) {
        setUserDao(userDaoImpl);
    }

    // напишем метод поиска участника по базе, в случае отсутствия такого участника в базе, пробрасывается ошибка.
    public User userFind(String login) throws ServiceException {
        User userFind = userDao.searchUserByLogin(login);
        if (userFind != null) {
            return userFind;
        } else throw new ServiceException(ServiceErrorCode.USER_NOT_FOUND);
    }

    /* сервисный метод по проверке передаваемых параметров и, в случае их корректности, регистрации радиослушателя.
    В случае передачи неверных данных возвращает Json с соответствующей ServiceException */
    public String userRegister(String requestJsonString) {
        RegisterUserDtoRequest dtoRequest = gson.fromJson(requestJsonString, RegisterUserDtoRequest.class);
        // выполняем проверку необходимых условий
        try {
            checkFirstName(dtoRequest.getFirstName());
            checkLastName(dtoRequest.getLastName());
            checkFullName(dtoRequest.getFullName());
            checkLogin(dtoRequest.getLogin());
            checkPassword(dtoRequest.getPassword());
            // после выполнения всех необходимых проверок, регестрируем нового пользователя
            User user = new User(dtoRequest.getFirstName(), dtoRequest.getLastName(),
                    dtoRequest.getLogin(), dtoRequest.getPassword());
            // после регистрации добавляем его в нашу базу и возвращаем результат.
            UserDtoResponse dtoResponse = new UserDtoResponse(getUserDao().insertUser(user));
            return gson.toJson(dtoResponse);
        } catch (ServiceException e) {
            UserDtoResponse dtoResponse = new UserDtoResponse(e.getMessage());
            return gson.toJson(dtoResponse);
        }
    }

    // сервисный метод для повторного входа участника
    public String userLogin(String requestJsonString) {
        /* при входе необходимо ввести логин и пароль, то есть две строки, они и присутствуют в запросе. Обработаем
        запрос и "выдерним" из него эти строки.*/
        UserDataLoginDtoRequest userDataInput = gson.fromJson(requestJsonString, UserDataLoginDtoRequest.class); //vs: почему используется String[] вместо объекта? Объект более понятен и проще в поддержке
        /* выполнив поиск по нашей базе данных, выполняем метод входа, с передачей пароля. Если пароль верный,
        выполняется вход, если нет, пробрасывается ошибка*/
        try {
            User user = userFind(userDataInput.getLogin());
            UserDtoResponse dtoResponse = new UserDtoResponse(user.loginUser(userDataInput.getPassword()));
            return gson.toJson(dtoResponse);
        } catch (ServiceException e) {
            UserDtoResponse dtoResponse = new UserDtoResponse(e.getMessage());
            return gson.toJson(dtoResponse);
        }
    }

    // сервисный метод для выхода участника
    public String userLogout(String requestJsonString) {
        // выполнив поиск по нашей базе данных, выполняем метод выхода, возвращаем результат в виде token и статуса
        try {
            User user = userFind(requestJsonString);
            UserDtoResponse dtoResponse = new UserDtoResponse(user.logoutUser());
            return gson.toJson(dtoResponse);
        } catch (ServiceException e) {
            UserDtoResponse dtoResponse = new UserDtoResponse(e.getMessage());
            return gson.toJson(dtoResponse);
        }
    }

    /* сервисный метод для удаления участника. Если радиослушатель покидает сервер, считается, что он отменяет все свои предложения,
    это возможно если на момент отмены, предложение не получило никаких оценок от других радиослушателей. Если же к
    этому моменту имеются другие оценки этого предложения, то удаляется лишь оценка этого предложения, сделанная его
    автором, а само предложение не удаляется, а автором предложения считается сообщество радиослушателей. */
    public String userRemove(String requestJsonString) {
        try {
            User user = userFind(requestJsonString);
            UserDtoResponse dtoResponse = new UserDtoResponse(getUserDao().remove(user));
            return gson.toJson(dtoResponse);
        } catch (ServiceException e) {
            UserDtoResponse dtoResponse = new UserDtoResponse(e.getMessage());
            return gson.toJson(dtoResponse);
        }
    }

    /* Нам необходимо чтобы при регистрации пользователь указывал реальные имя и фамилию, а значит надо проверить чтобы:
        - строка не была пустой,не менее 2-х символов и не более 12
        - начиналась с заглавной буквы
        - остальные буквы были маленькие
        - небыло символов и цифр
        - буквы русского алфавита
        */
    private void checkFirstName(String name) throws ServiceException {
        if (!name.matches("[А-Я][а-я]{1,12}")) {
            throw new ServiceException(ServiceErrorCode.USER_WRONG_FIRSTNAME);
        }
    }

    // аналогичный предыдущему методу
    private void checkLastName(String name) throws ServiceException {
        try {
            checkFirstName(name);
        } catch (ServiceException e) {
            throw new ServiceException(ServiceErrorCode.USER_WRONG_LASTNAME);
        }
    }

    /* Согласно техзадания, пользователь не может регистрироваться дважды, поэтому проверим нет ли такого пользователя
     в нашей базе, среди зарегистрированных или удаленных. Конечно бывают "полные тезки", однако, это на столько
     редкое явление, тем более, в рамках одного конкурса, что имеет смысл им пренебреч*/

    private void checkFullName(String fullName) throws ServiceException {
        if (userDao.searchUserByFullName(fullName)) {
            throw new ServiceException(ServiceErrorCode.THE_USER_WAS);
        }
    }

    /* Логин должен соответствовать следующим критериям:
    - только латинские буквы любого регистра, не менее 2 и не более 12 символов
    - любые цифры и дефис
    - ни каких пробелов
    - свободен ли этот логин
     */
    private void checkLogin(String login) throws ServiceException {
        if (!login.matches("[a-zA-Z0-9\\-*]{2,12}")) {
            throw new ServiceException(ServiceErrorCode.USER_WRONG_LOGIN);
        }
        if (userDao.searchUserByLogin(login) != null) {
            throw new ServiceException(ServiceErrorCode.THE_LOGIN_WAS);
        }
    }

    /* Пароль должен соответствовать следующим критериям:
      - только латинские буквы любого регистра, не менее 6 и не более 10 символов
      - любые цифры
      - ни каких пробелов
    */
    private void checkPassword(String password) throws ServiceException {
        if (!password.matches("[a-zA-Z0-9]{6,10}")) {
            throw new ServiceException(ServiceErrorCode.USER_WRONG_PASSWORD);
        }
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
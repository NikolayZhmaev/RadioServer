package net.thumbtack.school.concert;

import com.google.gson.Gson;
import net.thumbtack.school.concert.dto.request.AddCommentSongDtoRequest;
import net.thumbtack.school.concert.dto.request.AddRatingSongDtoRequest;
import net.thumbtack.school.concert.dto.request.ChangeCommentSongDtoRequest;
import net.thumbtack.school.concert.dto.request.RemoveSongDtoRequest;
import net.thumbtack.school.concert.dto.response.SongDtoResponse;
import net.thumbtack.school.concert.errors.ServiceException;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestComments {
    private Server server = new Server();
    private Gson gson = new Gson();
    private SongDtoResponse dtoResponse;
    private AddRatingSongDtoRequest addRatingDto;
    private RemoveSongDtoRequest removeSongDto;
    private AddCommentSongDtoRequest addCommentDto;
    private ChangeCommentSongDtoRequest changeCommentDto;
    private DatabaseForTesting testingDataBase = new DatabaseForTesting(); // для формирования базы данных будем использовать этот класс

    // создадим метод для формирования запроса на добавление комментария
    private String commentFactory(String login, String title, String artist, String comment) {
        String id = server.getDataBase().searchUserByLogin(login).getId();
        String token = server.getDataBase().searchUserByLogin(login).getToken();
        addCommentDto = new AddCommentSongDtoRequest(id, token, title, artist, comment);
        return gson.toJson(addCommentDto);
    }

    // создадим метод для формирования запроса на изменение комментария
    private String changeCommentFactory(String login, String title, String artist, String comment, String commentNew) {
        String id = server.getDataBase().searchUserByLogin(login).getId();
        String token = server.getDataBase().searchUserByLogin(login).getToken();
        changeCommentDto = new ChangeCommentSongDtoRequest(id, token, title, artist, comment, commentNew);
        return gson.toJson(changeCommentDto);
    }

    //напишем метод для формирования json строки
    private String response(String response) {
        dtoResponse = new SongDtoResponse(response);
        return gson.toJson(dtoResponse);
    }

    @Test
    public void testAddComment() throws IOException, ServiceException {
        server = testingDataBase.newDataBase();
        server.addComment(commentFactory("Nuyanzina", "Вереск", "Хелависа", "Очень крутая песня!!!"));
        // проверим результат
        assertEquals(server.getDataBase().searchSongInDB("Вереск", "Хелависа").getComments().size(), 1);
        server.stopServer(null);
    }

    //пусть комментарий попробует удалить не его автор
    @Test
    public void testRemoveComment() throws IOException, ServiceException {
        server = testingDataBase.newDataBase();
        assertEquals(server.removeComment(commentFactory("Shatov", "Королевна", "Хелависа", "Да, песня очень сказочная")), response("Author comment is not valid"));
        server.stopServer(null);
    }

    //пусть пользователи присоединятся к комментам
    @Test
    public void testAddLikeComment() throws IOException, ServiceException {
        server = testingDataBase.newDataBase();
        server.addLikeComment(commentFactory("Fedorenco", "Королевна", "Хелависа", "Очень крутая песня!!!"));
        server.addLikeComment(commentFactory("Fedorenco", "Королевна", "Хелависа", "Да, песня очень сказочная"));
        server.addLikeComment(commentFactory("Doronin", "Королевна", "Хелависа", "Да, песня очень сказочная"));
        int like = server.getDataBase().searchSongInDB("Королевна", "Хелависа").searchComment("Да, песня очень сказочная").getLike();
        assertEquals(like, 5);
        server.stopServer(null);
    }

    //попробуем удалить лайк
    @Test
    public void testRemoveLikeComments() throws IOException, ServiceException {
        server = testingDataBase.newDataBase();
        server.removeLikeComment(commentFactory("Shatov", "Королевна", "Хелависа", "Да, песня очень сказочная"));
        int like = server.getDataBase().searchSongInDB("Королевна", "Хелависа").searchComment("Да, песня очень сказочная").getLike();
        assertEquals(like, 2);
        server.stopServer(null);
    }

    //пусть лайк попробует удалить тот кто его не ставил
    @Test
    public void testRemoveLikeNotAuthor() throws IOException, ServiceException {
        server = testingDataBase.newDataBase();
        assertEquals(server.removeLikeComment(commentFactory("Sinelnikov", "Королевна", "Хелависа",
                "Да, песня очень сказочная")), response("The user did not put a like"));
        server.stopServer(null);
    }

    //пусть автор изменит комментарий за который никто не голосовал
    @Test
    public void testChangeCommentWithoutLikes() throws IOException, ServiceException {
        server = testingDataBase.newDataBase();
        assertEquals(server.changeComment(changeCommentFactory("Doronin", "Королевна", "Хелависа",
                "А мне больше Дезертир нравится", "А мне больше Дезертир нравится и Вереск")),
                response(server.getDataBase().searchUserByLogin("Doronin").getId()));
        server.stopServer(null);
    }

    // теперь пусть совсем его удалит
    @Test
    public void testRemoveCommentWithoutLikes() throws IOException, ServiceException {
        server = testingDataBase.newDataBase();
        assertEquals(server.removeComment(commentFactory("Doronin", "Королевна", "Хелависа",
                "А мне больше Дезертир нравится")), response(server.getDataBase().searchUserByLogin("Doronin").getId()));
        assertEquals(server.getDataBase().searchSongInDB("Королевна", "Хелависа").getComments().size(), 2);
        server.stopServer(null);
    }

    // попробуем удалить комментарий с лайками (автором комментария должно стать сообщество, лайк предыдущего автора удаляется)
    @Test
    public void testRemoveCommentWithLikes() throws IOException, ServiceException {
        server = testingDataBase.newDataBase();
        assertEquals(server.removeComment(commentFactory("Shishlova", "Королевна", "Хелависа", "Да, песня очень сказочная")),
                response(null));
        assertEquals(server.getDataBase().searchSongInDB("Королевна", "Хелависа").getComments().size(), 3);
        int like = server.getDataBase().searchSongInDB("Королевна", "Хелависа").searchComment("Да, песня очень сказочная").getLike();
        assertEquals(like, 2);
        server.stopServer(null);
    }

    // попробуем изменить комментарий с лайками (добавляется новый комментарий, автором старого становится сообщество)
    @Test
    public void testChangeCommentWithLikes() throws IOException, ServiceException {
        server = testingDataBase.newDataBase();
        assertEquals(server.changeComment(changeCommentFactory("Nuyanzina", "Королевна", "Хелависа", "Очень крутая песня!!!",
                "А еще мне нравится Вереск!")), response(null));
        assertEquals(server.getDataBase().searchSongInDB("Королевна", "Хелависа").getComments().size(), 4);
        server.stopServer(null);
    }
}
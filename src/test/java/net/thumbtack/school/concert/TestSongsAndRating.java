package net.thumbtack.school.concert;

import com.google.gson.Gson;
import net.thumbtack.school.concert.daoimpl.SongDaoImpl;
import net.thumbtack.school.concert.dto.request.*;
import net.thumbtack.school.concert.dto.request.*;
import net.thumbtack.school.concert.dto.response.SongDtoResponse;
import net.thumbtack.school.concert.errors.ServiceException;
import net.thumbtack.school.concert.serviceClasses.SongService;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TestSongsAndRating {

    private Server server = new Server();
    private Gson gson = new Gson();
    private SongDtoResponse dtoResponse;
    private AddRatingSongDtoRequest addRatingDto;
    private RemoveSongDtoRequest removeSongDto;
    private AddCommentSongDtoRequest addCommentDto;
    private ChangeCommentSongDtoRequest changeCommentDto;
    private DatabaseForTesting testingDataBase = new DatabaseForTesting(); // для формирования базы данных будем использовать этот класс

    // для удобства создадим перечень песен для добавления
    private List<String> composer = new ArrayList<String>();
    private List<String> poet = new ArrayList<String>();

    @Rule
    public final TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

    // метод преобразования пользователя к Json строке
    public String userToJson(User user) {
        Gson gson = new Gson();
        return gson.toJson(user);
    }

    //метод для преобразования песни к Json строке
    public String songToJson(RegisterSongDtoRequest song) {
        Gson gson = new Gson();
        return gson.toJson(song);
    }

    // создадим шаблон по добавлению участников
    private void userFactory(String firstName, String lastName, String login, String password) throws ServiceException {
        User user = new User(firstName, lastName, login, password);
        server.registerUser(userToJson(user));
    }

    //напишем метод для формирования json строки
    private String response(String response) {
        dtoResponse = new SongDtoResponse(response);
        return gson.toJson(dtoResponse);
    }

    //создадим фабрику по добавлению песен. для простоты композиторы и поэты будут одни и теже.
    private String songFactory(String authorId, String token, String title, String artist, int time) {
        composer.add("Б. Хэй");
        composer.add("Дж. Коойман");
        poet.add("М.А. Пушкина");
        RegisterSongDtoRequest song = new RegisterSongDtoRequest(authorId, token, title, composer, poet,
                artist, time);
        return server.addSong(songToJson(song));
    }

    //создадим метод по формированию запроса по добавлению оценки
    private String ratingFactory(String login, String title, String artist, int rating) {
        String token = server.getDataBase().searchUserByLogin(login).getToken();
        String id = server.getDataBase().searchUserByLogin(login).getId();
        addRatingDto = new AddRatingSongDtoRequest(id, token, title, artist, rating);
        return gson.toJson(addRatingDto);
    }

    // проверим добавление песни в базу
    @Test
    public void testAddSong() throws IOException, ServiceException {
        server.startServer(null);
        userFactory("Николай", "Жмаев", "Zhmaev", "Zh1111");
        String token = server.getDataBase().searchUserByLogin("Zhmaev").getToken();
        String authorId = server.getDataBase().searchUserByLogin("Zhmaev").getId();
        songFactory(authorId, token, "Беспечный Ангел", "В. Кипелов", 256);
        assertEquals(server.getDataBase().getSongs().size(), 1);
        server.stopServer(null);
    }

    //с помощью мокирования проверим, метод удаления песни
    @Test
    public void testRemoveSongMok() {
        SongDaoImpl mockSongDaoImpl = mock(SongDaoImpl.class);
        User user = new User("", "", "", "");
        Song song = new Song("", "", "", composer, poet, "", 0);
        when(mockSongDaoImpl.searchSongInDB(any(String.class), any(String.class))).thenReturn(song);
        when(mockSongDaoImpl.removeSong(any())).thenReturn("SONG REMOVED");
        when(mockSongDaoImpl.searchUserByToken(any(String.class))).thenReturn(user);
        SongService songService = new SongService(mockSongDaoImpl);
        removeSongDto = new RemoveSongDtoRequest("", "", "Какая-то песня", "Какой-то артист");
        String jsonresponse = songService.removeSong(gson.toJson(removeSongDto));
        assertTrue(jsonresponse.contains("SONG REMOVED"));
    }

    //с помощью мокирования проверим как метод бросает ошибку при невалидном токине
    @Test
    public void testTrialConcertMok() {
        SongDaoImpl mockSongDaoImpl = mock(SongDaoImpl.class);
        User user = new User("", "", "", "");
        SongService songService = new SongService(mockSongDaoImpl);
        String jsonresponse = songService.concert("55555");
        assertTrue(jsonresponse.contains("token not valid"));
    }

    //с помощью мокирования проверим метод формирования концерта
    @Test
    public void testIncorrectTokenMok() {
        SongDaoImpl mockSongDaoImpl = mock(SongDaoImpl.class);
        User user = new User("", "", "", "");
        when(mockSongDaoImpl.concert()).thenReturn(null);
        when(mockSongDaoImpl.searchUserByToken(any(String.class))).thenReturn(user);
        SongService songService = new SongService(mockSongDaoImpl);
        String jsonresponse = songService.concert("55555");
        assertTrue(jsonresponse.contains("null"));
    }

    // попробуем еще раз добавить песню, существующую в базе
    @Test
    public void testAddSongTwice() throws IOException, ServiceException {
        server = testingDataBase.newDataBase();
        String token = server.getDataBase().searchUserByLogin("Zhmaev").getToken();
        String authorId = server.getDataBase().searchUserByLogin("Zhmaev").getId();
        assertEquals(songFactory(authorId, token, "Беспечный Ангел", "В. Кипелов", 256), response("Song registered in basel"));
        assertEquals(server.getDataBase().getSongs().size(), 16);
    }

    // попробуем добавить песню с неверным названием
    @Test
    public void testIncorrectTitle() throws IOException, ServiceException {
        server.startServer(null);
        userFactory("Николай", "Жмаев", "Zhmaev", "Zh1111");
        String token = server.getDataBase().searchUserByLogin("Zhmaev").getToken();
        String authorId = server.getDataBase().searchUserByLogin("Zhmaev").getId();
        assertEquals(songFactory(authorId, token, "Б", "В. Кипелов", 256), response("Title is not valid"));
        assertEquals(server.getDataBase().getSongs().size(), 0);
        server.stopServer(null);
    }

    // попробуем добавить песню с неверным исполнителем
    @Test
    public void testIncorrectArtist() throws IOException, ServiceException {
        server.startServer(null);
        userFactory("Николай", "Жмаев", "Zhmaev", "Zh1111");
        String token = server.getDataBase().searchUserByLogin("Zhmaev").getToken();
        String authorId = server.getDataBase().searchUserByLogin("Zhmaev").getId();
        assertEquals(songFactory(authorId, token, "Беспечный Ангел", "В.", 256), response("Artist is not valid"));
        assertEquals(server.getDataBase().getSongs().size(), 0);
        server.stopServer(null);
    }

    // попробуем добавить песню с неверным временем
    @Test
    public void testIncorrectTime() throws IOException, ServiceException {
        server.startServer(null);
        userFactory("Николай", "Жмаев", "Zhmaev", "Zh1111");
        String token = server.getDataBase().searchUserByLogin("Zhmaev").getToken();
        String authorId = server.getDataBase().searchUserByLogin("Zhmaev").getId();
        assertEquals(songFactory(authorId, token, "Беспечный Ангел", "В. Кипелов", 30), response("Time is not valid"));
        assertEquals(server.getDataBase().getSongs().size(), 0);
        server.stopServer(null);
    }

    // попытаемся зарегистрировать песню без указания композитора
    @Test
    public void testIncorrectComposer() throws IOException, ServiceException {
        server.startServer(null);
        userFactory("Николай", "Жмаев", "Zhmaev", "Zh1111");
        String token = server.getDataBase().searchUserByLogin("Zhmaev").getToken();
        String authorId = server.getDataBase().searchUserByLogin("Zhmaev").getId();
        List<String> wrongComposer = new ArrayList<String>();
        RegisterSongDtoRequest song = new RegisterSongDtoRequest(authorId, token, "Беспечный Ангел", wrongComposer,
                poet, "В. Кипелов", 256);
        assertEquals(server.addSong(songToJson(song)), response("Composer is not valid"));
        assertEquals(server.getDataBase().getSongs().size(), 0);
    }

    // попытаемся зарегистрировать песню без указания поэта
    @Test
    public void testIncorrectPoet() throws IOException, ServiceException {
        server.startServer(null);
        userFactory("Николай", "Жмаев", "Zhmaev", "Zh1111");
        String token = server.getDataBase().searchUserByLogin("Zhmaev").getToken();
        String authorId = server.getDataBase().searchUserByLogin("Zhmaev").getId();
        List<String> wrongPoet = new ArrayList<String>();
        List<String> composerNew = new ArrayList<String>();
        composerNew.add("Дж. Коойман");
        RegisterSongDtoRequest song = new RegisterSongDtoRequest(authorId, token, "Беспечный Ангел", composerNew,
                wrongPoet, "В. Кипелов", 256);
        assertEquals(server.addSong(songToJson(song)), response("Poet is not valid"));
        assertEquals(server.getDataBase().getSongs().size(), 0);
        server.stopServer(null);
    }

    // проверим добавление, изменение оценки одним из участников
    @Test
    public void testRatingSong() throws IOException, ServiceException {
        server = testingDataBase.newDataBase();
        assertEquals(server.getDataBase().searchSongInDB("Улица роз", "В. Кипелов").getRating(), 19);
        server.addRatingSong(ratingFactory("Reshetnikova", "Улица роз", "В. Кипелов", 5));
        assertEquals(server.getDataBase().searchSongInDB("Улица роз", "В. Кипелов").getRating(), 24);
        server.changeRatingSong(ratingFactory("Reshetnikova", "Улица роз", "В. Кипелов", 4));
        assertEquals(server.getDataBase().searchSongInDB("Улица роз", "В. Кипелов").getRating(), 23);
        server.stopServer(null);
    }

    //попробуем повторно оценить песню
    @Test
    public void testAddRatingSongTwice() throws IOException, ServiceException {
        server = testingDataBase.newDataBase();
        assertEquals(server.addRatingSong(ratingFactory("Reshetnikova", "Let It Rain", "С. Ли", 5)), response("Incorrect action"));
        server.stopServer(null);
    }

    //автор удалит песню за которую не голосовали
    @Test
    public void testRemoveSongWithoutRating() throws IOException, ServiceException {
        server = testingDataBase.newDataBase();
        String authorId = server.getDataBase().searchUserByLogin("Zhmaev").getId();
        String token = server.getDataBase().searchUserByLogin("Zhmaev").getToken();
        removeSongDto = new RemoveSongDtoRequest(authorId, token, "Там высоко", "В. Кипелов");
        server.removeSong(gson.toJson(removeSongDto));
        assertEquals(server.getDataBase().getSongs().size(), 15);
        server.stopServer(null);
    }

    //попробуем удалить песню, за которую проголосовали
    @Test
    public void testRemoveSongWithRating() throws IOException, ServiceException {
        server = testingDataBase.newDataBase();
        String authorId = server.getDataBase().searchUserByLogin("Zhmaev").getId();
        String token = server.getDataBase().searchUserByLogin("Zhmaev").getToken();
        int raiting = server.getDataBase().searchSongInDB("Улица роз", "В. Кипелов").getRating();
        removeSongDto = new RemoveSongDtoRequest(authorId, token, "Улица роз", "В. Кипелов");
        assertEquals(server.removeSong(gson.toJson(removeSongDto)), response(authorId));
        // количество песен то же самое
        assertEquals(server.getDataBase().getSongs().size(), 16);
        // оценка автора удалена (оценка автора 5)
        assertEquals(server.getDataBase().searchSongInDB("Улица роз", "В. Кипелов").getRating(), raiting - 5);
        server.stopServer(null);
    }

    //попробуем удалить чужую песню
    @Test
    public void testRemoveSongNotAuthor() throws IOException, ServiceException {
        server = testingDataBase.newDataBase();
        String authorId = server.getDataBase().searchUserByLogin("Nuyanzina").getId();
        String token = server.getDataBase().searchUserByLogin("Nuyanzina").getToken();
        removeSongDto = new RemoveSongDtoRequest(authorId, token, "Беспечный Ангел", "В. Кипелов");
        assertEquals(server.removeSong(gson.toJson(removeSongDto)), response("Incorrect action"));
        assertEquals(server.getDataBase().getSongs().size(), 16);
    }

    // попробуем сформировать программу концерта, на основании голосования
    @Test
    public void testTrialConcert() throws IOException, ServiceException {
        server = testingDataBase.newDataBase();
        String token = server.getDataBase().searchUserByLogin("Nuyanzina").getToken();
        server.concert(token);

    /*по итогам проставленных нами оценок, первые три места должны распределиться так:
    - Bohemian Rhapsody - 30 лайков
    - We Will Rock You - 29 лайков
    - One Life One Soul - 28 лайков
    */
        assertEquals(server.getDataBase().getConcert().get(0).getTitle(), "Bohemian Rhapsody");
        assertEquals(server.getDataBase().getConcert().get(1).getTitle(), "We Will Rock You");
        assertEquals(server.getDataBase().getConcert().get(2).getTitle(), "One Life One Soul");

        // cуммарная продолжительность концерта не превышает 3600 минут, между песнями делается пауза в 10 секунд
        List<Song> concert = server.getDataBase().getConcert();
        int time = 0; // счетчик
        for (Song song : concert) {
            time += song.getTime() + 10;
        }
        assertTrue(time <= 3600);
        server.stopServer(null);
    }

    // попробуем получить все предложения конкретного участника (у нас все песни предложил Жмаев Николай (16 шт))
    @Test
    public void testGetAllUserSongs() throws IOException, ServiceException {
        server = testingDataBase.newDataBase();
        String request = gson.toJson(new String[]{server.getDataBase().searchUserByLogin("Zhmaev").getId(),
                server.getDataBase().searchUserByLogin("Zhmaev").getToken()});
        SongDtoResponse dtoResponse = gson.fromJson(server.getAllUserSongs(request), SongDtoResponse.class);
        List<Song> userSongs = gson.fromJson(dtoResponse.getTitle(), ArrayList.class);
        assertEquals(userSongs.size(), 16);
        server.stopServer(null);
    }
}
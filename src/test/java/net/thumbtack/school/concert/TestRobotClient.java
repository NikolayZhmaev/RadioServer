package net.thumbtack.school.concert;



import net.thumbtack.school.concert.daoimpl.SongDaoImpl;
import net.thumbtack.school.concert.daoimpl.UserDaoImpl;
import net.thumbtack.school.concert.errors.ServiceErrorCode;
import net.thumbtack.school.concert.errors.ServiceException;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class TestRobotClient {
    Server server = new Server();

    //протестируем регистрацию пользователя
    @Test
    public void testRegisterUser() throws IOException, ServiceException {
        UserDaoImpl mockUserDaoImpl = mock(UserDaoImpl.class);
        server.startServer(null);
        server.getUserService().setUserDao(mockUserDaoImpl);
        User user = new User("Николай", "Жмаев", "Zhmaev", "Zh1111");
        when(mockUserDaoImpl.insertUser(any())).thenReturn("THE USER IS REGISTERED");
        RobotClient robotClient = new RobotClient(server, user);
        String jsonresponse = robotClient.registerUser();
        assertTrue(jsonresponse.contains("THE USER IS REGISTERED"));
    }

    //протестируем удаление юзера
    @Test
    public void testRemoveUser() throws IOException, ServiceException {
        UserDaoImpl mockUserDaoImpl = mock(UserDaoImpl.class);
        server.startServer(null);
        server.getUserService().setUserDao(mockUserDaoImpl);
        User user = new User("", "", "", "");
        when(mockUserDaoImpl.searchUserByLogin(any(String.class))).thenReturn(user);
        when(mockUserDaoImpl.remove(any())).thenReturn("THE USER IS REMOVED");
        RobotClient robotClient = new RobotClient(server, user);
        String jsonresponse = robotClient.removeUser();
        assertTrue(jsonresponse.contains("THE USER IS REMOVED"));
    }

    //протестируем получение количества песен
    @Test
    public void testConcert() throws IOException, ServiceException {
        List<Song> songs = new ArrayList<>(); // создадим лист который будет передаваться нашему клиенту (вместо обращения к базе)
        songs.add(null);
        songs.add(null);
        songs.add(null);

        SongDaoImpl mokSongDaoImpl = mock(SongDaoImpl.class);
        server.startServer(null);
        server.getSongService().setSongDao(mokSongDaoImpl);
        User user = new User("Николай", "Жмаев", "Zhmaev", "Zh1111");
        when(mokSongDaoImpl.searchUserByToken(any())).thenReturn(user);
        when(mokSongDaoImpl.concert()).thenReturn(songs);
        RobotClient robotClient = new RobotClient(server, user);
        int result = robotClient.robotClientWorking();
        assertEquals(result, 3);
    }

    //попробуем подключиться к неработающему серверу
    @Test(expected = ServiceException.class)
    public void testСonnectingToDisabledServer() throws ServiceException {
        User user = new User("Николай", "Жмаев", "Zhmaev", "Zh1111");
        server = mock(Server.class);
        when(server.registerUser(any())).thenThrow(new ServiceException(ServiceErrorCode.WRONG_SERVER_STATUS));
        RobotClient robotClient = new RobotClient(server, user);
        robotClient.robotClientWorking();
    }
}
package net.thumbtack.school.concert;

import net.thumbtack.school.concert.errors.ServiceException;
import net.thumbtack.school.concert.serviceClasses.StatusServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class TestStartAndStopServer {

    private Server server = new Server();

    @Rule
    public final TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

    // попытаемся остановить не запущенный сервер
    @Test(expected = ServiceException.class)
    public void testStopServerServiceException() throws ServiceException, IOException {
        server.stopServer(null);
    }

    // запустим сервер и попробуем запустить снова, перехватим ошибку
    @Test(expected = ServiceException.class)
    public void testStartServerServiceException() throws IOException, ServiceException {
        server.startServer(null);
        server.startServer(null);
    }

    // запустим сервер после чего его остановим (без создания файла)
    @Test
    public void testStartAndStopServerWithoutSaving() throws IOException, ServiceException {
        server.startServer(null);
        assertEquals(server.getStatus(), StatusServer.ACTIVE);
        server.stopServer(null);
        assertEquals(server.getStatus(), StatusServer.INACTIVE);
    }

    // запустим сервер после чего остановим его с записью базы данных в файл
    @Test
    public void testStartAndStopServerWithSaving() throws IOException, ServiceException {
        server.startServer(null);
        File file = TEMP_FOLDER.newFile("test.txt");
        server.stopServer(file.getName());
        assertTrue(file.exists());
    }
}
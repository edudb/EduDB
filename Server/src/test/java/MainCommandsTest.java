/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

import net.edudb.Request;
import net.edudb.Response;
import net.edudb.Server;
import net.edudb.ServerHandler;
import net.edudb.authentication.Authentication;
import net.edudb.authentication.UserRole;
import net.edudb.engine.Config;
import net.edudb.engine.FileManager;
import net.edudb.exceptions.AuthenticationFailedException;
import net.edudb.exceptions.UserAlreadyExistException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

public class MainCommandsTest {
    Server server = new Server();
    ServerHandler serverHandler = server.getServerHandler();

    private static final String USERNAME = "test";
    private static final String PASSWORD = "test";
    private static String token;
    private static final String DATABASE = "test_db";
    private static final String TABLE = "test_table";

    @BeforeAll
    public static void setup() throws UserAlreadyExistException, AuthenticationFailedException {
        Authentication.createUser(USERNAME, PASSWORD, UserRole.DEFAULT_ROLE);
        token = Authentication.login(USERNAME, PASSWORD);

    }

    @AfterAll
    public static void tearDown() {
        TestUtils.deleteDirectory(new File(Config.absolutePath()));
    }


    public Response sendCommand(String command, String database) {
        Request request = new Request(command, database);
        request.setAuthToken(token);
        return serverHandler.handle(request);
    }

    public Response sendCommand(String command) {
        return sendCommand(command, DATABASE);
    }

    @Test
    public void testCreateDatabase() {
        // Create database
        sendCommand(TestUtils.createDatabase.apply(DATABASE), null);

        Assertions.assertTrue(new File(Config.databasePath(USERNAME, DATABASE)).exists());
        Assertions.assertTrue(new File(Config.tablesPath(USERNAME, DATABASE)).exists());
        Assertions.assertTrue(new File(Config.pagesPath(USERNAME, DATABASE)).exists());
        Assertions.assertTrue(new File(Config.schemaPath(USERNAME, DATABASE)).exists());
    }

    @Test
    public void testDropDatabase() {
        // Create database
        sendCommand(TestUtils.createDatabase.apply(DATABASE), null);
        // Drop database
        sendCommand(TestUtils.dropDatabase.apply(DATABASE));

        Assertions.assertFalse(new File(Config.databasePath(USERNAME, DATABASE)).exists());
        Assertions.assertFalse(new File(Config.tablesPath(USERNAME, DATABASE)).exists());
        Assertions.assertFalse(new File(Config.pagesPath(USERNAME, DATABASE)).exists());
        Assertions.assertFalse(new File(Config.schemaPath(USERNAME, DATABASE)).exists());
    }

    @Test
    public void testCreateTable() {
        // Create database
        sendCommand(TestUtils.createDatabase.apply(DATABASE), null);
        // Create table
        sendCommand(TestUtils.createTable.apply(TABLE));

        ArrayList<String> lines = FileManager.readFile(Config.schemaPath(USERNAME, DATABASE));

        Assertions.assertTrue(new File(Config.tablePath(USERNAME, DATABASE, TABLE)).exists());
        Assertions.assertEquals(1, lines.size());
        Assertions.assertEquals(TABLE, lines.get(0).split(" ")[0]);

    }

    @Test
    public void testDropTable() {
        // Create database
        sendCommand(TestUtils.createDatabase.apply(DATABASE), null);
        // Create table
        sendCommand(TestUtils.createTable.apply(TABLE));
        // Drop table
        sendCommand(TestUtils.dropTable.apply(TABLE));

        ArrayList<String> lines = FileManager.readFile(Config.schemaPath(USERNAME, DATABASE));

        Assertions.assertFalse(new File(Config.tablePath(USERNAME, DATABASE, TABLE)).exists());
        Assertions.assertEquals(0, lines.size());
    }

    @Test
    public void testInsertSelect() {
        // Create database
        sendCommand(TestUtils.createDatabase.apply(DATABASE), null);
        // Create table
        sendCommand(TestUtils.createTable.apply(TABLE));
        // Insert
        sendCommand(TestUtils.insert.apply(TABLE));
        // Select
        Response selectResponse = sendCommand(TestUtils.select.apply(TABLE));

        Assertions.assertEquals(1, selectResponse.getRecords().size());
    }


}

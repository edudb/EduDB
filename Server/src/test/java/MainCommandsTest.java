/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import net.edudb.Request;
import net.edudb.Response;
import net.edudb.Server;
import net.edudb.ServerHandler;
import net.edudb.data_type.DataType;
import net.edudb.data_type.VarCharType;
import net.edudb.engine.Config;
import net.edudb.engine.DatabaseEngine;
import net.edudb.engine.FileManager;
import net.edudb.engine.authentication.Authentication;
import net.edudb.engine.authentication.UserRole;
import net.edudb.exception.AuthenticationFailedException;
import net.edudb.exception.UserAlreadyExistException;
import net.edudb.exception.WorkspaceAlreadyExistException;
import net.edudb.exception.WorkspaceNotFoundException;
import net.edudb.index.Index;
import net.edudb.index.IndexManager;
import net.edudb.statistics.Schema;
import net.edudb.structure.Record;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class MainCommandsTest {
    @Container
    public static GenericContainer redis = new GenericContainer(DockerImageName.parse("redis:5.0.3-alpine"))
            .withExposedPorts(6379);
    private static FileSystem fs; // in-memory file system for testing (not working with indexes)
    @TempDir
    File tempDir;

    private static ServerHandler serverHandler;

    private final String WORKSPACE_NAME = "test";
    private static final String USERNAME = "test";
    private static final String PASSWORD = "test";
    private static String token;
    private static final String DATABASE_NAME = "test_db";
    private static final String TABLE_NAME = "test_table";
    private static final String[] COLUMN_NAMES = {"name"};
    private static final String[] COLUMN_TYPES = {"varchar"};
    private static final String[] COLUMN_VALUES = {"old"};
    private static final String[] COLUMN_VALUES_2 = {"old2"};
    private static final String[] COLUMN_NEW_VALUES = {"new"};

    @BeforeAll
    static void beforeAll() throws IOException {
        System.setProperty("REDIS_HOST", redis.getHost());
        System.setProperty("REDIS_PORT", String.valueOf(redis.getFirstMappedPort()));
    }

    @BeforeEach
    void setup() throws UserAlreadyExistException, AuthenticationFailedException, WorkspaceNotFoundException, WorkspaceAlreadyExistException, IOException {
        fs = Jimfs.newFileSystem(Configuration.unix());
        Config.setAbsolutePath(fs.getPath("test"));

        serverHandler = (new Server()).getServerHandler();

        DatabaseEngine.getInstance().createWorkspace(WORKSPACE_NAME);
        DatabaseEngine.getInstance().createUser(USERNAME, PASSWORD, UserRole.USER, WORKSPACE_NAME);
        token = Authentication.login(WORKSPACE_NAME, USERNAME, PASSWORD);

    }

    @AfterEach
    void tearDown() throws IOException {
        Schema.getInstance().reset();
        Config.setAbsolutePath(null);
        fs.close();
    }


    Response sendCommand(String command, String database) {
        Request request = new Request(command, database);
        request.setAuthToken(token);
        return serverHandler.handle(request);
    }

    public Response sendCommand(String command) {
        return sendCommand(command, DATABASE_NAME);
    }

    @Test
    void testCreateDatabase() {
        // Create database
        sendCommand(TestUtils.createDatabase(DATABASE_NAME), null);

        assertThat(Config.databasePath(USERNAME, DATABASE_NAME)).exists();
        assertThat(Config.tablesPath(USERNAME, DATABASE_NAME)).exists();
        assertThat(Config.pagesPath(USERNAME, DATABASE_NAME)).exists();
        assertThat(Config.schemaPath(USERNAME, DATABASE_NAME)).exists();


    }

    @Test
    void testDropDatabase() {
        // Create database
        sendCommand(TestUtils.createDatabase(DATABASE_NAME), null);
        // Drop database
        sendCommand(TestUtils.dropDatabase(DATABASE_NAME));

        assertThat(Config.databasePath(USERNAME, DATABASE_NAME)).doesNotExist();
        assertThat(Config.tablesPath(USERNAME, DATABASE_NAME)).doesNotExist();
        assertThat(Config.pagesPath(USERNAME, DATABASE_NAME)).doesNotExist();
        assertThat(Config.schemaPath(USERNAME, DATABASE_NAME)).doesNotExist();

    }

    @Test
    void testCreateTable() throws FileNotFoundException {
        // Create database
        sendCommand(TestUtils.createDatabase(DATABASE_NAME), null);
        // Create table
        sendCommand(TestUtils.createTable(TABLE_NAME, COLUMN_NAMES, COLUMN_TYPES));

        List<String> lines = FileManager.getInstance().readFile(Config.schemaPath(USERNAME, DATABASE_NAME));

        assertThat(Config.tablePath(USERNAME, DATABASE_NAME, TABLE_NAME)).exists();
        assertThat(lines).hasSize(1);
        assertThat(lines.get(0).split(" ")[0]).isEqualTo(TABLE_NAME);
    }

    @Test
    void testDropTable() throws FileNotFoundException {
        // Create database
        sendCommand(TestUtils.createDatabase(DATABASE_NAME), null);
        // Create table
        sendCommand(TestUtils.createTable(TABLE_NAME, COLUMN_NAMES, COLUMN_TYPES));
        // Drop table
        sendCommand(TestUtils.dropTable(TABLE_NAME));

        List<String> lines = FileManager.getInstance().readFile(Config.schemaPath(USERNAME, DATABASE_NAME));

        assertThat(Config.tablePath(USERNAME, DATABASE_NAME, TABLE_NAME)).doesNotExist();
        assertThat(lines).isEmpty();
    }

    @Test
    void testInsert() {
        // Create database
        sendCommand(TestUtils.createDatabase(DATABASE_NAME), null);
        // Create table
        sendCommand(TestUtils.createTable(TABLE_NAME, COLUMN_NAMES, COLUMN_TYPES));
        // Insert
        sendCommand(TestUtils.insert(TABLE_NAME, COLUMN_VALUES));
        // Select
        Response selectResponse = sendCommand(TestUtils.selectAll(TABLE_NAME));

        assertThat(selectResponse.getResultSetId()).isNotNull();
        String resultSetId = selectResponse.getResultSetId();
        List<Record> records = DatabaseEngine.getInstance().getNextRecord(WORKSPACE_NAME, DATABASE_NAME, resultSetId, 100);
        assertThat(records).hasSize(1);
        assertThat(records.get(0).getData().values().toArray()[0]).hasToString(COLUMN_VALUES[0]);
    }

    @Test
    void testUpdate() {
        // Create database
        sendCommand(TestUtils.createDatabase(DATABASE_NAME), null);
        // Create table
        sendCommand(TestUtils.createTable(TABLE_NAME, COLUMN_NAMES, COLUMN_TYPES));
        // Insert
        sendCommand(TestUtils.insert(TABLE_NAME, COLUMN_VALUES));
        // Update
        sendCommand(TestUtils.update(TABLE_NAME, COLUMN_NAMES[0], COLUMN_VALUES[0], COLUMN_NEW_VALUES[0]));
        // Select
        Response selectResponse = sendCommand(TestUtils.selectAll(TABLE_NAME));

        assertThat(selectResponse.getResultSetId()).isNotNull();
        String resultSetId = selectResponse.getResultSetId();
        List<Record> records = DatabaseEngine.getInstance().getNextRecord(WORKSPACE_NAME, DATABASE_NAME, resultSetId, 100);
        assertThat(records).hasSize(1);
        assertThat(records.get(0).getData().values().toArray()[0]).hasToString(COLUMN_NEW_VALUES[0]);
    }

    @Test
    void testDelete() {
        // Create database
        sendCommand(TestUtils.createDatabase(DATABASE_NAME), null);
        // Create table
        sendCommand(TestUtils.createTable(TABLE_NAME, COLUMN_NAMES, COLUMN_TYPES));
        // Insert
        sendCommand(TestUtils.insert(TABLE_NAME, COLUMN_VALUES));
        // Delete
        sendCommand(TestUtils.delete(TABLE_NAME, COLUMN_NAMES[0], COLUMN_VALUES[0]));
        // Select
        Response selectResponse = sendCommand(TestUtils.selectAll(TABLE_NAME));

        assertThat(selectResponse.getResultSetId()).isNotNull();
        String resultSetId = selectResponse.getResultSetId();
        List<Record> records = DatabaseEngine.getInstance().getNextRecord(WORKSPACE_NAME, DATABASE_NAME, resultSetId, 100);
        assertThat(records).isEmpty();
    }

    @Test
    void testSelect() {
        // Create database
        sendCommand(TestUtils.createDatabase(DATABASE_NAME), null);
        // Create table
        sendCommand(TestUtils.createTable(TABLE_NAME, COLUMN_NAMES, COLUMN_TYPES));
        // Insert
        sendCommand(TestUtils.insert(TABLE_NAME, COLUMN_VALUES));
        sendCommand(TestUtils.insert(TABLE_NAME, COLUMN_VALUES_2));
        // Select
        Response selectResponse = sendCommand(TestUtils.select(TABLE_NAME, COLUMN_NAMES[0], COLUMN_VALUES[0]));

        assertThat(selectResponse.getResultSetId()).isNotNull();
        String resultSetId = selectResponse.getResultSetId();
        List<Record> records = DatabaseEngine.getInstance().getNextRecord(WORKSPACE_NAME, DATABASE_NAME, resultSetId, 100);
        assertThat(records).hasSize(1);
        assertThat(records.get(0).getData().values().toArray()[0]).hasToString(COLUMN_VALUES[0]);
    }


    @Test
    void testCreateIndex() {
        Config.setAbsolutePath(tempDir.toPath()); // you can not use fs with indices tests
        // Create database
        sendCommand(TestUtils.createDatabase(DATABASE_NAME), null);
        // Create table
        sendCommand(TestUtils.createTable(TABLE_NAME, COLUMN_NAMES, COLUMN_TYPES));
        // Insert
        sendCommand(TestUtils.insert(TABLE_NAME, COLUMN_VALUES));
        sendCommand(TestUtils.insert(TABLE_NAME, COLUMN_VALUES_2));

        // Create index
        sendCommand(TestUtils.createIndex(TABLE_NAME, COLUMN_NAMES[0]));
        assertThat(Config.indexPath(USERNAME, DATABASE_NAME, TABLE_NAME, COLUMN_NAMES[0])).exists();

        Optional<Index<DataType>> indexOptional = IndexManager.getInstance()
                .getIndex(WORKSPACE_NAME, DATABASE_NAME, TABLE_NAME, COLUMN_NAMES[0]);
        assertThat(indexOptional).isPresent();

        Index<DataType> index = indexOptional.get();
        Set<String> result = index.search(new VarCharType(COLUMN_VALUES[0]));
        assertThat(result).hasSize(1);
    }

    @Test
    void testDropIndex() {
        Config.setAbsolutePath(tempDir.toPath()); // you can not use fs with indices tests
        // Create database
        sendCommand(TestUtils.createDatabase(DATABASE_NAME), null);
        // Create table
        sendCommand(TestUtils.createTable(TABLE_NAME, COLUMN_NAMES, COLUMN_TYPES));
        // Insert
        sendCommand(TestUtils.insert(TABLE_NAME, COLUMN_VALUES));
        sendCommand(TestUtils.insert(TABLE_NAME, COLUMN_VALUES_2));
        // Create index
        sendCommand(TestUtils.createIndex(TABLE_NAME, COLUMN_NAMES[0]));

        // Drop Index
        sendCommand(TestUtils.dropIndex(TABLE_NAME, COLUMN_NAMES[0]));

        Optional<Index<DataType>> indexOptional = IndexManager.getInstance()
                .getIndex(WORKSPACE_NAME, DATABASE_NAME, TABLE_NAME, COLUMN_NAMES[0]);
        assertThat(indexOptional).isEmpty();

        assertThat(Config.indexPath(USERNAME, DATABASE_NAME, TABLE_NAME, COLUMN_NAMES[0])).doesNotExist();
    }

    @Test
    void testUpdateIndexOnInsert() {
        Config.setAbsolutePath(tempDir.toPath()); // you can not use fs with indices tests
        // Create database
        sendCommand(TestUtils.createDatabase(DATABASE_NAME), null);
        // Create table
        sendCommand(TestUtils.createTable(TABLE_NAME, COLUMN_NAMES, COLUMN_TYPES));
        // Create index
        sendCommand(TestUtils.createIndex(TABLE_NAME, COLUMN_NAMES[0]));
        // Insert
        sendCommand(TestUtils.insert(TABLE_NAME, COLUMN_VALUES_2));

        Optional<Index<DataType>> indexOptional = IndexManager.getInstance()
                .getIndex(WORKSPACE_NAME, DATABASE_NAME, TABLE_NAME, COLUMN_NAMES[0]);
        assertThat(indexOptional).isPresent();

        Index<DataType> index = indexOptional.get();
        Set<String> result = index.search(new VarCharType(COLUMN_VALUES_2[0]));
        assertThat(result).hasSize(1);
    }
}

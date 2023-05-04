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
import net.edudb.data_type.TimestampType;
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
import net.edudb.statistics.Schema;
import net.edudb.structure.Record;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.ArrayList;
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
    private static final String[][] TABLE_SCHEMA = {
            {"name", "varchar"},
            {"age", "integer"},
            {"male", "boolean"},
            {"salary", "decimal"},
            {"birthday", "timestamp"}
    };
    private static final String[][] TABLE_DATA = {
            {"John", "20", "true", "100.0", "2000-01-01 10:00:00"},
            {"Mary", "21", "false", "200.0", "1999-01-01 01:00:00"},
            {"John", "22", "true", "300.0", "1990-01-01 00:20:00"}
    };

    @BeforeAll
    static void beforeAll() {
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

    void validateRecord(Record record, String[] expectedValuesAsStrings) {
        List<DataType> values = new ArrayList<>(record.getData().values());
        assertThat(values).hasSize(expectedValuesAsStrings.length);

        for (int i = 0; i < values.size(); i++) {
            DataType value = values.get(i);
            String expectedString = expectedValuesAsStrings[i];
            if (value instanceof TimestampType) {
                assertThat(value).isNotNull().hasToString(expectedString + ".0");
            } else {
                assertThat(value).isNotNull().hasToString(expectedString);
            }
        }
    }

    @Test
    void testCreateDatabase() {
        // Create database
        sendCommand(CommandsGenerators.createDatabase(DATABASE_NAME), null);

        assertThat(Config.databasePath(USERNAME, DATABASE_NAME)).exists();
        assertThat(Config.tablesPath(USERNAME, DATABASE_NAME)).exists();
        assertThat(Config.pagesPath(USERNAME, DATABASE_NAME)).exists();
        assertThat(Config.schemaPath(USERNAME, DATABASE_NAME)).exists();


    }

    @Test
    void testDropDatabase() {
        // Create database
        sendCommand(CommandsGenerators.createDatabase(DATABASE_NAME), null);
        // Drop database
        sendCommand(CommandsGenerators.dropDatabase(DATABASE_NAME));

        assertThat(Config.databasePath(USERNAME, DATABASE_NAME)).doesNotExist();
        assertThat(Config.tablesPath(USERNAME, DATABASE_NAME)).doesNotExist();
        assertThat(Config.pagesPath(USERNAME, DATABASE_NAME)).doesNotExist();
        assertThat(Config.schemaPath(USERNAME, DATABASE_NAME)).doesNotExist();

    }

    @Test
    void testCreateTable() throws FileNotFoundException {
        // Create database
        sendCommand(CommandsGenerators.createDatabase(DATABASE_NAME), null);
        // Create table
        Response response = sendCommand(CommandsGenerators.createTable(TABLE_NAME, TABLE_SCHEMA));
        System.out.println(response);
        List<String> lines = FileManager.getInstance().readFile(Config.schemaPath(USERNAME, DATABASE_NAME));

        assertThat(Config.tablePath(USERNAME, DATABASE_NAME, TABLE_NAME)).exists();
        assertThat(lines).hasSize(1);
        assertThat(lines.get(0).split(" ")[0]).isEqualTo(TABLE_NAME);
    }

    @Test
    void testDropTable() throws FileNotFoundException {
        // Create database
        sendCommand(CommandsGenerators.createDatabase(DATABASE_NAME), null);
        // Create table
        sendCommand(CommandsGenerators.createTable(TABLE_NAME, TABLE_SCHEMA));
        // Drop table
        sendCommand(CommandsGenerators.dropTable(TABLE_NAME));

        List<String> lines = FileManager.getInstance().readFile(Config.schemaPath(USERNAME, DATABASE_NAME));

        assertThat(Config.tablePath(USERNAME, DATABASE_NAME, TABLE_NAME)).doesNotExist();
        assertThat(lines).isEmpty();
    }

    @Test
    void testInsert() {
        // Create database
        sendCommand(CommandsGenerators.createDatabase(DATABASE_NAME), null);
        // Create table
        sendCommand(CommandsGenerators.createTable(TABLE_NAME, TABLE_SCHEMA));
        // Insert
        sendCommand(CommandsGenerators.insertIntoTable(TABLE_NAME, TABLE_DATA[0]));
        // Select
        Response selectResponse = sendCommand(CommandsGenerators.selectFromTable(TABLE_NAME));

        assertThat(selectResponse.getResultSetId()).isNotNull();
        String resultSetId = selectResponse.getResultSetId();
        List<Record> records = DatabaseEngine.getInstance().getNextRecord(WORKSPACE_NAME, DATABASE_NAME, resultSetId, 100);
        assertThat(records).hasSize(1);

        validateRecord(records.get(0), TABLE_DATA[0]);
    }

    @Test
    void testUpdate() {
        // Create database
        sendCommand(CommandsGenerators.createDatabase(DATABASE_NAME), null);
        // Create table
        sendCommand(CommandsGenerators.createTable(TABLE_NAME, TABLE_SCHEMA));
        // Insert
        sendCommand(CommandsGenerators.insertIntoTable(TABLE_NAME, TABLE_DATA[0]));
        sendCommand(CommandsGenerators.insertIntoTable(TABLE_NAME, TABLE_DATA[1]));
        // Update
        sendCommand(CommandsGenerators.updateTable(TABLE_NAME,
                new String[][]{{TABLE_SCHEMA[1][0], TABLE_DATA[1][1]}},
                new String[][]{{TABLE_SCHEMA[0][0], TABLE_DATA[0][0]}}));
        // Select
        Response selectResponse = sendCommand(CommandsGenerators.selectFromTable(TABLE_NAME, new String[][]{
                {TABLE_SCHEMA[1][0], TABLE_DATA[1][1]}
        }));

        assertThat(selectResponse.getResultSetId()).isNotNull();
        String resultSetId = selectResponse.getResultSetId();
        List<Record> records = DatabaseEngine.getInstance().getNextRecord(WORKSPACE_NAME, DATABASE_NAME, resultSetId, 100);
        assertThat(records).hasSize(2);

        String[] expectedRecord = TABLE_DATA[0];
        expectedRecord[1] = TABLE_DATA[1][1];

        validateRecord(records.get(0), expectedRecord);
        validateRecord(records.get(1), TABLE_DATA[1]);
    }

    @Test
    void testUpdate2() {
        // Create database
        sendCommand(CommandsGenerators.createDatabase(DATABASE_NAME), null);
        // Create table
        sendCommand(CommandsGenerators.createTable(TABLE_NAME, TABLE_SCHEMA));
        // Insert
        sendCommand(CommandsGenerators.insertIntoTable(TABLE_NAME, TABLE_DATA[0]));
        sendCommand(CommandsGenerators.insertIntoTable(TABLE_NAME, TABLE_DATA[2]));
        // Update
        sendCommand(CommandsGenerators.updateTable(TABLE_NAME,
                new String[][]{{TABLE_SCHEMA[0][0], TABLE_DATA[1][0]}},
                new String[][]{{TABLE_SCHEMA[1][0], TABLE_DATA[2][1]}}
        ));
        // Select
        Response selectResponse = sendCommand(CommandsGenerators.selectFromTable(TABLE_NAME));

        assertThat(selectResponse.getResultSetId()).isNotNull();
        String resultSetId = selectResponse.getResultSetId();
        List<Record> records = DatabaseEngine.getInstance().getNextRecord(WORKSPACE_NAME, DATABASE_NAME, resultSetId, 100);
        assertThat(records).hasSize(2);

        String[] expectedRecord = TABLE_DATA[2];
        expectedRecord[0] = TABLE_DATA[1][0];

        validateRecord(records.get(0), TABLE_DATA[0]);
        validateRecord(records.get(1), expectedRecord);
    }

    @Test
    void testDelete() {
        // Create database
        sendCommand(CommandsGenerators.createDatabase(DATABASE_NAME), null);
        // Create table
        sendCommand(CommandsGenerators.createTable(TABLE_NAME, TABLE_SCHEMA));
        // Insert
        sendCommand(CommandsGenerators.insertIntoTable(TABLE_NAME, TABLE_DATA[0]));
        // Delete
        sendCommand(CommandsGenerators.deleteFromTable(TABLE_NAME, new String[][]{{TABLE_SCHEMA[0][0], TABLE_DATA[0][0]}}));
        // Select
        Response selectResponse = sendCommand(CommandsGenerators.selectFromTable(TABLE_NAME));

        assertThat(selectResponse.getResultSetId()).isNotNull();
        String resultSetId = selectResponse.getResultSetId();
        List<Record> records = DatabaseEngine.getInstance().getNextRecord(WORKSPACE_NAME, DATABASE_NAME, resultSetId, 100);
        assertThat(records).isEmpty();
    }

    @Test
    void testSelect() {
        // Create database
        sendCommand(CommandsGenerators.createDatabase(DATABASE_NAME), null);
        // Create table
        sendCommand(CommandsGenerators.createTable(TABLE_NAME, TABLE_SCHEMA));
        // Insert
        sendCommand(CommandsGenerators.insertIntoTable(TABLE_NAME, TABLE_DATA[0]));
        sendCommand(CommandsGenerators.insertIntoTable(TABLE_NAME, TABLE_DATA[1]));
        // Select
        Response selectResponse = sendCommand(CommandsGenerators.selectFromTable(TABLE_NAME, new String[][]{{TABLE_SCHEMA[0][0], TABLE_DATA[0][0]}}));

        assertThat(selectResponse.getResultSetId()).isNotNull();
        String resultSetId = selectResponse.getResultSetId();
        List<Record> records = DatabaseEngine.getInstance().getNextRecord(WORKSPACE_NAME, DATABASE_NAME, resultSetId, 100);
        assertThat(records).hasSize(1);
        validateRecord(records.get(0), TABLE_DATA[0]);
    }


    @Test
    void testCreateIndex() {
        Config.setAbsolutePath(tempDir.toPath()); // you can not use fs with indices tests
        // Create database
        sendCommand(CommandsGenerators.createDatabase(DATABASE_NAME), null);
        // Create table
        sendCommand(CommandsGenerators.createTable(TABLE_NAME, TABLE_SCHEMA));
        // Insert
        sendCommand(CommandsGenerators.insertIntoTable(TABLE_NAME, TABLE_DATA[0]));
        sendCommand(CommandsGenerators.insertIntoTable(TABLE_NAME, TABLE_DATA[1]));

        // Create index
        sendCommand(CommandsGenerators.createIndex(TABLE_NAME, TABLE_SCHEMA[0][0]));
        assertThat(Config.indexPath(USERNAME, DATABASE_NAME, TABLE_NAME, TABLE_SCHEMA[0][0])).exists();

        Optional<Index<DataType>> indexOptional = DatabaseEngine.getInstance().getIndexManager()
                .getIndex(WORKSPACE_NAME, DATABASE_NAME, TABLE_NAME, TABLE_SCHEMA[0][0]);
        assertThat(indexOptional).isPresent();

        Index<DataType> index = indexOptional.get();
        Set<String> result = index.search(new VarCharType(TABLE_DATA[0][0]));
        assertThat(result).hasSize(1);
    }

    @Test
    void testDropIndex() {
        Config.setAbsolutePath(tempDir.toPath()); // you can not use fs with indices tests
        // Create database
        sendCommand(CommandsGenerators.createDatabase(DATABASE_NAME), null);
        // Create table
        sendCommand(CommandsGenerators.createTable(TABLE_NAME, TABLE_SCHEMA));
        // Insert
        sendCommand(CommandsGenerators.insertIntoTable(TABLE_NAME, TABLE_DATA[0]));
        sendCommand(CommandsGenerators.insertIntoTable(TABLE_NAME, TABLE_DATA[1]));
        // Create index
        sendCommand(CommandsGenerators.createIndex(TABLE_NAME, TABLE_SCHEMA[0][0]));

        // Drop Index
        sendCommand(CommandsGenerators.dropIndex(TABLE_NAME, TABLE_SCHEMA[0][0]));

        Optional<Index<DataType>> indexOptional = DatabaseEngine.getInstance().getIndexManager()
                .getIndex(WORKSPACE_NAME, DATABASE_NAME, TABLE_NAME, TABLE_SCHEMA[0][0]);
        assertThat(indexOptional).isEmpty();

        assertThat(Config.indexPath(USERNAME, DATABASE_NAME, TABLE_NAME, TABLE_SCHEMA[0][0])).doesNotExist();
    }

    @Test
    void testUpdateIndexOnInsert() {
        Config.setAbsolutePath(tempDir.toPath()); // you can not use fs with indices tests
        // Create database
        sendCommand(CommandsGenerators.createDatabase(DATABASE_NAME), null);
        // Create table
        sendCommand(CommandsGenerators.createTable(TABLE_NAME, TABLE_SCHEMA));
        // Create index
        sendCommand(CommandsGenerators.createIndex(TABLE_NAME, TABLE_SCHEMA[0][0]));
        // Insert
        sendCommand(CommandsGenerators.insertIntoTable(TABLE_NAME, TABLE_DATA[0]));

        Optional<Index<DataType>> indexOptional = DatabaseEngine.getInstance().getIndexManager()
                .getIndex(WORKSPACE_NAME, DATABASE_NAME, TABLE_NAME, TABLE_SCHEMA[0][0]);
        assertThat(indexOptional).isPresent();

        Index<DataType> index = indexOptional.get();
        Set<String> result = index.search(new VarCharType(TABLE_DATA[0][0]));
        assertThat(result).hasSize(1);
    }

    @Test
    void testSelectUsingIndex() {
        Config.setAbsolutePath(tempDir.toPath()); // you can not use fs with indices tests
        // Create database
        sendCommand(CommandsGenerators.createDatabase(DATABASE_NAME), null);
        // Create table
        sendCommand(CommandsGenerators.createTable(TABLE_NAME, TABLE_SCHEMA));
        // Insert
        sendCommand(CommandsGenerators.insertIntoTable(TABLE_NAME, TABLE_DATA[0]));
        // Create index
        sendCommand(CommandsGenerators.createIndex(TABLE_NAME, TABLE_SCHEMA[0][0]));

        // Select
        Response selectResponse = sendCommand(CommandsGenerators.selectFromTable(TABLE_NAME, new String[][]{
                {TABLE_SCHEMA[0][0], TABLE_DATA[0][0]}
        }));

        assertThat(selectResponse.getResultSetId()).isNotNull();
        String resultSetId = selectResponse.getResultSetId();
        List<Record> records = DatabaseEngine.getInstance().getNextRecord(WORKSPACE_NAME, DATABASE_NAME, resultSetId, 100);
        assertThat(records).hasSize(1);
        validateRecord(records.get(0), TABLE_DATA[0]);
    }

    @Test
    @DisplayName("should select with index correctly after deletion")
    void testSelectUsingIndexAfterDeletion() {
        Config.setAbsolutePath(tempDir.toPath()); // you can not use fs with indices tests
        // Create database
        sendCommand(CommandsGenerators.createDatabase(DATABASE_NAME), null);
        // Create table
        sendCommand(CommandsGenerators.createTable(TABLE_NAME, TABLE_SCHEMA));
        // Insert
        sendCommand(CommandsGenerators.insertIntoTable(TABLE_NAME, TABLE_DATA[0]));
        sendCommand(CommandsGenerators.insertIntoTable(TABLE_NAME, TABLE_DATA[1]));
        // Create index
        sendCommand(CommandsGenerators.createIndex(TABLE_NAME, TABLE_SCHEMA[0][0]));
        // Delete
        sendCommand(CommandsGenerators.deleteFromTable(TABLE_NAME, new String[][]{
                {TABLE_SCHEMA[0][0], TABLE_DATA[0][0]}
        }));

        // Select
        Response selectResponse = sendCommand(CommandsGenerators.selectFromTable(TABLE_NAME, new String[][]{
                {TABLE_SCHEMA[0][0], TABLE_DATA[0][0]}
        }));

        assertThat(selectResponse.getResultSetId()).isNotNull();
        String resultSetId = selectResponse.getResultSetId();
        List<Record> records = DatabaseEngine.getInstance().getNextRecord(WORKSPACE_NAME, DATABASE_NAME, resultSetId, 100);
        assertThat(records).isEmpty();
    }

    @Test
    @DisplayName("should select with index correctly after partial deletion")
    void testSelectUsingIndexAfterDeletion2() {
        Config.setAbsolutePath(tempDir.toPath()); // you can not use fs with indices tests
        // Create database
        sendCommand(CommandsGenerators.createDatabase(DATABASE_NAME), null);
        // Create table
        sendCommand(CommandsGenerators.createTable(TABLE_NAME, TABLE_SCHEMA));
        // Insert
        sendCommand(CommandsGenerators.insertIntoTable(TABLE_NAME, TABLE_DATA[0]));
        sendCommand(CommandsGenerators.insertIntoTable(TABLE_NAME, TABLE_DATA[2]));
        // Create index
        sendCommand(CommandsGenerators.createIndex(TABLE_NAME, TABLE_SCHEMA[0][0]));
        // Delete
        sendCommand(CommandsGenerators.deleteFromTable(TABLE_NAME, new String[][]{
                {TABLE_SCHEMA[1][0], TABLE_DATA[0][1]}
        }));

        // Select
        Response selectResponse = sendCommand(CommandsGenerators.selectFromTable(TABLE_NAME, new String[][]{
                {TABLE_SCHEMA[0][0], TABLE_DATA[2][0]}
        }));

        assertThat(selectResponse.getResultSetId()).isNotNull();
        String resultSetId = selectResponse.getResultSetId();
        List<Record> records = DatabaseEngine.getInstance().getNextRecord(WORKSPACE_NAME, DATABASE_NAME, resultSetId, 100);
        assertThat(records).hasSize(1);
        validateRecord(records.get(0), TABLE_DATA[2]);
    }

    @Test
    @DisplayName("should select with index correctly after update")
    void testSelectUsingIndexAfterUpdate() {
        Config.setAbsolutePath(tempDir.toPath()); // you can not use fs with indices tests
        // Create database
        sendCommand(CommandsGenerators.createDatabase(DATABASE_NAME), null);
        // Create table
        sendCommand(CommandsGenerators.createTable(TABLE_NAME, TABLE_SCHEMA));
        // Insert
        sendCommand(CommandsGenerators.insertIntoTable(TABLE_NAME, TABLE_DATA[0]));
        sendCommand(CommandsGenerators.insertIntoTable(TABLE_NAME, TABLE_DATA[2]));
        // Create index
        sendCommand(CommandsGenerators.createIndex(TABLE_NAME, TABLE_SCHEMA[0][0]));
        // Update
        sendCommand(CommandsGenerators.updateTable(TABLE_NAME,
                new String[][]{{TABLE_SCHEMA[0][0], TABLE_DATA[1][0]}},
                new String[][]{{TABLE_SCHEMA[1][0], TABLE_DATA[2][1]}}
        ));

        // Select
        Response selectResponse = sendCommand(CommandsGenerators.selectFromTable(TABLE_NAME, new String[][]{
                {TABLE_SCHEMA[0][0], TABLE_DATA[0][0]},
        }));

        assertThat(selectResponse.getResultSetId()).isNotNull();
        String resultSetId = selectResponse.getResultSetId();
        List<Record> records = DatabaseEngine.getInstance().getNextRecord(WORKSPACE_NAME, DATABASE_NAME, resultSetId, 100);
        assertThat(records).hasSize(1);
        validateRecord(records.get(0), TABLE_DATA[0]);
    }
}

/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

import net.edudb.Client;
import net.edudb.ClientHandler;
import net.edudb.RPCClient;
import net.edudb.exceptions.RabbitMQConnectionException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;

import java.util.function.Function;

public class MainFlowTest {

    private static ClientHandler handler;
    final String DATABASE_NAME_1 = "test_db";
    final String DATABASE_NAME_2 = "test_db_2";
    final String TABLE_NAME_1 = "test_table";
    final String TABLE_NAME_2 = "test_table_2";

    Function<String, String> createDatabase = (databaseName) -> "create database " + databaseName;
    Function<String, String> openDatabase = (databaseName) -> "open database " + databaseName;
    Function<String, String> dropDatabase = (databaseName) -> "drop database " + databaseName;
    Function<String, String> createTable = (tableName) -> "create table " + tableName + " (name varchar)";
    Function<String, String> dropTable = (tableName) -> "drop table " + tableName;
    Function<String, String> insert = (tableName) -> "insert into " + tableName + " values ('test')";
    Function<String, String> select = (tableName) -> "select * from " + tableName;


    @BeforeAll
    public static void setup() throws RabbitMQConnectionException {
        Client client = Client.getInstance();
        RPCClient rpcClient = new RPCClient("server");
        rpcClient.initializeConnection();
        client.setRpcClient(rpcClient);
        rpcClient.handshake(null, "admin", "admin");
        handler = client.getHandler();
    }

    @Disabled
    public void mainFlowTest() { //TODO: fix this test


        System.out.println(handler.handle(createDatabase.apply(DATABASE_NAME_1)));

        System.out.println(handler.handle(openDatabase.apply(DATABASE_NAME_1)));

        System.out.println(handler.handle(createTable.apply(TABLE_NAME_1)));

        System.out.println(handler.handle(insert.apply(TABLE_NAME_1)));

        System.out.println(handler.handle(select.apply(TABLE_NAME_1)));

        System.out.println(handler.handle(dropTable.apply(TABLE_NAME_1)));

        System.out.println(handler.handle(dropDatabase.apply(DATABASE_NAME_1)));
    }
}

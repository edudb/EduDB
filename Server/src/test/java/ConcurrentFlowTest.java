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
import net.edudb.engine.Config;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

import java.io.File;

public class ConcurrentFlowTest {
    final static Server server = new Server();
    final static String DB_NAME_1 = "test_db_1";
    final static String DB_NAME_2 = "test_db_2";
    final static String TABLE_NAME_1 = "test_table_1";
    final static String TABLE_NAME_2 = "test_table_2";

    final static Request CREATE_DB_1 = new Request("create database " + DB_NAME_1);
    final static Request CREATE_TABLE_1 = new Request("create table " + TABLE_NAME_1 + " (name varchar)", DB_NAME_1);
    final static Request INSERT_1 = new Request("insert into " + TABLE_NAME_1 + " values ('test1')", DB_NAME_1);
    final static Request SELECT_1 = new Request("select * from " + TABLE_NAME_1, DB_NAME_1);
    final static Request DROP_TABLE_1 = new Request("drop table " + TABLE_NAME_1, DB_NAME_1);
    final static Request DROP_DB_1 = new Request("drop database " + DB_NAME_1);

    final static Request CREATE_DB_2 = new Request("create database " + DB_NAME_2);
    final static Request CREATE_TABLE_2 = new Request("create table " + TABLE_NAME_2 + " (name varchar, test varchar)", DB_NAME_2);
    final static Request INSERT_2 = new Request("insert into " + TABLE_NAME_2 + " values ('test2', 'test2')", DB_NAME_2);
    final static Request SELECT_2 = new Request("select * from " + TABLE_NAME_2, DB_NAME_2);
    final static Request DROP_TABLE_2 = new Request("drop table " + TABLE_NAME_2, DB_NAME_2);
    final static Request DROP_DB_2 = new Request("drop database " + DB_NAME_2);

    @BeforeEach
    public void setUp() {
        server.getServerHandler().handle(CREATE_DB_1);
        server.getServerHandler().handle(CREATE_DB_2);
//        DatabaseSystem.getInstance().setDatabaseIsOpen(true);

    }

    @AfterEach
    public void tearDown() {
        server.getServerHandler().handle(DROP_DB_1);
        server.getServerHandler().handle(DROP_DB_2);
    }

    @Disabled
    void testConcurrentUsers() throws InterruptedException {
        ServerHandler serverHandler = server.getServerHandler();


        Thread thread1 = new Thread(() -> {
            Response createTableResponse = serverHandler.handle(CREATE_TABLE_1);
            System.out.println("Thread 1 (create table): " + createTableResponse);
//            Assertions.assertTrue(new File(String.format("%s/databases/%s/tables/%s.table", Config.absolutePath(), DB_NAME_1, TABLE_NAME_1)).exists());

            Response insertResponse = serverHandler.handle(INSERT_1);
            System.out.println("Thread 1 (insert): " + insertResponse);
            System.out.println("Thread 1 (insert): " + insertResponse.getRecords());
//            Assertions.assertEquals(1, insertResponse.getRecords().size());

            Response selectResponse = serverHandler.handle(SELECT_1);
            System.out.println("Thread 1 (select): " + selectResponse);
//            Assertions.assertEquals(1, selectResponse.getRecords().size());

            serverHandler.handle(DROP_TABLE_1);
//            Assertions.assertFalse(new File(String.format("%s/databases/%s/tables/%s.table", Config.absolutePath(), DB_NAME_1, TABLE_NAME_1)).exists());

        });

        Thread thread2 = new Thread(() -> {
            Response createTableResponse = serverHandler.handle(CREATE_TABLE_2);
            System.out.println("Thread 2 (create table): " + createTableResponse);
//            Assertions.assertTrue(new File(String.format("%s/databases/%s/tables/%s.table", Config.absolutePath(), DB_NAME_2, TABLE_NAME_2)).exists());

            Response insertResponse = serverHandler.handle(INSERT_2);
            System.out.println("Thread 2 (insert): " + insertResponse);
            System.out.println("Thread 2 (insert): " + insertResponse.getRecords());
//            Assertions.assertEquals(1, insertResponse.getRecords().size());

            Response selectResponse = serverHandler.handle(SELECT_2);
            System.out.println("Thread 2 (select): " + selectResponse);
//            Assertions.assertEquals(1, selectResponse.getRecords().size());

            serverHandler.handle(DROP_TABLE_2);
//            Assertions.assertFalse(new File(String.format("%s/databases/%s/tables/%s.table", Config.absolutePath(), DB_NAME_2, TABLE_NAME_2)).exists());
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

    }

    @Disabled
    void testInterchangingUsers() {
        ServerHandler serverHandler = server.getServerHandler();

        serverHandler.handle(CREATE_TABLE_2);
        Assertions.assertTrue(new File(String.format("%s/databases/%s/tables/%s.table", Config.absolutePath(), DB_NAME_1, TABLE_NAME_1)).exists());

        serverHandler.handle(CREATE_TABLE_1);
        Assertions.assertTrue(new File(String.format("%s/databases/%s/tables/%s.table", Config.absolutePath(), DB_NAME_2, TABLE_NAME_2)).exists());

        Response insertResponse1 = serverHandler.handle(INSERT_1);
        Assertions.assertEquals(1, insertResponse1.getRecords().size());

        Response insertResponse2 = serverHandler.handle(INSERT_2);
        Assertions.assertEquals(1, insertResponse2.getRecords().size());

        Response selectResponse1 = serverHandler.handle(SELECT_1);
        Assertions.assertEquals(1, selectResponse1.getRecords().size());

        Response selectResponse2 = serverHandler.handle(SELECT_2);
        Assertions.assertEquals(1, selectResponse2.getRecords().size());

        serverHandler.handle(DROP_TABLE_1);
        Assertions.assertFalse(new File(String.format("%s/databases/%s/tables/%s.table", Config.absolutePath(), DB_NAME_1, TABLE_NAME_1)).exists());

        serverHandler.handle(DROP_TABLE_2);
        Assertions.assertFalse(new File(String.format("%s/databases/%s/tables/%s.table", Config.absolutePath(), DB_NAME_2, TABLE_NAME_2)).exists());

    }


}

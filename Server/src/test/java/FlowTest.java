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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FlowTest {
    final Server server = new Server();
    final String DB_NAME = "test_db";
    final String TABLE_NAME = "test_table";

    @Before
    public void setUp() {
        final Request CREATE_DB = new Request("create database " + DB_NAME);
        server.getServerHandler().handle(CREATE_DB);
    }

    @Test
    @org.junit.Ignore
    public void test() {
        ServerHandler serverHandler = server.getServerHandler();

        final Request CREATE_TABLE = new Request("create table " + TABLE_NAME + " (name varchar)");
        final Request INSERT = new Request("insert into " + TABLE_NAME + " values ('test')");
        final Request SELECT = new Request("select * from " + TABLE_NAME);
        final Request DROP_TABLE = new Request("drop table " + TABLE_NAME);

        Response createTableResponse = serverHandler.handle(CREATE_TABLE);
        System.out.println(createTableResponse);
        Response insertResponse = serverHandler.handle(INSERT);
        System.out.println(insertResponse);
        System.out.println(insertResponse.getRecords());
        assert insertResponse.getRecords().size() == 1;
        System.out.println(insertResponse.getRecords().get(0).getData());
        Response selectResponse = serverHandler.handle(SELECT);
        System.out.println(selectResponse);
        serverHandler.handle(DROP_TABLE);

    }

    @After
    public void tearDown() {
        final Request DROP_DB = new Request("drop database " + DB_NAME);
        server.getServerHandler().handle(DROP_DB);
    }
}

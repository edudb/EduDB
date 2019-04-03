/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.meta_manager;

import com.google.common.base.Charsets;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import net.edudb.engine.Utility;
import net.edudb.master.MasterWriter;
import net.edudb.request.Request;
import net.edudb.response.Response;
import net.edudb.structure.Record;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * A singleton that handles all interactions with database holding
 * all the cluster's meta data.
 *
 * @author Fady Sameh
 *
 */
public class MetaManager implements MetaDAO, Runnable {

    private static MetaManager instance = new MetaManager();
    private int port;
    /**
     * Used to generate busy waiting until response is received
     * from the meta data database server
     */
    private Hashtable<String, Response> pendingRequests = new Hashtable<String, Response>();
    private boolean connected;
    private MetaHandler metaHandler;



    public void setPendingRequests(Hashtable<String, Response> pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    private MetaManager () {
        this.metaHandler = new MetaHandler();
        this.port = 9999;
    }

    public static MetaManager getInstance() { return instance; };

    public void run() {
        System.out.println("meta manager started");
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(
                            new ObjectDecoder(2147483647, ClassResolvers.softCachingResolver(null)),
                            new ObjectEncoder(),
                            metaHandler);
                }
            });

            // Start the client.
            ChannelFuture f = b.connect("localhost", port).sync();

//			clientHandler.setReceiving(true);
           // ByteBuf buf = Unpooled.copiedBuffer(, Charsets.UTF_8);
            ChannelFuture future = f.channel().writeAndFlush(new Request(null,"[edudb::admin:admin]"));

            while (!connected) {
                Thread.sleep(10);
            }

            //run(f.channel());

            if (future != null) {
                future.sync();
            }

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.err.println("Could not connect to the server. Please make sure that it is running.\nExiting...");
            workerGroup.shutdownGracefully();
        }
    }

    public void createDatabase(String databaseName) {

        Response response = forwardCommand("create database " + databaseName);

        if (response.getMessage().startsWith("Created database")) {
            initializeTables();
            MasterWriter.getInstance().write(new Response("Created database '" + databaseName + "'"));
        }
        else {
            MasterWriter.getInstance().write(new Response("Database '" + databaseName + "' already exists"));
        }
    }

    public void openDatabase(String databaseName) {

        Response response = forwardCommand("open database " + databaseName);

        if (response.getMessage().startsWith("Opened database")) {
            MasterWriter.getInstance().write(new Response("Opened database '" + databaseName + "'"));
        }
        else {
            MasterWriter.getInstance().write(new Response("Database '" + databaseName + "' does not exist"));
        }
    }

    public void closeDatabase() {
        Response response = forwardCommand("close database");

        if (response.getMessage().startsWith("Closed database")) {
            MasterWriter.getInstance().write(new Response(response.getMessage()));
        }
        else {
            MasterWriter.getInstance().write(new Response("No open database"));
        }
    }

    /**
     * This method is responsible for creating all the metadata
     * tables, once a new database is created
     */
    private void initializeTables() {

        createWorkersTable();
        createTablesTable();
    }

    private void createWorkersTable() {

        forwardCommand("create table workers (host Varchar, port Integer)");
    }

    private void createTablesTable() {
        forwardCommand("create table tables (name Varchar, metadata Varchar)");
    }

    public ArrayList<Record> getAll(String tableName) {
        Response response = forwardCommand("select * from " + tableName);
        return response.getRecords();
    }

    /**
     * This function is used for sending commands
     * to the meta database
     *
     * @param command
     * The command to be sent to the meta database
     */
    public Response forwardCommand(String command) {
        System.out.println("inside forward command");
        System.out.println(command);
        System.out.println("------------------");
        String id = Utility.generateUUID();
        Request request = new Request(id, command);
        MetaWriter.getInstance().write(request);

        /**
         * busy waiting till response is received
         */
        while (pendingRequests.get(id) == null);

        System.out.println("Response arrived at forwardCommand");
        System.out.println(pendingRequests.get(id).getMessage());
        System.out.println(pendingRequests.get(id).getRecords());

        return pendingRequests.remove(id);
    }

    public void setConnected(boolean connected) { this.connected = connected; }

    public boolean isConnected() { return this.connected; }

    public Hashtable<String, Response> getPendingRequests() {
        return pendingRequests;
    }
}

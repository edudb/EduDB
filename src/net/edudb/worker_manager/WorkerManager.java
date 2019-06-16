/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.worker_manager;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import net.edudb.engine.Utility;
import net.edudb.master.MasterWriter;
import net.edudb.meta_manager.MetaManager;
import net.edudb.request.Request;
import net.edudb.response.Response;
import net.edudb.workers_manager.WorkersManager;

import java.util.Hashtable;

/**
 * This class handles a connection to a worker node
 *
 * @author Fady Sameh
 */
public class WorkerManager implements Runnable, HandlerListener, WorkerDAO {

    private int port;
    private String host;
    private Hashtable<String, Response> pendingRequests = new Hashtable<String, Response>();

    private boolean connected = false;
    private WorkerWriter workerWriter;
    private WorkerHandler workerHandler;

    public WorkerManager(int port, String host) {
        this.port = port;
        this.host = host;
        this.workerWriter = new WorkerWriter();
        this.workerHandler = new WorkerHandler(this);
    }

    public void run() {

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
                            workerHandler);
                }
            });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();

//			clientHandler.setReceiving(true);
            // ByteBuf buf = Unpooled.copiedBuffer(, Charsets.UTF_8);
            ChannelFuture future = f.channel().writeAndFlush(new Request("init","[edudb::admin:admin]"));

            while (!connected) {
                Thread.sleep(10);
            }

            //run(f.channel());

            if (future != null) {
                future.sync();
            }

            /**
             * Once workers is connected, the current database need to be created
             * or opened
             */
            forwardCommand("create database " + MetaManager.getInstance().getDatabaseName());
            forwardCommand("open database " + MetaManager.getInstance().getDatabaseName());
            WorkersManager.getInstance().registerWorker(this);
            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            MasterWriter.getInstance().write("Could not connect to server at " + host +":" + port + ". Please make sure it's running.");
            WorkersManager.getInstance().removeWorker(this);
            System.err.println("Could not connect to the server. Please make sure that it is running.\nExiting...");
            workerGroup.shutdownGracefully();
        }

    }

    public void closeDatabase() {
        forwardCommand("close database");
    }

    public Response dropDatabase(String databaseName) {
        return forwardCommand("drop database " + databaseName);
    }

    public void createTable(String tableName, String metadata) {
        String command = "create table " + tableName + " (";
        String[] metadataArgs = metadata.split(" ");
        for (int i = 0; i < metadataArgs.length; i+=2) {
            command += metadataArgs[i] + " " + metadataArgs[i+1];
            if (i != metadataArgs.length - 2)
                command += ", ";
        }
        command += ")";
        forwardCommand(command);
    }

    public Response insert(String insertStatement) {
        return forwardCommand(insertStatement);
    }

    public Response forwardCommand(String command) {
        //System.out.println("Inside " + host + ":" + port + " forward command");

        String id = Utility.generateUUID();
        Request request = new Request(id, command);
//        System.out.println(request.getId());
//        System.out.println(request.getCommand());
        workerWriter.write(request);

        /**
         * busy waiting till response is received
         */
        while (pendingRequests.get(id) == null);
//        System.out.println("response received at " + host + ":" + port + " handler");
//        System.out.println(pendingRequests.get(id).getMessage());
//        System.out.println(pendingRequests.get(id).getRecords());

        return pendingRequests.remove(id);
    }

    public void onResponseArrival(ChannelHandlerContext ctx, Response response) {
//        System.out.println("Response arrive at " + host + ":" + port + " handler");
//        System.out.println(response.getId());
//        System.out.println(response.getMessage());

        //System.out.println("context " +ctx);


        if (response.getId() != null
                && !response.getId().equals("")) {
            if (response.getMessage().equals("[edudb::init]")) {
                setConnected(true);
                workerWriter.setContext(ctx);


            }
            else {
                workerWriter.setContext(ctx);
                pendingRequests.put(response.getId(), response);
            }
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }
}

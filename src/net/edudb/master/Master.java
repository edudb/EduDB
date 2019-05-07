/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.master;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import net.edudb.master.executor.*;

/**
 *
 * @author Fady Sameh
 *
 */
public class Master {

    private int port;

    public Master(int port) { this.port = port; }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new ObjectDecoder(2147483647, ClassResolvers.softCachingResolver(null)),
                                    new ObjectEncoder(),
                                    new MasterHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync();

            System.out.println("The master server is up and running on port: " + port);

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to
            // gracefully shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public  static MasterExecutorChain getExecutionChain() {
        MasterExecutorChain init = new InitializeExecutor();
        MasterExecutorChain createDatabase = new CreateDatabaseExecutor();
        MasterExecutorChain openDatabase = new OpenDatabaseExecutor();
        MasterExecutorChain closeDatabase = new CloseDatabaseExecutor();
        MasterExecutorChain replicateTable = new ReplicateTableExecutor();
        MasterExecutorChain shardTable = new ShardTableExecutor();
        MasterExecutorChain createShard = new CreateShardExecutor();
        MasterExecutorChain conncectWorker = new ConnectWorkerExecutor();
        MasterExecutorChain connectWorkers = new ConnectWorkersExecutor();
        MasterExecutorChain viewWorkers = new ViewWorkersExecutor();
        MasterExecutorChain viewShards = new ViewShardsExecutor();
        MasterExecutorChain showColumns = new ShowColumnsExecutor();
        MasterExecutorChain dropTable = new DropTableExecutor();
        MasterExecutorChain dropDatabase = new DropDatabaseExecutor();
        MasterExecutorChain sqlExecutor = new SQLExecutor();

        return connectChain(new MasterExecutorChain[] {
                init,
                createDatabase,
                openDatabase,
                closeDatabase,
                replicateTable,
                shardTable,
                createShard,
                conncectWorker,
                connectWorkers,
                viewWorkers,
                viewShards,
                showColumns,
                dropTable,
                dropDatabase,
                sqlExecutor
        });
    }

    public static MasterExecutorChain connectChain(MasterExecutorChain[] chainElements) {
        for (int i = 0; i < chainElements.length - 1; i++) {
            chainElements[i].setNextElementInChain(chainElements[i + 1]);
        }
        return chainElements[0];
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        else {
            port = 8080;
        }

        Master master = new Master(port);
        master.run();
    }
}

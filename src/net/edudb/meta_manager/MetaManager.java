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
    private boolean connected;

    private MetaHandler metaHandler;

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
                    ch.pipeline().addLast(metaHandler);
                }
            });

            // Start the client.
            ChannelFuture f = b.connect("localhost", port).sync();

//			clientHandler.setReceiving(true);
            ByteBuf buf = Unpooled.copiedBuffer("[edudb::admin:admin]", Charsets.UTF_8);
            ChannelFuture future = f.channel().writeAndFlush(buf);

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

    public void setConnected(boolean connected) { this.connected = connected; }

    public boolean isConnected() { return this.connected; }
}

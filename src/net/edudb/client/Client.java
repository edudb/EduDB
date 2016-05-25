/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.client;

import com.google.common.base.Charsets;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import jline.console.ConsoleReader;
//import jline.console.UserInterruptException;
import net.edudb.client.console.Console;

import java.io.IOException;

/**
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class Client {

	private static Client instance = new Client();
	private ClientHandler clientHandler;
	private ConsoleReader consoleReader;
	private Channel channel;
	private String host;
	private int port;
	private String username;
	private String password;
	private boolean connected;

	private Client() {
		this.clientHandler = new ClientHandler();
		try {
			this.consoleReader = new ConsoleReader();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Client getInstance() {
		return instance;
	}

	public ClientHandler getHandler() {
		return clientHandler;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean isConnected() {
		return connected;
	}

	public void start() {
		this.host = "localhost";
		this.port = 9999;
		this.username = "admin";
		this.password = "admin";

		try {
			String h = consoleReader.readLine("Host: ");
			if (h.length() > 0) {
				this.host = h;
			}

			String p = consoleReader.readLine("Port: ");
			if (p.length() > 0) {
				this.port = Integer.parseInt(p);
			}

			String u = consoleReader.readLine("Username: ");
			if (u.length() > 0) {
				this.username = u;
			}
			String pa = consoleReader.readLine("Password: ", '\0');
			if (pa.length() > 0) {
				this.password = pa;
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(clientHandler);
				}
			});

			// Start the client.
			ChannelFuture f = b.connect(host, port).sync();

//			clientHandler.setReceiving(true);
			ByteBuf buf = Unpooled.copiedBuffer("[edudb::" + username + ":" + password + "]", Charsets.UTF_8);
			ChannelFuture future = f.channel().writeAndFlush(buf);

			while (!connected) {
				Thread.sleep(10);
			}

			run(f.channel());

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

	public void run(Channel channel) {
		this.channel = channel;
		Console console = Console.getInstance();
		console.setPrompt("edudb$ ");
		console.start();
	}
}

/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.server;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.edudb.console.ConsoleExecutorChain;
import net.edudb.console.CopyExecutor;
import net.edudb.console.NullExecutor;
import net.edudb.console.SQLExecutor;
import net.edudb.engine.DatabaseSystem;

/**
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class Server {

	private int port;

	public Server(int port) {
		this.port = port;
	}

	public void run() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new ServerHandler());
						}
					}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
			// Bind and start to accept incoming connections.
			ChannelFuture f = b.bind(port).sync();

			System.out.println("The server is up and running on port: " + port);

			// Wait until the server socket is closed.
			// In this example, this does not happen, but you can do that to
			// gracefully shut down your server.
			f.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	public void showTray() {
		System.setProperty("apple.awt.UIElement", "true");

		final TrayIcon trayIcon;

		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/E.png"));

			PopupMenu popup = new PopupMenu();

			MenuItem portItem = new MenuItem("Listening on port " + port);
			portItem.setEnabled(false);

			MenuItem clientItem = new MenuItem("Open client");
			ActionListener clientListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String url = ClassLoader.getSystemClassLoader().getResource(".").getPath();
					String path = url + "edudb-client";

					String command = null;
					if (System.getProperty("os.name").startsWith("Windows")) {
						command = "cmd /c start " + path.substring(1);
					} else {
						command = "/usr/bin/open -a Terminal " + path;
					}
					try {
						Runtime.getRuntime().exec(command);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			};
			clientItem.addActionListener(clientListener);

			MenuItem quitItem = new MenuItem("Quit Server");
			ActionListener exitListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					DatabaseSystem.getInstance().exit(0);
				}
			};
			quitItem.addActionListener(exitListener);

			popup.add(portItem);
			popup.addSeparator();
			popup.add(clientItem);
			popup.addSeparator();
			popup.add(quitItem);

			trayIcon = new TrayIcon(image, "EduDB", popup);

			trayIcon.setImageAutoSize(true);

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println("TrayIcon could not be added.");
			}

		} else {
			JFrame frame = new JFrame("EduDB Server");
			frame.setSize(320, 160);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLocationRelativeTo(null);

			JLabel label = new JLabel("Server is up and running");
			label.setHorizontalAlignment(JLabel.CENTER);
			frame.add(label, BorderLayout.CENTER);

			frame.setVisible(true);
		}
	}

	public static void main(String[] args) throws Exception {

		/**
		 * ATTENTION
		 * 
		 * Important call.
		 */
		DatabaseSystem.getInstance().initializeDirectories();

		int port;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		} else {
			port = 9999;
		}
		Server server = new Server(port);
		server.showTray();
		server.run();
	}

	public static ConsoleExecutorChain getExecutionChain() {
		ConsoleExecutorChain init = new InitializeExecutor();
		ConsoleExecutorChain exit = new ExitExecutor();
		ConsoleExecutorChain copy = new CopyExecutor();
		ConsoleExecutorChain sql = new SQLExecutor();

		init.setNextInChain(exit);
		exit.setNextInChain(copy);
		copy.setNextInChain(sql);
		sql.setNextInChain(new NullExecutor());

		return init;
	}
}

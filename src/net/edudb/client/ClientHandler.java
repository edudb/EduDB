/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {

	/**
	 * Used to initiate a busy waiting until the server completes sending its
	 * data for which the JLine console waits until reprinting the prompt.
	 */
	private boolean receiving;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ByteBuf in = (ByteBuf) msg;
		String s = "";
		try {
			while (in.isReadable()) {
				s += (char) in.readByte();
			}

			ClientWriter.getInstance().setContext(ctx);
			if (s.contains("[edudb::init]")) {
				Client.getInstance().setConnected(true);
			} else if (s.contains("[edudb::mismatch]")) {
				System.out.println("Wrong username and/or password\nExiting...");
				exit();
			} else if (s.contains("[edudb::exit]")) {
				System.out.println("The server went away");
				exit();
			} else if (s.contains("[edudb::endofstring]")) {
				s = s.replace("[edudb::endofstring]\r\n", "");

				if (s.length() > 0) {
					System.out.print(s);
				}
				setReceiving(false);
			} else {
				System.out.print(s);
			}

		} finally {
			in.release();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	/**
	 * Used to signal when the server has stopped sending strings to the client
	 * using the [edudb::endofstring] delimiter.
	 * 
	 * @param receiving
	 *            The state of the stream.
	 */
	public void setReceiving(boolean receiving) {
		this.receiving = receiving;
	}

	public boolean isReceiving() {
		return this.receiving;
	}
	
	private void exit() {
		if (Client.getInstance().getChannel() != null) {
			Client.getInstance().getChannel().close();
		}
		System.exit(0);
	}
}

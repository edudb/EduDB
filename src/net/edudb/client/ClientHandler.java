/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.edudb.response.Response;
import net.edudb.structure.Record;

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
		if (msg instanceof Response) {
			Response response = (Response) msg;
			//System.out.println(response.getMessage());
			if (response.getMessage().equals("relation")) {
				//System.out.println("test");
				if (response.getRecords() != null) {
					for (Record record : response.getRecords())
						System.out.println(record.toString());
				}
			}
			else {
				//System.out.println("test2");
				System.out.println(response.getMessage());
			}

			Client.getInstance().setConnected(true);
			ClientWriter.getInstance().setContext(ctx);
			setReceiving(false);
		}
		else {
			String response = (String) msg;
			System.out.println(response);
			Client.getInstance().setConnected(true);
			ClientWriter.getInstance().setContext(ctx);
			setReceiving(false);
		}

//		ByteBuf in = (ByteBuf) msg;
//		String s = "";
//		try {
//			while (in.isReadable()) {
//				s += (char) in.readByte();
//			}
//
//			System.out.println("message from server");
//			System.out.println(s);
//			ClientWriter.getInstance().setContext(ctx);
//			if (s.contains("[edudb::init]")) {
//				Client.getInstance().setConnected(true);
//			} else if (s.contains("[edudb::mismatch]")) {
//				System.out.println("Wrong username and/or password\nExiting...");
//				exit();
//			} else if (s.contains("[edudb::exit]")) {
//				System.out.println("The server went away");
//				exit();
//			} else if (s.contains("[edudb::endofstring]")) {
//				s = s.replace("[edudb::endofstring]\r\n", "");
//
//				if (s.length() > 0) {
//					//System.out.print(s);
//				}
//				setReceiving(false);
//			} else {
//				//System.out.print(s);
//			}
//
//		} finally {
//			in.release();
//		}
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

/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.server;

import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import net.edudb.console.DatabaseConsole;

/**
 * A singleton that handles writing to the client.
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class ServerWriter {
	private static ServerWriter instance = new ServerWriter();
	private ChannelHandlerContext context;

	private ServerWriter() {
	}

	public static ServerWriter getInstance() {
		return instance;
	}

	public void setContext(ChannelHandlerContext context) {
		this.context = context;
	}

	public ChannelHandlerContext getContext() {
		return context;
	}

	/**
	 * Writes to the client iff the context is not null.
	 * 
	 * @param object
	 *            Object to write.
	 */
	public void write(Object obj) {
		if (context != null) {
			ByteBuf buf = Unpooled.copiedBuffer(obj.toString(), Charsets.UTF_8);

			context.writeAndFlush(buf);
		} else {
			DatabaseConsole.getInstance().write(obj);
		}
	}

	/**
	 * Writes a line to the client iff the context is not null.
	 * 
	 * @param object
	 *            Object to write.
	 */
	public void writeln(Object obj) {
		if (context != null) {
			ByteBuf buf = Unpooled.copiedBuffer(obj.toString() + "\r\n", Charsets.UTF_8);

			context.writeAndFlush(buf);
		} else {
			DatabaseConsole.getInstance().writeln(obj);
		}
	}
}

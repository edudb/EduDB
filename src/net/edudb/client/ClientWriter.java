package net.edudb.client;

import io.netty.channel.ChannelHandlerContext;
import net.edudb.request.Request;

/**
 * @author Ahmed Abdul Badie
 */
public class ClientWriter {

	private static ClientWriter instance = new ClientWriter();
	private ChannelHandlerContext context;

	private ClientWriter() {
	}

	public void setContext(ChannelHandlerContext context) {
		this.context = context;
	}

	public void writeln(Object obj) {
		if (context != null) {

			//ByteBuf buf = Unpooled.copiedBuffer(obj.toString() + "\n", Charsets.UTF_8);
			Request request = new Request(null, (String)obj);
			context.writeAndFlush(request);
		}
	}

	public void write(Object obj) {
		if (context != null) {
			//ByteBuf buf = Unpooled.copiedBuffer(obj.toString(), Charsets.UTF_8);

			Request request = new Request(null, (String)obj);
			context.writeAndFlush(request);
		}
	}

	public static ClientWriter getInstance() {
		return instance;
	}

}

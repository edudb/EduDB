package net.edudb.client;
import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

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
			ByteBuf buf = Unpooled.copiedBuffer(obj.toString() + "\n", Charsets.UTF_8);

			context.writeAndFlush(buf);
		}
	}

	public void write(Object obj) {
		if (context != null) {
			ByteBuf buf = Unpooled.copiedBuffer(obj.toString(), Charsets.UTF_8);

			context.writeAndFlush(buf);
		}
	}

	public static ClientWriter getInstance() {
		return instance;
	}

}

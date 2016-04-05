package net.edudb.server;

import adipe.translate.TranslationException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import net.edudb.user_interface.Parser;

/**
 * Handles a server-side channel.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter { // (1)

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ByteBuf in = (ByteBuf) msg;

		String s = "";
		try {
			// Do something with msg
			while (in.isReadable()) {
				s += (char) in.readByte();
			}
			// s = s.substring(0, s.length() - 1);

			ServerWriter.getInstance().setContext(ctx);

			switch (handleString(s)) {
			case -1:
				ctx.close();
				return;
			case 0:
				return;
			default:
				break;
			}

			Parser parser = new Parser();
			parser.parseSQL(s);
			
			ServerWriter.getInstance().writeln("edudb$");

		} catch (TranslationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}

	private int handleString(String str) {
		switch (str) {
		case "exit":
			return -1;
		case "[edudb::init]":
		case "clear":
			ServerWriter.getInstance().writeln("Initialized connection\nedudb$");
			return 0;
		default:
			return 1;
		}
	}
}

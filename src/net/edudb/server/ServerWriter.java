package net.edudb.server;

import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class ServerWriter {
	private static ServerWriter instance = new ServerWriter();
	private ChannelHandlerContext context;
	
	private ServerWriter(){}
	
	public static ServerWriter getInstance() {
		return instance;
	}
	
	public void setContext(ChannelHandlerContext context) {
		this.context = context;
	}
	
	public void writeln(Object obj) {
		if (context != null) {
			ByteBuf buf = Unpooled.copiedBuffer("edudb$ " + obj.toString()+"\n", Charsets.UTF_8);
			
			context.writeAndFlush(buf);
		} else {
			System.out.println("edudb$ " + obj);
		}
	}
	
	public void write(Object obj) {
		if (context != null) {
			ByteBuf buf = Unpooled.copiedBuffer(obj.toString(), Charsets.UTF_8);
			
			context.writeAndFlush(buf);
		} else {
			System.out.print(obj);
		}
	}
}

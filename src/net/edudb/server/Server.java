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
public class Server extends ChannelInboundHandlerAdapter { // (1)

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ByteBuf in = (ByteBuf) msg;
		
		String s = "";
	    try {
	        // Do something with msg
	    	while (in.isReadable()) { // (1)
	            s += (char) in.readByte();
	        }
	    	s = s.substring(0, s.length() - 1);
	    	
	    	ServerWriter.getInstance().setContext(ctx);
	    	
	    	Parser parser = new Parser();
	    	parser.parseSQL(s);
	    	
		} catch (TranslationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	    finally {
	        ReferenceCountUtil.release(msg);
	    }
//		ctx.writeAndFlush(msg);
	}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}

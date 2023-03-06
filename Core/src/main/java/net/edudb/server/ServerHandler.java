/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import net.edudb.Request;
import net.edudb.Response;
import net.edudb.engine.Utility;

import java.util.regex.Matcher;

/**
 * Handles a server-side channel.
 *
 * @author Ahmed Abdul Badie
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * This regex is used is to extract the message id from the incoming
     * message.
     */
    private final String regex = "(\\[id::(.+?)\\])";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("inside server channel read");

        Request request = (Request) msg;
        String s = request.getCommand();
        try {

            System.out.println("Incoming string " + s);
            Matcher matcher = Utility.getMatcher(s, regex);

            String messageID = "";

            if (request.getId() != null
                    && !request.getId().equals(""))
                messageID = request.getId();

            ServerWriter.getInstance().setContext(ctx);

            Response response = Server.getExecutionChain().execute(s);

            response.setId(messageID);
            //Response response = new Response(result, null, null);
            //System.out.println(response.getMessage());
//			if (response.getMessage().equals("relation"))
//				System.out.println(Relation.toString(response.getRelation()));

            ServerWriter.getInstance().write(response);
            //ServerWriter.getInstance().writeln("[edudb::endofstring]");

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
}

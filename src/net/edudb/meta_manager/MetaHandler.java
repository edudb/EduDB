/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.meta_manager;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 *
 * @author Fady Sameh
 *
 */
public class MetaHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        String s = "";
        try {
            while (in.isReadable()) {
                s += (char) in.readByte();
            }

            System.out.println("Message from meta database");
            System.out.println(s);

            MetaWriter.getInstance().setContext(ctx);
            if (s.contains("[edudb::init]")) {
                MetaManager.getInstance().setConnected(true);
            } else if (s.contains("[edudb::mismatch]")) {
                System.out.println("Wrong username and/or password\nExiting...");
                //exit();
            } else if (s.contains("[edudb::exit]")) {
                System.out.println("The server went away");
                //exit();
            } else if (s.contains("[edudb::endofstring]")) {
                s = s.replace("[edudb::endofstring]\r\n", "");

                if (s.length() > 0) {
                    System.out.print(s);
                }
                //setReceiving(false);
            } else {
                System.out.print(s);
            }

        } finally {
            in.release();
        }
    }
}

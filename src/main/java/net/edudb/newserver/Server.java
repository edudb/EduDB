/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.newserver;

import jline.console.ConsoleReader;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Server {
    private static final String DEFAULT_SERVER_NAME = "server";
    private RPCServer rpcServer;
    private ServerHandler handler;

    public Server() {
        this.handler = new ServerHandler();
    }

    public void setRpcServer(RPCServer rpcServer) {
        this.rpcServer = rpcServer;
    }

    private static String getServerNameFromUser(String[] args) {
        if (args.length == 0) {
            try {
                ConsoleReader reader = new ConsoleReader();

                String message = String.format("Enter server name [%s]: ", DEFAULT_SERVER_NAME);
                String userInput = reader.readLine(message);

                if (userInput.equals("")) {
                    return DEFAULT_SERVER_NAME;
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return args[0];
    }

    public static void main(String[] args) {
        Server server = new Server();

        String serverName = getServerNameFromUser(args);
        server.setRpcServer(new RPCServer(serverName));
        System.out.println(String.format("Starting server %s", serverName));

        try {
            server.rpcServer.initializeConnection();
            server.rpcServer.handleRequests(server.handler);
        } catch (IOException | TimeoutException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

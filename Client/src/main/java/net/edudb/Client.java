/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb;

import net.edudb.exceptions.RabbitMQConnectionException;

public class Client {
    private static Client instance = new Client();
    private static final String DEFAULT_SERVER_NAME = "server";
    private String connectedDatabase;
    private Console console;
    private RPCClient rpcClient;
    private ClientHandler handler;

    private Client() {
        this.console = new Console();
        this.handler = new ClientHandler();
    }

    public static Client getInstance() {
        return instance;
    }

    private String getServerNameFromUser() {
        String prompt = String.format("Enter server name [%s]: ", DEFAULT_SERVER_NAME);
        String userInput = this.console.readLine(prompt);

        if (userInput.equals("")) {
            return DEFAULT_SERVER_NAME;
        }

        return userInput;
    }

    public ClientHandler getHandler() {
        return handler;
    }

    public void setRpcClient(RPCClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    public Console getConsole() {
        return console;
    }

    public RPCClient getRpcClient() {
        return rpcClient;
    }

    public String getConnectedDatabase() {
        return connectedDatabase;
    }

    public void setConnectedDatabase(String connectedDatabase) {
        this.connectedDatabase = connectedDatabase;
    }

    public static void main(String[] args) {
        Client client = Client.getInstance();

        String serverName = client.getServerNameFromUser();

        RPCClient rpcClient = new RPCClient(serverName);
        client.setRpcClient(rpcClient);

        client.console.setPrompt(String.format("EduDB-%s> ", serverName));


        try {
            rpcClient.initializeConnection();

            Response handshakeResponse = rpcClient.handshake();
            client.console.displayMessage(handshakeResponse.getMessage());

            while (true) {
                if (client.getConnectedDatabase() != null)
                    client.console.setPrompt(String.format("EduDB-%s-(%s)> ", serverName, client.getConnectedDatabase()));
                else {
                    client.console.setPrompt(String.format("EduDB-%s> ", serverName));
                }
                String input = client.console.readLine();
                String output = client.handler.handle(input);
                client.console.displayMessage(output);
            }
        } catch (RabbitMQConnectionException e) {
            System.err.println(e.getMessage());
        }

    }
}

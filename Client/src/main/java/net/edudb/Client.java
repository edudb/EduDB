/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Client {
    private static Client instance = new Client();
    private static final String DEFAULT_SERVER_NAME = "server";
    private Console console;
    private ClientHandler handler;
    private Connection connection;

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

    private String getWorkspaceNameFromUser() {
        String prompt = "Enter workspace name (leave it empty if you are logging as admin): ";
        return this.console.readLine(prompt);
    }

    private String getUsernameFromUser() {
        String prompt = "Enter username: ";
        return this.console.readLine(prompt);
    }

    private String getPasswordFromUser() {
        String prompt = "Enter password: ";
        return this.console.readPassword(prompt);
    }

    public Console getConsole() {
        return console;
    }


    public Connection getConnection() {
        return connection;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Client client = Client.getInstance();

        while (true) {
            String serverName = client.getServerNameFromUser();
            String workspaceName = client.getWorkspaceNameFromUser();
            String username = client.getUsernameFromUser();
            String password = client.getPasswordFromUser();

            Properties properties = new Properties();
            properties.setProperty("username", username);
            properties.setProperty("password", password);

            Class.forName("net.edudb.jdbc.EdudbDriver");
            try {
                if (workspaceName.equals("")) {
                    client.connection = DriverManager.getConnection(String.format("jdbc:edudb://%s", serverName), properties);
                } else {
                    client.connection = DriverManager.getConnection(String.format("jdbc:edudb://%s:%s", serverName, workspaceName), properties);
                }
                client.console.setPrompt("EduDB> ");
                client.console.displayMessage("You are now connected to the server.");
                break;
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                client.console.displayMessage("Please try again.");
            }
        }

        while (true) {
            if (client.connection.getSchema() == null)
                client.console.setPrompt("EduDB> ");
            else {
                client.console.setPrompt(String.format("EduDB-(%s)> ", client.connection.getSchema()));
            }
            String input = client.console.readLine();
            String output = client.handler.handle(input);
            client.console.displayMessage(output);
        }
    }
}

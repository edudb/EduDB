/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */
package net.edudb;

import net.edudb.exceptions.SerializationException;

import java.io.*;

/**
 * From this class, the request objects sent from
 * the client to the server are instantiated
 *
 * @author Fady Sameh
 */
public class Request implements Serializable {
    //TODO: this class should be refactored to subclasses

    private String command;
    private String workspaceName;
    private String databaseName;
    private RequestType type;
    private String username;
    private String password;
    private String connectionQueueName;
    private String authToken;

    public Request(String command) {
        this.command = command;
        this.type = RequestType.NORMAL;
    }

    public Request(String command, RequestType type) {
        this.command = command;
        this.type = type;
    }

    public Request(String command, String databaseName) {
        this.command = command;
        this.databaseName = databaseName;
    }

    public Request(String workspaceName, String username, String password, String connectionQueueName) {
        this.type = RequestType.HANDSHAKE;
        this.workspaceName = workspaceName;
        this.username = username;
        this.password = password;
        this.connectionQueueName = connectionQueueName;
    }

    public static byte[] serialize(Request request) throws SerializationException {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(request);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("Error serializing request", e);
        }
    }

    public static Request deserialize(byte[] serializedData) throws SerializationException {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(serializedData);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (Request) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new SerializationException("Error deserializing request", e);
        }
    }


    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public RequestType getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getConnectionQueueName() {
        return connectionQueueName;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }
}

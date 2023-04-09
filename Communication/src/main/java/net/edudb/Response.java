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
import net.edudb.structure.Record;

import java.io.*;
import java.util.List;

/**
 * From this class, the response objects sent from
 * the server to the client are instantiated
 *
 * @author Fady Sameh
 */
public class Response implements Serializable {

    private String message;
    private String id;
    private List<Record> records;
    private boolean connectionUpdated;
    private String connectedDatabase;
    private ResponseStatus status;
    private String authToken;
    private String resultSetId;

    public Response(String message) {
        this.message = message;
        this.connectionUpdated = false;
    }

    public Response(String message, ResponseStatus status) {
        this.message = message;
        this.status = status;
    }

    public Response(String message, List<Record> records) {
        this.message = message;
        this.records = records;
    }

    public Response(String message, List<Record> records, String id) {
        this.message = message;
        this.id = id;
        this.records = records;
    }

    public Response(String message, boolean connectionUpdated, String connectedDatabase) {
        this.message = message;
        this.connectedDatabase = connectedDatabase;
        this.connectionUpdated = connectionUpdated;
    }

    public static byte[] serialize(Response response) throws SerializationException {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(response);
            oos.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("Failed to serialize response", e);
        }
    }

    public static Response deserialize(byte[] serializedData) throws SerializationException {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(serializedData);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (Response) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new SerializationException("Failed to deserialize response", e);
        }
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isConnectionUpdated() {
        return connectionUpdated;
    }

    public String getConnectedDatabase() {
        return connectedDatabase;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getResultSetId() {
        return resultSetId;
    }

    public void setResultSetId(String resultSetId) {
        this.resultSetId = resultSetId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(this.message);
        if (this.records != null) {
            for (Record currentRecord : this.records) {
                builder.append("\n").append(currentRecord.toString());
            }
        }
        return builder.toString();
    }

}

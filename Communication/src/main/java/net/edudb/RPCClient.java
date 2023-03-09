/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import net.edudb.exceptions.RabbitMQConnectionException;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * The RPCClient class represents a remote procedure call (RPC) client that is used to send a request to a server and wait for its response.
 * The client is initialized with a server name that is used to construct a queue name to communicate with the server. The AMQP URL is obtained from the system property "AMQP_URL".
 *
 * @author Ahmed Nasser Gaafar
 */
public class RPCClient {
    private String handshakeQueueName;
    private String connectionQueueName;
    private String clientId;
    private Connection connection;
    private Channel channel;

    /**
     * Constructs a new RPCClient object with the given server name.
     *
     * @param serverName The name of the server to communicate with.
     * @author Ahmed Nasser Gaafar
     */
    public RPCClient(String serverName) {
        this.clientId = Utils.getUniqueID();
        this.handshakeQueueName = Utils.getHandshakeQueueName(serverName);
        this.connectionQueueName = Utils.getConnectionQueueName(serverName, this.clientId);
    }

    /**
     * Initializes the connection to the RabbitMQ server's queue.
     *
     * @throws IOException      if there is a problem with the IO while establishing the connection.
     * @throws TimeoutException If the connection to the server times out.
     * @author Ahmed Nasser Gaafar
     */
    public void initializeConnection() throws RabbitMQConnectionException {
        ConnectionFactory factory = new ConnectionFactory();

        try {
            this.connection = factory.newConnection(Utils.getAMQPURL());
            this.channel = this.connection.createChannel();
        } catch (Exception e) {
            throw new RabbitMQConnectionException("Could not connect to RabbitMQ.", e);
        }
    }

    public Response handshake() {
        Request request = new Request(this.connectionQueueName, RequestType.HANDSHAKE);
        try {
            return this.call(request);
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Sends a request message to the server and waits for a response message.
     *
     * @param request The request to send to the server.
     * @return The response from the server.
     * @throws IOException          If there is a problem with the IO while sending or receiving messages.
     * @throws InterruptedException If the thread is interrupted while waiting for the response.
     * @throws ExecutionException   If there is an error while processing the response.
     * @author Ahmed Nasser Gaafar
     */
    public Response call(Request request) throws IOException, InterruptedException, ExecutionException {
        byte[] serializedRequest = Request.serialize(request);

        final String corrId = UUID.randomUUID().toString();
        String replyQueueName = channel.queueDeclare().getQueue();

        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", this.handshakeQueueName, props, serializedRequest);

        final CompletableFuture<Response> response = new CompletableFuture<>();

        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                try {
                    response.complete(Response.deserialize(delivery.getBody()));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }, consumerTag -> {
        });

        Response result = response.get();
        channel.basicCancel(ctag);
        return result;
    }

    /**
     * Closes the connection to the server.
     *
     * @throws IOException If there is a problem with the IO while closing the connection.
     * @author Ahmed Nasser Gaafar
     */
    public void closeConnection() throws IOException {
        this.connection.close();
    }

}

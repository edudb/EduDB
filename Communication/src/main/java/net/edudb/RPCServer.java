/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb;

import com.rabbitmq.client.*;
import net.edudb.exceptions.RabbitMQConnectionException;
import net.edudb.exceptions.RabbitMQCreateQueueException;
import net.edudb.exceptions.SerializationException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * The RPCServer class represents a server that handles remote procedure calls (RPC) from clients.
 * The server is initialized with a server name that is used to construct a queue name to communicate with the clients. The AMQP URL is obtained from the system property "AMQP_URL".
 *
 * @author Ahmed Nasser Gaafar
 */
public class RPCServer {
    private String handshakeQueueName;
    private Connection connection;
    private Channel channel;
    private RequestHandler requestHandler;
    private HandshakeHandler handshakeHandler;

    /**
     * Creates a new instance of RPCServer with the specified server name.
     *
     * @param serverName
     * @author Ahmed Nasser Gaafar
     */
    public RPCServer(String serverName, RequestHandler requestHandler, HandshakeHandler handshakeHandler) {
        this.handshakeQueueName = Utils.getHandshakeQueueName(serverName);
        this.requestHandler = requestHandler;
        this.handshakeHandler = handshakeHandler;
    }

    /**
     * Initializes the connection to the RabbitMQ server's queue.
     *
     * @throws IOException      if there is a problem with the IO while establishing the connection.
     * @throws TimeoutException If the connection to the server times out.
     * @author Ahmed Nasser Gaafar
     */
    public void initializeConnection() throws RabbitMQConnectionException, RabbitMQCreateQueueException {

        ConnectionFactory factory = new ConnectionFactory();

        try {
            this.connection = factory.newConnection(Utils.getAMQPURL());
            this.channel = this.connection.createChannel();
            this.channel = this.connection.createChannel();
        } catch (Exception e) {
            throw new RabbitMQConnectionException("Could not connect to RabbitMQ.", e);
        }

        try {
            this.channel.queueDeclare(this.handshakeQueueName, false, false, false, null);
            this.channel.queuePurge(this.handshakeQueueName);
            this.channel.basicConsume(this.handshakeQueueName, false, getHandshakeDeliverCallback(handshakeHandler), consumerTag -> {
            });
        } catch (Exception e) {
            throw new RabbitMQCreateQueueException("Could not create handshake queue.", e);
        }

    }

    private DeliverCallback getHandshakeDeliverCallback(HandshakeHandler handler) {
        return (consumerTag, delivery) -> {
            String correlationId = delivery.getProperties().getCorrelationId();
            String replyTo = delivery.getProperties().getReplyTo();

            try {
                Request request = Request.deserialize(delivery.getBody());

                if (request.getType() != RequestType.HANDSHAKE) {
                    sendResponse(new Response(String.format("Expected handshake request, got %s", request.getCommand())), correlationId, replyTo);
                    return;
                }

                System.out.println("Received handshake request: " + request.getConnectionQueueName());

                String connectionQueueName = request.getConnectionQueueName();
                String workspaceName = request.getWorkspaceName();
                String username = request.getUsername();
                String password = request.getPassword();

                Response response = handler.authenticate(workspaceName, username, password);


                if (response.getStatus() == ResponseStatus.HANDSHAKE_OK) {
                    this.channel.queueDeclare(connectionQueueName, false, false, false, null);
                    this.channel.queuePurge(connectionQueueName);
                    this.channel.basicConsume(connectionQueueName, false, getRequestDeliverCallback(this.requestHandler), consumerTag2 -> {
                    });
                }

                sendResponse(response, correlationId, replyTo);
                this.channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

            } catch (SerializationException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        };
    }

    private DeliverCallback getRequestDeliverCallback(RequestHandler handler) {
        return (consumerTag, delivery) -> {
            String correlationId = delivery.getProperties().getCorrelationId();
            String replyTo = delivery.getProperties().getReplyTo();

            try {
                Request request = Request.deserialize(delivery.getBody());

                System.out.println("Received request: " + request.getCommand());

                Response response = handler.handle(request);
                sendResponse(response, correlationId, replyTo);
                this.channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                System.out.println("Sent response: " + response.getMessage());

            } catch (SerializationException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        };
    }


    /**
     * Sends a response back to the client.
     *
     * @param response      The response to send.
     * @param correlationId The correlation ID of the request.
     * @param replyTo       The queue name to send the response to.
     * @throws IOException
     * @author Ahmed Nasser Gaafar
     */
    private void sendResponse(Response response, String correlationId, String replyTo) throws IOException, SerializationException {
        byte[] serializedResponse = Response.serialize(response);

        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(correlationId)
                .build();

        channel.basicPublish("", replyTo, props, serializedResponse);
    }


    /**
     * Closes the connection to the RabbitMQ server's queue.
     *
     * @throws IOException      if there is a problem with the IO while closing the connection.
     * @throws TimeoutException If the connection to the server times out.
     * @author Ahmed Nasser Gaafar
     */
    public void closeConnection() throws IOException, TimeoutException {
        this.channel.queueDelete(this.handshakeQueueName);
        this.channel.close();
        this.connection.close();
    }

}

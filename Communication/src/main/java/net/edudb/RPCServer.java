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

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RPCServer {
    private String queueName;
    private Connection connection;
    private Channel channel;


    public RPCServer(String serverName) {
        this.queueName = String.format("%s_queue", serverName);
    }

    public void initializeConnection() throws IOException, TimeoutException {
        String AMQP_URL = System.getProperty("AMQP_URL");

        ConnectionFactory factory = new ConnectionFactory();
        this.connection = factory.newConnection(AMQP_URL);
        this.channel = connection.createChannel();
        this.channel.queueDeclare(this.queueName, false, false, false, null);
        this.channel.queuePurge(this.queueName);
    }

    public void handleRequests(RequestHandler handler) throws IOException {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String correlationId = delivery.getProperties().getCorrelationId();
            String replyTo = delivery.getProperties().getReplyTo();

            try {
                Request request = Request.deserialize(delivery.getBody());
                System.out.println("Received request: " + request.getCommand());

                Response response = handler.handle(request);
                System.out.println("Sending response: " + response.getMessage());

                sendResponse(response, correlationId, replyTo);

                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        };

        channel.basicConsume(queueName, false, deliverCallback, consumerTag -> {
        });
    }

    private void sendResponse(Response response, String correlationId, String replyTo) throws IOException {
        byte[] serializedResponse = Response.serialize(response);

        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(correlationId)
                .build();

        channel.basicPublish("", replyTo, props, serializedResponse);
    }

    public void closeConnection() throws IOException, TimeoutException {
        this.channel.close();
        this.connection.close();
    }

}

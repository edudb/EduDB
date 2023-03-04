/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.newclient;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.github.cdimascio.dotenv.Dotenv;
import net.edudb.request.Request;
import net.edudb.response.Response;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class RPCClient {
    private String queueName;
    private Connection connection;
    private Channel channel;

    public RPCClient(String serverName) {
        this.queueName = String.format("%s_queue", serverName);
    }

    public void initializeConnection() throws IOException, TimeoutException {
        Dotenv dotenv = Dotenv.load();
        String AMQP_URL = dotenv.get("AMQP_URL");

        ConnectionFactory factory = new ConnectionFactory();
        this.connection = factory.newConnection(AMQP_URL);
        this.channel = connection.createChannel();
    }

    public Response call(Request request) throws IOException, InterruptedException, ExecutionException {
        byte[] serializedRequest = Request.serialize(request);

        final String corrId = UUID.randomUUID().toString();
        String replyQueueName = channel.queueDeclare().getQueue();

        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", this.queueName, props, serializedRequest);

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

    public void closeConnection() throws IOException {
        this.connection.close();
    }

}

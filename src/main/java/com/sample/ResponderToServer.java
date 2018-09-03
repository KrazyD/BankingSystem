package com.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.enums.LoggerTypes;
import com.sample.model.BankRequest;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ResponderToServer implements Runnable {

    private final String CONNECTION_STRING = "tcp://localhost:61616";
    private static BlockingQueue<Message> blockingQueue = new LinkedBlockingQueue<>();

    public static BlockingQueue<Message> getBlockingQueue() {
        return blockingQueue;
    }

    ResponderToServer() { }

    @Override
    public void run() {

        try {

            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(CONNECTION_STRING);
            Connection connection = factory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue("responseQueue");
            MessageProducer publisher = session.createProducer(queue);
            connection.start();

            Message message;

            while (true) {
                if (blockingQueue.size() > 0) {
                    message = blockingQueue.take();
                    publisher.send(processingReceivedMessage(message));
                    LoggerWriter.createMessage(LoggerTypes.INFO, "Message with action "
                            + message.getStringProperty("action") + " was successfully sent");
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Message processingReceivedMessage(Message message) throws JMSException {
        Message returnedMessage = new ActiveMQTextMessage();
        SQLiteConnector connector = SQLiteConnector.getInstance();
        switch (message.getStringProperty("action")) {
            case "create": {
                try {
                    String request = message.getStringProperty("bankRequest");
                    BankRequest bankRequest = new ObjectMapper().readValue(request, BankRequest.class);
                    connector.createRequest(bankRequest);

                    returnedMessage.setStringProperty("status", "ok");
                    String response = new ObjectMapper().writeValueAsString(bankRequest);
                    returnedMessage.setStringProperty("response", response);
                } catch (Exception e) {
                    e.printStackTrace();
                    returnedMessage.setStringProperty("status", "error");
                    returnedMessage.setStringProperty("errorMessage", e.getLocalizedMessage());
                }
                break;
            }
            case "edit": {
                try {
                    String request = message.getStringProperty("bankRequest");
                    BankRequest bankRequest = new ObjectMapper().readValue(request, BankRequest.class);

                    connector.editRequest(bankRequest);
                    returnedMessage.setStringProperty("status", "ok");
                    String response = new ObjectMapper().writeValueAsString(bankRequest);
                    returnedMessage.setStringProperty("response", response);
                } catch (Exception e) {
                    e.printStackTrace();
                    returnedMessage.setStringProperty("status", "error");
                    returnedMessage.setStringProperty("errorMessage", e.getLocalizedMessage());
                }
                break;
            }
            case "withdrawn": {
                try {
                    String request = message.getStringProperty("bankRequest");
                    BankRequest bankRequest = new ObjectMapper().readValue(request, BankRequest.class);
                    connector.withdrawRequest(bankRequest);
                    returnedMessage.setStringProperty("status", "ok");
                    returnedMessage.setStringProperty("response", "{ \"Message\": \"Request was withdrawn\" }");
                } catch (Exception e) {
                    e.printStackTrace();
                    returnedMessage.setStringProperty("status", "error");
                    returnedMessage.setStringProperty("errorMessage", e.getLocalizedMessage());
                }
                break;
            }
            case "filter": {
                try {

                    HashMap<String, String> params = new ObjectMapper().readValue(message.getStringProperty("params"),
                            HashMap.class);

                    List<BankRequest> filteredRequests = connector.getRequestsByFilter(params);
                    returnedMessage.setStringProperty("status", "ok");
                    returnedMessage.setObjectProperty("response", filteredRequests);
                } catch (Exception e) {
                    e.printStackTrace();
                    returnedMessage.setStringProperty("status", "error");
                    returnedMessage.setStringProperty("errorMessage", e.getLocalizedMessage());
                }
                break;
            }
            default:
                returnedMessage.setStringProperty("status", "error");
                returnedMessage.setStringProperty("errorMessage", "Value of action property is not correct");
                break;
        }
        return returnedMessage;
    }
}

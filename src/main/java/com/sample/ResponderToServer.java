package com.sample;

import com.sample.model.BankRequest;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ResponderToServer implements Runnable {

    private final String CONNECTION_STRING = "tcp://localhost:61616";
    private static BlockingQueue<Message> blockingQueue;

    public static BlockingQueue<Message> getBlockingQueue() {
        return blockingQueue;
    }

    ResponderToServer() {
        blockingQueue = new LinkedBlockingDeque<>();
    }

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
                    publisher.send(message);
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

    public Message processingRecivedMessage(Message message) throws JMSException {
        Message returnedMessage = new ActiveMQTextMessage();
        SQLiteConnector connector = SQLiteConnector.getInstance();
        switch (message.getStringProperty("action")) {
            case "create": {
                try {
                    connector.createRequest((BankRequest) message.getObjectProperty("bankRequest"));
                    returnedMessage.setStringProperty("status", "ok");
                } catch (Exception e) {
                    e.printStackTrace();
                    returnedMessage.setStringProperty("status", "error");
                    returnedMessage.setStringProperty("errorMessage", e.getLocalizedMessage());
                }
                break;
            }
            case "edit": {
                try {
                    connector.editRequest((BankRequest) message.getObjectProperty("bankRequest"));
                    returnedMessage.setStringProperty("status", "ok");
                } catch (Exception e) {
                    e.printStackTrace();
                    returnedMessage.setStringProperty("status", "error");
                    returnedMessage.setStringProperty("errorMessage", e.getLocalizedMessage());
                }
                break;
            }
            case "withdraw": {
                try {
                    connector.withdrawRequest((BankRequest) message.getObjectProperty("bankRequest"));
                    returnedMessage.setStringProperty("status", "ok");
                } catch (Exception e) {
                    e.printStackTrace();
                    returnedMessage.setStringProperty("status", "error");
                    returnedMessage.setStringProperty("errorMessage", e.getLocalizedMessage());
                }
                break;
            }
            case "filter": {
                try {
                    if (message.getObjectProperty("params") instanceof HashMap) {
                        List<BankRequest> filteredRequests = connector.getRequestsByFilter(
                                (HashMap<String, String>) message.getObjectProperty("params"));
                        returnedMessage.setStringProperty("status", "ok");
                        returnedMessage.setObjectProperty("filteredRequests", filteredRequests);
                    } else {
                        returnedMessage.setStringProperty("status", "error");
                        returnedMessage.setStringProperty("errorMessage", "Type of params does not match HashMap");
                    }
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

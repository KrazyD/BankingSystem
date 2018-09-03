package com.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sample.enums.LoggerTypes;
import com.sample.model.BankRequest;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ResponderToServer implements Runnable {

    private final String CONNECTION_STRING = "tcp://localhost:61616";
    private static BlockingQueue<Message> blockingQueue = new LinkedBlockingQueue<>();
    private Type hashMapType;
    private Type bankRequestType;
    private Gson gson;

    public static BlockingQueue<Message> getBlockingQueue() {
        return blockingQueue;
    }

    ResponderToServer() {
        hashMapType = new TypeToken<HashMap<String, String>>() {}.getType();
        bankRequestType = new TypeToken<BankRequest>() {}.getType();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateAdapter());
        gsonBuilder.setDateFormat("yyyy-MM-dd");
        gson = gsonBuilder.create();
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
                    BankRequest bankRequest = gson.fromJson(request, bankRequestType);
                    connector.createRequest(bankRequest);

                    returnedMessage.setStringProperty("status", "ok");

                    String response = gson.toJson(bankRequest);
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
                    BankRequest bankRequest = gson.fromJson(request, bankRequestType);

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
                    BankRequest bankRequest = gson.fromJson(request, bankRequestType);
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

                    HashMap<String, String> params = gson.fromJson(message.getStringProperty("params"), hashMapType);

                    List<BankRequest> filteredRequests = connector.getRequestsByFilter(params);

                    String filteredRequestsJSON = gson.toJson(filteredRequests);

                    returnedMessage.setStringProperty("status", "ok");
                    returnedMessage.setStringProperty("response", filteredRequestsJSON);
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

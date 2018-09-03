package com.sample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sample.enums.RequestStatuses;
import com.sample.model.BankRequest;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.jupiter.api.*;

import javax.jms.JMSException;
import javax.jms.Message;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResponderToServerTest {

    private Message message;
    private ResponderToServer responder;
    private BankRequest bankRequest;
    private SQLiteConnector connector;
    private Type bankRequestType;
    private static Gson gson;

    @BeforeAll
    static void configure() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new GsonDateAdapter());
        gsonBuilder.setDateFormat("yyyy-MM-dd");
        gson = gsonBuilder.create();
    }

    @AfterAll
    static void stop() {
        gson = null;
    }

    @BeforeEach
    void setUp() {
        message = new ActiveMQTextMessage();
        responder = new ResponderToServer();
        bankRequest = new BankRequest(1, "Свалов Дмитрий Андреевич", "кредит", new Date(0),
                new Date(0), RequestStatuses.NEW, "Комментарий");
        connector = SQLiteConnector.getInstance();
        bankRequestType = new TypeToken<BankRequest>() {}.getType();
    }

    @AfterEach
    void tearDown() {
        message = null;
        responder = null;
        bankRequest = null;
        connector = null;
        bankRequestType = null;
    }

    @Test
    void processingRecivedMessage_create_action() throws JMSException {
        message.setStringProperty("action", "create");
        String bankRequestJSON = gson.toJson(bankRequest);
        message.setStringProperty("bankRequest", bankRequestJSON);

        Message returnedMessage = responder.processingReceivedMessage(message);
        String returnedBankRequestJSON = returnedMessage.getStringProperty("response");
        BankRequest returnedBankRequest = gson.fromJson(returnedBankRequestJSON, bankRequestType);

        assertEquals(new Integer(1), returnedBankRequest.getNumberOfRequest());
        assertEquals("Свалов Дмитрий Андреевич", returnedBankRequest.getClient());
        assertEquals("кредит", returnedBankRequest.getNameOfService());
        assertEquals(new Date(0), returnedBankRequest.getCreated());
        assertEquals(new Date(0), returnedBankRequest.getLastChanged());
        assertEquals(RequestStatuses.NEW, returnedBankRequest.getStatus());
        assertEquals("Комментарий", returnedBankRequest.getComment());
    }

    @Test
    void processingRecivedMessage_edit_action() throws JMSException {

        connector.createRequest(bankRequest);

        message.setStringProperty("action", "edit");
        bankRequest.setStatus(RequestStatuses.IN_PROCRESSING);
        String bankRequestJSON = gson.toJson(bankRequest);
        message.setStringProperty("bankRequest", bankRequestJSON);

        Message returnedMessage = responder.processingReceivedMessage(message);

        String returnedBankRequestJSON = returnedMessage.getStringProperty("response");
        BankRequest returnedBankRequest = gson.fromJson(returnedBankRequestJSON, bankRequestType);

        assertEquals(new Integer(1), returnedBankRequest.getNumberOfRequest());
        assertEquals("Свалов Дмитрий Андреевич", returnedBankRequest.getClient());
        assertEquals("кредит", returnedBankRequest.getNameOfService());
        assertEquals(new Date(0), returnedBankRequest.getCreated());
        assertEquals(new Date(0), returnedBankRequest.getLastChanged());
        assertEquals(RequestStatuses.IN_PROCRESSING, returnedBankRequest.getStatus());
        assertEquals("Комментарий", returnedBankRequest.getComment());
    }

    @Test
    void processingRecivedMessage_withdrawn_action() throws JMSException {

        connector.createRequest(bankRequest);

        message.setStringProperty("action", "withdrawn");
        String bankRequestJSON = gson.toJson(bankRequest);
        message.setStringProperty("bankRequest", bankRequestJSON);

        Message returnedMessage = responder.processingReceivedMessage(message);

        String returnedResponse = returnedMessage.getStringProperty("response");

        assertEquals("{ \"Message\": \"Request was withdrawn\" }", returnedResponse);
    }

    @Test
    void processingRecivedMessage_filter_action() throws JMSException {

        BankRequest bankRequest2 = new BankRequest(1, "Коновалов Александр Евгеньевич",
                "кредит", new Date(0), new Date(0), RequestStatuses.NEW, "Комментарий");

        connector.createRequest(bankRequest);
        connector.createRequest(bankRequest2);

        HashMap<String, String> params = new LinkedHashMap<>();
        params.put("client", "Свалов Дмитрий Андреевич");

        String paramsJSON = gson.toJson(params);

        message.setStringProperty("action", "filter");
        message.setStringProperty("params", paramsJSON);

        Message returnedMessage = responder.processingReceivedMessage(message);

        Type listType = new TypeToken<List<BankRequest>>() {}.getType();
        String filteredRequestsJSON = returnedMessage.getStringProperty("response");
        List<BankRequest> filteredRequests = gson.fromJson(filteredRequestsJSON, listType);

        boolean isRequiredClient = true;
        for (BankRequest bankRequest: filteredRequests) {
            if (!bankRequest.getClient().equals("Свалов Дмитрий Андреевич")) {
                isRequiredClient = false;
            }
        }
        assertEquals(true, isRequiredClient);
    }
}
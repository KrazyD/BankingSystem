package com.sample;

import com.sample.enums.LoggerTypes;

import javax.jms.*;

public class MQListener implements MessageListener {

    public void onMessage(Message m) {
        try {

            LoggerWriter.createMessage(LoggerTypes.INFO, "Received a message with action: " + m.getStringProperty("action"));

            ResponderToServer.getBlockingQueue().put(m);

        } catch (InterruptedException | JMSException e) {
            e.printStackTrace();
        }
    }
}

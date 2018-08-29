package com.sample;

import javax.jms.*;

public class MQListener implements MessageListener {

    public void onMessage(Message m) {
        try {

            ResponderToServer.getBlockingQueue().put(m);

            TextMessage msg = (TextMessage) m;

            System.out.println("Message with action is received " + msg.getStringProperty("action"));
            System.out.println("BankRequest " + msg.getObjectProperty("request"));
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

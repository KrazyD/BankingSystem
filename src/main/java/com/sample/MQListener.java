package com.sample;

import javax.jms.*;

public class MQListener implements MessageListener {

    public void onMessage(Message m) {
        try {

            TextMessage msg = (TextMessage) m;

            System.out.println("following message is received: " + msg.getText());
            System.out.println("Property " + msg.getStringProperty("name"));
        } catch (JMSException e) {
            System.out.println(e);
        }
    }
}

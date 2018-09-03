package com.sample;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        try {

            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            QueueConnection connection = connectionFactory.createQueueConnection();
            connection.start();

            QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue requestQueue = session.createQueue("requestQueue");
            QueueReceiver receiver = session.createReceiver(requestQueue);
            MQListener listener = new MQListener();
            receiver.setMessageListener(listener);

        } catch (JMSException e) {
            e.printStackTrace();
        }

        ResponderToServer responder = new ResponderToServer();
        Thread responderThread = new Thread(responder);
        responderThread.join();
        responderThread.start();
    }
}

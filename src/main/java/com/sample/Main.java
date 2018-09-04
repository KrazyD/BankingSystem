package com.sample;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.PropertyConfigurator;

import javax.jms.*;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        try {

            PropertyConfigurator.configure("log4j.properties");

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

package com.sample;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class MQMessageReceiver implements Runnable {
    public void run() {
        try {

            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            QueueConnection connection = connectionFactory.createQueueConnection();
            connection.start();

            QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue requestQueue = session.createQueue("requestQueue");
            QueueReceiver receiver = session.createReceiver(requestQueue);
            MQListener listener = new MQListener();
            receiver.setMessageListener(listener);

            while (true) {

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

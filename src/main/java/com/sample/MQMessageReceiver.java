package com.sample;


import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import javax.naming.InitialContext;

public class MQMessageReceiver implements Runnable {
    public void run() {
        try {

            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            QueueConnection connection = connectionFactory.createQueueConnection();
            connection.start();


            QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

            Queue sessionQueue = session.createQueue("clientQueue");
//            Queue sessionQueue = (Queue) context.lookup("clientQueue");

            QueueReceiver receiver = session.createReceiver(sessionQueue);
            MQListener listener = new MQListener();
            receiver.setMessageListener(listener);

//            Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
//            Queue destination = session.createQueue("serverQueue");
//            MessageConsumer consumer = session.createConsumer(destination);

            while (true) {
//                Message message = consumer.receive();
//                System.out.println(message);
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

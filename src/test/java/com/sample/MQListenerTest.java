package com.sample;

import org.apache.activemq.command.ActiveMQMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.jms.JMSException;
import javax.jms.Message;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MQListenerTest {

    private Message message;
    private MQListener mqListener;

    @BeforeEach
    void setUp() throws JMSException {
        message = new ActiveMQMessage();
        message.setStringProperty("test", "value of property test");
        mqListener = new MQListener();
    }

    @AfterEach
    void tearDown() {
        message = null;
        mqListener = null;
    }

    @Test
    void onMessage() throws InterruptedException, JMSException {
        mqListener.onMessage(message);
        Message messageFromQueue = ResponderToServer.getBlockingQueue().take();
        assertEquals(message, messageFromQueue);
        assertEquals("value of property test", messageFromQueue.getStringProperty("test"));
    }
}
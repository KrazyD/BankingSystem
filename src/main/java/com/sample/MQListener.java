package com.sample;

import javax.jms.*;

public class MQListener implements MessageListener {

    public void onMessage(Message m) {
        try {

            ResponderToServer.getBlockingQueue().put(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

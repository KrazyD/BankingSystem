package com.sample;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        MQMessageReceiver messageReceiver = new MQMessageReceiver();
        Thread receiverThread = new Thread(messageReceiver);
        receiverThread.start();
        receiverThread.join();

        ResponderToServer responder = new ResponderToServer();
        Thread responderThread = new Thread(responder);
        responderThread.start();
        responderThread.join();
    }
}

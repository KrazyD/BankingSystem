package com.sample;


import com.sample.enums.RequestStatuses;
import com.sample.model.BankRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Main {

    private static SQLiteConnector sqLiteConnector;

    public static void main(String[] args) {
//        System.out.println("Hello, World!");
//
//        sqLiteConnector = SQLiteConnector.getInstance();
//
////        Написать тесты для проверки методов БД
//
//        BankRequest bankRequest = new BankRequest(1, "Петров Василий Петрович",
//                "кредит", new Date(), new Date(), RequestStatuses.NEW, "Комментарий");
//
//        BankRequest bankRequest2 = new BankRequest(1, "Смирнов Василий Петрович",
//                "кредит", new Date(), new Date(), RequestStatuses.NEW, "Комментарий");
//
//        BankRequest bankRequest3 = new BankRequest(1, "Петров Василий Петрович",
//                "кредит", new Date(), new Date(), RequestStatuses.NEW, "Комментарий");
//
//        sqLiteConnector.createRequest(bankRequest);
//        sqLiteConnector.createRequest(bankRequest2);
//        sqLiteConnector.createRequest(bankRequest3);
//
//        List<BankRequest> data;
//        List<BankRequest> data = sqLiteConnector.getTableData();
//        for (BankRequest request: data) {
//            System.out.println(request);
//        }
//
//        bankRequest2.setStatus(RequestStatuses.IN_PROCRESSING);
//
//        sqLiteConnector.editRequest(bankRequest2);
//
//        data = sqLiteConnector.getTableData();
//        for (BankRequest request: data) {
//            System.out.println(request);
//        }
//
//        System.out.println(bankRequest.getId());

//        sqLiteConnector.withdrawRequest(bankRequest);
//
//        HashMap<String, String> params = new HashMap<>();
//        params.put("client", "Петров Василий Петрович");
//
//        data = sqLiteConnector.getRequestsByFilter(params);
//        for (BankRequest request: data) {
//            System.out.println(request);
//        }
//
//        int q = 0;

        MQMessageReceiver messageReceiver = new MQMessageReceiver();
        Thread thread = new Thread(messageReceiver);
//        thread.setDaemon(true);
        thread.start();
        while (true) {
            System.out.println("Working");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

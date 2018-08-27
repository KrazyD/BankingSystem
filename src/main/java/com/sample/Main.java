package com.sample;


import com.sample.model.BankRequest;

import java.util.Date;
import java.util.List;

public class Main {

    private static SQLiteConnector sqLiteConnector;

    public static void main(String[] args) {
        System.out.println("Hello, World!");

        sqLiteConnector = SQLiteConnector.getInstance();

        BankRequest bankRequest = new BankRequest(1, "Петров Василий Петрович",
                "кредит", new Date(), new Date(), "Новая", "Комментарий");

        sqLiteConnector.createRequest(bankRequest);

        List<BankRequest> data = sqLiteConnector.getTableData();

        for (BankRequest request: data) {
            System.out.println(request);
        }
    }
}

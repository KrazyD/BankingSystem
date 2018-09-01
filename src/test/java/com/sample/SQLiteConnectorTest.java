package com.sample;

import com.sample.enums.RequestStatuses;
import com.sample.model.BankRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SQLiteConnectorTest {

    private SQLiteConnector connector;
    private BankRequest bankRequest1;
    private BankRequest bankRequest2;
    private BankRequest bankRequest3;
    private HashMap<String, String> params;


    @BeforeEach
    void setUp() {
        connector = SQLiteConnector.getInstance();
        bankRequest1 = new BankRequest(1, "Свалов Дмитрий Андреевич",
                "кредит", new Date(0),
                new Date(0), RequestStatuses.NEW, "Комментарий1");
        bankRequest2 = new BankRequest(1, "Свалов Дмитрий Игоревич",
                "кредит", new Date(0),
                new Date(0), RequestStatuses.NEW, "Комментарий2");
        bankRequest3 = new BankRequest(1, "Свалов Дмитрий Андреевич",
                "выдача карты", new Date(0),
                new Date(0), RequestStatuses.NEW, "Комментарий3");
        params = new LinkedHashMap<>();
    }

    @AfterEach
    void tearDown() {
        connector = null;
        bankRequest1 = null;
        params = null;
    }

    @Test
    void getInstance() {
        SQLiteConnector testConnector = SQLiteConnector.getInstance();
        assertEquals(connector, testConnector);
    }

    @Test
    void createRequest() {
        connector.createRequest(bankRequest1);
        BankRequest loadedRequest = connector.getRequestById(bankRequest1.getId());
        assertEquals(bankRequest1.getId(), loadedRequest.getId());
        assertEquals(new Integer(1), loadedRequest.getNumberOfRequest());
        assertEquals("Свалов Дмитрий Андреевич", loadedRequest.getClient());
        assertEquals("кредит", loadedRequest.getNameOfService());
        assertEquals(new Date(0), loadedRequest.getCreated());
        assertEquals(new Date(0), loadedRequest.getLastChanged());
        assertEquals(RequestStatuses.NEW, loadedRequest.getStatus());
        assertEquals("Комментарий1", loadedRequest.getComment());

    }

    @Test
    void editRequest() {
        connector.createRequest(bankRequest1);
        bankRequest1.setStatus(RequestStatuses.IN_PROCRESSING);
        connector.editRequest(bankRequest1);
        BankRequest loadedRequest = connector.getRequestById(bankRequest1.getId());
        assertEquals(RequestStatuses.IN_PROCRESSING, loadedRequest.getStatus());
    }

    @Test
    void withdrawRequest() {
        connector.createRequest(bankRequest1);
        connector.withdrawRequest(bankRequest1);
        BankRequest loadedRequest = connector.getRequestById(bankRequest1.getId());
        assertEquals(RequestStatuses.WITHDRAWN, loadedRequest.getStatus());
    }

    @Test
    void getRequestsByFilter_with_one_param() {

        connector.createRequest(bankRequest1);
        connector.createRequest(bankRequest2);
        connector.createRequest(bankRequest3);
        params.put("client", "Свалов Дмитрий Андреевич");
        List<BankRequest> filteredRequests = connector.getRequestsByFilter(params);

        assertEquals(true, isContainsRequestWithId(bankRequest1.getId(), filteredRequests));
        assertEquals(false, isContainsRequestWithId(bankRequest2.getId(), filteredRequests));
        assertEquals(true, isContainsRequestWithId(bankRequest3.getId(), filteredRequests));
    }

    @Test
    void getRequestsByFilter_with_many_params() {

        connector.createRequest(bankRequest1);
        connector.createRequest(bankRequest2);
        connector.createRequest(bankRequest3);
        params.put("client", "Свалов Дмитрий Андреевич");
        params.put("comment", "Комментарий3");

        List<BankRequest> filteredRequests = connector.getRequestsByFilter(params);

        assertEquals(false, isContainsRequestWithId(bankRequest1.getId(), filteredRequests));
        assertEquals(false, isContainsRequestWithId(bankRequest2.getId(), filteredRequests));
        assertEquals(true, isContainsRequestWithId(bankRequest3.getId(), filteredRequests));
    }

    private boolean isContainsRequestWithId(Integer id, List<BankRequest> list) {
        for (BankRequest request: list) {
            if (request.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
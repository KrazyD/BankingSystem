package com.sample.model;

import java.util.Date;

public class BankRequest {

    private Integer numberOfRequest;
    private String client;
    private String nameOfService;
    private Date created;
    private Date lastChanged;
    private String status;
    private String comment;

    public BankRequest(Integer numberOfRequest, String client, String nameOfService, Date created, Date lastChanged, String status, String comment) {
        this.numberOfRequest = numberOfRequest;
        this.client = client;
        this.nameOfService = nameOfService;
        this.created = created;
        this.lastChanged = lastChanged;
        this.status = status;
        this.comment = comment;
    }

    public Integer getNumberOfRequest() {
        return numberOfRequest;
    }

    public void setNumberOfRequest(Integer numberOfRequest) {
        this.numberOfRequest = numberOfRequest;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getNameOfService() {
        return nameOfService;
    }

    public void setNameOfService(String nameOfService) {
        this.nameOfService = nameOfService;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastChanged() {
        return lastChanged;
    }

    public void setLastChanged(Date lastChanged) {
        this.lastChanged = lastChanged;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

package com.sample.model;

import com.sample.enums.RequestStatuses;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "bank_request")
public class BankRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "number_of_request")
    private Integer numberOfRequest;

    @Column(name = "client")
    private String client;

    @Column(name = "name_of_service")
    private String nameOfService;

    @Column(name = "created")
    private Date created;

    @Column(name = "last_changed")
    private Date lastChanged;

    @Column(name = "status")
    private RequestStatuses status;

    @Column(name = "comment")
    private String comment;

    public BankRequest() { }

    public BankRequest(Integer numberOfRequest, String client, String nameOfService, Date created, Date lastChanged, RequestStatuses status, String comment) {
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

    public RequestStatuses getStatus() {
        return status;
    }

    public void setStatus(RequestStatuses status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id + " " + numberOfRequest + " " +
                client + " " + nameOfService + " " + created + " " +
                lastChanged + " " + status + " " + comment;
    }

}

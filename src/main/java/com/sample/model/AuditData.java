package com.sample.model;

import com.sample.enums.*;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "audit_data")
public class AuditData implements Serializable {

    public AuditData(String client, ActionTypes action, OperationTypes operation, Date date) {
        this.client = client;
        this.action = action;
        this.operation = operation;
        this.date = date;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "client")
    private String client;

    @Column(name = "action")
    private ActionTypes action;

    @Column(name = "operation")
    private OperationTypes operation;

    @Column(name = "date")
    private Date date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public ActionTypes getAction() {
        return action;
    }

    public void setAction(ActionTypes action) {
        this.action = action;
    }

    public OperationTypes getOperation() {
        return operation;
    }

    public void setOperation(OperationTypes operation) {
        this.operation = operation;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

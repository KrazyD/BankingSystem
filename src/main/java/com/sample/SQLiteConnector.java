package com.sample;

import com.sample.enums.ActionTypes;
import com.sample.enums.OperationTypes;
import com.sample.enums.RequestStatuses;
import com.sample.model.AuditData;
import com.sample.model.BankRequest;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.util.*;

public class SQLiteConnector {

    private SessionFactory sessionFactory;
    private ServiceRegistry serviceRegistry;

    private static SQLiteConnector sqLiteConnector;

    private SQLiteConnector() {

        Configuration configuration = new Configuration();
        configuration.configure();
        Properties properties = configuration.getProperties();

        serviceRegistry = new ServiceRegistryBuilder().applySettings(properties).buildServiceRegistry();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    }

    public static SQLiteConnector getInstance() {
        if (sqLiteConnector == null) {
            sqLiteConnector = new SQLiteConnector();
        }
        return sqLiteConnector;
    }

    public void createRequest(BankRequest request) {
        AuditData auditData = new AuditData( ActionTypes.SENDING_REQUEST,
                OperationTypes.CREATING, new Date());
        saveAuditData(auditData);

        Session session = null;
        Transaction tx = null;

        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();

            session.save(request);

            session.flush();
            tx.commit();

            auditData = new AuditData( ActionTypes.RECEIVING_RESPONSE,
                    OperationTypes.CREATING, new Date());
            saveAuditData(auditData);
        } catch (Exception ex) {
            ex.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
            throw ex;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void editRequest(BankRequest request) {
        AuditData auditData = new AuditData( ActionTypes.SENDING_REQUEST,
                OperationTypes.EDITING, new Date());
        saveAuditData(auditData);

        Session session = null;
        Transaction tx = null;

        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();

            BankRequest loadedRequest = (BankRequest) session.load(BankRequest.class, request.getId());
            loadedRequest.setStatus(request.getStatus());
            loadedRequest.setLastChanged(new Date());
            session.update(loadedRequest);

            session.flush();
            tx.commit();

            auditData = new AuditData( ActionTypes.RECEIVING_RESPONSE,
                    OperationTypes.EDITING, new Date());
            saveAuditData(auditData);

        } catch (Exception ex) {
            ex.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void withdrawRequest(BankRequest request) {

        AuditData auditData = new AuditData( ActionTypes.SENDING_REQUEST,
                OperationTypes.WITHDRAWAL, new Date());
        saveAuditData(auditData);

        Session session = null;
        Transaction tx = null;

        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();

            BankRequest loadedRequest = (BankRequest) session.load(BankRequest.class, request.getId());
            loadedRequest.setStatus(RequestStatuses.WITHDRAWN);
            loadedRequest.setLastChanged(new Date());
            session.update(loadedRequest);

            session.flush();
            tx.commit();

            auditData = new AuditData( ActionTypes.RECEIVING_RESPONSE,
                    OperationTypes.WITHDRAWAL, new Date());
            saveAuditData(auditData);


        } catch (Exception ex) {
            ex.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public List<BankRequest> getRequestsByFilter(HashMap<String, String> params) {

        AuditData auditData = new AuditData( ActionTypes.SENDING_REQUEST,
                OperationTypes.FILTERING, new Date());

        saveAuditData(auditData);

        if (params.size() == 0) {
            return new ArrayList<>();
        }

        List<BankRequest> tableData;
        Session session = null;
        Transaction tx = null;

        try
        {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
            Criteria criteria = session.createCriteria(BankRequest.class);

            for (Map.Entry entry : params.entrySet()) {
                //                           (Столбец,                значение)
                criteria.add(Restrictions.eq((String) entry.getKey(), entry.getValue()));
            }

            tableData = criteria.list();
            session.flush();
            tx.commit();

            auditData = new AuditData( ActionTypes.RECEIVING_RESPONSE, OperationTypes.FILTERING, new Date());
            saveAuditData(auditData);

        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            tableData = new ArrayList<>();
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return tableData;
    }

    public BankRequest getRequestById(Integer id) {
        Session session = null;
        Transaction tx = null;
        BankRequest request = null;

        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();

            request = (BankRequest) session.load(BankRequest.class, id);
            request.toString();

            session.flush();
            tx.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return request;
    }

    private void saveAuditData(AuditData auditData) {
        Session session = null;
        Transaction tx = null;

        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();

            session.save(auditData);

            session.flush();
            tx.commit();
        } catch (Exception ex) {
            ex.printStackTrace();

            if (tx != null) {
                tx.rollback();
            }

            throw ex;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}

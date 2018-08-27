package com.sample;

import com.sample.model.BankRequest;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import java.text.SimpleDateFormat;
import java.util.*;

public class SQLiteConnector {

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private SessionFactory sessionFactory = null;
    private ServiceRegistry serviceRegistry = null;

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

        Session session = null;
        Transaction tx = null;

        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();

            session.save(request);

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
    }

    public void editRequest(BankRequest request) {

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

        Session session = null;
        Transaction tx = null;

        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();

            BankRequest loadedRequest = (BankRequest) session.load(BankRequest.class, request.getId());
            loadedRequest.setStatus("Отозвана");
            loadedRequest.setLastChanged(new Date());
            session.update(loadedRequest);

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
    }

    public List<BankRequest> getTableData() {
        List<BankRequest> tableData;
        Session session = null;

        try {
            session = sessionFactory.openSession();
            tableData = session.createCriteria(BankRequest.class).list();
            session.flush();
        } catch (Exception e) {
            e.printStackTrace();
            tableData = new ArrayList<>();
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return tableData;
    }

//    public List<BankRequest> getWeatherByFilter(HashMap<String, String> params) {
//        List<BankRequest> tableData;
//        Session session = null;
//
//        try
//        {
//            session = sessionFactory.openSession();
//            Criteria criteria = session.createCriteria(BankRequest.class);
//
//            for (Map.Entry entry : params.entrySet()) {
//
//                if (entry.getValue())
//
//                criteria.add(Restrictions.eq((String) entry.getKey(), entry.getValue()));
//
//                System.out.println("Key: " + entry.getKey() + " Value: "
//                        + entry.getValue());
//            }
//
//
//            if (startDateStr.length() != 0) {
//                Date startDate = formatter.parse(startDateStr + " 00:00:00");
//                criteria.add(Restrictions.ge("date", startDate));
//            }
//            if (endDateStr.length() != 0) {
//                Date endDate = formatter.parse(endDateStr + " 23:59:59");
//                criteria.add(Restrictions.le("date", endDate));
//            }
//            if (!city.equals("none")) {
//                criteria.add(Restrictions.eq("city", city));
//            }
//            tableData = criteria.list();
//            session.flush();
//        } catch (Exception e) {
//            e.printStackTrace();
//            tableData = new ArrayList<>();
//        } finally {
//            if (session != null) {
//                session.close();
//            }
//        }
//
//        return tableData;
//    }
}

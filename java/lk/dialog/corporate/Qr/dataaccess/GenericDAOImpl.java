/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

/**
 * Implementation class for GenericDAO interface. All other specific DAO Implementation classes in our application will
 * be extends this class to use this class features.
 *
 * @author Dewmini
 * @version 2.1
 */
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;

public abstract class GenericDAOImpl<T, ID extends Serializable> implements GenericDAO<T, ID> {

    protected Session getSession() {
        return HibernateUtil.getSession();
    }

    public void save(Object object) {
        Session hibernateSession = this.getSession();
        hibernateSession.saveOrUpdate(object);
        hibernateSession.flush();
    }

    public void update(T entity) {
        Session hibernateSession = this.getSession();
        hibernateSession.update(entity);
        hibernateSession.flush();
    }

    public void delete(T entity) {
        Session hibernateSession = this.getSession();
        hibernateSession.delete(entity);
        hibernateSession.flush();
    }

    public List<T> findMany(Query query) {
        List<T> t;
        t = (List<T>) query.list();
        return t;
    }

    public T findOne(Query query) {
        T t;
        t = (T) query.uniqueResult();
        return t;
    }

    public T findByID(Class clazz, Number id) {
        Session hibernateSession = this.getSession();
        T t = null;
        t = (T) hibernateSession.get(clazz, id);
        return t;
    }

    public List findAll(Class clazz) {
        Session hibernateSession = this.getSession();
        List T = null;
        Query query = hibernateSession.createQuery("from " + clazz.getName());
        T = query.list();
        return T;
    }
}
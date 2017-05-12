/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

/**
 * This was implemented as a good practice.i.e. generic DAO that will be used from all of our specific DAO’s since
 * some operations are common among all DAO’s. Our generic DAO will implement methods like save, merge, delete, findAll,
 * findByID, findMany and findOne.
 *
 * @author Dewmini
 * @version 2.1
 */
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import org.hibernate.Query;

public interface GenericDAO<T, ID extends Serializable> {

    public void save(T entity);

    public void update(T entity);

    public void delete(T entity);

    public List<T> findMany(Query query);

    public T findOne(Query query);

    public List findAll(Class clazz);

    public T findByID(Class clazz, Number id);
}
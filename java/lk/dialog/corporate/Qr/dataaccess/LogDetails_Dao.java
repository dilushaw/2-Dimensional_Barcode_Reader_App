package lk.dialog.corporate.Qr.dataaccess;

import java.util.ArrayList;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Anuradha
 */
public class LogDetails_Dao {

    Transaction tx = null;
    Session session = null;

    /*
     * start tranaction
     */
    public void startTransaction() {


        try {
            if (session == null || !session.isOpen()) {
                HibernateSession hib_sess = new HibernateSession();
                session = hib_sess.getSession();
            }
            tx = session.beginTransaction();
        } catch (HibernateException e) {
            //logger.error(e.toString());

        } catch (RuntimeException e) {
            //logger.error(e.toString());
        }

    }
    /*
     * insert data for table
     */

    public void saveTableRow(Object o) {
        try {
            session.save(o);
        } catch (HibernateException e) {
            tx.rollback();
            //logger.error(e.toString());
        }
    }
    /*
     * commit tranaction
     */

    public void commitTransaction() {
        try {
            tx.commit();
        } catch (HibernateException e) {
            // logger.error(e.toString());
        }

    }

    public void updateTableRow(Object o) {
        try {

            session.update(o);

        } catch (HibernateException e) {
            tx.rollback();
            // logger.error(e.toString());
        }
    }

    /*
     * get role types t
     *
     */
    public String getRoleType(String username) {
        String role_type = null;
        ArrayList<String> role_type_list = null;


        startTransaction();

        Query q1 = session.createQuery("select user.role.roleType from User user where user.userName='" + username + "'");

        role_type_list = (ArrayList<String>) q1.list();
        role_type = role_type_list.get(0);

        return role_type;


    }
}

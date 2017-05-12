package lk.dialog.corporate.Qr.dataaccess;

import java.util.ArrayList;
import lk.dialog.corporate.Qr.data.User;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Anuradha
 */
public class ValidateUser {

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
    /*
     * chaek user validity
     */

    public boolean isValidUser(String userName, String passwaord) {
        boolean is_validate_user = false;
        ArrayList<User> user = null;
        startTransaction();

        Query q1 = session.createQuery("select  user from User user where user.userName='" + userName + "'");
        user = (ArrayList<User>) q1.list();
        commitTransaction();

        try {

            if (user == null || user.size() < 0) {
                is_validate_user = false;
            } else if (user != null && user.size() > 0) {
                if (user.get(0).getPassword().equals(passwaord)) {
                    is_validate_user = true;
                } else {
                    is_validate_user = false;
                }

            }

        } catch (Exception e) {
            is_validate_user = false;
        }

        return is_validate_user;

    }
}

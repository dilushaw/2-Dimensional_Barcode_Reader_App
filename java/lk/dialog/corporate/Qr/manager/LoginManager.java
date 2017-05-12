package lk.dialog.corporate.Qr.manager;

import lk.dialog.corporate.Qr.data.Corporate;
import lk.dialog.corporate.Qr.data.User;
import lk.dialog.corporate.Qr.dataaccess.UserDAO;
import lk.dialog.corporate.Qr.dataaccess.UserDAOImpl;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import javax.persistence.NonUniqueResultException;
import lk.dialog.corporate.Qr.exception.QrException;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Provide user login related functionalities. Initial class in the manager package. 
 *
 * @author Dewmini
 * @version 2.1
 */
@Component
public class LoginManager {

    static Logger log = Logger.getLogger(LoginManager.class);
    //private UserDAO userDAO = new UserDAOImpl();
    @Autowired
    UserDAO userDAO;

    /**
     * According to the user credentials find the user login to the system. added by Dewmini
     *
     * @param userName - entered by user at login form
     * @param password - entered by user at login form
     * @param account - entered by user at login form
     * @return - return a User object matching above parameters, returns null if user doesn't exists
     * @throws QrException
     */
    public User findUser(String userName, String password, String account) throws QrException {
        User user = null;
        try {
            HibernateUtil.beginTransaction();
            user = userDAO.findLoginUser(userName, password, account);
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {
            log.error("Query returned more than one results.", ex);
            HibernateUtil.rollbackTransaction();
            throw new QrException("NonUniqueResultException occured", ex);
        } catch (HibernateException ex) {
            log.error("Handle your error here", ex);
            HibernateUtil.rollbackTransaction();
            throw new QrException("HibernateException occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return user;
    }
}

/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

import java.util.List;
import lk.dialog.corporate.Qr.data.Privileges;
import lk.dialog.corporate.Qr.data.User;
import lk.dialog.corporate.Qr.data.UserPrivileges;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.springframework.stereotype.Component;

/**
 * deals with user privileges table
 *
 * @author Dilusha
 * @version 2.0
 */
@Component
public class UserPrivilegesDAOImpl extends GenericDAOImpl<UserPrivileges, Long> implements UserPrivilegesDAO {

    /**
     * save records in user privileges table,added by Dilusha
     *
     * @param userPrivileges
     */
    public void saveinUserPrivileges(UserPrivileges userPrivileges) {
        save(userPrivileges);
    }

    /**
     * delete user privileges of a particular user,added by Dilusha
     *
     * @param user
     */
    public void deleteUserPrivilegesByUserID(User user) {
        try {
            List<UserPrivileges> userPrivileges = this.findUserPrivilegesByUser(user);

            for (int i = 0; i < userPrivileges.size(); i++) {

                delete(userPrivileges.get(i));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * finds user privileges of a user,added by Dilusha
     *
     * @param user
     * @return list of user privileges
     */
    public List<UserPrivileges> findUserPrivilegesByUser(User user) {
        List<UserPrivileges> userPrivilegesList = null;
        try {


            String sql = "FROM UserPrivileges u WHERE u.user = :user ";

            Query query = HibernateUtil.getSession().createQuery(sql).setParameter("user", user);
            userPrivilegesList = findMany(query);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return userPrivilegesList;
    }
}

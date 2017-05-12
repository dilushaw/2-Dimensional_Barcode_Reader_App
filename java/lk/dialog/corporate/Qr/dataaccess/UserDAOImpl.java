/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

import java.math.BigDecimal;
import java.util.List;
import lk.dialog.corporate.Qr.data.*;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.springframework.stereotype.Component;

/**
 * This class is to communicate with the database regarding the user entity
 *
 * @author Dewmini
 * @version 2.0
 */
@Component
public class UserDAOImpl extends GenericDAOImpl<User, Long> implements UserDAO {

    /**
     * If name and email is known it will find the matching user.
     *
     * @param name - name of the user
     * @param email - email of the user
     * @return
     */
    public User findByNameAndEmail(String name, String email) {
        User user = null;
        String sql = "FROM User u WHERE u.name = :name AND u.email = :email";
        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("name", name).setParameter("email", email);
        user = findOne(query);
        return user;
    }

    /**
     * When user login in to the system by using the user entered information it will find such user can be found on the
     * system. (added by Dewmini)
     *
     * @param userName - username entered
     * @param password - password entered
     * @param account - account entered
     * @return - User object matching above parameters; ow=null(such user doesn't exists)
     */
    public User findLoginUser(String userName, String password, String account) {
        User user = null;
        String sql = "FROM User u WHERE u.userName = :userName AND u.password = :password AND u.corporate.corporateAccount = :accNo";
        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("userName", userName).setParameter("password", password).setParameter("accNo", account);
        user = findOne(query);
        if (user != null) {
            Hibernate.initialize(user.getRole());
        }
        return user;
    }

//added by dilusha
    public void saveinUser(User user) {
        save(user);
    }

    //added by dilusha
    public void deleteUserByCorp(Corporate corporate) {
        try {
            List<User> users = this.getUserByCorpID(corporate);

            for (int i = 0; i < users.size(); i++) {

                delete(users.get(i));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //added by dilusha
    public User findCorpSuperAdminByCoporateAcct(String account) {
        User superAdmin = null;
        String hql = "From User u where u.corporate.corporateAccount =:account and u.role.roleId=:roleId ";

        Query query = HibernateUtil.getSession().createQuery(hql).setParameter("account", account).setParameter("roleId", "4");

        superAdmin = findOne(query);

        return superAdmin;
    }

    //added by dilusha
    public User findUserByCorpIDandUsername(Corporate corporate, String userName) {
        User superAdmin = null;
        String hql = "From User u where u.corporate = :corporate and u.userName=:userName ";

        Query query = HibernateUtil.getSession().createQuery(hql).setParameter("corporate", corporate).setParameter("userName", userName);

        superAdmin = findOne(query);

        return superAdmin;
    }

//added by dilusha
    public List<User> getUserByCorpID(Corporate corporate) {
        List<User> users = null;
        String sql = "FROM User u WHERE u.corporate = :corporate";

        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("corporate", corporate);
        users = findMany(query);
        return users;
    }

    public void updateUser(User user) {
        update(user);
    }

    /**
     * For a given corporate return the user list of specified role for the given status of them. added by Dewmini
     *
     * @param coporateId - ID of the corporate
     * @param roleId - role of the user type to be return
     * @param status - status of the users i.e. 1=active, 0=inactive, -1=active and inactive
     * @return - list of users matching the parameters given
     */
    public List<User> findCorporateUsersByCorporateAndRole(Integer coporateId, String roleId, Integer status) {
        List<User> users;
        String hql = "From User u where u.corporate.corporateId=:id and u.role.roleId=:roleId and u.userStatus";
        if (status == -1) {
            hql += " in(0,1)";
        } else {
            hql += " in(" + status + ")";
        }
        Query query = HibernateUtil.getSession().createQuery(hql).setParameter("id", coporateId).setParameter("roleId", roleId);
        users = findMany(query);
        //admins =(List<User>)query.list(); 
        //Hibernate.initialize(admins); 
        return users;
    }

    /**
     * Find corporate users when corporate id is given
     *
     * @param coporateId - corporate id
     * @return - list of users
     */
    public List<User> findCorporateUsersByCorporate(Integer coporateId) {
        String hql = "From User u where u.corporate.corporateId=:id";
        Query query = HibernateUtil.getSession().createQuery(hql).setParameter("id", coporateId);
        List<User> admins = (List<User>) query.list();
        //Hibernate.initialize(admins); 
        return admins;
    }

    /**
     * This will return Corporate Super Admin (CSA) for the given corporate - applicable when there is only one
     * CSA.(method name findAdminsByCoporateId better to be change as findCSAdminsByCoporateId) added by dewmini
     *
     * @param coporateId
     * @return User This method returns Corporate Super Admin User created by DSA (assuming there is only one CSA
     * exists)
     */
    public User findAdminsByCoporateId(Integer coporateId) {

        User admin = null;
        String hql = "From User u where u.corporate.corporateId=:id and u.role.roleId=:roleId";
        Query query = HibernateUtil.getSession().createQuery(hql).setParameter("id", coporateId).setParameter("roleId", "4");
        //List<User> admins = (List<User>)query.list();
        admin = findOne(query);
        //Hibernate.initialize(admins);     
        return admin;

    }

    /**
     * As username is unique only within the corporate the unique key here is the combination of corporateAccount and
     * username. So this will be useful to find the user, when we know the username and the corporate account he belongs
     * to. As an example when we can get those values from the current session we can get the current user logged in to
     * the system.(added by Dewmini)
     *
     * @param userName -username of the user
     * @param account - corporate account
     * @return - User object matching above parameters.
     */
    public User findUserByUserNameAndAccount(String userName, String account) {
        User user = null;
        String hql = "From User u where u.userName=:userName and u.corporate.corporateAccount=:account";
        Query query = HibernateUtil.getSession().createQuery(hql).setParameter("userName", userName).setParameter("account", account);
        user = findOne(query);
        return user;
    }

    /**
     * provide users list for the given corporate id and users status by executing hibernate query. If user status,
     *
     * -1 = means active and inactive users, 0 = means active user only, 1 = means inactive users only (added by
     * Dewmini)
     *
     * @param corporateId - Unique id to identify corporate.
     * @param userStatus - User's current status
     * @return List of users for given corporate id and status
     */
    public List<User> findUsersByCorporateAndStatus(Integer corporateId, Integer userStatus) {
        List<User> users;
        String hql = "From User u where u.corporate.corporateId=:id and u.userStatus";
        if (userStatus == -1) {
            hql += " in(0,1)";
        } else {
            hql += " in(" + userStatus + ")";
        }
        Query query = HibernateUtil.getSession().createQuery(hql).setParameter("id", corporateId);
        users = findMany(query);
        //admins =(List<User>)query.list(); 
        //Hibernate.initialize(admins); 
        return users;
    }
}

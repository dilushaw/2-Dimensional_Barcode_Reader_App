/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

import java.util.List;
import lk.dialog.corporate.Qr.data.Corporate;
import lk.dialog.corporate.Qr.data.User;

/**
 * This is to communicate with the database regarding the user entity
 *
 * @author Dewmini
 * @version 2.1
 */
public interface UserDAO extends GenericDAO<User, Long> {

    User findByNameAndEmail(String name, String email);//added by dewmini

    User findLoginUser(String userName, String password, String account);//added by dewmini

    User findUserByCorpIDandUsername(Corporate corporate, String userName);//added by dilusha

    void saveinUser(User user); //added by dilusha

    void deleteUserByCorp(Corporate corporate);//added by dilusha

    User findCorpSuperAdminByCoporateAcct(String account);//added by dilusha

    List<User> getUserByCorpID(Corporate corporate);//added by dilusha

    List<User> findCorporateUsersByCorporate(Integer coporateId);//added by dilusha

    void updateUser(User user); //added by dilusha

    List<User> findCorporateUsersByCorporateAndRole(Integer coporateId, String roleId, Integer status);//added by dewmini

    User findAdminsByCoporateId(Integer coporateId);//added by dewmini

    User findUserByUserNameAndAccount(String userName, String account);//added by dewmini

    List<User> findUsersByCorporateAndStatus(Integer corporateId, Integer userStatus);//added by dewmini
}

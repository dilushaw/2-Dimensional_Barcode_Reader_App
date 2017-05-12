/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

import java.util.List;
import lk.dialog.corporate.Qr.data.User;
import lk.dialog.corporate.Qr.data.UserPrivileges;

/**
 *
 * @author Dilusha
 * @version 2.0
 */
public interface UserPrivilegesDAO extends GenericDAO<UserPrivileges, Long>{
    void saveinUserPrivileges(UserPrivileges userPrivileges);
    void deleteUserPrivilegesByUserID(User user);
    List<UserPrivileges> findUserPrivilegesByUser(User user);
}

/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

import java.util.List;
import lk.dialog.corporate.Qr.data.Privileges;

/**
 *
 * @author Dilusha
 * @version 2.0
 */
public interface PrivilegesDAO extends GenericDAO<Privileges, Long> {

    Privileges findPrivilegeByName(String privilegeName);

    Privileges findPrivilegeById(int privilegeId);

    List<Privileges> findPrivilegesByRoleId(String roleId);//added by dewmini

    List<Privileges> findPrivilegesByUserId(Long userId);//added by dewmini
}

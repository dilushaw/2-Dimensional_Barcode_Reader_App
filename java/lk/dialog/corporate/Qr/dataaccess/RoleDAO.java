/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

import lk.dialog.corporate.Qr.data.Role;
import lk.dialog.corporate.Qr.exception.QrException;

/**
 * communicate with DB regarding role entity
 *
 * @author Dewmini
 * @version 2.1
 */
public interface RoleDAO extends GenericDAO<Role, String> {

    public Role findRoleByID(String roleId) throws QrException;
}

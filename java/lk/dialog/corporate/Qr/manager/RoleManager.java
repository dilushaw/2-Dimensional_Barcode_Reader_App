/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.manager;

import lk.dialog.corporate.Qr.data.Role;
import lk.dialog.corporate.Qr.exception.QrException;

/**
 *
 * @author Dewmini
 */
public interface RoleManager {
    public Role findRoleByID(String roleId) throws QrException;
}

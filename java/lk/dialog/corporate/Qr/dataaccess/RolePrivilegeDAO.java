/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.dataaccess;

import java.util.List;
import lk.dialog.corporate.Qr.data.Privileges;
import lk.dialog.corporate.Qr.data.Role;
import lk.dialog.corporate.Qr.data.RolePrivilege;

/**
 *
 * @author Dilusha
 * @version 2.0
 */
public interface RolePrivilegeDAO extends GenericDAO<RolePrivilege, Long> {
   List<RolePrivilege> findRolePrivilegeByRoleid(Role role);  
}

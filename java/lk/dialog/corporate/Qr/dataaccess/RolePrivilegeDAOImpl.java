/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.dataaccess;

import java.util.List;
import lk.dialog.corporate.Qr.data.Privileges;
import lk.dialog.corporate.Qr.data.Role;
import lk.dialog.corporate.Qr.data.RolePrivilege;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import org.hibernate.Query;
import org.springframework.stereotype.Component;

/**
 * deals with role privileges table
 *
 * @author Dilusha
 * @version 2.0
 */
@Component
public class RolePrivilegeDAOImpl extends GenericDAOImpl<RolePrivilege, Long> implements RolePrivilegeDAO {

    /**
     * find the role privilege by Role ,added by Dilusha
     *
     * @param role role object
     * @return
     */
    public List<RolePrivilege> findRolePrivilegeByRoleid(Role role) {

        List<RolePrivilege> roleprivilege;

        String sql = "Select rolePrivilege FROM RolePrivilege rolePrivilege WHERE rolePrivilege.role = :role";

        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("role", role);
        roleprivilege = findMany(query);

        return roleprivilege;
    }
}

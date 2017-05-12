/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.dataaccess;

import lk.dialog.corporate.Qr.data.Role;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import org.hibernate.Query;
import org.springframework.stereotype.Component;

/**
 * Communicate with DB regarding the role entity
 *
 * @author Dewmini
 * @version 2.1
 */
@Component
public class RoleDAOImpl extends GenericDAOImpl<Role, String> implements RoleDAO {

    /**
     * find the role details for particular role id
     *
     * @param roleId - unique id of the role
     * @return - Role object matching the given role id
     */
    public Role findRoleByID(String roleId) {
        Role role = null;
        String hql = "From Role r where r.roleId=:id";
        Query query = HibernateUtil.getSession().createQuery(hql).setParameter("id", roleId);
        role = findOne(query);
        return role;
    }
}

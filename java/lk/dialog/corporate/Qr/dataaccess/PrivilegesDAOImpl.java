/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

import java.util.List;
import lk.dialog.corporate.Qr.data.Privileges;
import lk.dialog.corporate.Qr.data.UserPrivileges;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import org.hibernate.Query;
import org.springframework.stereotype.Component;

/**
 * handles privileges transactions
 *
 * @author Dilusha
 * @version 2.0
 */
@Component
public class PrivilegesDAOImpl extends GenericDAOImpl<Privileges, Long> implements PrivilegesDAO {

    /**
     * find the privileges information by privilege name,added by Dilusha
     *
     * @param privilegeName
     * @return
     */
    public Privileges findPrivilegeByName(String privilegeName) {

        Privileges privilege;

        String sql = "Select p FROM Privileges p WHERE p.privilegeName = :privilegeName";

        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("privilegeName", privilegeName);
        privilege = findOne(query);

        return privilege;
    }

    /**
     * find the privileges information by privilege id,added by Dilusha
     *
     * @param privilegeName
     * @return
     */
    public Privileges findPrivilegeById(int privilegeId) {

        Privileges privilege;

        String sql = "Select p FROM Privileges p WHERE p.privilegeId = :privilegeId";

        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("privilegeId", privilegeId);
        privilege = findOne(query);

        return privilege;
    }

    /**
     * Find privileges available for the particular role. added by Dewmini
     *
     * @param roleId - unique id of the role
     * @return - List<Privileges>
     */
    public List<Privileges> findPrivilegesByRoleId(String roleId) {
        String hql = "select r.privileges FROM RolePrivilege r where r.role.roleId=:roleId";
        Query query = HibernateUtil.getSession().createQuery(hql).setParameter("roleId", roleId);
        List<Privileges> rolePriv = findMany(query);
        return rolePriv;
    }

    /**
     * Find privileges assigned for the particular user. added by Dewmini
     *
     * @param userId - unique id of the user
     * @return - List<Privileges>
     */
    public List<Privileges> findPrivilegesByUserId(Long userId) {
        String hql = "select up.privileges FROM UserPrivileges up where up.user.userId=:userId";
        Query query = HibernateUtil.getSession().createQuery(hql).setParameter("userId", userId);
        List<Privileges> userPriv = findMany(query);
        return userPriv;

    }
}

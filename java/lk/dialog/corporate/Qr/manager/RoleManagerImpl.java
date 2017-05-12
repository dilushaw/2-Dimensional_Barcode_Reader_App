/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.manager;

import javax.persistence.NonUniqueResultException;
import lk.dialog.corporate.Qr.data.Corporate;
import lk.dialog.corporate.Qr.data.Role;
import lk.dialog.corporate.Qr.dataaccess.RoleDAO;
import lk.dialog.corporate.Qr.exception.QrException;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Dewmini
 */
@Component
public class RoleManagerImpl implements RoleManager{
    
    @Autowired
    RoleDAO roleDAO;
    
    public Role findRoleByID(String roleId) throws QrException{
        Role role = null;
        try {
            HibernateUtil.beginTransaction();
            role = roleDAO.findRoleByID(roleId);
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate exception occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return role;
    }
}

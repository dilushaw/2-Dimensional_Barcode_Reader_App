/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

import java.util.List;
import lk.dialog.corporate.Qr.data.*;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

/**
 * this class is used to handle data access object level processes of corporate,in corporate table
 *
 * @author Dilusha
 * @version 2.0
 */
@Component
public class CorporateDAOImpl extends GenericDAOImpl<Corporate, Long> implements CorporateDAO {

    /**
     * to save a corporate in a database once created,added by Dilusha
     *
     * @param corporate corporate object
     */
    public void saveinCorporate(Corporate corporate) {
        save(corporate);

    }

    /**
     * corporate modifications are saved,added by Dilusha
     *
     * @param corporate
     */
    public void updateinCorporate(Corporate corporate) {
        update(corporate);


    }

    /**
     * This will return all the registered of Corporate except for the delete corporate (status=3,Deleted) stored in
     * database. added by dewmini
     *
     * @return - List<Corporate>
     */
    public List<Corporate> loadAllCorporates() {
        //Corporate corporate = null;
        String sql = "FROM Corporate c WHERE c.corporateStatus != :status and c.corporateAccount not in (:account1,:account2)";
        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("status", 3).setParameter("account1", "dialog").setParameter("account2", "default");
        //List<Corporate> allcorporates = findAll(Corporate.class);
        List<Corporate> allcorporates = findMany(query);
        return allcorporates;
    }

    /**
     * Find the particular corporate when account is given (account is unique within the system). added by Dewmini
     *
     * @param account - corporate account
     * @return - Corporate object matching the given account
     */
    public Corporate findCorporate(String account) {
        Corporate corporate = null;
        String sql = "FROM Corporate c WHERE c.corporateAccount = :account";
        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("account", account);

        corporate = findOne(query);
        return corporate;
    }

    /**
     * get all the corporate according to status predefined. if it is want to find deleted(status=3), expired(status=2)
     * corporate it should be changed the hql accordingly. added by Dewmini -1 = active & inactive, 1=active, 0=inactive
     *
     * @param corpStatus - Status of the corporate
     * @return - List<Corporate>
     */
    public List<Corporate> loadAllCorporatesByStatus(Integer corpStatus) {
        String hql = "FROM Corporate c WHERE c.corporateAccount not in (:account1,:account2) and  c.corporateStatus";
        if (corpStatus == -1) {
            hql += " in(0,1)";
        } else {
            hql += " in(" + corpStatus + ")";
        }
        Query query = HibernateUtil.getSession().createQuery(hql).setParameter("account1", "dialog").setParameter("account2", "default");

        List<Corporate> allcorporates = findMany(query);
        return allcorporates;
    }
}

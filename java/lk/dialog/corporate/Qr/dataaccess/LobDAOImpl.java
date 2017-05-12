/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

import java.util.List;
import lk.dialog.corporate.Qr.data.Corporate;
import lk.dialog.corporate.Qr.data.LobGroup;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import org.hibernate.Query;
import org.springframework.stereotype.Component;

/**
 * Implementation of LobDAO Interface; communicate regarding to the LobGroup entity
 *
 * @author Hasala
 * @version 2.1
 */
@Component
public class LobDAOImpl extends GenericDAOImpl<LobGroup, Integer> implements LobDAO {

    public void saveLob(LobGroup lob) {
        save(lob);
    }

    /**
     * output all LOBs for a given corporate id. added by Dewmini
     *
     * @param corporateId - unique id of the corporate
     * @return - List<LobGroup>
     */
    public List<LobGroup> findLobByCorporateId(Integer corporateId) {
        List<LobGroup> lob = null;
        String hql = "From LobGroup l where l.corporate.corporateId=:id";
        Query query = HibernateUtil.getSession().createQuery(hql).setParameter("id", corporateId);
        lob = findMany(query);
        return lob;
    }

    /**
     * Find the lob when lob name and corporate are given (corporate - particular lob name is search within this given
     * corporate)
     *
     * @param corporate - Corporate object
     * @param lobName - name of the lob
     * @return - LobGroup
     */
    public LobGroup findLobByCorpIDandLobName(Corporate corporate, String lobName) {
        LobGroup lob = null;
        String hql = "From LobGroup l where l.corporate = :corporate and l.lobName=:lobName ";
        Query query = HibernateUtil.getSession().createQuery(hql).setParameter("corporate", corporate).setParameter("lobName", lobName);
        lob = findOne(query);
        return lob;
    }
}

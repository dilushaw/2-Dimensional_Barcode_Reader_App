/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.manager;

import java.util.*;
import javax.persistence.NonUniqueResultException;
import lk.dialog.corporate.Qr.data.Campaign;
import lk.dialog.corporate.Qr.data.Corporate;
import lk.dialog.corporate.Qr.data.LobGroup;
import lk.dialog.corporate.Qr.dataaccess.CampaignDAO;
import lk.dialog.corporate.Qr.dataaccess.LobDAO;
import lk.dialog.corporate.Qr.dataaccess.LobDAOImpl;
import lk.dialog.corporate.Qr.exception.QrException;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import lk.dialog.corporate.Qr.utils.QrConstants;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Hasala
 */
@Component
public class LobManagerImpl implements LobManager {

    @Autowired
    LobDAO lobDAO;
    @Autowired
    CampaignDAO campDAO;

    public void saveLob(LobGroup lobGroup) throws QrException {
        try {
            HibernateUtil.beginTransaction();
            //LobDAO lobDAO = new LobDAOImpl();
            lobDAO.saveLob(lobGroup);
            HibernateUtil.commitTransaction();
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("HibernateException occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    public List<LobGroup> findLobByCorporateId(Integer corporateId) throws QrException {
        List<LobGroup> lob = null;

        try {
            HibernateUtil.beginTransaction();
            lob = lobDAO.findLobByCorporateId(corporateId);
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
        return lob;
    }

    public LobGroup findLobByLobId(Integer lobId) throws QrException {
        LobGroup lob = null;
        try {
            HibernateUtil.beginTransaction();
            lob = lobDAO.findByID(LobGroup.class, lobId);

            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate exception occured", ex);
        }catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return lob;
    }

    //display corporateDashboard (LOBs and Campaigns))
    //campaign status(0,1) - all active and incative campaigns showtogether
    public Map<?, ?>[] findLobsAndCampaignsByCorproateId(Integer corporateId) throws QrException {
        try{
        Map<LobGroup, List<Campaign>> map = new TreeMap<LobGroup, List<Campaign>>();
        Map<Integer, Integer> barcodeCount = new HashMap<Integer, Integer>();
        Map<?, ?>[] arr = new Map<?, ?>[2];
        HibernateUtil.beginTransaction();
        List<LobGroup> lobs = lobDAO.findLobByCorporateId(corporateId);
        for (LobGroup lob : lobs) {
            List<Campaign> campaigns = campDAO.findCampaignsByLob(lob.getLobId(), QrConstants.ACTIVE_AND_INACTIVE);
            Integer count = prepareBarcodeCount(campaigns);
            barcodeCount.put(lob.getLobId(), count);
            map.put(lob, campaigns);
        }
        arr[0] = map;
        arr[1] = barcodeCount;
        HibernateUtil.commitTransaction();
        return arr;
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate exception occured", ex);
        }catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    //this method was used when filter by campaign name/ status
    //diplay corporateDashboard according to filter criteria
    //campaign status(0/1/(0,1)) - all active/incative/(active,inactive) campaigns showtogether
    public Map<?, ?>[] findLobsAndCampaignsByCorproateId(Integer corporateId, String campaignName, String status) throws QrException {
        try{
        Map<LobGroup, List<Campaign>> map = new TreeMap<LobGroup, List<Campaign>>();
        Map<Integer, Integer> barcodeCount = new HashMap<Integer, Integer>();
        Map<?, ?>[] arr = new Map<?, ?>[2];
        HibernateUtil.beginTransaction();
        List<LobGroup> lobs = lobDAO.findLobByCorporateId(corporateId);
        //  Collections.sort(lobs);
        for (LobGroup lob : lobs) {
            List<Campaign> campaigns = campDAO.findCampaignsByLobNameStatus(lob.getLobId(), campaignName, status);
            if (campaigns != null && !campaigns.isEmpty()) {
                Integer count = prepareBarcodeCount(campaigns);
                barcodeCount.put(lob.getLobId(), count);
                map.put(lob, campaigns);
            }
        }

        arr[0] = map;
        arr[1] = barcodeCount;

        HibernateUtil.commitTransaction();
        return arr;
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate exception occured", ex);
        }catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    public LobGroup findLobByCorpIDandLobName(Corporate corporate, String lobName) throws QrException {

        LobGroup lobGroup = null;
        try {
            HibernateUtil.beginTransaction();
            lobGroup = lobDAO.findLobByCorpIDandLobName(corporate, lobName);
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate exception occured", ex);
        }catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return lobGroup;
    }

    private Integer prepareBarcodeCount(List<Campaign> campaigns) {
        Integer count = 0;
        for (Campaign camp : campaigns) {
            count += camp.getBarcodes().size();
        }
        return count;
    }
}

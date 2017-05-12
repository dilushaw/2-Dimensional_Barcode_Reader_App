/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.manager;

import lk.dialog.corporate.Qr.data.User;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.NonUniqueResultException;
import lk.dialog.corporate.Qr.data.*;
import lk.dialog.corporate.Qr.dataaccess.*;
import lk.dialog.corporate.Qr.dto.CreateCampaignDTO;
import lk.dialog.corporate.Qr.exception.QrException;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import lk.dialog.corporate.Qr.utils.QrConstants;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Hasala
 */
@Component
public class CampaignManagerImpl implements CampaignManager {

    @Autowired
    private CampaignDAO campaignDAO;
    @Autowired
    private UserCampaignDAO userCampaignDAO;

    public void saveCampaign(Campaign campaign) throws QrException {
        try {
            HibernateUtil.beginTransaction();
            //CampaignDAO campaignDAO = new CampaignDAOImpl();
            campaignDAO.saveCampaign(campaign);
            HibernateUtil.commitTransaction();
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("HibernateException occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    public void updateCampaign(CreateCampaignDTO editDTO) throws QrException {
        try {
            Campaign campaign = this.findCampaignByCampaignId(editDTO.getCampaignId());
            HibernateUtil.beginTransaction();
            campaign.setCampaignName(editDTO.getCampaignName());
            DateFormat formatter = new SimpleDateFormat(QrConstants.DATEFORMAT);
            campaign.setStartDate((Date) formatter.parse(editDTO.getStartDate()));
            campaign.setExpireDate((Date) formatter.parse(editDTO.getEndDate()));
            campaign.setCampaignStatus(editDTO.getCampaignStatus());
            LobGroup lob = new LobGroup();
            lob.setLobId(editDTO.getLobId());
            lob.setLobName(editDTO.getLobName());
            campaign.setLobGroup(lob);
            campaignDAO.updateCampaign(campaign);
            HibernateUtil.commitTransaction();
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate exception occured", ex);
        } catch (ParseException ex) {
            HibernateUtil.rollbackTransaction();
            
            throw new QrException("ParseException occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            
            throw new QrException("Exception occured", ex);
        }
    }

    //added by dewmini
    public List<Campaign> findCampaignsByUserId(Long userId, Integer status) throws QrException {
        List<Campaign> campaigns = null;
        try {
            HibernateUtil.beginTransaction();
            campaigns = campaignDAO.findCampaignsByUserId(userId, status);
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
        return campaigns;
    }

    //added by Hasala
    public List<Campaign> findCampaignsByLob(Integer lobId, Integer status) throws QrException {
        List<Campaign> campaigns = null;
        try {
            HibernateUtil.beginTransaction();
            campaigns = campaignDAO.findCampaignsByLob(lobId, status);
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
        return campaigns;
    }

    //added by Hasala
    public Campaign findCampaignByCampaignId(Integer campaignId) throws QrException {
        Campaign campaign = null;
        try {
            HibernateUtil.beginTransaction();
            campaign = campaignDAO.findCampaignByCampaignId(campaignId);
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
        return campaign;
    }

    public void deleteUserCampaignsByUser(User user) throws QrException {
        try {
            HibernateUtil.beginTransaction();
            userCampaignDAO.deleteUserCampaignsByUser(user);
            HibernateUtil.commitTransaction();
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate exception occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    public Campaign findCampaignByLobIdAndCampaign(LobGroup lobgroup, String campaignName, String status) throws QrException {

        Campaign campaign = null;
        try {
            HibernateUtil.beginTransaction();
            campaign = campaignDAO.findCampaignByLobIdAndCampaignName(lobgroup, campaignName, status);
            HibernateUtil.commitTransaction();

        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("HibernateException occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return campaign;
    }

    public void updateCampaign(Campaign campign) throws QrException {
        try {
            HibernateUtil.beginTransaction();
            campaignDAO.update(campign);
            HibernateUtil.commitTransaction();
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("HibernateException occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    public void updateExpiredCampaign(Campaign campign, int status) throws QrException {
        try {
            HibernateUtil.beginTransaction();
            campaignDAO.update(campign);
            Campaign campaign = campaignDAO.findCampaignByCampaignId(campign.getCampaignId());
            campaign.setCampaignStatus(status);
            campaignDAO.updateCampaign(campaign);
            HibernateUtil.commitTransaction();
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("HibernateException occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    public List<Campaign> loadAllCampaigns() throws QrException {
        List<Campaign> campaigns = null;
        try {
            HibernateUtil.beginTransaction();
            campaigns = campaignDAO.loadCampaigns();
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {
            throw new QrException("Hibernate exception occured", ex);
        }catch (Exception ex) {
            throw new QrException("Exception occured", ex);
        }
        return campaigns;
    }

    public List<Campaign> findCampaignByNameAndLob(String campaignName, int lobId) throws QrException {
        List<Campaign> campaign = null;
        try {
            HibernateUtil.beginTransaction();
            campaign = campaignDAO.findCampaignByNameAndLob(campaignName, lobId);
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
        return campaign;
    }
}

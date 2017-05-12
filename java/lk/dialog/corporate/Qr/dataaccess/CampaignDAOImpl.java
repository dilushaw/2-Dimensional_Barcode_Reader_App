/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

import java.util.List;
import lk.dialog.corporate.Qr.data.Campaign;
import lk.dialog.corporate.Qr.data.Corporate;
import lk.dialog.corporate.Qr.data.LobGroup;
import lk.dialog.corporate.Qr.data.Privileges;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.springframework.stereotype.Component;

/**
 * Communicate with data base regarding campaign entity using hibernate.
 *
 * @author Hasala
 */
@Component
public class CampaignDAOImpl extends GenericDAOImpl<Campaign, Long> implements CampaignDAO {

    public void saveCampaign(Campaign campaign) {
        save(campaign);
    }

    public void updateCampaign(Campaign campaign) {
        update(campaign);
    }

    /**
     * Find the campaign(s) assigned for a particular user according to the given status of the campaign. status->
     * -1=active and inactive, 1=active, 0=inactive (added by Dewmini)
     *
     * @param userId - id of the user
     * @param status - status of the campaign
     * @return - List of campaigns
     */
    public List<Campaign> findCampaignsByUserId(Long userId, Integer status) {
        List<Campaign> campaign = null;
        String hql = "select uc.campaign FROM UserCampaign uc where uc.user.userId=:userId and uc.campaign.campaignStatus";
        //String hql = "select upriv.privilages from User u inner join fetch u.UserPrivileges as upriv where u.userId=:userId";
        if (status == -1) {
            hql += " in(0,1)";
        } else {
            hql += " in(" + status + ")";
        }
        Query query = HibernateUtil.getSession().createQuery(hql).setParameter("userId", userId);
        campaign = findMany(query);
        return campaign;
    }

    /**
     * Find campaigns belongs to particular LOb group according to status predefined. status-> -1=active and inactive,
     * 1=active, 0=inactive added by Hasala
     *
     * @param lobId - id of the LOB Group
     * @param status - Status of the campaign
     * @return - List of Campaigns
     */
    public List<Campaign> findCampaignsByLob(Integer lobId, Integer status) {
        List<Campaign> campaigns = null;
        // String hql = "select uc.campaign FROM Campaign uc where uc.campaign.lobId=:lobId and uc.campaign.campaignStatus=:campStatus";
        String hql = "FROM Campaign cam where cam.lobGroup.lobId=" + lobId + " and (cam.campaignStatus=";
        if (status == -1) {
            hql += "0 or cam.campaignStatus=1)";
        } else {
            hql += status + ")";
        }

        //String hql = "select upriv.privilages from User u inner join fetch u.UserPrivileges as upriv where u.userId=:userId";
        Query query = HibernateUtil.getSession().createQuery(hql);
        campaigns = findMany(query);

        return campaigns;
    }

    /**
     * When we want to find particular campaign details, when the campaign id is known. added by Hasala
     *
     * @param campaignId - id of the campaign
     * @return - Campaign object matching the id
     */
    public Campaign findCampaignByCampaignId(Integer campaignId) {
        Campaign campaign = null;
        String hql = "FROM Campaign cam where cam.campaignId=" + campaignId;
        Query query = HibernateUtil.getSession().createQuery(hql);
        campaign = findOne(query);
        return campaign;
    }

    /**
     * When all or part of the campaign name is known and according to campaign predefined status it will find the
     * matching campaigns for a particular lob group
     *
     * @param lobId - id of the lob group
     * @param campaignName - part/whole name of the campaign
     * @param status - status of the campaign
     * @return - list of campaigns
     */
    public List<Campaign> findCampaignsByLobNameStatus(Integer lobId, String campaignName, String status) {
        List<Campaign> campaigns = null;
        int intStatus = Integer.parseInt(status);
        // String hql = "select uc.campaign FROM Campaign uc where uc.campaign.lobId=:lobId and uc.campaign.campaignStatus=:campStatus";
        String hql = "FROM Campaign cam where cam.lobGroup.lobId=" + lobId + " and cam.campaignName like '%" + campaignName + "%' and (cam.campaignStatus=";
        if (intStatus == -1) {
            hql += "0 or cam.campaignStatus=1)";
        } else {
            hql += status + ")";
        }

        Query query = HibernateUtil.getSession().createQuery(hql);
        campaigns = findMany(query);

        return campaigns;
    }

    public Campaign findCampaignByLobIdAndCampaignName(LobGroup lobGroup, String campaignName, String status) {

        Campaign campaign;

        //String hql ="FROM Campaign cam where cam.lobGroup.lobId=:lobId and cam.campaignName=:campaignName";
        String hql = "FROM Campaign cam where cam.lobGroup=" + lobGroup + " and cam.campaignName=" + campaignName;

        // Query query = HibernateUtil.getSession().createQuery(hql).setParameter("lobGroup.lobId", lobId).setParameter("campaignName", campaignName);
        Query query = HibernateUtil.getSession().createQuery(hql);
        campaign = findOne(query);
        return campaign;

    }

    public List<Campaign> loadCampaigns() {
        List<Campaign> campaigns = null;
        int status = 1;
        String sql = "FROM Campaigns cam WHERE cam.campaignStatus = :status";
        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("status", status);

        campaigns = findMany(query);
        return campaigns;
    }

    /**
     * Find campaign given campaign name within particular LOB. eg. To check campaign name exists. added by Dewmini
     *
     * @param campaignName - name of the campaign
     * @param lobId - id of the lob group
     * @return - List of campaigns matching above parameters
     */
    public List<Campaign> findCampaignByNameAndLob(String campaignName, int lobId) {
        List<Campaign> campaign = null;
        String sql = "FROM Campaign cam WHERE cam.campaignName = :campaignName and cam.lobGroup.lobId = :lobId";
        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("campaignName", campaignName).setParameter("lobId", lobId);

        campaign = findMany(query);
        return campaign;
    }
}

/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

import java.util.List;
import lk.dialog.corporate.Qr.data.Campaign;
import lk.dialog.corporate.Qr.data.LobGroup;

/**
 *
 * @author Hasala
 */
public interface CampaignDAO extends GenericDAO<Campaign, Long> {

    void saveCampaign(Campaign campaign);

    void updateCampaign(Campaign campaign);

    List<Campaign> findCampaignsByUserId(Long userId, Integer status);//added by dewmini

    List<Campaign> findCampaignsByLob(Integer lobId, Integer status);

    Campaign findCampaignByCampaignId(Integer campaignId); //added by Hasala

    List<Campaign> findCampaignsByLobNameStatus(Integer lobId, String campaignName, String status);//added by dewmini

    Campaign findCampaignByLobIdAndCampaignName(LobGroup lobGroup, String campaignName, String status);

    List<Campaign> loadCampaigns();

    List<Campaign> findCampaignByNameAndLob(String campaignName, int lobId);//added by dewmini
}

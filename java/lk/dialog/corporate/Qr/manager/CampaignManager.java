/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.manager;

import lk.dialog.corporate.Qr.data.User;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import lk.dialog.corporate.Qr.data.Campaign;
import lk.dialog.corporate.Qr.data.LobGroup;
import lk.dialog.corporate.Qr.dto.CreateCampaignDTO;
import lk.dialog.corporate.Qr.exception.QrException;

/**
 *
 * @author Hasala
 */
public interface CampaignManager {
    void saveCampaign(Campaign campaign) throws QrException;

    void updateCampaign (CreateCampaignDTO editDTO) throws QrException; //added by Hasala
    void updateCampaign (Campaign campign) throws QrException; //added by Dewmini
    public void updateExpiredCampaign(Campaign campign,int status) throws QrException;//added by dilusha
    public Campaign findCampaignByCampaignId (Integer campaignId) throws QrException; //added by Hasala
    public void deleteUserCampaignsByUser(User user) throws QrException; //added by Hasala

    public List<Campaign> findCampaignsByUserId(Long userId,Integer status)  throws QrException;//added by dewmini
    public List<Campaign> findCampaignsByLob(Integer lobId,Integer status) throws QrException;
    public Campaign findCampaignByLobIdAndCampaign(LobGroup lobgroup, String campaignName, String status) throws QrException;
    public List<Campaign> loadAllCampaigns() throws QrException;

    public List<Campaign> findCampaignByNameAndLob(String campaignName, int lobId) throws QrException;//added by dewmini
}

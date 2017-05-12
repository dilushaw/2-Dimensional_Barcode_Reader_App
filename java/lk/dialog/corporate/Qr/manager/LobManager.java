/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.manager;

import java.util.List;
import java.util.Map;
import lk.dialog.corporate.Qr.data.Campaign;
import lk.dialog.corporate.Qr.data.Corporate;
import lk.dialog.corporate.Qr.data.LobGroup;
import lk.dialog.corporate.Qr.exception.QrException;

/**
 *
 * @author Hasala
 */
public interface LobManager {
    void saveLob (LobGroup lobGroup) throws QrException;
    List<LobGroup> findLobByCorporateId(Integer corporateId) throws QrException;
    LobGroup findLobByLobId(Integer lobId) throws QrException;    
    Map<?,?> [] findLobsAndCampaignsByCorproateId(Integer corporateId) throws QrException;//added by dewmini

    public Map<?,?> [] findLobsAndCampaignsByCorproateId(Integer corporateId, String campaignName, String status) throws QrException;
    public LobGroup findLobByCorpIDandLobName(Corporate corporate, String lobName) throws QrException;
}

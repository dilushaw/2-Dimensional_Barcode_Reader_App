/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

import java.util.List;
import lk.dialog.corporate.Qr.data.Corporate;
import lk.dialog.corporate.Qr.data.LobGroup;
/**
 *
 * @author Hasala
 */
public interface LobDAO extends GenericDAO<LobGroup, Integer> {
    
    public void saveLob(LobGroup lob);
    public List<LobGroup> findLobByCorporateId(Integer corporateId);//added by dewmini
    public LobGroup findLobByCorpIDandLobName(Corporate corporate, String lobName);

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.manager;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lk.dialog.corporate.Qr.data.Campaign;
import lk.dialog.corporate.Qr.data.Privileges;
import lk.dialog.corporate.Qr.data.User;
import lk.dialog.corporate.Qr.data.UserPrivileges;
import lk.dialog.corporate.Qr.exception.QrException;

/**
 * This interface provide user management related functionality for the
 * implementation.
 *
 * @author Dewmini
 */
public interface UserManager {
    
    List<UserPrivileges> loadUserPrivileges(Number id) throws QrException;
    List<UserPrivileges> loadDefaultPrivileges(String roleType) throws QrException;
    void assignPriviledgesToUser(Number userId,List<Privileges> privileges) throws QrException;
    User loadLoginUser(User user)throws QrException;//added by dewmini
    public User findUserByID(Number id) throws QrException;//added by dewmini
    public User findUserByUserNameAndAccount(String userName, String account) throws QrException;//added by dewmini

     void prepareAndSetPrivilages(Integer[] privileges, User user) throws QrException;
     Privileges findPriviledgeById(Integer id) throws QrException;
     
     void prepareAndSetCampaigns(Integer[] campaigns, User user) throws QrException;
     Campaign findCampaignById(Integer id) throws QrException;

    public Map<String, String> prparePriviForSecurity(Set<UserPrivileges> userPrivilegeses);
    
    public void updateUser(User user) throws QrException;//added by dewmini
    public void setCUCreatedCampaignForCU(Campaign campaign, User user) throws QrException; //added by Hasala
}
 
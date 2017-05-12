/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.manager;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lk.dialog.corporate.Qr.data.*;
import lk.dialog.corporate.Qr.dto.CoporateAdminCreationDTO;
import lk.dialog.corporate.Qr.dto.CoporateCreationDTO;
import lk.dialog.corporate.Qr.dto.CreateUserDTO;
import lk.dialog.corporate.Qr.dto.UserProfileDTO;
import lk.dialog.corporate.Qr.exception.QrException;

/**
 *This interface is used to handle corporate based transactions,called by Controller methods
 * @author Dilusha
 * @version 2.0
 */
public interface CorporateManager {

    void saveCorporate(Corporate corporate) throws QrException;

    void updateCorporate(CoporateCreationDTO corporateCreation) throws QrException;

    void updateCorporate(Corporate corporateold, int status) throws QrException;

    void updateCorporateandUserStatus(Corporate corporateold, int status) throws QrException;

    void updateCorpSuperadminUser(CoporateCreationDTO corporateCreation) throws QrException;

    // add Set<UserPrivileges> userPrivi param
    void updateCorpadminUser(CoporateAdminCreationDTO corporateAdminCreation, Corporate oldCorporate, String userName, Set<UserPrivileges> userPriviSet) throws QrException;

    void updateuserProfile(UserProfileDTO userProfile, Corporate oldCorporate, String userName) throws QrException;

    void updatePassword(UserProfileDTO userProfile, String account, String userName) throws QrException;

    void saveUser(User user) throws QrException;

    void saveUserPrivileges(UserPrivileges userPrivileges) throws QrException;

    Privileges getPrivileges(String privilege) throws QrException;

    Privileges getPrivileges(int privilegeId) throws QrException;

    List<RolePrivilege> getRolePrivilege(Role role) throws QrException;

    Corporate checkCorporateAccountExist(String accountName) throws QrException;

    void deleteCorpInUserPrivileges(User user) throws QrException;

    void deleteCorpInUser(Corporate corporate) throws QrException;

    void deleteCorpInCorporate(Corporate corporate) throws QrException;

    void deleteUser(User user) throws QrException;

    User findUserByCorpIDandUserName(Corporate corporate, String userName) throws QrException;

    List<User> getUserByCorpID(Corporate corporate) throws QrException;

    Corporate findCorporateByAccount(String account) throws QrException;

    List<User> findCorporateUsersByCorporate(Integer coporateId) throws QrException;

    List<Corporate> loadCorporateDashboard() throws QrException;//added by dewmini

    List<Corporate> findAllCorporatesByStatus(Integer corpStatus) throws QrException;//added by dewmini

    /*
     * this method is to get user count for each corprate. By passing the status
     * parameters for both corporate and users you can get the customizes output
     * corpStatus=1,userStatus =1 -All active users for active corporate
     * corpStatus=1,userStatus =0 -All inactive users for active corporate
     * corpStatus=1,userStatus =-1 -All active&inctive users for active
     * corporate corpStatus=0,userStatus =-1 -All active&inctive users for
     * inactive corporate corpStatus=-1,userStatus =-1 -All active&inctive users
     * for active&inactive corporate etc. Map-key = corporate_id, Map-value =
     * usercount
     */
    Map<Integer, Integer> findCorporateUserCount(Integer corpStatus, Integer userStatus) throws QrException;//added by dewmini

    Corporate findCorporateByID(Number id) throws QrException;//added by dewmini

    List<User> findCorporateUsersByCorporateAndRole(Integer valueOf, String roleId, Integer status) throws QrException;//added by dewmini-romove later

    User findAdminsByCoporateId(Integer valueOf) throws QrException;//added by dewmini

    List<Privileges> findPrivilegesByRoleId(String roleId) throws QrException;

    List<Privileges> findPrivilegesByUserId(Long userId) throws QrException;

    void updateCorparateUser(CreateUserDTO corporateAdminCreation, Corporate oldCorporate, String userName, Role role) throws QrException;
}

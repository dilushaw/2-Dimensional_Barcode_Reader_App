/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.manager;

import controllers.CampaignController;
import controllers.CorporateController;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.NonUniqueResultException;
import lk.dialog.corporate.Qr.data.*;
import lk.dialog.corporate.Qr.dataaccess.*;
import lk.dialog.corporate.Qr.dto.CoporateAdminCreationDTO;
import lk.dialog.corporate.Qr.dto.CoporateCreationDTO;
import lk.dialog.corporate.Qr.dto.CreateUserDTO;
import lk.dialog.corporate.Qr.dto.UserProfileDTO;
import lk.dialog.corporate.Qr.exception.QrException;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import lk.dialog.corporate.Qr.utils.QrConstants;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * to manage corporate based transactions
 *
 * @author Dilusha
 * @version 2.0
 */
@Component
public class CorporateManagerImpl implements CorporateManager {

    static Logger log = Logger.getLogger(CorporateManager.class);
    @Autowired
    private CorporateDAO corporateDAO;
    @Autowired
    private PrivilegesDAO privilegesDAO;
    @Autowired
    private UserPrivilegesDAO userPrivilegesDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private RolePrivilegeDAO rolePrivilegeDAO;

    /**
     * save a corporate in database once it is created,added by Dilusha
     *
     * @param corporate corporate object
     * @throws QrException
     */
    public void saveCorporate(Corporate corporate) throws QrException {
        try {
            HibernateUtil.beginTransaction();
            corporateDAO.saveinCorporate(corporate);
            HibernateUtil.commitTransaction();
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    public void deleteUser(User user) throws QrException {
        try {
            HibernateUtil.beginTransaction();

            userDAO.delete(user);

            HibernateUtil.commitTransaction();
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    /**
     * to update a corporate,added by Dilusha
     *
     * @param corporateCreation DTO object
     * @throws QrException
     */
    public void updateCorporate(CoporateCreationDTO corporateCreation) throws QrException {


        try {
            HibernateUtil.beginTransaction();
            log.debug("2 st dt: " + corporateCreation.getLicenseStartDate());
            log.debug("2 end dt: " + corporateCreation.getLicenseEndDate());
            Corporate corporate = corporateDAO.findCorporate(corporateCreation.getCorporateAccount());
            corporate.setAddress(corporateCreation.getAddress());
            corporate.setContactName(corporateCreation.getContactName());
            corporate.setCorporateAccount(corporateCreation.getCorporateAccount());
            corporate.setCorporateName(corporateCreation.getCorporateName());
            if (corporateCreation.getCorporateStatus().equalsIgnoreCase("Disable")) {
                corporate.setCorporateStatus(0);
            } else {
                corporate.setCorporateStatus(1);
            }

            DateFormat formatter = new SimpleDateFormat(QrConstants.DATEFORMAT);

            if (corporateCreation.getLicenseStartDate() != "") {
                corporate.setLicenseStartDate(formatter.parse(corporateCreation.getLicenseStartDate()));
                log.debug("3 st dt: " + formatter.parse(corporateCreation.getLicenseStartDate()));
            }
            if (corporateCreation.getLicenseEndDate() != "") {
                corporate.setLicenseEndDate(formatter.parse(corporateCreation.getLicenseEndDate()));
                log.debug("3 end dt: " + formatter.parse(corporateCreation.getLicenseEndDate()));
            }
            corporate.setDescription(corporateCreation.getDescription());
            corporate.setEmail(corporateCreation.getEmail());
            corporate.setFax(corporateCreation.getFax());
            corporate.setTelephone(corporateCreation.getTelephone());
            corporateDAO.updateinCorporate(corporate);
            HibernateUtil.commitTransaction();
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    //this method is not being used currently
    public void updateCorporate(Corporate corporateold, int status) throws QrException {

        try {
            HibernateUtil.beginTransaction();
            Corporate corporate = corporateDAO.findCorporate(corporateold.getCorporateAccount());
            corporate.setCorporateStatus(status);
            corporateDAO.updateinCorporate(corporate);
            HibernateUtil.commitTransaction();
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    /**
     * used to update the corporate table and user status in user table,added by Dilusha
     *
     * @param corporateold previously created corporate object
     * @param status modified status of a user
     * @throws QrException
     */
    public void updateCorporateandUserStatus(Corporate corporateold, int status) throws QrException {


        try {
            HibernateUtil.beginTransaction();
            // in oredr to modify a record it should be done inside a same transaction
            Corporate corporate = corporateDAO.findCorporate(corporateold.getCorporateAccount());
            List<User> users = userDAO.getUserByCorpID(corporate);
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                user.setUserStatus(status);
                userDAO.updateUser(user);
            }
            corporate.setCorporateStatus(status);
            corporateDAO.updateinCorporate(corporate);
            HibernateUtil.commitTransaction();
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    /**
     * update corporate super admin account,added by Dilusha
     *
     * @param corporateCreation
     * @throws QrException
     */
    public void updateCorpSuperadminUser(CoporateCreationDTO corporateCreation) throws QrException {
        try {
            HibernateUtil.beginTransaction();

            Corporate corporate = corporateDAO.findCorporate(corporateCreation.getCorporateAccount());
            User user = userDAO.findCorpSuperAdminByCoporateAcct(corporateCreation.getCorporateAccount());
            user.setDescription(corporateCreation.getDescription());
            user.setEmail(corporateCreation.getEmail());

            user.setPassword(user.getPassword());
            user.setMobile(corporateCreation.getTelephone());

            user.setName(corporateCreation.getContactName());
            //this should be auto created
            if (corporateCreation.getCorporateStatus().equalsIgnoreCase("Disable")) {
                user.setUserStatus(0);
            } else {
                user.setUserStatus(1);
            }


            user.setCorporate(corporate);
            user.setUserName(corporateCreation.getUserName());

            userDAO.updateUser(user);
            HibernateUtil.commitTransaction();
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    /**
     * user profile modification,added by Dilusha
     *
     * @param userProfile DTO
     * @param oldCorporate corporate of the user
     * @param userName
     * @throws QrException
     */
    public void updateuserProfile(UserProfileDTO userProfile, Corporate oldCorporate, String userName) throws QrException {
        try {
            HibernateUtil.beginTransaction();

            Corporate corporate = corporateDAO.findCorporate(userProfile.getCorporateAccount());
            User user = userDAO.findUserByCorpIDandUsername(oldCorporate, userName);
            user.setDescription(userProfile.getDescription());
            user.setEmail(userProfile.getEmail());

            //in database mobile name should changed to telephone

            user.setMobile(userProfile.getTelephone());

            user.setName(userProfile.getContactName());

            user.setCorporate(corporate);
            user.setRole(user.getRole());

            user.setUserName(userProfile.getUserName());

            userDAO.updateUser(user);
            HibernateUtil.commitTransaction();
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    /**
     * password change in user profile,added by Dilusha
     *
     * @param userProfile DTO
     * @param account corporate account of the user
     * @param userName
     * @throws QrException
     */
    public void updatePassword(UserProfileDTO userProfile, String account, String userName) throws QrException {
        try {
            HibernateUtil.beginTransaction();



            User user = userDAO.findUserByUserNameAndAccount(userName, account);
            user.setPassword(userProfile.getNewpassword());

            user.setRole(user.getRole());
            userDAO.updateUser(user);
            HibernateUtil.commitTransaction();
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    // add Set<UserPrivileges> userPrivi param
    public void updateCorpadminUser(CoporateAdminCreationDTO corporateAdminCreation, Corporate oldCorporate, String userName, Set<UserPrivileges> userPrivi) throws QrException {
        try {
            HibernateUtil.beginTransaction();

            Corporate corporate = corporateDAO.findCorporate(corporateAdminCreation.getCorporateAccount());
            User user = userDAO.findUserByCorpIDandUsername(oldCorporate, userName);
            user.setDescription(corporateAdminCreation.getDescription());
            user.setEmail(corporateAdminCreation.getEmail());

            //in database mobile name should changed to telephone
            user.setPassword(user.getPassword());
            user.setMobile(corporateAdminCreation.getTelephone());

            user.setName(corporateAdminCreation.getContactName());


            if (corporateAdminCreation.getCorporateStatus().equalsIgnoreCase("Disable")) {
                user.setUserStatus(0);
            } else {
                user.setUserStatus(1);
            }
            user.setCorporate(corporate);



            user.setUserName(corporateAdminCreation.getUserName());
            user.setUserPrivilegeses(userPrivi); // new set userprivilages before save
            userDAO.updateUser(user);

            HibernateUtil.commitTransaction();
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    public void saveUser(User user) throws QrException {
        try {
            HibernateUtil.beginTransaction();
            userDAO.saveinUser(user);
            HibernateUtil.commitTransaction();
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    /**
     * handles saving user privileges in user privileges table,added by Dilusha
     *
     * @param userPrivileges
     * @throws QrException
     */
    public void saveUserPrivileges(UserPrivileges userPrivileges) throws QrException {
        try {
            HibernateUtil.beginTransaction();
            userPrivilegesDAO.saveinUserPrivileges(userPrivileges);
            HibernateUtil.commitTransaction();
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    public User findUserByCorpIDandUserName(Corporate corporate, String username) throws QrException {
        User user = null;
        try {
            HibernateUtil.beginTransaction();
            user = userDAO.findUserByCorpIDandUsername(corporate, username);
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("NonUniqueResultException occured", ex);
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return user;
    }

    /**
     * get relevant privileges information by privilege name,added by Dilusha
     *
     * @param privilegeName
     * @return Privilege record in privilege table
     * @throws QrException
     */
    public Privileges getPrivileges(String privilegeName) throws QrException {
        Privileges privilege = null;

        try {
            HibernateUtil.beginTransaction();

            privilege = privilegesDAO.findPrivilegeByName(privilegeName);
            HibernateUtil.commitTransaction();


        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }


        return privilege;
    }

    /**
     * get privileges by privilege ID,added by Dilusha
     *
     * @param privilegeId
     * @return Privilege record in privilege table
     * @throws QrException
     */
    public Privileges getPrivileges(int privilegeId) throws QrException {
        Privileges privilege = null;

        try {

            HibernateUtil.beginTransaction();

            privilege = privilegesDAO.findPrivilegeById(privilegeId);
            HibernateUtil.commitTransaction();


        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }


        return privilege;
    }

    /**
     * get role privileges by Role,in roleprivileges table,added by Dilusha
     *
     * @param role object
     * @return list of roleprivileges records
     * @throws QrException
     */
    public List< RolePrivilege> getRolePrivilege(Role role) throws QrException {
        List< RolePrivilege> roleprivilege = null;

        try {

            HibernateUtil.beginTransaction();

            roleprivilege = rolePrivilegeDAO.findRolePrivilegeByRoleid(role);
            HibernateUtil.commitTransaction();


        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }


        return roleprivilege;
    }

    /**
     * check whether a corporate account is exists in the corporate table,added by Dilusha
     *
     * @param accountName
     * @return corporate object
     * @throws QrException
     */
    public Corporate checkCorporateAccountExist(String accountName) throws QrException {
        Corporate corporate = null;
        try {

            HibernateUtil.beginTransaction();

            corporate = corporateDAO.findCorporate(accountName);

            HibernateUtil.commitTransaction();


        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }

        return corporate;
    }

    /**
     * when editing a corporate user,user privileges can also be modified and first already assigned user
     * privileges,added by Dilusha should be deleted
     *
     * @param user user to be edited
     * @throws QrException
     */
    public void deleteCorpInUserPrivileges(User user) throws QrException {

        try {
            HibernateUtil.beginTransaction();

            userPrivilegesDAO.deleteUserPrivilegesByUserID(user);

            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("NonUniqueResultException occured", ex);
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }
//currently not in use

    public void deleteCorpInUser(Corporate corporate) throws QrException {
        try {
            HibernateUtil.beginTransaction();
            userDAO.deleteUserByCorp(corporate);
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("NonUniqueResult Exception occured", ex);
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    /**
     * not in use,but can be used if requirement changes,for now record is not deleted,but flag is set,added by Dilusha
     *
     * @param corporate
     * @throws QrException
     */
    public void deleteCorpInCorporate(Corporate corporate) throws QrException {
        try {
            HibernateUtil.beginTransaction();
            corporateDAO.delete(corporate);
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("NonUniqueResult Exception occured", ex);
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    /**
     * get the list of users who are in a corporate,added by Dilusha
     *
     * @param corporate corporate object
     * @return list of users
     * @throws QrException
     */
    public List<User> getUserByCorpID(Corporate corporate) throws QrException {

        List<User> users = null;
        try {
            HibernateUtil.beginTransaction();
            users = userDAO.getUserByCorpID(corporate);
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("NonUniqueResult Exception occured", ex);
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return users;

    }

    /**
     * Method used to get all the corporate to display in DSA Dashboard - But no longer using for that purpose.
     * (corporate for the DSA dashboard are get from another method.)added by Dewmini
     *
     * @return - List of corporate
     * @throws QrException
     */
    public List<Corporate> loadCorporateDashboard() throws QrException {

        List<Corporate> allCorporates = null;
        try {
            HibernateUtil.beginTransaction();
            allCorporates = corporateDAO.loadAllCorporates();
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return allCorporates;
    }

    /**
     * once account is known corporate can be found,added by Dilusha
     *
     * @param account
     * @return relevant account
     * @throws QrException
     */
    public Corporate findCorporateByAccount(String account) throws QrException {

        Corporate corporate = null;
        try {
            HibernateUtil.beginTransaction();
            corporate = corporateDAO.findCorporate(account);
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return corporate;
    }

    /**
     * once the corporate id is known all the users under that corporate can be found out,added by Dilusha
     *
     * @param coporateId
     * @return list of users under a corporate
     * @throws QrException
     */
    public List<User> findCorporateUsersByCorporate(Integer coporateId) throws QrException {
        List<User> users = null;
        try {
            HibernateUtil.beginTransaction();
            users = userDAO.findCorporateUsersByCorporate(coporateId);
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return users;
    }

    /**
     * added by dewmini This will return Corporate object for the given coporate id stored in database.
     *
     * @param id - Corporate Id
     * @return Corporate
     * @throws QrException
     */
    public Corporate findCorporateByID(Number id) throws QrException {

        Corporate corporate = null;
        try {
            HibernateUtil.beginTransaction();
            corporate = corporateDAO.findByID(Corporate.class, id);
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return corporate;
    }

    //added by dewmini
    // refer UserDAOImpl - public List<User> findCorporateUsersByCoporateIdAndRoleId(Integer coporateId)
    public List<User> findCorporateUsersByCorporateAndRole(Integer coporateId, String roleId, Integer status) throws QrException {
        List<User> users = null;
        try {
            HibernateUtil.beginTransaction();
            users = userDAO.findCorporateUsersByCorporateAndRole(coporateId, roleId, status);
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return users;
    }

    //added by dewmini
    public User findAdminsByCoporateId(Integer coporateId) throws QrException {
        User user = null;
        try {
            HibernateUtil.beginTransaction();
            user = userDAO.findAdminsByCoporateId(coporateId);
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return user;
    }

    //added by dewmini
    public List<Privileges> findPrivilegesByRoleId(String roleId) throws QrException {
        List<Privileges> rp = null;
        try {
            HibernateUtil.beginTransaction();

            rp = privilegesDAO.findPrivilegesByRoleId(roleId);
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return rp;
    }

    /**
     * find all the privileges assigned to a particular user,added by Dilusha
     *
     * @param userId
     * @return list of all the assigned privileges
     * @throws QrException
     */
    public List<Privileges> findPrivilegesByUserId(Long userId) throws QrException {
        List<Privileges> up = null;
        try {
            HibernateUtil.beginTransaction();

            up = privilegesDAO.findPrivilegesByUserId(userId);
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return up;
    }

    /**
     *
     * @param updateUserDTO
     * @param oldCorporate
     * @param userName
     * @param role
     * @throws QrException
     */
    public void updateCorparateUser(CreateUserDTO updateUserDTO, Corporate oldCorporate, String userName, Role role) throws QrException {
        try {
            HibernateUtil.beginTransaction();

            User user = userDAO.findUserByCorpIDandUsername(oldCorporate, userName);
            user.setDescription(updateUserDTO.getDescription());
            user.setEmail(updateUserDTO.getEmail());

            user.setPassword(user.getPassword());
            user.setMobile(updateUserDTO.getMobile());
            user.setName(updateUserDTO.getFullName());
            user.setUserStatus(updateUserDTO.getStatus());
            user.setCorporate(oldCorporate);
            user.setRole(role);
            user.setUserName(updateUserDTO.getUserName());
            userDAO.updateUser(user);
            HibernateUtil.commitTransaction();

        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
    }

    public List<Corporate> findAllCorporatesByStatus(Integer corpStatus) throws QrException {
        List<Corporate> corporates = null;
        try {
            HibernateUtil.beginTransaction();
            corporates = corporateDAO.loadAllCorporatesByStatus(corpStatus);
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return corporates;
    }

    //Map<CorporateId,UserCount> returns the map of corporate id and relevant user count for the corporate
    public Map<Integer, Integer> findCorporateUserCount(Integer corpStatus, Integer userStatus) throws QrException {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        try {

            HibernateUtil.beginTransaction();
            List<Corporate> corporate = corporateDAO.loadAllCorporatesByStatus(corpStatus);
            for (Corporate cop : corporate) {
                List<User> users = userDAO.findUsersByCorporateAndStatus(cop.getCorporateId(), userStatus);
                if (users != null && !users.isEmpty()) {
                    map.put(cop.getCorporateId(), users.size());
                }
            }
            HibernateUtil.commitTransaction();
        } catch (HibernateException ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate Exception occured", ex);
        } catch (Exception ex) {

            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return map;
    }
}

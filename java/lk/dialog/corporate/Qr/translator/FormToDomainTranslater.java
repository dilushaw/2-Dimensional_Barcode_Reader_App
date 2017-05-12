/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.translator;

import controllers.CampaignController;
import controllers.CorporateController;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import lk.dialog.corporate.Qr.data.*;
import lk.dialog.corporate.Qr.dto.*;
import lk.dialog.corporate.Qr.utils.QrConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Dewmini
 */
@Component
public class FormToDomainTranslater {

    private Vector<Object> corperateCreationVector;
    private Vector<UserPrivileges> UserPrivilegesVector;

    public User translateLoginFormToUser(UserLoginDTO loginForm) {

        User user = new User();

        user.setUserName(loginForm.getUserName());
        user.setPassword(loginForm.getPassword());
        // bellow line added by
        user.setCorporate(new Corporate());
        user.getCorporate().setCorporateAccount(loginForm.getAccount());
        //String account = loginForm.getAccount();

        return user;

    }

    /**
     * corporate creation form is handled by setting proper values from form to
     * the relevant objects,added by Dilusha
     *
     * @param corporateDTO DTO object which contains all the information
     * @param privileges assigned user privileges,for a CSA all the privileges
     * are assigned
     * @param role role type of the user
     * @return all the relevant table record objects which should be entered
     * once a corporate is created
     */
    public Vector<Object> translateCorporateCreationForm(CoporateCreationDTO corporateDTO, Vector<Privileges> privileges, Role role) {
        corperateCreationVector = new Vector<Object>();
        Corporate corporate = this.handleCorporateBean(corporateDTO);
        corperateCreationVector.add(corporate);
        User user;
        user = this.handleUserBean(corporateDTO, corporate, role);
        corperateCreationVector.add(user);
        Vector<UserPrivileges> userPrivileges = this.HandleUserPrivilegeBean(privileges, user);
        for (int i = 0; i < userPrivileges.size(); i++) {
            corperateCreationVector.add(userPrivileges.get(i));
        }

        return corperateCreationVector;
    }

    /**
     * corporate admin creation form is handled by setting proper values from
     * form to the relevant objects,added by Dilusha
     *
     * @param corporateAdminDTO DTO object which contains all the information
     * @param privileges assigned privileges to the CA
     * @param corporate corporate name of that CA
     * @param role role type
     * @return all the relevant table record objects which should be entered
     * once a corporate admin is created
     */
    public Vector<Object> translateCorporateAdminCreationForm(CoporateAdminCreationDTO corporateAdminDTO, Vector<Privileges> privileges, Corporate corporate, Role role) {
        corperateCreationVector = new Vector<Object>();

        corperateCreationVector.add(corporate);
        User user;
        user = this.handleUserBean(corporateAdminDTO, corporate, role);
        corperateCreationVector.add(user);
        Vector<UserPrivileges> userPrivileges = this.HandleUserPrivilegeBean(privileges, user);
        for (int i = 0; i < userPrivileges.size(); i++) {
            corperateCreationVector.add(userPrivileges.get(i));
        }

        return corperateCreationVector;
    }

    /**
     * handles assigning values to the corporate object once a corporate is
     * created,added by Dilusha
     *
     * @param corporateDTO
     * @return created corporate
     */
    private Corporate handleCorporateBean(CoporateCreationDTO corporateDTO) {
        Corporate corporate = new Corporate();
        corporate.setCorporateName(corporateDTO.getCorporateName());
        corporate.setAddress(corporateDTO.getAddress());
        corporate.setContactName(corporateDTO.getContactName());
        corporate.setCorporateAccount(corporateDTO.getCorporateAccount());

        corporate.setCorporateStatus(this.getStatus(corporateDTO));
        corporate.setDescription(corporateDTO.getDescription());
        corporate.setEmail(corporateDTO.getEmail());
        corporate.setFax(corporateDTO.getFax());

        DateFormat formatter = new SimpleDateFormat(QrConstants.DATEFORMAT);
        if (corporateDTO.getLicenseStartDate() != "") {
            try {
                corporate.setLicenseStartDate(formatter.parse(corporateDTO.getLicenseStartDate()));


            } catch (ParseException ex) {
                Logger.getLogger(FormToDomainTranslater.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (corporateDTO.getLicenseEndDate() != "") {
            try {
                corporate.setLicenseEndDate(formatter.parse(corporateDTO.getLicenseEndDate()));

            } catch (ParseException ex) {
                Logger.getLogger(FormToDomainTranslater.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        corporate.setTelephone(corporateDTO.getTelephone());

        return corporate;
    }

    /**
     * handles assigning values to the user object once a corporate is
     * created,added by Dilusha
     *
     * @param corporateDTO
     * @return created user
     */
    private User handleUserBean(CoporateCreationDTO corporateDTO, Corporate corporate, Role role) {

        User user1 = new User();
        user1.setDescription(corporateDTO.getDescription());
        user1.setEmail(corporateDTO.getEmail());
        user1.setMobile(corporateDTO.getTelephone());
        user1.setName(corporateDTO.getContactName());
        user1.setUserStatus(corporate.getCorporateStatus());
        // user1.setPassword(this.randomPasswordGenerator());
        user1.setPassword("dialog");
        user1.setCorporate(corporate);
        user1.setRole(role);
        user1.setUserName(corporateDTO.getUserName());

        return user1;
    }

    /**
     * handles assigning values to the user object once a corporate admin is
     * created,added by Dilusha
     *
     * @param corporateAdminDTO
     * @param corporate corporate of the CA
     * @param role
     * @return created User
     */
    private User handleUserBean(CoporateAdminCreationDTO corporateAdminDTO, Corporate corporate, Role role) {
        User userCorpAdmin = new User();
     
        userCorpAdmin.setDescription(corporateAdminDTO.getDescription());
        userCorpAdmin.setEmail(corporateAdminDTO.getEmail());
        
        userCorpAdmin.setMobile(corporateAdminDTO.getTelephone());
        userCorpAdmin.setName(corporateAdminDTO.getContactName());
        //this should be auto created
        if (corporateAdminDTO.getCorporateStatus().equalsIgnoreCase("Disable")) {
            userCorpAdmin.setUserStatus(0);
        } else {
            userCorpAdmin.setUserStatus(1);
        }

        // userCorpAdmin.setPassword(this.randomPasswordGenerator());
        userCorpAdmin.setPassword("dialog");
        userCorpAdmin.setCorporate(corporate);
        userCorpAdmin.setRole(role);
        
        userCorpAdmin.setUserName(corporateAdminDTO.getUserName());

        return userCorpAdmin;
    }
    //added by Hasala

    public User handleCorporateUserBean(CreateUserDTO userDTO) {
        User corporateUser = new User();

        corporateUser.setUserName(userDTO.getUserName());
        // corporateUser.setPassword(this.randomPasswordGenerator());
        corporateUser.setPassword("dialog");
        corporateUser.setUserStatus(userDTO.getStatus());
        corporateUser.setName(userDTO.getFullName());
        corporateUser.setDesignation(userDTO.getPosition());
        corporateUser.setDescription(userDTO.getDescription());
        corporateUser.setMobile(userDTO.getMobile());
        corporateUser.setEmail(userDTO.getEmail());
        //   corporateUser.setLastLoginDate(new java.util.Date());

        return corporateUser;
    }// end

    /**
     * handles assigning values to the user privileges object,added by Dilusha
     *
     * @param privileges assigned privileges
     * @param user created user
     * @return vector of all the user privileges
     */
    public Vector<UserPrivileges> HandleUserPrivilegeBean(Vector<Privileges> privileges, User user) {
        UserPrivilegesVector = new Vector<UserPrivileges>();

        for (int i = 0; i < privileges.size(); i++) {
            UserPrivileges userPrivileges = new UserPrivileges();
            userPrivileges.setPrivileges(privileges.get(i));
            userPrivileges.setUser(user);
            UserPrivilegesVector.add(userPrivileges);
        }
        return UserPrivilegesVector;
    }

    /**
     * if 0-Disable,1-Enable status of the corporate,added by Dilusha
     *
     * @param corporateDTO
     * @return
     */
    private int getStatus(CoporateCreationDTO corporateDTO) {
        if (corporateDTO.getCorporateStatus().equalsIgnoreCase("Disable")) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * a 8 character length password will get generated,added by Dilusha
     *
     * @return password
     */
    public String randomPasswordGenerator() {
        String validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        String password = "";
        Random generator = new Random();
        for (int i = 0; i < 8; i++) {
            password += validChars.charAt(generator.nextInt(validChars.length()));
        }

        return password;
    }
    //added by Hasala

    public LobGroup handleLOB(CreateLobDTO lobDTO) {
        LobGroup lob = new LobGroup();
        lob.setLobName(lobDTO.getLobName());
        return lob;
    }
    //added by Hasala

    public Campaign handleCampaign(CreateCampaignDTO campaignDTO) {
        Campaign campaign = new Campaign();

        campaign.setCampaignName(campaignDTO.getCampaignName());
        campaign.setCampaignStatus(campaignDTO.getCampaignStatus());

        DateFormat formatter = new SimpleDateFormat(QrConstants.DATEFORMAT);
        if (campaignDTO.getStartDate() != null) {
            try {
                campaign.setStartDate((Date) formatter.parse(campaignDTO.getStartDate()));


            } catch (ParseException ex) {
                Logger.getLogger(CampaignController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (campaignDTO.getEndDate() != null) {
            try {

                campaign.setExpireDate((Date) formatter.parse(campaignDTO.getEndDate()));

            } catch (ParseException ex) {
                Logger.getLogger(CampaignController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return campaign;
    }// end
}

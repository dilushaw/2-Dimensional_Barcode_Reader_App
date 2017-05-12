/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.translator;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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
public class DomainToFormTranslater {

    @Autowired
    private CoporateCreationDTO coporateCreateDTO;
    @Autowired
    CreateBarcodeDTO createBC;
    @Autowired
    CreateUserDTO createUserDTO;
    @Autowired
    CreateUserDTO userDTO;
    @Autowired
    CreateCampaignDTO editCampaignDTO;

    /**
     * in corporate admin creation,corporate account should be displayed,added by Dilusha added by Dilusha
     *
     * @param corporate corporate of the CA
     * @return
     */
    public CoporateAdminCreationDTO translateDataToCorporateAdminCreation(Corporate corporate) {
        CoporateAdminCreationDTO coporateAdminCreateDTO = new CoporateAdminCreationDTO();
        coporateAdminCreateDTO.setCorporateAccount(corporate.getCorporateAccount());
        return coporateAdminCreateDTO;
    }

    /**
     * Set the data by the currently editing corporate/corporate super admin's(CSA) information and display in the relevant
     * inputs of Edit Corporate form. added by Dewmini
     *
     * @param user - user object which here is the type of Corporate Super Admin associated with curruntly editing
     * corporate
     * @param corporate - Currently editing Corporate object
     * @return - Form object (DTO) with corporate/CSA information
     */
    public CoporateCreationDTO translateDataToEditCorporate(User user, Corporate corporate) {
        coporateCreateDTO.setCorporateAccount(corporate.getCorporateAccount());
        coporateCreateDTO.setCorporateName(corporate.getCorporateName());
        coporateCreateDTO.setDescription(corporate.getDescription());
        coporateCreateDTO.setUserName(user.getUserName());
        coporateCreateDTO.setContactName(corporate.getContactName());
        String status = null;
        if (corporate.getCorporateStatus() == 1) {
            status = "Enable";
        } else {
            status = "Disable";
        }
        coporateCreateDTO.setCorporateStatus(status);
        coporateCreateDTO.setTelephone(corporate.getTelephone());
        coporateCreateDTO.setFax(corporate.getFax());
        coporateCreateDTO.setEmail(corporate.getEmail());
        Format formatter = new SimpleDateFormat(QrConstants.DATEFORMAT);
        if (corporate.getLicenseStartDate() != null) {
            coporateCreateDTO.setLicenseStartDate(formatter.format(corporate.getLicenseStartDate()));

        }
        if (corporate.getLicenseEndDate() != null) {
            coporateCreateDTO.setLicenseEndDate(formatter.format(corporate.getLicenseEndDate()));

        }
        coporateCreateDTO.setAddress(corporate.getAddress());
        return coporateCreateDTO;
    }

    /**
     * Set the data by the currently editing CA's information and display in the relevant inputs of Edit Corporate Admin
     * form. added by Dewmini
     *
     * @param privilages - currently assigned Corporate admin's privileges
     * @param user - user object which here is the type of Corporate Admin-CA associated with currently editing
     * @param corporate - Currently editing Corporate object
     * @return - Form object (DTO) with CA information
     */
    public CoporateAdminCreationDTO translateDataToEditCorporateAdmin(List<Privileges> privilages, User user, Corporate corporate) {
        CoporateAdminCreationDTO editAdminCreateDTO = new CoporateAdminCreationDTO();

        editAdminCreateDTO.setCorporateAccount(corporate.getCorporateAccount());
        editAdminCreateDTO.setDescription(user.getDescription());
        editAdminCreateDTO.setUserName(user.getUserName());
        editAdminCreateDTO.setContactName(user.getName());
        String status = null;
        if (user.getUserStatus() == 1) {
            status = "Enable";
        } else {
            status = "Disable";
        }
        editAdminCreateDTO.setCorporateStatus(status);
        editAdminCreateDTO.setTelephone(user.getMobile());
        editAdminCreateDTO.setEmail(user.getEmail());

        int i = 0;
        String[] privArray = new String[privilages.size()];
        for (Privileges p : privilages) {
            privArray[i] = p.getPrivilegeName();
            i++;
        }
        editAdminCreateDTO.setPrivileges(privArray);
        return editAdminCreateDTO;
    }

    /**
     *
     * Set the data by the currently editing user's information and display in the relevant inputs of Edit Corporate
     * User form. added by Dewmini
     *
     * @param privilages - currently assigned Corporate user's privileges
     * @param user - user object is been edited
     * @param campaigns - user's currently assigned campaigns
     * @return - Form object (DTO) with user information
     */
    public CreateUserDTO translateDataToEditCU(List<Privileges> privilages, User user, List<Campaign> campaigns) {
        createUserDTO.setUserName(user.getUserName());
        createUserDTO.setFullName(user.getName());
        createUserDTO.setEmail(user.getEmail());
        createUserDTO.setMobile(user.getMobile());
        createUserDTO.setStatus(user.getUserStatus());
        createUserDTO.setPosition(user.getDesignation());
        createUserDTO.setDescription(user.getDescription());
        Integer lobId = user.getLobGroup() != null ? user.getLobGroup().getLobId() : null;
        createUserDTO.setLobId(lobId);
        int i = 0;
        Integer[] privIdArray = new Integer[privilages.size()];
        for (Privileges p : privilages) {
            privIdArray[i] = p.getPrivilegeId();
            i++;
        }
        int j = 0;
        Integer[] campaignArray = new Integer[campaigns.size()];
        for (Campaign c : campaigns) {
            campaignArray[j] = c.getCampaignId();
            j++;
        }
        createUserDTO.setPrivileges(privIdArray);
        createUserDTO.setCampaignId(campaignArray);
        return createUserDTO;
    }

    /**
     * to edit user's own profile already assigned information is displayed added by Dilusha
     *
     * @param corporate
     * @param user
     * @return
     */
    public UserProfileDTO translateUserProfile(Corporate corporate, User user) {
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setCorporateAccount(corporate.getCorporateAccount());
        userProfileDTO.setUserName(user.getUserName());
        userProfileDTO.setDescription(user.getDescription());
        userProfileDTO.setEmail(user.getEmail());
        userProfileDTO.setContactName(user.getName());
        userProfileDTO.setTelephone(user.getMobile());
        return userProfileDTO;
    }

    /**
     * Set the campaign edit form input fields values by the Currently editing campaign's values. added by Hasala
     *
     * @param editCampaign - currently editing campaign object
     * @return - Form object (DTO) which values are set to currently editing campaign's values
     */
    public CreateCampaignDTO translateDataToEditCampaign(Campaign editCampaign) {
        editCampaignDTO.setCampaignName(editCampaign.getCampaignName());
        Format formatter = new SimpleDateFormat(QrConstants.DATEFORMAT);
        editCampaignDTO.setStartDate(formatter.format(editCampaign.getStartDate()));
        editCampaignDTO.setEndDate(formatter.format(editCampaign.getExpireDate()));
        editCampaignDTO.setLobName(editCampaign.getLobGroup().getLobName());
        editCampaignDTO.setLobId(editCampaign.getLobGroup().getLobId());
        editCampaignDTO.setLobName(editCampaign.getLobGroup().getLobName());
        editCampaignDTO.setCampaignStatus(editCampaign.getCampaignStatus());
        editCampaignDTO.setCampaignId(editCampaign.getCampaignId());

        return editCampaignDTO;
    }
}

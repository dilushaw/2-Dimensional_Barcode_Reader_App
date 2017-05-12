/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.dto;

import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.stereotype.Component;

/**
 * data transfer object to create an corporate admin(CA)
 *
 * @author Dilusha
 * @version 2.0
 */
@Component
public class CoporateAdminCreationDTO {

    /**
     * all these fields are in the corporate admin creation JSP
     */
    private String userName;
    private String corporateAccount;
    private String[] privileges;
    private String telephone;
    private int fax;
    private String email;
    private String contactName;
    private String corporateStatus;

    public String getCorporateStatus() {
        return corporateStatus;
    }

    public void setCorporateStatus(String corporateStatus) {
        this.corporateStatus = corporateStatus;
    }
    private String description;

    public String[] getPrivileges() {
        return privileges;
    }

    public void setPrivileges(String[] privileges) {
        this.privileges = privileges;
    }

    public String getCorporateAccount() {
        return corporateAccount;
    }

    public void setCorporateAccount(String corporateAccount) {
        this.corporateAccount = corporateAccount;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getFax() {
        return fax;
    }

    public void setFax(int fax) {
        this.fax = fax;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

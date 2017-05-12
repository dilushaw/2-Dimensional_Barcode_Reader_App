/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.Size;
import lk.dialog.corporate.Qr.data.RolePrivilege;
import lk.dialog.corporate.Qr.data.UserPrivileges;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.stereotype.Component;

/**
 * Date 23 July 2012 DTO for 'Create User ' to transfer data between View and Model
 *
 * @author Hasala
 * @version 2.1
 */
@Component
public class CreateUserDTO implements Serializable {

    //private static final long serialVersionUID = 1L;
    @NotEmpty
    @Size(min = 1, max = 20)
    private String userName;
    //private String userStatus;
    private int status;
    private int lob_id;
    private int campaign_id;
    @NotEmpty
    @Size(min = 1, max = 20)
    private String fullName;
    private String position;
    private String description;
    //@NotEmpty
    //@Size(min = 10 , max = 10)
    private String mobile;
    @NotEmpty
    @Email
    private String email;
    private Integer[] privileges;
    private Integer lobId;
    private Integer[] campaignId;
    private Set<RolePrivilege> rolePrivilege = new HashSet<RolePrivilege>(0);
    private Set<UserPrivileges> userPrivileges = new HashSet<UserPrivileges>(0);

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

//        public void setStatus(String status) {
//                
//             this.userStatus = status;
//        }
//        
//        public String getStatus() {
//             return userStatus;
//        }
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

//        //method to convert String status into Integer status
//        public int getCoporateUserStatus(){
//            if( userStatus.equalsIgnoreCase("1")){
//                return 1;
//            }
//            else {
//                return 0;
//            }
//        }
//        public void setLobID(int lob_id) {
//                this.lob_id = lob_id;
//        }
//        
//        public int getLobID() {
//            return lob_id;
//        }
    public void setCampaignID(int campaign_id) {
        this.campaign_id = campaign_id;
    }

    public int getCampaignID() {
        return campaign_id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public Integer[] getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Integer[] privileges) {
        this.privileges = privileges;
    }

    public Integer getLobId() {
        return lobId;
    }

    public void setLobId(Integer lobId) {
        this.lobId = lobId;
    }

    public Integer[] getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Integer[] campaignId) {
        this.campaignId = campaignId;
    }
}

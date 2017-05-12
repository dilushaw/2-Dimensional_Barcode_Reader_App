package lk.dialog.corporate.Qr.dto;

import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * This will map user login form values. Validations are also applied here.
 * Thursday, July ‎12, ‎2012, ‏‎9:32:13 AM
 *
 * @author Dewmini
 * @version 2.1
 */
public class UserLoginDTO {

    @NotEmpty
    @Size(min = 1, max = 20)
    private String userName;
    @NotEmpty
    @Size(min = 1, max = 10)
    private String password;
    @NotEmpty
    @Size(min = 1, max = 20)
    private String account;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }
}

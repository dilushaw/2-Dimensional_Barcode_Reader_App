package lk.dialog.corporate.Qr.dto;

import java.io.Serializable;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.stereotype.Component;

/**
 * this will be mapped form values of create/edit LOB Groups
 *
 * @author Hasala Date 31 July 2012 DTO for Lob
 * @version 2.1
 */
@Component
public class CreateLobDTO implements Serializable {

    @NotEmpty
    @Size(min = 1, max = 50)
    private String lobName;
    private int lobID;

    public void setLobName(String lobName) {
        this.lobName = lobName;
    }

    public String getLobName() {
        return lobName;
    }

    public void setLobID(int lobID) {
        this.lobID = lobID;
    }

    public int getLobID() {
        return lobID;
    }
}

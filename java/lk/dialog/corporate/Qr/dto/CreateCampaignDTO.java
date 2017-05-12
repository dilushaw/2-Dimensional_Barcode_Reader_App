/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.dto;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import lk.dialog.corporate.Qr.data.LobGroup;
import org.springframework.stereotype.Component;

/**
 * This is to map form values relating to create/edit campaigns.
 *
 * @author Hasala
 * @version 2.1
 */
@Component
public class CreateCampaignDTO implements Serializable {

    private String campaignName;
    private Integer campaignId;
    private Integer lobId;
    private LobGroup lobGroup;
    private Integer campaignStatus;
    private String startDate;
    private String endDate;
    private String lobName;

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public Integer getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Integer campaignId) {
        this.campaignId = campaignId;
    }

    public Integer getLobId() {
        return lobId;
    }

    public void setLobId(Integer lobId) {
        this.lobId = lobId;
    }

    public LobGroup getLobGroup() {
        return lobGroup;
    }

    public void setLobGroup(LobGroup lobGroup) {
        this.lobGroup = lobGroup;
    }

    public Integer getCampaignStatus() {
        return campaignStatus;
    }

    public void setCampaignStatus(Integer campaignStatus) {
        this.campaignStatus = campaignStatus;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getLobName() {
        return lobName;
    }

    public void setLobName(String lobName) {
        this.lobName = lobName;
    }

    
}

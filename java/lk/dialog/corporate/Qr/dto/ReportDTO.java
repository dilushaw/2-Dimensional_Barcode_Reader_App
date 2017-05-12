/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.dto;

/**
 *
 * @author Dewmini
 */
public class ReportDTO {

    String corporateName;
    String lobName ;
    String campaignName ;
    String codeName ;
    String dateCreated ;
    String userName ;
    String designation ;
    String corporateId;
    String lobId;
    String campaignId;
    
    boolean corporateColumn ;
    boolean lobColumn ;
    boolean campainColumn ;
    boolean codeNameColumn ;
    boolean codeSizeColumn ;
    boolean dateColumn ;
    boolean userColumn ;
    boolean desigColumn ;
    boolean urlHitColumn ;

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public boolean isCampainColumn() {
        return campainColumn;
    }

    public void setCampainColumn(boolean campainColumn) {
        this.campainColumn = campainColumn;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public boolean isCodeNameColumn() {
        return codeNameColumn;
    }

    public void setCodeNameColumn(boolean codeNameColumn) {
        this.codeNameColumn = codeNameColumn;
    }

    public boolean isCodeSizeColumn() {
        return codeSizeColumn;
    }

    public void setCodeSizeColumn(boolean codeSizeColumn) {
        this.codeSizeColumn = codeSizeColumn;
    }

    public boolean isCorporateColumn() {
        return corporateColumn;
    }

    public void setCorporateColumn(boolean corporateColumn) {
        this.corporateColumn = corporateColumn;
    }

    public String getCorporateName() {
        return corporateName;
    }

    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }

    public boolean isDateColumn() {
        return dateColumn;
    }

    public void setDateColumn(boolean dateColumn) {
        this.dateColumn = dateColumn;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isDesigColumn() {
        return desigColumn;
    }

    public void setDesigColumn(boolean desigColumn) {
        this.desigColumn = desigColumn;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public boolean isLobColumn() {
        return lobColumn;
    }

    public void setLobColumn(boolean lobColumn) {
        this.lobColumn = lobColumn;
    }

    public String getLobName() {
        return lobName;
    }

    public void setLobName(String lobName) {
        this.lobName = lobName;
    }

    public boolean isUrlHitColumn() {
        return urlHitColumn;
    }

    public void setUrlHitColumn(boolean urlHitColumn) {
        this.urlHitColumn = urlHitColumn;
    }

    public boolean isUserColumn() {
        return userColumn;
    }

    public void setUserColumn(boolean userColumn) {
        this.userColumn = userColumn;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getCorporateId() {
        return corporateId;
    }

    public void setCorporateId(String corporateId) {
        this.corporateId = corporateId;
    }

    

    public String getLobId() {
        return lobId;
    }

    public void setLobId(String lobId) {
        this.lobId = lobId;
    }

    
}

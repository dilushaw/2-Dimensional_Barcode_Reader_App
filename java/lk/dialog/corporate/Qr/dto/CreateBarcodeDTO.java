package lk.dialog.corporate.Qr.dto;

import org.springframework.stereotype.Component;

/**
 * As barcode related activities are done by ajax calls(which was done under phase 1 development) there no actual
 * neediness of keeping a separate DTO for that. But when refactoring the code under phase 2 development this class was
 * added to keep the consistency of Spring MVC framework. other than that there is no actual usage. Unless if someone
 * wants to save/generate barcodes under the mechanisms purely based on Spring MVC, this class can be removed.
 *
 * @author Dewmini
 * @version 2.0
 */
@Component
public class CreateBarcodeDTO {

    private String bcAction;
    private String websiteAddress;
    private String dynamicwebsiteAddress;
    private String message;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String organization;
    private String sms;
    private String smsmessage;
    private String call;
    private String barCodetype;
    private String barcodeLength;
    private String barcodeWidth;
    private String imageType;
    private String errorCorrection;
    private String titleText;
    private Integer campaignId;
    private Integer lobId;
    private String codeName;

    public String getDynamicwebsiteAddress() {
        return dynamicwebsiteAddress;
    }

    public void setDynamicwebsiteAddress(String dynamicwebsiteAddress) {
        this.dynamicwebsiteAddress = dynamicwebsiteAddress;
    }

    public String getBcAction() {
        return bcAction;
    }

    public void setBcAction(String bcAction) {
        this.bcAction = bcAction;
    }

    public void setWebsiteAddress(String websiteAddress) {
        this.websiteAddress = websiteAddress;
    }

    public String getWebsiteAddress() {
        return websiteAddress;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getSmsmessage() {
        return smsmessage;
    }

    public void setSmsmessage(String smsmessage) {
        this.smsmessage = smsmessage;
    }

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public String getBarCodetype() {
        return barCodetype;
    }

    public void setBarCodetype(String barCodetype) {
        this.barCodetype = barCodetype;
    }

    public String getBarcodeLength() {
        return barcodeLength;
    }

    public void setBarcodeLength(String barcodeLength) {
        this.barcodeLength = barcodeLength;
    }

    public String getBarcodeWidth() {
        return barcodeWidth;
    }

    public void setBarcodeWidth(String barcodeWidth) {
        this.barcodeWidth = barcodeWidth;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getErrorCorrection() {
        return errorCorrection;
    }

    public void setErrorCorrection(String errorCorrection) {
        this.errorCorrection = errorCorrection;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public Integer getcampaignId() {
        return campaignId;
    }

    public void setcampaignId(Integer campaignId) {
        this.campaignId = campaignId;
    }

    public Integer getLobId() {
        return lobId;
    }

    public void setLobId(Integer lobId) {
        this.lobId = lobId;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public Integer getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Integer campaignId) {
        this.campaignId = campaignId;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lk.dialog.corporate.Qr.data.*;
import lk.dialog.corporate.Qr.dto.BarCodeDataStoreDTO;
import lk.dialog.corporate.Qr.dto.CreateCampaignDTO;
import lk.dialog.corporate.Qr.dto.CreateLobDTO;
import lk.dialog.corporate.Qr.exception.QrException;
import lk.dialog.corporate.Qr.manager.*;
import lk.dialog.corporate.Qr.manager.LobManager;
import lk.dialog.corporate.Qr.translator.DomainToFormTranslater;
import lk.dialog.corporate.Qr.translator.FormToDomainTranslater;
import lk.dialog.corporate.Qr.utils.QrConstants;
import org.apache.log4j.Logger;
//import logic.barcodes.Barcode;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for Campaign related requests
 *
 * @author Hasala
 * @version 2.1
 */
@Controller
public class CampaignController {

    static Logger log = Logger.getLogger(CampaignController.class);
    @Autowired
    private CampaignManager campaignManager;
    @Autowired
    private UserManager userManager;
    @Autowired
    private UserManager userManager2; //used for create new campaign by CU
    @Autowired
    private LobManager lobManager;
    @Autowired
    private DomainToFormTranslater domainToFormTranslater;
    @Autowired
    private FormToDomainTranslater formToDomainTranslater;
    @Autowired
    BarcodeManager barcodeManager;
    @Autowired
    UserLogsManager userLogsManager;
    public static final String CREATE_CAMPAIGN = "createCampaign";
    public static final String CU_CREATE_CAMPAIGN = "userCreateCampaign";
    public static final String CU_DASHBOARD = "cuDashboard";
    public static final String EDIT_CAMPAIGN = "editCampaign";
    public static final String VIEW_CAMPAIGN = "viewCampaign";
    public static final String CU_EDIT_CAMPAIGN = "userEditCampaign";

    /**
     * Display 'Create Campaign' page. Campaign is to be created under particular LOB Group by CSA/CA type users. added
     * by Hasala
     *
     * @param request - current HTTP request
     * @param model - mapping form actions to the controller and set form values
     * @return the name to be rendered by the relevant JSP page(in tiles.xml)
     */
    @RequestMapping(value = "/createCampaign", method = RequestMethod.GET)
    public String showCreateCampaign(HttpServletRequest request, Map map) {
        log.debug("Show admins create campaign form");
        log.info("Show admins create campaign form");
        CreateCampaignDTO createCampaign = new CreateCampaignDTO();
        try {
            log.info("hightlight current tab");
            request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.DASHBOARD_TAB);
            log.info("current user");
            User user = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            log.info("before out lobs");
            map.put("lobs", lobManager.findLobByCorporateId(user.getCorporate().getCorporateId()));
log.info("after out lobs");
log.info("map put createCampaign");
        map.put(CREATE_CAMPAIGN, createCampaign);
        log.info("after map put createCampaign");
        return CREATE_CAMPAIGN;
        } catch (QrException ex) {
            map.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            log.error(ex);
            return QrConstants.ERROR;
        } catch (Exception ex) {
            log.error(ex);
            map.put(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.ERROR;
        }
        
    }

    /**
     * Save the campaign under particular LOB Group(when the action is taken by CA/CSA). Display the proper messages in
     * the events of error happen or successfully saved. (added by Hasala)
     *
     * @param request - Current HTTP Request
     * @param createCampaign - Data Transfer Object for create campaign form
     * @param result - bind the result according to the validation applied
     * @param model - mapping form actions to the controller and set form values
     * @return the name to be rendered by the relevant JSP page(in tiles.xml)
     */
    @RequestMapping(value = "/createCampaign", method = RequestMethod.POST)
    public String createCampaign(HttpServletRequest request, @Valid @ModelAttribute("createCampaign") CreateCampaignDTO createCampaign, BindingResult result, Map model) {
        log.debug("Save campaigns by admins");
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.DASHBOARD_TAB);
        model.put("createCampaign", createCampaign);
        try {
            User user = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));

            DateFormat formatter = new SimpleDateFormat(QrConstants.DATEFORMAT);
            Date currentdate = new Date();

            Date startDate = formatter.parse(createCampaign.getStartDate());
            Date endDate = formatter.parse(createCampaign.getEndDate());


            if (endDate.before(startDate)) {
                model.put(QrConstants.ERRORMESSAGE, "Please ensure that the License End Date is greater than or equals to the License Start Date");
                model.put("lobs", lobManager.findLobByCorporateId(user.getCorporate().getCorporateId()));

                return CREATE_CAMPAIGN;
            }
            if (endDate.before(currentdate)) {
                model.put(QrConstants.ERRORMESSAGE, "Please ensure that the License End Date is greater than or equals to the Current Date");
                model.put("lobs", lobManager.findLobByCorporateId(user.getCorporate().getCorporateId()));

                return CREATE_CAMPAIGN;
            }

            Campaign campaign = formToDomainTranslater.handleCampaign(createCampaign);
            campaign.setLobGroup(lobManager.findLobByLobId(createCampaign.getLobId()));
            campaign.setCreatedUserId(user.getUserId());//save created user - line added by dewmini
            campaign.setDateCreated(Calendar.getInstance().getTime());//save created date - line added by dewmini
            if (campaign.getLobGroup() == null) {
                model.put(QrConstants.ERRORMESSAGE, "No LOBs found for this corporate. Create an LOB first");
                return QrConstants.ERROR;
            }
            campaignManager.saveCampaign(campaign);
            model.put(QrConstants.SUCCESSMESSAGE, "You have successfully created a new campaign");
        } catch (QrException ex) {
            model.put(QrConstants.ERRORMESSAGE, "Cannot process your request at this moment :" + ex.getMessage());
            log.error(ex);
            return QrConstants.ERROR;
        } catch (Exception ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.ERROR;
        }
        return showCreateCampaign(request, model);
    }

    /**
     * Display 'Create Campaign' page. Campaign is to be created under particular LOB Group by CU type users. added by
     * Hasala
     *
     * @param request - current HTTP request
     * @param model - mapping form actions to the controller and set form values
     * @return the name to be rendered by the relevant JSP page(in tiles.xml)
     */
    @RequestMapping(value = "/userCreateCampaign", method = RequestMethod.GET)
    public String showCUCreateCampaign(HttpServletRequest request, Map map) {
        log.debug("Show users create campaign form");
        try {
            request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.DASHBOARD_TAB);
            CreateCampaignDTO createCampaign = new CreateCampaignDTO();
            //request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.DASHBOARD_TAB);
            User currentUser = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            if (currentUser != null) {
                map.put("lobId", currentUser.getLobGroup().getLobId());
            } else {
                map.put("lobId", "-1");
            }
            map.put(CU_CREATE_CAMPAIGN, createCampaign);


        } catch (QrException ex) {
            log.error(ex);
            map.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {
            log.error(ex);
            map.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
        return CU_CREATE_CAMPAIGN;
    }

    /**
     * Save the campaign under particular LOB Group(when the action is taken by CU). Display the proper messages in the
     * events of error happen or successfully saved. (added by Hasala)
     *
     * @param request - Current HTTP Request
     * @param createCampaign - Data Transfer Object for create campaign form
     * @param result - bind the result according to the validation applied
     * @param model - mapping form actions to the controller and set form values
     * @return the name to be rendered by the relevant JSP page(in tiles.xml)
     */
    @RequestMapping(value = "/userCreateCampaign", method = RequestMethod.POST)
    public String saveCUCreateCampaign(HttpServletRequest request, @Valid @ModelAttribute("userCreateCampaign") CreateCampaignDTO createCampaign, BindingResult result, Map model) {
        log.debug("Save campaigna by users");
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.DASHBOARD_TAB);
        model.put("userCreateCampaign", createCampaign);
        try {
            //here validate  dates for firefox
            DateFormat formatter = new SimpleDateFormat(QrConstants.DATEFORMAT);
            Date currentdate = new Date();

            Date startDate = (Date) formatter.parse(createCampaign.getStartDate());
            Date endDate = (Date) formatter.parse(createCampaign.getEndDate());
            if (endDate.before(startDate)) {
                model.put(QrConstants.ERRORMESSAGE, "Please ensure that the License End Date is greater than or equals to the License Start Date");
                return CU_CREATE_CAMPAIGN;
            }
            if (endDate.before(currentdate)) {
                model.put(QrConstants.ERRORMESSAGE, "Please ensure that the License End Date is greater than or equals to the Current Date");
                return CU_CREATE_CAMPAIGN;
            }

            User campaignUser = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));

            Campaign campaign = formToDomainTranslater.handleCampaign(createCampaign);
            campaign.setLobGroup(campaignUser.getLobGroup());
            campaign.setCreatedUserId(campaignUser.getUserId());//save created user id - line added by dewmini
            campaign.setDateCreated(Calendar.getInstance().getTime());//save created date - line added by dewmini
            campaignManager.saveCampaign(campaign);
            userManager.setCUCreatedCampaignForCU(campaign, campaignUser);
            model.put(QrConstants.SUCCESSMESSAGE, "You have successfully created a new campaign");
        } catch (QrException ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.ERROR;
        } catch (Exception ex) {

            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.ERROR;

        }
        return showCUCreateCampaign(request, model);
    }

    /**
     * Show edit campaign form to CA/CSA. added by Hasala
     *
     * @param request - current HTTP request
     * @param map - mapping form actions to the controller and set form values
     * @return the name to be rendered by the relevant JSP page(in tiles.xml)
     */
    @RequestMapping(value = "/editCampaign", method = RequestMethod.GET)
    public String showEditCampaign(HttpServletRequest request, Map map) {
        try {
            request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.DASHBOARD_TAB);
            CreateCampaignDTO editCampaign;
            Campaign campaign;
            campaign = campaignManager.findCampaignByCampaignId(Integer.valueOf(request.getParameter("campaignId")));
            editCampaign = domainToFormTranslater.translateDataToEditCampaign(campaign);
            map.put(EDIT_CAMPAIGN, editCampaign);

        } catch (QrException ex) {
            log.error(ex);
            map.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {
            log.error(ex);
            map.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
        return EDIT_CAMPAIGN;
    }

    /**
     * Edit the campaign under particular LOB Group(when the action is taken by CA/CSA). Display the proper messages in
     * the events of error happen or successfully saved. (added by Hasala)
     *
     * @param request - Current HTTP Request
     * @param editCampaign - Data Transfer Object for edit campaign form
     * @param result - bind the result according to the validation applied
     * @param map - mapping form actions to the controller and set form values
     * @return the name to be rendered by the relevant JSP page(in tiles.xml)
     */
    @RequestMapping(value = "/editCampaign", method = RequestMethod.POST)
    public String updateEditCampaign(HttpServletRequest request, @Valid @ModelAttribute("editCampaign") CreateCampaignDTO editCampaign, BindingResult result, Map map) {
        try {
            request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.DASHBOARD_TAB);
            User currentUser = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));

            map.put("editCampaign", editCampaign);

            DateFormat formatter = new SimpleDateFormat(QrConstants.DATEFORMAT);
            Date currentdate = new Date();
            Date startDate = formatter.parse(editCampaign.getStartDate());
            Date endDate = formatter.parse(editCampaign.getEndDate());
            if (endDate.before(startDate)) {
                map.put(QrConstants.ERRORMESSAGE, "Please ensure that the License End Date is greater than or equals to the License Start Date");
                return EDIT_CAMPAIGN;
            }
            if (endDate.before(currentdate)) {
                map.put(QrConstants.ERRORMESSAGE, "Please ensure that the License End Date is greater than or equals to the Current Date");
                return EDIT_CAMPAIGN;
            }
            campaignManager.updateCampaign(editCampaign);
            userLogsManager.saveEntityActionInLogs(currentUser, String.valueOf(editCampaign.getCampaignId()), QrConstants.LOG_EDIT_CAMPAIGN, "edit campaign by ca/csa");
            map.put("successMessage", "You have successfully edited the campaign");
            //map.put("campaignId", editCampaign.getCampaignId());
            return showEditCampaign(request, map);

        } catch (QrException ex) {
            log.error(ex);
            map.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {
            log.error(ex);
            map.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
    }

    /**
     * Show edit campaign form to CU. added by Hasala
     *
     * @param request - current HTTP request
     * @param map - mapping form actions to the controller and set form values
     * @return the name to be rendered by the relevant JSP page(in tiles.xml)
     */
    @RequestMapping(value = "/userEditCampaign", method = RequestMethod.GET)
    public String showEditCampaignCU(HttpServletRequest request, Map map) {
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.DASHBOARD_TAB);
        CreateCampaignDTO editCampaign;
        try {
            User currentUser = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            if (currentUser != null) {
                map.put("lobId", currentUser.getLobGroup().getLobId());
            } else {
                map.put("lobId", "-1");
            }
            Campaign campaign = campaignManager.findCampaignByCampaignId(Integer.valueOf(request.getParameter("campaignId")));
            editCampaign = domainToFormTranslater.translateDataToEditCampaign(campaign);
            map.put(CU_EDIT_CAMPAIGN, editCampaign);
        } catch (QrException ex) {
            log.error(ex);
            map.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {
            log.error(ex);
            map.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
        return CU_EDIT_CAMPAIGN;
    }

    /**
     * Edit the campaign under particular LOB Group(when the action is taken by CA/CSA). Display the proper messages in
     * the events of error happen or successfully saved. (added by Hasala)
     *
     * @param request - Current HTTP Request
     * @param editCampaign - Data Transfer Object for edit campaign form
     * @param result - bind the result according to the validation applied
     * @param map - mapping form actions to the controller and set form values
     * @return the name to be rendered by the relevant JSP page(in tiles.xml)
     */
    @RequestMapping(value = "/userEditCampaign", method = RequestMethod.POST)
    public String updateCUEditCampaign(HttpServletRequest request, @Valid @ModelAttribute("userEditCampaign") CreateCampaignDTO editCampaign, BindingResult result, Map map) {
        try {
            request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.DASHBOARD_TAB);
            User currentUser = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));

            map.put("userEditCampaign", editCampaign);

            DateFormat formatter = new SimpleDateFormat(QrConstants.DATEFORMAT);
            Date currentdate = new Date();

            Date startDate = formatter.parse(editCampaign.getStartDate());
            Date endDate = formatter.parse(editCampaign.getEndDate());

            if (endDate.before(startDate)) {
                map.put(QrConstants.ERRORMESSAGE, "Please ensure that the License End Date is greater than or equals to the License Start Date");
                return CU_EDIT_CAMPAIGN;
            }
            if (endDate.before(currentdate)) {
                map.put(QrConstants.ERRORMESSAGE, "Please ensure that the License End Date is greater than or equals to the Current Date");
                return CU_EDIT_CAMPAIGN;
            }

            campaignManager.updateCampaign(editCampaign);
            userLogsManager.saveEntityActionInLogs(currentUser, String.valueOf(editCampaign.getCampaignId()), QrConstants.LOG_EDIT_CAMPAIGN, "edit campaign by user");
            map.put("successMessage", "You have successfully edited the campaign");
            return CU_EDIT_CAMPAIGN;
        } catch (QrException ex) {
            log.error(ex);
            map.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {
            log.error(ex);
            map.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
    }

    /**
     * List campaign names proceed with the check box (To select campaigns) for a given LOB Group. (added by Dewmini)
     *
     * @param request - current HTTP request
     * @return - html code snippet as String
     */
    @RequestMapping(value = "/loadCampaignsCheckBox", method = RequestMethod.GET)
    public @ResponseBody
    String loadCampaignsCheckBox(HttpServletRequest request) {
        String html = null;
        try {
            List<Campaign> campaings = campaignManager.findCampaignsByLob(Integer.valueOf(request.getParameter("id")), QrConstants.ACTIVE);
            html = prepareCampaignsCheckBox(campaings);
        } catch (QrException ex) {
            log.error(ex);
            html = "Couldn't load the campaigns!";
        } catch (Exception ex) {
            log.error(ex);
            html = "Couldn't load the campaigns!";
        }
        return html;
    }

    /**
     * For a given list of campaigns prepare list of campaigns which proceed with a check box in a specific format.
     * (added by Dewmini)
     *
     * @param campaings - list of campaigns
     * @return - html code as a String
     */
    private String prepareCampaignsCheckBox(List<Campaign> campaings) {
        StringBuilder checkBox = new StringBuilder();
        if (campaings != null && !campaings.isEmpty()) {
            int i = 1;
            checkBox.append("<div class=\"ScrollBox\">"
                    + "<table width=\"100%\" border=\"0\" style=\"margin:0px; padding:0px;\">");
            for (Campaign cam : campaings) {
                checkBox.append("<tr>");
                checkBox.append("<td width=\"14%\">");
                checkBox.append("<input type=\"checkbox\" name=\"campaignId\" id=\"campaignId\" value=\"" + cam.getCampaignId() + "\" />");
                checkBox.append("</td>");
                checkBox.append("<td width=\"86%\"> " + cam.getCampaignName() + "</td>");
                checkBox.append("</tr>");
                i++;
            }
            checkBox.append("</table></div>");
        } else {
            checkBox.append("No active campaings found for the selected Lob.");
        }
        return checkBox.toString();
    }

    /**
     * Get campaign drop down according to the selected LOB. (added by Dewmini)
     *
     * @param request - current HTTP request
     * @return - html code snippet as a String
     */
    @RequestMapping(value = "/loadCampaignsDropDown", method = RequestMethod.GET)
    public @ResponseBody
    String loadCampaignsDropDown(HttpServletRequest request) {
        String html = null;
        try {
            List<Campaign> campaings = campaignManager.findCampaignsByLob(Integer.valueOf(request.getParameter("id")), QrConstants.ACTIVE);
            html = prepareCampaignsDropDown(campaings);
        } catch (QrException ex) {
            log.error(ex);
            html = "Couldn't load the campaigns!";
        } catch (Exception ex) {
            log.error(ex);
            html = "Couldn't load the campaigns!";
        }
        return html;
    }

    /**
     * For a given list of campaigns it will give a output of drop down box of campaigns. (added by Dewmini)
     *
     * @param campaings - List of Campaigns
     * @return - html code as String
     */
    private String prepareCampaignsDropDown(List<Campaign> campaings) {
        StringBuilder dropDown = new StringBuilder();

        if (campaings != null && !campaings.isEmpty()) {
            int i = 1;
            dropDown.append("<select name=\"campaignId\" id=\"campaignId\" >"
                    + "<option value=\"\">--Please Select</option>");

            for (Campaign cam : campaings) {

                dropDown.append("<option value=\"" + cam.getCampaignId() + "\" >" + cam.getCampaignName() + "</option>");
                i++;
            }
            dropDown.append("</select>");
        } else {
            dropDown.append("No active campaings found for selected Lob.");
        }
        return dropDown.toString();
    }

    /**
     * Prepare dashboard for the corporate user by the campaigns assigned to him. (added by Dewmini)
     *
     * @param request - current HTTP request
     * @param model - mapping controller values to the jsp page
     * @return String for the jsp page to be rendered
     */
    @RequestMapping(value = "/cuDashboard", method = {RequestMethod.POST, RequestMethod.GET})
    public String showCUDashboard(HttpServletRequest request, Map model) {
        try {
            request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.DASHBOARD_TAB);
            User user = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            model.put("campaigns", campaignManager.findCampaignsByUserId(user.getUserId(), QrConstants.ACTIVE_AND_INACTIVE));
            model.put("currentUser", user);
            //model.put("scanCount", campaignManager.findCampaignBarcodeCount(Integer.MIN_VALUE));
        } catch (QrException ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {

            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;

        }
        return CU_DASHBOARD;
    }

    /**
     * Get the details of the currently selected campaigns and barcodes created under the campaigns. (added by Dewmini)
     *
     * @param request - Current HTTP request
     * @param model - map the controller values to the jsp page
     * @return - relevant string to be rendered by the jsp page
     */
    @RequestMapping(value = "/viewCampaign", method = {RequestMethod.GET})
    public String showViewCampaign(HttpServletRequest request, Map model) {
        try {
            request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.DASHBOARD_TAB);
            Campaign campaign = campaignManager.findCampaignByCampaignId(Integer.valueOf(request.getParameter("campaignId")));
            Map<String, String[]> bcNameRow = null;
            HashMap<String, String> priviMap = (HashMap<String, String>) request.getSession().getAttribute("priviMap");
            //diaply baroced details under particular campaign, if the user has the privilege viewbarcodes
            if (priviMap.containsKey("viewbarcodes")) {
                bcNameRow = barcodeManager.loadBarcodeDetails(campaign.getCampaignId());
            }
            model.put("campaign", campaign);
            model.put("bcNameRow", bcNameRow);

        } catch (QrException ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return showCUDashboard(request, model);
        } catch (Exception ex) {

            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return showCUDashboard(request, model);

        }
        return VIEW_CAMPAIGN;
    }

    /**
     * Delete the currently selected campaign by ajax call. (added by Dewmini)
     *
     * @param request - Current HTTP request
     * @param userId - Id of the user who is deleting the campaign
     * @param campId - currently deleting campaign ID
     * @return - String message relevant to barcode deletion. i.e. 'yes'=campaign deleted , 'no'=not deleted(error
     * happen)
     */
    @RequestMapping(value = "/deleteCampaign", method = RequestMethod.GET)
    public @ResponseBody
    String deleteCampaign(HttpServletRequest request, @RequestParam Long userId, Integer campId) {

        String msg = "";
        try {
            /*
             * user id was passed by the ajax call to save the action taken by this user in the database. To save in the
             * another addtional column of campaign(deleted_by) or to save in user_logs table
             */
            User currentUser = userManager.findUserByID(userId);
            Campaign campaign = campaignManager.findCampaignByCampaignId(campId);
            if (campaign != null) {
                campaign.setCampaignStatus(QrConstants.DELETED);
                userLogsManager.saveEntityActionInLogs(currentUser, String.valueOf(campId), QrConstants.LOG_DELETE_CAMPAIGN, "delete campaign");
                campaignManager.updateCampaign(campaign);

                msg = "yes";
            } else {
                msg = "no";
            }
        } catch (QrException ex) {
            log.error(ex);
            msg = "no";
            return msg;
        } catch (Exception e) {
            msg = "no";
            return msg;
        }


        return msg;
    }

    /**
     * When creating the campaign check campaign name already exist within a given LOB Group - Campaign name is unique
     * under particular LOB. (added by Dewmini)
     *
     * @param request - Current HTTP Request
     * @param campaignName - user entered campaign name
     * @param lobId - Id of the LOB Group which the campaign is going to be created
     * @return - true if campaign name exists, if not relevant message
     */
    @RequestMapping(value = "/checkCampaignName", method = RequestMethod.GET)
    public @ResponseBody
    String checkCampaignName(HttpServletRequest request, @RequestParam String campaignName, int lobId) {
        String msg = "";
        boolean check = false;
        try {
            List<Campaign> campaign = campaignManager.findCampaignByNameAndLob(campaignName, lobId);
            if (campaign != null) {
                for (Campaign cam : campaign) {
                    if (campaignName.equals(cam.getCampaignName())) {
                        check = true;
                        break;
                    }
                }
                //String camName = campaign.getCampaignName();
                if (check) {
                    //msg = "This campaign name '" + campaignName + "' already exists.";
                    msg = "true";
                }
            }
        } catch (QrException ex) {
            log.error(ex);
            msg = "Campaign name check was unsuccessfull. Try Again!";
            return msg;

        } catch (Exception ex) {

            log.error(ex);
            msg = "Campaign name check was unsuccessfull. Try Again!";
            return msg;

        }
        return msg;
    }

    /**
     * to load the campaign drop down list for dynamic url,added by Dilusha
     *
     * @param request
     * @return list of campaigns
     */
    @RequestMapping(value = "/loadCampaignsDropDownforDynamicUrl", method = RequestMethod.GET)
    public @ResponseBody
    String loadCampaignsDropDownforDynamicUrl(HttpServletRequest request) {

        String html = null;
        try {
            List<Campaign> campaings = campaignManager.findCampaignsByLob(Integer.valueOf(request.getParameter("id")), QrConstants.ACTIVE);

            html = prepareCampaignsDropDownforDynamicUrl(campaings);

        } catch (QrException ex) {
            log.error(ex);
            html = "Couldn't load the campaigns!";
        } catch (Exception ex) {

            log.error(ex);
            html = "Couldn't load the campaigns!";
        }

        return html;
    }

    /**
     * this method being called by loadCampaignsDropDownforDynamicUrl for further processing of listing campaigns,added
     * by Dilusha
     *
     * @param campaings list of all the campaigns which are under a particular LOb
     * @return the correct dropdown list
     */
    private String prepareCampaignsDropDownforDynamicUrl(List<Campaign> campaings) {
        StringBuilder dropDown = new StringBuilder();

        if (campaings != null && !campaings.isEmpty()) {
            int i = 1;
            dropDown.append("<select name=\"campaignId\" id=\"campaignId\" onchange=\"showDynamicWebsite(this.value)\" >"
                    + "<option value=\"\">--Please Select</option>");

            for (Campaign cam : campaings) {

                dropDown.append("<option value=\"" + cam.getCampaignId() + "\">" + cam.getCampaignName() + "</option>");

                i++;
            }
            dropDown.append("</select>");
        } else {
            dropDown.append("No active campaings found for selected Lob.");
        }
        return dropDown.toString();
    }

    /**
     * to load all the dynamic urls which are under a selected campaign,added by Dilusha
     *
     * @param request
     * @param camid selected campaign id
     * @return drop down list
     */
    @RequestMapping(value = "/loadDynamicUrlDropDown", method = RequestMethod.GET)
    public @ResponseBody
    String loadDynamicUrlDropDown(HttpServletRequest request, @RequestParam Integer camid) {


        String html = null;
        try {


            List<String> codeNames = barcodeManager.findBarcodeByCampaignAndBcAction("6", camid);
            List<Barcode> barcodes = new ArrayList<Barcode>();
            for (int i = 0; i < codeNames.size(); i++) {
                List<Barcode> manySizeBarcodes = barcodeManager.findBarcodeByCampaignAndCodeName(codeNames.get(i), camid);

                barcodes.add(manySizeBarcodes.get(0));


            }


            if (!barcodes.isEmpty() && barcodes != null) {

                html = prepareDynamicUrlDropDown(barcodes);
            } else {
                html = "No Dynamic Websites found under this campaign";
            }
        } catch (QrException ex) {
            log.error(ex);
            html = "Couldn't load the Dynamic Websites!";
        } catch (Exception ex) {

            log.error(ex);
            html = "Couldn't load the Dynamic Websites!";
        }

        return html;
    }

    /**
     * To actually creates the final dropdown list for dynamic url to send to loadDynamicUrlDropDown,added by Dilusha
     *
     * @param barcodes
     * @return dynamic url dropdown list
     */
    private String prepareDynamicUrlDropDown(List<Barcode> barcodes) {
        StringBuilder dropDown = new StringBuilder();

        if (barcodes != null && !barcodes.isEmpty()) {
            int i = 1;
            dropDown.append("<select name=\"barcodeId\" id=\"barcodeId\">"
                    + "<option value=\"\">--Please Select</option>");

            for (Barcode bc : barcodes) {
                BarcodeData bcdata = barcodeManager.findBarcodedataByBarcodeId(bc);
                BarcodeData bcdataRefined = barcodeManager.findBarcodedataByBarcodeId(bc);
                dropDown.append("<option value=\"" + bc.getBarcodeId() + "\">" + bcdata.getValue() + "</option>");

                i++;
            }
            dropDown.append("</select>");
        } else {
            dropDown.append("No active barcodes found for selected campaign.");
        }
        return dropDown.toString();
    }

    /**
     * get the drop down box of campaigns for a selected LOB Group. (added by Dewmini)
     *
     * @param request - current HTTP request
     * @return - html code as a String which contain a dropdown box of campaigns
     */
    @RequestMapping(value = "/loadCampaignsForReport", method = RequestMethod.GET)
    public @ResponseBody
    String loadCampaignsForReport(HttpServletRequest request) {
        String html = null;
        try {
            List<Campaign> campaings = campaignManager.findCampaignsByLob(Integer.valueOf(request.getParameter("lobId")), QrConstants.ACTIVE_AND_INACTIVE);

            html = prepareCampaignsForReport(campaings);

        } catch (QrException ex) {
            log.error(ex);
            html = "Couldn't load the campaigns!";
        } catch (Exception ex) {

            log.error(ex);
            html = "Couldn't load the campaigns!";
        }

        return html;
    }

    /**
     * For a given list of campaigns prepare a dropdown box of campaigns. javascript method to load codenames when
     * change the campaign name. (added by Dewmini)
     *
     * @param campaings - list of campaigns
     * @return - html drop down box as String
     */
    private String prepareCampaignsForReport(List<Campaign> campaings) {
        StringBuilder dropDown = new StringBuilder();

        if (campaings != null && !campaings.isEmpty()) {
            int i = 1;
            dropDown.append("<select name=\"campaignId\" id=\"campaignId\" class=\"selectbox\" onchange=\"showCodeNames(this.value)\">"
                    + "<option value=\"\">Any</option>");

            for (Campaign cam : campaings) {

                dropDown.append("<option value=\"" + cam.getCampaignId() + "\" >" + cam.getCampaignName() + "</option>");
                //dropDown.append("<option value=\"" + cam.getCampaignId() + "\" >" + cam.getCampaignName() + "</option>");
                i++;
            }
            dropDown.append("</select>");
        } else {
            //dropDown.append("No Campaing(s) found.");
            dropDown.append("<select name=\"campaignId\" id=\"campaignId\"  class=\"selectbox\" onchange=\"showCodeNames(this.value)\">"
                    + "<option value=\"\" >Any</option>"
                    + "</select><br> No Campaing(s)!");
        }
        return dropDown.toString();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.*;
import lk.dialog.corporate.Qr.data.User;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lk.dialog.corporate.Qr.data.Campaign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lk.dialog.corporate.Qr.data.LobGroup;
import lk.dialog.corporate.Qr.data.User;
import lk.dialog.corporate.Qr.dto.CreateLobDTO;
import lk.dialog.corporate.Qr.exception.QrException;
import lk.dialog.corporate.Qr.manager.CampaignManager;
import lk.dialog.corporate.Qr.manager.LobManager;
import lk.dialog.corporate.Qr.manager.LobManagerImpl;
import lk.dialog.corporate.Qr.manager.UserManager;
import lk.dialog.corporate.Qr.translator.FormToDomainTranslater;
import lk.dialog.corporate.Qr.utils.QrConstants;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Hasala Date: 02/08/2012
 */
@Controller
public class LobController {

    static Logger log = Logger.getLogger(LobController.class);
    @Autowired
    private LobManager lobManager;
    @Autowired
    private UserManager userManager;
    @Autowired
    CampaignManager campaignManager;
    private static final String CREATELOB = "createLob";
    private static final String CORPORATEDASHBOARD = "corporateDashboard";

    @RequestMapping(value = "/createLob", method = RequestMethod.GET)
    public String showCreateLob(HttpServletRequest request, Map model) {
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.DASHBOARD_TAB);
        CreateLobDTO createLob = new CreateLobDTO();
        model.put(CREATELOB, createLob);
        //return MainController.LOGGED_USER_SCREEN;
        return CREATELOB;
    }

    @RequestMapping(value = "/createLob", method = RequestMethod.POST)
    public String createLob(HttpServletRequest request, @Valid @ModelAttribute("createLob") CreateLobDTO createLob, BindingResult result, Map map, Model model) {

        map.put(CREATELOB, createLob);
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.DASHBOARD_TAB);
        try {
            request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.DASHBOARD_TAB);
            User lobUser = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            FormToDomainTranslater translater = new FormToDomainTranslater();
            LobGroup checklob = lobManager.findLobByCorpIDandLobName(lobUser.getCorporate(), createLob.getLobName());

            if (checklob == null) {
                LobGroup newLob = translater.handleLOB(createLob);
                newLob.setCorporate(lobUser.getCorporate());
                newLob.setCreatedUserId(lobUser.getUserId());
                newLob.setDateCreated(Calendar.getInstance().getTime());
                lobManager.saveLob(newLob);
                model.addAttribute(QrConstants.SUCCESSMESSAGE, "You have successfully created a new Line Of Business Group");
                return QrConstants.SUCCESS;
            } else {

                model.addAttribute(QrConstants.ERRORMESSAGE, "LOB name must be unique inside the corporate. Another LOB called '" + createLob.getLobName() + "' already exists.");
                return CREATELOB;

            }
        } catch (QrException ex) {
            map.put(QrConstants.ERRORMESSAGE, "Cannot process your request at this moment :" + ex.getMessage());
            log.error(ex);
            return QrConstants.ERROR;

        } catch (Exception ex) {

            log.error(ex);
            map.put(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.ERROR;

        }

    }

    //added by dewmini
    @RequestMapping(value = "/corporateDashboard", method = {RequestMethod.POST, RequestMethod.GET})
    public String showCorporateDashboard(HttpServletRequest request, Map model) {
        try {
            request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.DASHBOARD_TAB);
            User user = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));

            String campaignName = request.getParameter("campaign");
            String status = request.getParameter("status");
            String filter = request.getParameter("filter");

//            Map<LobGroup, List<Campaign>> lobCampaignsMap = null;
            Map<?, ?>[] lobCampaignsMap = null;
            if (campaignName != null && !campaignName.isEmpty() || status != null) {
                lobCampaignsMap = lobManager.findLobsAndCampaignsByCorproateId(user.getCorporate().getCorporateId(), campaignName, status);
            } else {
                lobCampaignsMap = lobManager.findLobsAndCampaignsByCorproateId(user.getCorporate().getCorporateId());
            }

//            model.put("lobsAndCampaings", lobCampaignsMap);
//            model.put("lobSize", lobCampaignsMap != null ? lobCampaignsMap.keySet().size() : 0);
            model.put("currentUser", user);
            model.put("lobsAndCampaings", (TreeMap<LobGroup, List<Campaign>>) lobCampaignsMap[0]);
            model.put("barcodeCount", (HashMap<Integer, Integer>) lobCampaignsMap[1]);
            model.put("lobSize", lobCampaignsMap != null ? ((TreeMap<LobGroup, List<Campaign>>) lobCampaignsMap[0]).keySet().size() : 0);
            model.put("filter", filter);
            model.put("campaignName", campaignName);
            model.put("status", status);

        } catch (QrException ex) {
            model.put(QrConstants.ERRORMESSAGE, "Cannot process your request at this moment :" + ex.getMessage());
            log.error(ex);
            return QrConstants.ERROR;

        } catch (Exception ex) {

            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;

        }
        return CORPORATEDASHBOARD;
    }

    @RequestMapping(value = "/loadLobsForReport", method = RequestMethod.GET)
    public @ResponseBody
    String loadLobsForReport(HttpServletRequest request, Map model, @RequestParam Integer corporateId) {
        String html = null;
        try {
            List<LobGroup> lobs = lobManager.findLobByCorporateId(corporateId);

            html = prepareLobsForReport(lobs);

        } catch (QrException ex) {
            log.error(ex);
            html = "Couldn't load the campaigns!";
        } catch (Exception ex) {

            log.error(ex);
            html = "Couldn't load the campaigns!";
        }

        return html;
    }

    private String prepareLobsForReport(List<LobGroup> lobs) {
        StringBuilder dropDown = new StringBuilder();

        if (lobs != null && !lobs.isEmpty()) {
            int i = 1;
            dropDown.append("<select name=\"lobId\" id=\"lobId\"  class=\"selectbox\" onchange=\"showCampaigns(this.value)\">"
                    + "<option value=\"\">Any</option>");

            for (LobGroup lob : lobs) {

                dropDown.append("<option value=\"" + lob.getLobId() + "\">" + lob.getLobName() + "</option>");
                //dropDown.append("<option value=\"" + cam.getCampaignId() + "\">" + cam.getCampaignName() + "</option>");
                i++;
            }
            dropDown.append("</select>");
        } else {
            //dropDown.append("No Lob(s) found.");
            dropDown.append("<select name=\"lobId\" id=\"lobId\"  class=\"selectbox\"  onchange=\"showCampaigns(this.value)\">"
                    + "<option value=\"\">Any</option>"
                    + "</select><br> No Lob(s)!");
        }
        return dropDown.toString();
    }

    /**
     * When creating the LOB, check LOB name already exist within the particular corporate - as LOB name is unique under
     * particular Corporate. (added by Dewmini)
     *
     * @param request - Current HTTP Request
     * @param lobName - user entered LOB name
     * @return - true if LOB name exists within the corporate, if not relevant message
     */
    @RequestMapping(value = "/checkLOBName", method = RequestMethod.GET)
    public @ResponseBody
    String checkLOBName(HttpServletRequest request, @RequestParam String lobName) {
        String msg = "";
        boolean check = false;
        try {
            User currentUser = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            LobGroup lob = lobManager.findLobByCorpIDandLobName(currentUser.getCorporate(), lobName);
            if (lob != null) {
                if (lobName.equals(lob.getLobName())) {
                    check = true;
                }
                if (check) {
                    msg = "true";
                }
            }
        } catch (QrException ex) {
            log.error(ex);
            msg = "LOB name check was unsuccessfull. Try Again!";
            return msg;
        } catch (Exception ex) {
            log.error(ex);
            msg = "LOB name check was unsuccessfull. Try Again!";
            return msg;
        }
        return msg;
    }
}

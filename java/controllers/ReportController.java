/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import lk.dialog.corporate.Qr.data.*;
import lk.dialog.corporate.Qr.dataaccess.ReportViewDAO;
import lk.dialog.corporate.Qr.dataaccess.ReportViewDAOImpl;
import lk.dialog.corporate.Qr.dto.ReportDTO;
import lk.dialog.corporate.Qr.exception.QrException;
import lk.dialog.corporate.Qr.manager.*;
import lk.dialog.corporate.Qr.utils.QrConstants;

import net.sf.jasperreports.engine.JRDataSource;

import org.apache.log4j.Logger;
import org.extremecomponents.table.context.Context;
import org.extremecomponents.table.context.HttpServletRequestContext;
import org.extremecomponents.table.limit.Limit;
import org.extremecomponents.table.limit.LimitFactory;
import org.extremecomponents.table.limit.TableLimit;
import org.extremecomponents.table.limit.TableLimitFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * This controller class is to handle the request relating to create/download report activities
 *
 * @author Dewmini
 * @version 2.1
 */
@Controller
public class ReportController {

    protected static Logger log = Logger.getLogger(ReportController.class);
    @Autowired
    ReportViewManager reportViewManager;
    @Autowired
    UserManager userManager;
    @Autowired
    CorporateManager corporateManager;
    @Autowired
    LobManager lobManager;
    @Autowired
    CampaignManager campaignManager;
    @Autowired
    BarcodeManager barcodeManager;

    /**
     * Handles and retrieves the DSA report creation page with search criteria
     *
     * @param request - current HTTP request
     * @param model - map data between controller and jsp
     * @return the name of the JSP page to rendered by tiles.xml
     */
    @RequestMapping(value = "/reportDSA", method = {RequestMethod.POST, RequestMethod.GET})
    public String getDSAReportDisplayPage(HttpServletRequest request, Map model) {
        try {
            request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.REPORTS_TAB);
            log.debug("Received request to show download page");
            ReportDTO reportDTO = new ReportDTO();
            List<Corporate> corporates = corporateManager.findAllCorporatesByStatus(QrConstants.ACTIVE_AND_INACTIVE);

            model.put("dsaReport", reportDTO);
            model.put(QrConstants.SHOW_DSA_REPORT, false);//in the jsp set the shoe report true, showDSAReport=false; as not create the repoert yet
            reportDTO.setCorporateColumn(true);
            reportDTO.setLobColumn(true);
            reportDTO.setCampainColumn(true);
            reportDTO.setCodeNameColumn(true);
            reportDTO.setCodeSizeColumn(true);
            reportDTO.setDateColumn(true);
            reportDTO.setUserColumn(true);
            reportDTO.setDesigColumn(true);
            reportDTO.setUrlHitColumn(true);
//            String checkedCB = "true";
//            model.put("showCorporateColumn", true);
//            model.put(QrConstants.SHOW_LOB, checkedCB);
//            model.put("showCampaignColumn", checkedCB);
//            model.put(QrConstants.SHOW_CODENAME, true);
//            model.put(QrConstants.SHOW_CODESIZE, true);
//            model.put(QrConstants.SHOW_DATE, true);
//            model.put(QrConstants.SHOW_USER, true);
//            model.put(QrConstants.SHOW_DESIGNATION, true);
//            model.put(QrConstants.SHOW_URL_HIT, true);

            model.put("corporates", corporates);


        } catch (QrException ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
        return "reportDSA";

    }

    /**
     * When user(DSA) click on create report it will navigate to the link reportDSAResult.htm and relevant logic is
     * handle here
     *
     * @param request - current HTTP request
     * @param model - map data between controller and jsp
     * @param reportDTO - current Data Transfer Object relevant to Create Report Form
     * @return - name to be rendered by tiles.xml to show relevant jsp
     */
    @RequestMapping(value = "/reportDSAResult", method = {RequestMethod.POST, RequestMethod.GET})
    public String getDSAReportResultPage(HttpServletRequest request, Map model, @ModelAttribute("dsaReport") ReportDTO reportDTO) {
        try {
            request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.REPORTS_TAB);
            log.debug("Received request to show report result as table page");

            List<Corporate> corporates = corporateManager.findAllCorporatesByStatus(QrConstants.ACTIVE_AND_INACTIVE);

            String corporateId = reportDTO.getCorporateId();
            String lobId = reportDTO.getLobId();
            String campaignId = reportDTO.getCampaignId();
            String codeName = reportDTO.getCodeName();
            String dateCreated = reportDTO.getDateCreated();
            String userName = reportDTO.getUserName();
            String designation = reportDTO.getDesignation();

            corporateId = corporateId.isEmpty() ? "-1" : corporateId;
            lobId = lobId == null ? "" : lobId;
            campaignId = campaignId == null ? "" : campaignId;
            codeName = codeName == null ? "" : codeName;

            //get the define report columns check boxes values(checke or not = true/false)
            boolean corporateColumn = reportDTO.isCorporateColumn();
            boolean lobColumn = reportDTO.isLobColumn();
            boolean campaignColumn = reportDTO.isCampainColumn();
            boolean codeNameColumn = reportDTO.isCodeNameColumn();
            boolean codeSizeColumn = reportDTO.isCodeSizeColumn();
            boolean dateColumn = reportDTO.isDateColumn();
            boolean userColumn = reportDTO.isUserColumn();
            boolean desigColumn = reportDTO.isDesigColumn();
            boolean urlHitColumn = reportDTO.isUrlHitColumn();

            List<ReportView> reportViewList = reportViewManager.getCustomizedReportView(Integer.valueOf(corporateId), lobId, campaignId, codeName, dateCreated, userName, designation);
            model.put("dsaReport", reportDTO);
            model.put("dsaReportView", reportViewList);

            String reqType = request.getParameter("action");
            if (reqType != null && reqType.equalsIgnoreCase("new")) {
                model.put("newReq", true);
                List<LobGroup> lobs = null;
                List<Campaign> camps = null;
                List<String> codeNames = null;
                if (!corporateId.isEmpty() && corporateId != null && !corporateId.equalsIgnoreCase("-1")) {
                    lobs = lobManager.findLobByCorporateId(Integer.valueOf(corporateId));
                }
                if (!lobId.isEmpty() && lobId != null) {
                    camps = campaignManager.findCampaignsByLob(Integer.valueOf(lobId), QrConstants.ACTIVE_AND_INACTIVE);
                }
                if (!campaignId.isEmpty() && campaignId != null) {
                    codeNames = barcodeManager.findBarcodeNamesByCampaign(Integer.valueOf(campaignId));
                }
                model.put("lobs", lobs);
                model.put("campaigns", camps);
                model.put("codeNames", codeNames);
            } else {
                model.put("newReq", false);
            }
            //in the jsp set the shoe report true, showDSAReport=true
            model.put(QrConstants.SHOW_DSA_REPORT, true);
            /*
             * set the deifine column check box values to its prvious state. ie.e when creating the report if only
             * 'corporate column, cmapign column' are checked, after creating report those columns showed as cheked
             * while others are not
             */
            model.put(QrConstants.SHOW_CORPORATE, corporateColumn);
            model.put(QrConstants.SHOW_LOB, lobColumn);
            model.put(QrConstants.SHOW_CAMPAIGN, campaignColumn);
            model.put(QrConstants.SHOW_CODENAME, codeNameColumn);
            model.put(QrConstants.SHOW_CODESIZE, codeSizeColumn);
            model.put(QrConstants.SHOW_DATE, dateColumn);
            model.put(QrConstants.SHOW_USER, userColumn);
            model.put(QrConstants.SHOW_DESIGNATION, desigColumn);
            model.put(QrConstants.SHOW_URL_HIT, urlHitColumn);
            model.put("corporates", corporates);
        } catch (QrException ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
        return "reportDSA";

    }

    /**
     * Handles multi-format report requests for DSA Developed to Display downloaded pdf by using Jasper Reports. This
     * method is no need if download report is done using extreme table (ec:table) framework
     *
     * @param request - current HTTP request
     * @param type - the format of the report, i.e pdf This controller method is combined with the javascript function
     * @param corporateId - corporate id
     * @param lobId - lob id
     * @param campaignId - campaign id
     * @param codeName - code name of barcode
     * @param dateCreated - created date of barcode
     * @param userName - created username of barcode
     * @param designation - created user's designation
     * @param modelAndView - To combine model and view
     * @param model - current model
     * @return - Return the View and the Model combined
     */
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ModelAndView doDSAMultiReport(HttpServletRequest request, @RequestParam("type") String type, int corporateId, String lobId, String campaignId, String codeName, String dateCreated, String userName, String designation,
            ModelAndView modelAndView, ModelMap model) {
        try {
            log.debug("Received request to download multi report");
            log.info("inside download pdf jasper");

            JRDataSource datasource = reportViewManager.getCustomizedDataSource(corporateId, lobId, campaignId, codeName, dateCreated, userName, designation);

            // In order to use Spring's built-in Jasper support, 
            // We are required to pass our datasource as a map parameter

            // Add our datasource parameter
            model.addAttribute("datasource", datasource);
            // Add the report format
            model.addAttribute("format", type);

            // multiReport is the View of our application
            // This is declared inside the /WEB-INF/jasper-views.xml
            modelAndView = new ModelAndView("multiReport", model);


        } catch (QrException ex) {
            log.error(ex);
        } catch (Exception ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            //return QrConstants.ERROR;
        }
        // Return the View and the Model combined
        return modelAndView;
    }

    /**
     * Handles and retrieves the CSA report creation page with search criteria
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/reportCSA", method = {RequestMethod.POST, RequestMethod.GET})
    public String getCSAReportDownloadPage(HttpServletRequest request, Map model) {

        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.REPORTS_TAB);
        try {
            User currentUser = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            Corporate corporate = currentUser.getCorporate();
            List<LobGroup> lobs = lobManager.findLobByCorporateId(currentUser.getCorporate().getCorporateId());
            ReportDTO reportDTO = new ReportDTO();

            model.put("csaReport", reportDTO);
            model.put(QrConstants.SHOW_DSA_REPORT, false);
            model.put("corporate", corporate);
            model.put("lobs", lobs);

            reportDTO.setLobColumn(true);
            reportDTO.setCampainColumn(true);
            reportDTO.setCodeNameColumn(true);
            reportDTO.setCodeSizeColumn(true);
            reportDTO.setDateColumn(true);
            reportDTO.setUserColumn(true);
            reportDTO.setDesigColumn(true);
            reportDTO.setUrlHitColumn(true);
//            model.put(QrConstants.SHOW_LOB, "true");
//            model.put(QrConstants.SHOW_CAMPAIGN, true);
//            model.put(QrConstants.SHOW_CODENAME, true);
//            model.put(QrConstants.SHOW_CODESIZE, true);
//            model.put(QrConstants.SHOW_DATE, true);
//            model.put(QrConstants.SHOW_USER, true);
//            model.put(QrConstants.SHOW_DESIGNATION, true);
//            model.put(QrConstants.SHOW_URL_HIT, true);
        } catch (QrException ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
        return "reportCSA";

    }

    /**
     * When user(CSA) click on create report it will navigate to the link reportCSAResult.htm and relevant logic is
     * handle here
     *
     * @param request - current HTTP request
     * @param model - map data between controller and jsp
     * @param reportDTO - current Data Transfer Object relevant to Create Report Form
     * @return - name to be rendered by tiles.xml to show relevant jsp
     */
    @RequestMapping(value = "/reportCSAResult", method = {RequestMethod.POST, RequestMethod.GET})
    public String getCSAReportResultPage(HttpServletRequest request, Map model, @ModelAttribute("dsaReport") ReportDTO reportDTO) {
        try {
            request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.REPORTS_TAB);
            log.debug("Received request to show report result as table page");
            User currentUser = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            Corporate corporate = currentUser.getCorporate();

            String corporateId = String.valueOf(currentUser.getCorporate().getCorporateId());
            String lobId = reportDTO.getLobId();
            String campaignId = reportDTO.getCampaignId();
            String codeName = reportDTO.getCodeName();
            String dateCreated = reportDTO.getDateCreated();
            String userName = reportDTO.getUserName();
            String designation = reportDTO.getDesignation();

            lobId = lobId == null ? "" : lobId;
            campaignId = campaignId == null ? "" : campaignId;
            codeName = codeName == null ? "" : codeName;

            boolean corporateColumn = reportDTO.isCorporateColumn();
            boolean lobColumn = reportDTO.isLobColumn();
            boolean campaignColumn = reportDTO.isCampainColumn();
            boolean codeNameColumn = reportDTO.isCodeNameColumn();
            boolean codeSizeColumn = reportDTO.isCodeSizeColumn();
            boolean dateColumn = reportDTO.isDateColumn();
            boolean userColumn = reportDTO.isUserColumn();
            boolean desigColumn = reportDTO.isDesigColumn();
            boolean urlHitColumn = reportDTO.isUrlHitColumn();

            List<ReportView> reportViewList = reportViewManager.getCustomizedReportView(Integer.valueOf(corporateId), lobId, campaignId, codeName, dateCreated, userName, designation);
            model.put("csaReport", reportDTO);
            model.put("csaReportView", reportViewList);

            String reqType = request.getParameter("action");
            if (reqType != null && reqType.equalsIgnoreCase("new")) {
                model.put("newReq", true);
                List<LobGroup> lobs = null;
                List<Campaign> camps = null;
                List<String> codeNames = null;
                if (!corporateId.isEmpty() && corporateId != null && !corporateId.equalsIgnoreCase("-1")) {
                    lobs = lobManager.findLobByCorporateId(Integer.valueOf(corporateId));
                }
                if (!lobId.isEmpty() && lobId != null) {
                    camps = campaignManager.findCampaignsByLob(Integer.valueOf(lobId), QrConstants.ACTIVE_AND_INACTIVE);
                }
                if (!campaignId.isEmpty() && campaignId != null) {
                    codeNames = barcodeManager.findBarcodeNamesByCampaign(Integer.valueOf(campaignId));
                }
                model.put("lobs", lobs);
                model.put("campaigns", camps);
                model.put("codeNames", codeNames);
            } else {
                model.put("newReq", false);
            }
            model.put(QrConstants.SHOW_DSA_REPORT, true);
            model.put(QrConstants.SHOW_CORPORATE, corporateColumn);
            model.put(QrConstants.SHOW_LOB, lobColumn);
            model.put(QrConstants.SHOW_CAMPAIGN, campaignColumn);
            model.put(QrConstants.SHOW_CODENAME, codeNameColumn);
            model.put(QrConstants.SHOW_CODESIZE, codeSizeColumn);
            model.put(QrConstants.SHOW_DATE, dateColumn);
            model.put(QrConstants.SHOW_USER, userColumn);
            model.put(QrConstants.SHOW_DESIGNATION, desigColumn);
            model.put(QrConstants.SHOW_URL_HIT, urlHitColumn);
            model.put("corporate", corporate);
            // This will resolve to /WEB-INF/jsp/dsa/reportDSA.jsp
        } catch (QrException ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
        return "reportCSA";

    }

    /**
     * Handles multi-format report requests for CSA. added by Dewmini
     *
     * @param type the format of the report, i.e pdf
     */
    @RequestMapping(value = "/downloadCReport", method = RequestMethod.GET)
    public ModelAndView doCSAMultiReport(HttpServletRequest request, @RequestParam("type") String type, String lobId, String campaignId, String codeName, String dateCreated, String userName, String designation,
            ModelAndView modelAndView, ModelMap model) {
        try {
            log.debug("Received request to download multi report for CSA");

            User currentUser = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));

            JRDataSource datasource = reportViewManager.getCustomizedDataSource(currentUser.getCorporate().getCorporateId(), lobId, campaignId, codeName, dateCreated, userName, designation);

            // Add our datasource parameter
            model.addAttribute("datasource", datasource);
            // Add the report format
            model.addAttribute("format", type);

            modelAndView = new ModelAndView("multiReportCSA", model);

            // Return the View and the Model combined
        } catch (QrException ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
        } catch (Exception ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            //return QrConstants.ERROR;
        }
        return modelAndView;

    }
}

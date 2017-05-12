/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package controllers;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import lk.dialog.corporate.Qr.data.*;
import lk.dialog.corporate.Qr.dto.BarCodeDataStoreDTO;
import lk.dialog.corporate.Qr.dto.CreateBarcodeDTO;
import lk.dialog.corporate.Qr.exception.QrException;
import lk.dialog.corporate.Qr.manager.*;
import lk.dialog.corporate.Qr.utils.QrCommonUtil;
import lk.dialog.corporate.Qr.utils.QrConstants;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * This controller class is to handle the request relating to barcode activities eg. Show create barcode page, view
 * barcodes, Create Bulk Archives on barcodes
 *
 * @author Dewmini
 * @version 2.0
 */
@Controller
public class BarcodeController {

    static Logger log = Logger.getLogger(BarcodeController.class);
    @Autowired
    private CampaignManagerImpl campaignManager;
    @Autowired
    private UserManager userManager;
    @Autowired
    BarcodeManager barcodeManager;
    @Autowired
    private LobManager lobManager;
    @Autowired
    private CorporateManager corporateManager;
    private static final String CREATE_BARCODE = "createBarcode";
    private static final String USER_CREATE_BARCODE = "userCreateBarcode";
    private static final String CREATE_BULK_BC = "createBulkBC";
    private static final String DOWNLOAD_BULK_ARCHIVES = "downloadBulkArchives";

    /**
     * Process the request and return a String object which the DispatcherServlet will render. This method execute when
     * user(CSA/CA) access the create barcode page(createBarcode.htm)(added by dewmini)
     *
     * @param request - current HTTP request
     * @param model - mapping form actions to the controller and set form values
     * @return the name to be rendered by the relevant JSP page(in tiles.xml)
     */
    @RequestMapping(value = "/createBarcode", method = RequestMethod.GET)
    public String showFormBarcode(HttpServletRequest request, Map model) {
        try {
            //get current logged in user
            User user = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));

            //user action write to log file
            log.info(QrCommonUtil.getLogMessage((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), user.getCorporate().getCorporateAccount(), user.getRole().getRoleType(), user.getUserName(), "user access the create barcode page "));

            request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.BARCODE_TAB);
            CreateBarcodeDTO createBarcode = new CreateBarcodeDTO();
            model.put("lobs", lobManager.findLobByCorporateId(user.getCorporate().getCorporateId()));
            model.put("createBarcode", createBarcode);

        } catch (QrException ex) {
            log.error((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + " Error occured while user access create barcode page", ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return CREATE_BARCODE;

        } catch (Exception ex) {
            log.error((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + " Error occured while user access create barcode page", ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return CREATE_BARCODE;

        }
        return CREATE_BARCODE;
    }

    /**
     * Process the request to 'show create barcode' page by CU and return a String object which the DispatcherServlet
     * will render.(added by dewmini)
     *
     * @param request - current HTTP request
     * @param model - mapping form actions to the controller and set form values
     * @return the name to be rendered by the relevant JSP page(in tiles.xml)
     */
    @RequestMapping(value = "/userCreateBarcode", method = RequestMethod.GET)
    public String showFormBarcodeCU(HttpServletRequest request, Map model) {
        try {
            request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.BARCODE_TAB);

            User user = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));

            //user action write to log file
            log.info(QrCommonUtil.getLogMessage((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), user.getCorporate().getCorporateAccount(), user.getRole().getRoleType(), user.getUserName(), "user access the userCreateBarcode page "));

            CreateBarcodeDTO createBarcode = new CreateBarcodeDTO();
            model.put("createBarcode", createBarcode);
            //put the active campaigns to the model which are assigned for the logeed in user
            if (user != null) {
                model.put("lobName", user.getLobGroup().getLobName() == null ? "No Lob Found" : user.getLobGroup().getLobName());
                model.put("campaigns", campaignManager.findCampaignsByUserId(user.getUserId(), QrConstants.ACTIVE));
            } else {
                model.put(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
                return QrConstants.ERROR;
            }
        } catch (QrException ex) {
            log.error((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + " Error occured while user(CU) access create barcode page", ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return USER_CREATE_BARCODE;

        } catch (Exception ex) {

            log.error((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + " Error occured while user(CU) access create barcode page", ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.ERROR;

        }
        return USER_CREATE_BARCODE;
    }

    /**
     * Process the request to 'show create bulk barcode' page by CSA/CA/CU and return a String object which the
     * DispatcherServlet will render.(added by dewmini)
     *
     * @param request - current HTTP request
     * @param model - mapping form actions to the controller and set form values
     * @return the name to be rendered by the relevant JSP page(in tiles.xml)
     */
    @RequestMapping(value = "/createBulkBC", method = RequestMethod.GET)
    public String showFormBarcodeBulk(HttpServletRequest request, Map model) {
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.BARCODE_TAB);
        try {

            User user = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            //user action write to log file
            log.info(QrCommonUtil.getLogMessage((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), user.getCorporate().getCorporateAccount(), user.getRole().getRoleType(), user.getUserName(), "user access the createBulkBC page "));

            if (user.getRole().getRoleType().equals(QrConstants.CORPORATE_SUPER_ADMIN) || user.getRole().getRoleType().equals(QrConstants.CORPORATE_ADMIN)) {
                model.put("lobs", lobManager.findLobByCorporateId(user.getCorporate().getCorporateId()));
            }
            if (user.getRole().getRoleType().equals(QrConstants.CORPORATE_USER)) {
                //model.put("lobs", user.getLobGroup());
                model.put("campaigns", campaignManager.findCampaignsByUserId(user.getUserId(), QrConstants.ACTIVE));
            }
            return CREATE_BULK_BC;
        } catch (Exception ex) {
            log.error((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + " Error occured while user access Create Bulk barcode page", ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
        //return "createBulkBC";
    }

    /**
     * Process the request and return a String object which the DispatcherServlet will render. This method execute after
     * user submit uploading a csv file to generate bulk barcodes First request goes to upload.jsp file and by
     * upload.jsp file it will be redirected to the downloadBulkArchives.htm (added by dewmini)
     *
     * @param request - current HTTP request
     * @param model - mapping form actions to the controller and set form values
     * @return the name to be rendered by the relevant JSP page(in tiles.xml)
     */
    @RequestMapping(value = "/downloadBulkArchives", method = RequestMethod.GET)
    public String showDownloadBulkArchives(HttpServletRequest request, Map model) {
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.BARCODE_TAB);
        try {
            //user action write to log file
            log.info(QrCommonUtil.getLogMessage((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), "no param yet", "no param yet", "no param yet", "user access the downloadBulkArchives page "));

            return DOWNLOAD_BULK_ARCHIVES;
        } catch (Exception ex) {
            log.error((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + " Error occured while access download barcode - Bulk Archives", ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
    }

    /**
     * Check whether the codeName entered by the user is already exists within the campaign (within the campaign code
     * name is unique) (added by dewmini)
     *
     * @param request - current HTTP request
     * @param codeName - entered by the user
     * @param campaignId - the barcode belongs
     * @return String message if code name exists(or error occurred)
     */
    @RequestMapping(value = "/checkCodeName", method = RequestMethod.GET)
    public @ResponseBody
    String checkCodeName(HttpServletRequest request, @RequestParam String codeName, int campaignId) {
        String msg = "";
        try {
            //user action write to log file
            log.info(QrCommonUtil.getLogMessage((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), "no param yet", "no param yet", "no param yet", "check user given code name already exists"));

            String bcName = barcodeManager.findBarcodeName(codeName, campaignId);
            if (bcName != null && bcName.equalsIgnoreCase(codeName)) {
                msg = "This code name " + codeName + " already exists.";
            }
        } catch (QrException ex) {
            log.error((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + "Check for barcode's codeName - already exists", ex);
            msg = "Code name check was unsuccessfull. Try Again!";
            return msg;

        } catch (Exception ex) {

            log.error((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + "Check for barcode's codeName - already exists", ex);
            msg = "Code name check was unsuccessfull. Try Again!";
            return msg;

        }
        return msg;
    }

    /**
     * Check whether the codeName entered (when creating bulk barcodes) by the user is already exists within the
     * campaign (within the campaign code name is unique) This is done with the different method as bulk code name
     * changes in an incremental fashion eg. if BulkCode name = test_bulk , if 5 barcodes in the csv file CodeNames for
     * the 5 barcodes will be, test_bulk_1,test_bulk_2,test_bulk_3,test_bulk_4,test_bulk_5 So check with database if the
     * codeName like test_bulk% available or not (added by dewmini)
     *
     * @param request - current HTTP request
     * @param codeName - entered by the user
     * @param campaignId - the barcode belongs
     * @return String message if code name exists(or error occurred)
     */
    @RequestMapping(value = "/checkBulkCodeName", method = RequestMethod.GET)
    public @ResponseBody
    String checkBulkCodeName(HttpServletRequest request, @RequestParam String codeName, int campaignId) {
        String msg = "";
        try {
            //user action write to log file
            log.info(QrCommonUtil.getLogMessage((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), "no param yet", "no param yet", "no param yet", "check user given BULK code name already exists"));

            String bcName = barcodeManager.findBulkBarcodeName(codeName, campaignId);
            if (bcName != null && bcName.equalsIgnoreCase(codeName)) {
                msg = "This bulk code name '" + codeName + "' already exists.";
            }
        } catch (QrException ex) {
            log.error((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + "Check for barcode's bulk codeName - already exists", ex);
            msg = "Code name check was unsuccessfull. Try Again!";
            return msg;

        } catch (Exception ex) {

            log.error((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + "Check for barcode's bulk codeName - already exists", ex);
            msg = "Code name check was unsuccessfull. Try Again!";
            return msg;

        }
        return msg;
    }

    /**
     *
     * Method execute when user access view all barcodes (viewAllBarcodes.htm) (added by Dewmini)
     *
     * @param request - current HTTP request
     * @param model - mapping form actions to the controller and set form values
     * @return the name to be rendered by the relevant JSP page(in tiles.xml)
     */
    @RequestMapping(value = "/viewAllBarcodes", method = {RequestMethod.GET})
    public String showViewAllBarcodes(HttpServletRequest request, Map model) {
        try {
            //get current logged in user
            User user = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));

            //user action write to log file
            log.info(QrCommonUtil.getLogMessage((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), user.getCorporate().getCorporateAccount(), user.getRole().getRoleType(), user.getUserName(), "user access View all barcodes"));

            request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.BARCODE_TAB);
            Campaign campaign = campaignManager.findCampaignByCampaignId(Integer.valueOf(request.getParameter("campaignId")));
            List<BarCodeDataStoreDTO> barcodeList = null;
            if (campaign != null) {
                barcodeList = barcodeManager.loadBarcodeImages(campaign.getBarcodes());
            }
            model.put("campaign", campaign);
            model.put("imageList", barcodeList);
        } catch (QrException ex) {
            model.put(QrConstants.ERRORMESSAGE, "Cannot process your request at this moment :" + ex.getMessage());
            log.error((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + " User tried to access - view all barcodes", ex);
            return QrConstants.ERROR;

        } catch (Exception ex) {

            log.error((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + " User tried to access - view all barcodes", ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;

        }
        return "viewAllBarcodes";
    }

    /**
     * After user view campaign it will show the list of CodeNames within the campaign and this method execute when user
     * click on the CodeName(added by Dewmini)
     *
     * @param request - current HTTP request
     * @param model - mapping form actions to the controller and set form values
     * @param codeName -
     * @param campaignId - user selected
     * @return - html snippet which contain the barcode(s) info with image(s) relevant to clicked codeName
     */
    @RequestMapping(value = "/loadBarcodesByCampaignAndCodeName", method = {RequestMethod.GET})
    public @ResponseBody
    String showBarcodesByCampaignAndCodeName(HttpServletRequest request, Map model, @RequestParam String codeName, Integer campaignId) {
        String html = " ";
        try {
            //get current logged in user
            User user = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));

            //user action write to log file
            log.info(QrCommonUtil.getLogMessage((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), user.getCorporate().getCorporateAccount(), user.getRole().getRoleType(), user.getUserName(), "user access View barcodes, by click on code name"));

            request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.BARCODE_TAB);
            List<BarCodeDataStoreDTO> barcodeImgList = null;
            List<Barcode> barcodeSet = new ArrayList<Barcode>();
            barcodeSet = barcodeManager.findBarcodeByCampaignAndCodeName(codeName, campaignId);
            barcodeImgList = barcodeManager.loadBarcodeImages(barcodeSet);
            html = prepareBarcodeImages(barcodeImgList, campaignId);

        } catch (QrException ex) {
            log.error((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + " User tried to - load barcodes, when click on code name", ex);
            html = "Couldn't load Barcode Images...Please Try again later!";
        } catch (Exception ex) {

            log.error((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + " User tried to - load barcodes, when click on code name", ex);
            html = "Couldn't load Barcode Images...Please Try again later!";

        }
        return html;
    }

    /**
     * This method get the information of the barcode i.e. barcodeId,title,image byte stream, image type, code name With
     * above data it will create the barcode image(s) from the image byte stream Currently used in to prepare output
     * when user click on code name to view barcodes(added by dewmini)
     *
     * @param barcodeList - List of barcode containing information according to BarCodeDataStoreDTO
     * @param campaignId - is passed to be used when deleting barcodes
     * @return String of the html output to be displayed
     */
    private String prepareBarcodeImages(List<BarCodeDataStoreDTO> barcodeList, Integer campaignId) {
        StringBuilder imagesDisplay = new StringBuilder();

        if (barcodeList != null && !barcodeList.isEmpty()) {

            for (BarCodeDataStoreDTO bcDTO : barcodeList) {

                imagesDisplay.append("<div class=\"imageBox\">"
                        + "<br> <img src=\"data:image/" + bcDTO.getImageType() + ";base64," + bcDTO.getImage() + "\" />"
                        + "<br><label>" + bcDTO.getTitle() + "</label>"
                        + "<br><input type='submit' name='buttonSave' value='Delete' class='button' onClick=deleteBarcode('" + bcDTO.getBarcodeId() + "','" + campaignId + "') /> <br>"
                        + "</div>");
            }

        } else {
            imagesDisplay.append("Couldn't found barcodes for your request!");
        }
        return imagesDisplay.toString();
    }

    /**
     * delete a selected barcode. added by dilusha
     *
     * @param request
     * @param barcodeId id of the barcode which is
     * @return success or failure message to the ajax call
     */
    @RequestMapping(value = "/deleteBarcode", method = RequestMethod.GET)
    public @ResponseBody
    String deleteBarcode(HttpServletRequest request, @RequestParam String barcodeId) {

        String msg = "";
        try {

            Barcode barcode = barcodeManager.findBarcodeByID(barcodeId);
            if (barcode != null) {
                Campaign cam = barcode.getCampaign();
                barcodeManager.deleteBarcode(barcode);
                for (int i = 0; i < barcode.getCampaign().getBarcodes().size(); i++) {
                    if (cam.getBarcodes().contains(barcode)) {
                        boolean x = barcode.getCampaign().getBarcodes().remove(barcode);
                    }
                }
                msg = "yes";
            } else {
                msg = "no";
            }

        } catch (Exception ex) {
            log.error((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + " User tried to - delete barcode", ex);
            msg = "no";

        }

        return msg;
    }

    /**
     * show dynamic url selection page(as a button inside create barcode webpage). added by Dilusha
     *
     * @param request
     * @param model to insert the lobs,campaigns to the web form
     * @return dynamicURL webpage
     */
    @RequestMapping(value = "/dynamicURL", method = {RequestMethod.POST, RequestMethod.GET})
    public String showDynamicURL(HttpServletRequest request, Map model) {

        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.BARCODE_TAB);
        try {

            User user = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            String userType = user.getRole().getRoleType();
            if (userType.equalsIgnoreCase(QrConstants.CORPORATE_ADMIN) || userType.equalsIgnoreCase(QrConstants.CORPORATE_SUPER_ADMIN)) {
                model.put("lobs", lobManager.findLobByCorporateId(user.getCorporate().getCorporateId()));
            } else {
                model.put("lob", user.getLobGroup().getLobName());
                model.put("campaigns", campaignManager.findCampaignsByUserId(user.getUserId(), QrConstants.ACTIVE));
            }
            model.put("userType", userType);
            return "dynamicURL";
        } catch (QrException ex) {
            log.error((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + " User tried to - access Dynamic URL page", ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {

            log.error((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + " User tried to - access Dynamic URL page", ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
    }

    /**
     * processing dynamic url modification process. added by Dilusha
     *
     * @param request
     * @param barcodeId barcode id of the dynamic url barcode
     * @param appendingString new dynamicurl value
     * @return successful or unsuccessful processing indication
     */
    @RequestMapping(value = "/processDynamicURL.htm", method = RequestMethod.GET)
    public @ResponseBody
    String processDynamicUrl(HttpServletRequest request, @RequestParam String barcodeId, @RequestParam String appendingString) {

        try {
            request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.BARCODE_TAB);

            Barcode barcode = barcodeManager.findBarcodeByID(barcodeId);

            barcodeManager.modifyRedirectUrl(appendingString, barcode);


        } catch (Exception ex) {
            log.error((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + " User tried to - modify a dynamic URL", ex);
            return "no";
        }

        return "yes";
    }

    /**
     * For a selected Campaign of a particular LOB, Display drop down box of relevant CodeNames of the barcodes created
     * within that campaign. Drop down box is prepared at prepareCodeNamesForReport(List<String> codeNames) method. This
     * method is currently using, when reporting pages are getting loaded.(added by Dewmini)
     *
     * @param request - current HTTP request
     * @param model - mapping form actions to the controller and set form values
     * @return - String containing html code snippet i.e. Drop down box of code names
     */
    @RequestMapping(value = "/loadCodeNamesForReport", method = RequestMethod.GET)
    public @ResponseBody
    String loadCodeNamesForReport(HttpServletRequest request, Map model) {
        String html = null;
        try {
            log.debug((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + " Create Drop down of codeNames - show in reports page");

            List<String> codeNames = barcodeManager.findBarcodeNamesByCampaign(Integer.valueOf(request.getParameter("campaignId")));

            html = prepareCodeNamesForReport(codeNames);

        } catch (QrException ex) {
            log.error((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + "tried to create Drop down of codeNames", ex);
            html = "Couldn't load the campaigns!";
        } catch (Exception ex) {

            log.error((String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + "tried to create Drop down of codeNames", ex);
            html = "Couldn't load the campaigns!";
        }

        return html;
    }

    /**
     * When list of code names passed, It will be created a drop down box of the code names.
     *
     * @param codeNames - List of code names
     * @return - Drop down box of code names as a String
     */
    private String prepareCodeNamesForReport(List<String> codeNames) {
        StringBuilder dropDown = new StringBuilder();

        if (codeNames != null && !codeNames.isEmpty()) {
            int i = 1;
            dropDown.append("<select name=\"codeName\" id=\"codeName\"  class=\"selectbox\">"
                    + "<option value=\"\">Any</option>");
            for (String cname : codeNames) {
                dropDown.append("<option value=\"" + cname + "\" >" + cname + "</option>");
                i++;
            }
            dropDown.append("</select>");
        } else {
            dropDown.append("<select name=\"codeName\" id=\"codeName\"  class=\"selectbox\">"
                    + "<option value=\"\">Any</option>"
                    + "</select><br> No Code(s)!");
        }
        return dropDown.toString();
    }
}

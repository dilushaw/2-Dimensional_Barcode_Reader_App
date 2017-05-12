package controllers;

import Email.EmailSender;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lk.dialog.corporate.Qr.data.Corporate;
import lk.dialog.corporate.Qr.data.LobGroup;
import lk.dialog.corporate.Qr.dto.UserLoginDTO;
import lk.dialog.corporate.Qr.data.User;
import lk.dialog.corporate.Qr.dto.*;
import lk.dialog.corporate.Qr.exception.QrException;
import lk.dialog.corporate.Qr.manager.CampaignManager;
import lk.dialog.corporate.Qr.manager.CorporateManager;
import lk.dialog.corporate.Qr.manager.LobManager;
import lk.dialog.corporate.Qr.manager.UserManager;
import lk.dialog.corporate.Qr.translator.DomainToFormTranslater;
import lk.dialog.corporate.Qr.translator.FormToDomainTranslater;
import lk.dialog.corporate.Qr.utils.QrConstants;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import logic.barcodes.Barcode;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.ModelAndView;

/**
 *
 * This class is to handle main actions of 2DBC system eg. login, logout, define home page according to user type.
 * Relevant controller action for generating barcode also contains here as a phase 1 development while with minor
 * changes happened after phase 2 development was begun.
 *
 * @author nadeeka
 * @author Dewmini
 * @author Dilusha
 * @version 2.2
 */
//July15,2012 Dewmini-Modified login action(while validating the user) through purely on spring framework, prviously it was done via ajax call
//Sept15,2012 Dilusha - Added new method to forcefully change password for the user's first time log in
//Jan 03,2013 Dilusha - Reset Password, Forgot Password functionalities were added 
@Controller
@SessionAttributes({"user", "userAccessLevel", "imagePaths"})
public class MainController {

    static Logger log = Logger.getLogger(MainController.class);
    @Autowired
    private UserManager userManager;
    @Autowired
    private CorporateManager corporateManager;
    @Autowired
    FormToDomainTranslater translater;
    @Autowired
    private Barcode barcode;
    @Autowired
    LobController lobController;
    @Autowired
    CampaignController campaignController;
    @Autowired
    CorporateController corporateController;
    @Autowired
    DomainToFormTranslater domainToFormtranslater;
    @Autowired
    EmailSender email;
    private static final String LOGIN = "login";
    private static final String LOGGED_USER_SCREEN = "loggeduserscreen";
    private static final String LOGIN_FORM = "loginForm";
    private static final String CHANGEPASSWORDNEWUSER = "changePasswordNewUser";

    @RequestMapping(value = "/getBarcodeParamsDefault", method = RequestMethod.GET)
    public @ResponseBody
    String getBarcodeParamsDefault(HttpServletRequest request, @RequestParam String ipAddress, String barcodeAction, String webSiteAddress, String message, String contactName, String contactPhone, String contactEmail, String organization, String sms, String call, String barcodeType, String barCodeSize, String errorCorrection, String title, String ImageType, ModelMap model, String smsmessage) {
        String result = "0";
        //Barcode barcode = new Barcode();
        result = barcode.getDefaultBarcodeParams(ipAddress, barcodeAction, webSiteAddress, message, contactName, contactPhone, contactEmail, organization, sms, call, barcodeType, barCodeSize, errorCorrection, title, ImageType, smsmessage);

        log.info("Servlet path " + request.getContextPath());

        String[] resultArray = result.split("\\|");
        model.addAttribute("imagePaths", resultArray[0]);
        /**
         *
         * following code can be used when image wants to be created dynamically display directly from image byte
         * stream. Currently it was done by using the image byte stream image is saved at a predefined location and give
         * the image path to the relevant location. This approach may be not a good practice when more and more request
         * are getting to create barcode, size of the directory given for image save may rapidly increase, system is
         * keeping vast amount of unnecessary data
         */
        //String value = "<img src=\"data:image/jpg;base64," + result + "\" /><br>" + title;
        //return value;
        return resultArray[1];
    }

    @RequestMapping(value = "/getBarcodeParamsLogged", method = RequestMethod.GET)
    public @ResponseBody
    String getBarcodeParamsLogged(HttpServletRequest request, @RequestParam String user, String userAccessLevel, String ipAddress, String barcodeAction, String webSiteAddress, String dynamicwebSiteAddress, String message, String contactName, String contactPhone, String contactEmail, String organization, String sms, String call, String barcodeType, String barcodeLength, String barcodeWidth, String imageType, String errorCorrection, String title, ModelMap model, String smsmessage, Integer campaignId, String account, String type, String codeName) {
        log.debug("username:" + user + "-account" + account);

        String result = "ok";
        //  Barcode barcode = new Barcode();
        result = barcode.getLoggedBarcodeParams(user, userAccessLevel, ipAddress, barcodeAction, webSiteAddress, dynamicwebSiteAddress, message, contactName, contactPhone, contactEmail, organization, sms, call, barcodeType, barcodeLength, barcodeWidth, imageType, errorCorrection, title, smsmessage, campaignId, account, codeName);//added(17/8/2012): paas argument ,campaignId
        String[] resultArray = result.split("\\|");
        model.addAttribute("imagePaths", resultArray[0]);
        log.debug("--------------------: " + resultArray[1]);
        return resultArray[1];
    }

    /**
     * Following entries are added as best practice of Spring MVC (with phase2 development)*
     */
    /**
     * Controller method to show login page. added by Dewmini
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String showForm(Map model, HttpServletRequest request) {
        log.info("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + " Show login page");
        UserLoginDTO loginForm = new UserLoginDTO();
        log.debug("Created loggin form DTO");
        model.put(LOGIN_FORM, loginForm);
        return LOGIN;
    }

    /**
     * Handle user login action and display specific home page according to the user role, if user provide invalid data
     * for login it is redirected back to login page with proper message.(added by Dewmini)
     *
     * @param request - current HTTP request
     * @param loginForm - Data Transfer Object for Login Form
     * @param result - bind the result according to the validation applied - eg. username - should not be empty
     * @param model - mapping form actions to the controller and set form values
     * @return - relevant string to render by jsp page according to the user level and validations.
     */
    @RequestMapping(value = "/home", method = {RequestMethod.POST, RequestMethod.GET})
    public String processForm(HttpServletRequest request, @Valid @ModelAttribute("loginForm") UserLoginDTO loginForm, BindingResult result,
            Map model) {
        try {
            //UserProfileDTO userProfileDTO = null;

            model.put(LOGIN_FORM, loginForm);
            HttpSession session = request.getSession();
            /*
             * BindingResult get the values from UserLoginDTO and validate the fields as described in the UserLoginDTO
             * and display if result has error display the predifned message accrding to messages.properties file
             *
             */
            if (result.hasErrors()) {
                return LOGIN;
            }

            User user = translater.translateLoginFormToUser(loginForm);
            user = userManager.loadLoginUser(user);

            if (user != null) {

                /*
                 * Check the corporate license end date is greater than the current date and change the corporate,user
                 * staus as expired and not allowed to login. This relevant code snipet is added by Dilusha
                 */
                Corporate corporate = user.getCorporate();
                Date currentdate = new Date();
                Date endDate = corporate.getLicenseEndDate();
                if (!(corporate.getCorporateAccount().equalsIgnoreCase("dialog"))
                        && (endDate.before(currentdate))) {
                    corporateManager.updateCorporateandUserStatus(corporate, 2);
                    model.put(QrConstants.ERRORMESSAGE, "Inactive Login. Contact Administrator!");
                    return LOGIN;
                }
                //end corporate license end date check

                int status = user.getUserStatus();
                if (status == 0) {
                    model.put(QrConstants.ERRORMESSAGE, "Inactive Login. Contact Administrator!");
                    return LOGIN;
                }
                if (status == 3) {//status=3 indicates deleted. hence display message same for the invlid user.
                    model.put(QrConstants.ERRORMESSAGE, "Invalid User name /Password /Account");
                    return LOGIN;
                }
                /*
                 * Check the user's last login date and if it is null means he is a firt time login user Hence force to
                 * change his/her password. This relevant code snipet is added by Dilusha
                 *
                 */
                if (user.getLastLoginDate() == null || user.getUserStatus() == QrConstants.PASSWORDRESET) {
                    session.setAttribute(QrConstants.USER_NAME, loginForm.getUserName());
                    session.setAttribute(QrConstants.ACCOUNT, loginForm.getAccount());
                    session.setAttribute(QrConstants.USER_ROLE, user.getRole().getRoleType());
                    session.setAttribute(QrConstants.LOGGED_USER, true);
                    Map<String, String> priviMap = userManager.prparePriviForSecurity(user.getUserPrivilegeses());
                    session.setAttribute(QrConstants.PRIVILEGE_MAP, priviMap);
                    UserProfileDTO userProfileDTO = domainToFormtranslater.translateUserProfile(user.getCorporate(), user);

                    model.put("changePasswordNewUser", userProfileDTO);
                    return CHANGEPASSWORDNEWUSER;
                }

            }
            if (user == null) { // Not a valid user
                model.put(QrConstants.ERRORMESSAGE, "Invalid User name /Password /Account");
                return LOGIN;
            }

            // Setting session values to use in header page
            session.setAttribute(QrConstants.USER_NAME, loginForm.getUserName());
            session.setAttribute(QrConstants.ACCOUNT, loginForm.getAccount());
            session.setAttribute(QrConstants.USER_ROLE, user.getRole().getRoleType());
            session.setAttribute(QrConstants.LOGGED_USER, true);

            //update user login date time 
            user.setLastLoginDate(Calendar.getInstance().getTime());
            userManager.updateUser(user);

            //handle user privileges: set current user privileges to the session
            Map<String, String> priviMap = userManager.prparePriviForSecurity(user.getUserPrivilegeses());

            session.setAttribute(QrConstants.PRIVILEGE_MAP, priviMap);

            String roleType = user.getRole().getRoleType();
            if (roleType != null) {
                if (roleType.equals(QrConstants.DIALOG_SUPER_ADMIN)) {

                    /*
                     * get only active and inactive user count for the corporates as when delete a user status is
                     * changed to 3,we need to eleminate such users from the user count. hence only calculate for user
                     * with status 1/0(active/inactive)
                     */
                    corporateController.showDSADashboard(request, model);
                } else if (roleType.equals(QrConstants.CORPORATE_SUPER_ADMIN)) {

                    lobController.showCorporateDashboard(request, model);
                } else if (roleType.equals(QrConstants.CORPORATE_ADMIN)) {

                    lobController.showCorporateDashboard(request, model);
                } else if (roleType.equals(QrConstants.CORPORATE_USER)) {
                    //campaignController.showCUDashboard(request, model);
                    return "forward:/cuDashboard.htm";
                } else {
                    model.put(QrConstants.ERRORMESSAGE, " Undefined role type :" + roleType);
                    return LOGIN;
                }
            } else {
                model.put(QrConstants.ERRORMESSAGE, "Cannot find the role type");
                return LOGIN;

            }
            /*
             * Create userManager and check the user is valid or not(You might return a user object from the DB. If user
             * objectis null he is not a valid user. if it is not null he is a valid user)Then you can call again
             * manager to load his previlages using returned objectHence forth you can handle the front end display
             * logic
             *
             */
            log.info("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + "User Found, username:" + user.getUserName() + " And Role type is " + user.getRole().getRoleType());
        } catch (QrException ex) {
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + "Error occured while logged in/access home page", ex);
            return LOGIN;

        } catch (Exception ex) {

            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + "Error occured while logged in/access home page", ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return LOGIN;

        }

        return LOGGED_USER_SCREEN;

    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, Map model, SessionStatus sessionStatus) {
        log.info("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + " account:" + (String) request.getSession().getAttribute(QrConstants.ACCOUNT) + " username:" + (String) request.getSession().getAttribute(QrConstants.USER_NAME) + " User tries Logout from the system");

        model.remove(LOGIN_FORM);

        request.getSession().removeAttribute(QrConstants.USER_NAME);
        request.getSession().removeAttribute(QrConstants.ACCOUNT);
        request.getSession().removeAttribute(QrConstants.USER_ROLE);
        request.getSession().removeAttribute(QrConstants.LOGGED_USER);
        request.getSession().removeAttribute(QrConstants.PRIVILEGE_MAP);
        //request.getSession().removeAttribute(QrConstants.IP_ADDRESS);

        sessionStatus.setComplete();

        UserLoginDTO loginForm = new UserLoginDTO();
        model.put(LOGIN_FORM, loginForm);
        log.info("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS) + "User Logout from the system");

        return LOGIN;
    }

    /**
     * Show form to change password by user login for the first time. added by Dilusha
     *
     * @param request
     * @param model
     * @return
     * @throws QrException
     */
    @RequestMapping(value = "/changePasswordNewUser", method = RequestMethod.GET)
    public String showChangePasswordNewUser(HttpServletRequest request, Map model) throws QrException {

        UserProfileDTO userProfileDTO = new UserProfileDTO();

        model.put("changePasswordNewUser", userProfileDTO);
        return CHANGEPASSWORDNEWUSER;
    }

    @RequestMapping(value = "/changePasswordNewUser", method = {RequestMethod.POST})
    public String newUserChangePassword(HttpServletRequest request, @Valid @ModelAttribute("changePasswordNewUser") UserProfileDTO userProfileDTO, BindingResult result,
            Map map, Model model) {
        try {
            HttpSession session = request.getSession();
            map.put("changePasswordNewUser", userProfileDTO);

            User user = null;
            //try {
            // user = userManager.findUserByUserNameAndAccount(userProfileDTO.getUserName(), userProfileDTO.getCorporateAccount());
            user = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));






            if ((userProfileDTO.getOldpassword() != "") & (userProfileDTO.getNewpassword() != "") & (userProfileDTO.getRetypepassword() != "")) {
                if (userProfileDTO.getOldpassword().equals(user.getPassword())) {
                    if (userProfileDTO.getNewpassword().length() > 10) {
                        model.addAttribute(QrConstants.ERRORMESSAGE, "Password length should be less than 10");
                        return CHANGEPASSWORDNEWUSER;
                    }
                    if (userProfileDTO.getNewpassword().equals(userProfileDTO.getRetypepassword())) {

                        corporateManager.updatePassword(userProfileDTO, (String) request.getSession().getAttribute("account"), user.getUserName());

                    } else {
                        model.addAttribute(QrConstants.ERRORMESSAGE, "Passwords dont match");
                        return CHANGEPASSWORDNEWUSER;
                    }
                } else {
                    model.addAttribute(QrConstants.ERRORMESSAGE, "Wrong password;Please enter the correct password");
                    return CHANGEPASSWORDNEWUSER;
                }

                session.setAttribute(QrConstants.USER_NAME, user.getUserName());
                session.setAttribute(QrConstants.ACCOUNT, user.getCorporate().getCorporateAccount());
                session.setAttribute(QrConstants.USER_ROLE, user.getRole().getRoleType());

                session.setAttribute(QrConstants.LOGGED_USER, true);
                //  model.addAttribute(QrConstants.SUCCESSMESSAGE, "You have successfully changed your password");
            } else {
                model.addAttribute(QrConstants.ERRORMESSAGE, "Please Enter valid values");
                return CHANGEPASSWORDNEWUSER;
            }



            user.setLastLoginDate(Calendar.getInstance().getTime());
            userManager.updateUser(user);

            //handle user privileges: set current user privileges to the session
            Map<String, String> priviMap = userManager.prparePriviForSecurity(user.getUserPrivilegeses());

            session.setAttribute(QrConstants.PRIVILEGE_MAP, priviMap);

            String roleType = user.getRole().getRoleType();
            if (roleType != null) {
                if (roleType.equals(QrConstants.DIALOG_SUPER_ADMIN)) {
                    corporateController.showDSADashboard(request, map);
                } else if (roleType.equals(QrConstants.CORPORATE_SUPER_ADMIN)) {

                    lobController.showCorporateDashboard(request, map);
                } else if (roleType.equals(QrConstants.CORPORATE_ADMIN)) {

                    lobController.showCorporateDashboard(request, map);
                } else if (roleType.equals(QrConstants.CORPORATE_USER)) {
                    request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.DASHBOARD_TAB);
                    campaignController.showCUDashboard(request, map);
                } else {
                    map.put(QrConstants.ERRORMESSAGE, " Undefined role type :" + roleType);
                    return LOGIN;
                }
            }
        } catch (QrException ex) {
            map.put(QrConstants.ERRORMESSAGE, "Cannot process your request at this moment :" + ex.getMessage());
            //Logger.getLogger(CorporateController.class.getName()).log(Level.SEVERE, null, ex);
            return CHANGEPASSWORDNEWUSER;

        } catch (Exception ex) {

            //Logger.getLogger(CorporateController.class.getName()).log(Level.SEVERE, null, ex);
            map.put(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return CHANGEPASSWORDNEWUSER;

        }
        return LOGGED_USER_SCREEN;

    }

    /**
     * to display the forgot password page to user,added by Dilusha
     *
     * @param request
     * @param model to bind the forgotpassword DTO,here we used the same DTO which has been used for corporate creation
     * to eliminate code reuse
     * @return the forgot password webpage
     */
    @RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
    public String showForgotPassword(HttpServletRequest request, Map model) {

        CoporateCreationDTO forgotPasswordDTO = new CoporateCreationDTO();

        model.put(QrConstants.FORGOTPASSWORD, forgotPasswordDTO);
        return QrConstants.FORGOTPASSWORD;
    }

    /**
     * this is to reset password of a user,can be done by via Dialog super admin,added by Dilusha
     *
     * @param request
     * @param model to bind the DTO and messages
     * @return the reset password webpage
     */
    @RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
    public String showResetPassword(HttpServletRequest request, Map model) {

        CoporateCreationDTO resetPasswordDTO = new CoporateCreationDTO();
        model.put(QrConstants.RESETPASSWORD, resetPasswordDTO);
        return QrConstants.RESETPASSWORD;
    }

    /**
     * forgot password process is handled here,added by Dilusha
     *
     * @param request
     * @param forgotPasswordDTO here we used the same DTO which has been used for corporate creation to eliminate code
     * reuse
     * @param map to bind the DTO
     * @param model to bind the messages
     * @return forgot password web page with a relevant message
     */
    @RequestMapping(value = "/forgotPassword", method = {RequestMethod.POST})
    public String forgotPassword(HttpServletRequest request, @Valid @ModelAttribute(QrConstants.FORGOTPASSWORD) CoporateCreationDTO forgotPasswordDTO,
            Map map, Model model) {
        map.put(QrConstants.FORGOTPASSWORD, forgotPasswordDTO);
        User user = null;
        try {
            user = userManager.findUserByUserNameAndAccount(forgotPasswordDTO.getUserName(), forgotPasswordDTO.getCorporateAccount());
//to generate random password

            //         user.setPassword(translater.randomPasswordGenerator()); 
            if (user == null) {

                model.addAttribute(QrConstants.ERRORMESSAGE, "No corporate user account exists with these user name and account,Please enter correct user details");
                return QrConstants.FORGOTPASSWORD;
            } else {
                // for now password will be set to dialog
                user.setPassword("dialog");
                user.setUserStatus(QrConstants.PASSWORDRESET);
                userManager.updateUser(user);

            }

        } catch (QrException ex) {
            //Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            model.addAttribute(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.FORGOTPASSWORD;
        } catch (Exception ex) {
            //Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            model.addAttribute(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.FORGOTPASSWORD;
        }

        model.addAttribute(QrConstants.SUCCESSMESSAGE, "Your Password Has been reset to 'dialog'");


        return QrConstants.FORGOTPASSWORD;
    }

    /**
     * reset password process is handled here,added by Dilusha
     *
     * @param request
     * @param forgotPasswordDTO here we used the same DTO which has been used for corporate creation to eliminate code
     * reuse
     * @param map to bind the DTO
     * @param model to bind the messages
     * @return forgot password web page with a relevant message
     */
    @RequestMapping(value = "/resetPassword", method = {RequestMethod.POST})
    public String resetPassword(HttpServletRequest request, @Valid @ModelAttribute(QrConstants.RESETPASSWORD) CoporateCreationDTO resetPasswordDTO,
            Map map, Model model) {
        map.put(QrConstants.RESETPASSWORD, resetPasswordDTO);
        User user = null;
        try {
            user = userManager.findUserByUserNameAndAccount(resetPasswordDTO.getUserName(), resetPasswordDTO.getCorporateAccount());
//         user.setPassword(translater.randomPasswordGenerator());

            if (user == null) {
                model.addAttribute(QrConstants.ERRORMESSAGE, "No corporate user account exists with these user name and account,Please enter correct user details");
                return QrConstants.RESETPASSWORD;
            } else {
                user.setPassword("dialog");
                user.setUserStatus(QrConstants.PASSWORDRESET);
                userManager.updateUser(user);
            }
        } catch (QrException ex) {
            //Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            model.addAttribute(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.RESETPASSWORD;
        }
//model.addAttribute(QrConstants.SUCCESSMESSAGE, "Corporate User Password Has been reset and sent to user's e-mail account "+user.getEmail());
        model.addAttribute(QrConstants.SUCCESSMESSAGE, "Corporate User Password Has been reset to 'dialog' ");
        return QrConstants.SUCCESS;

    }
}

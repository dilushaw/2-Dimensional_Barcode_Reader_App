/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import lk.dialog.corporate.Qr.data.Role;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import lk.dialog.corporate.Qr.dto.CreateUserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lk.dialog.corporate.Qr.data.*;
import lk.dialog.corporate.Qr.dto.UserProfileDTO;
import lk.dialog.corporate.Qr.exception.QrException;
import lk.dialog.corporate.Qr.manager.CorporateManager;
import lk.dialog.corporate.Qr.manager.CorporateManagerImpl;
import lk.dialog.corporate.Qr.manager.LobManager;
import lk.dialog.corporate.Qr.manager.RoleManager;
import lk.dialog.corporate.Qr.manager.UserManager;
import lk.dialog.corporate.Qr.manager.CampaignManager;
import lk.dialog.corporate.Qr.manager.UserLogsManager;
import lk.dialog.corporate.Qr.translator.DomainToFormTranslater;
import lk.dialog.corporate.Qr.translator.FormToDomainTranslater;
import lk.dialog.corporate.Qr.utils.QrConstants;
import org.apache.log4j.Logger;

/**
 *
 * @author Hasala Controller class for Corporate User Date: 30/07/2012
 */
@Controller
public class UserController {

    static Logger log = Logger.getLogger(UserController.class);
    private static final String CU_FORM = "createuser";
    private static final String ALL_CORPORATE_USER = "allCorporateUsers";
    private static final String EDIT_USER = "editUser";
    private static final String VIEW_USER = "viewUser";
    private static final String USERPROFILE = "userProfile";
    private static final String CHANGEPASSWORD = "changePassword";
    private static final String VIEW_CORPORATE_ADMIN = "viewCorporateAdmin";
    private static final String CORPORATE_ADMINS = "corporateAdmins";
    private static final String CORPORATE_USERS = "corporateUsers";
    @Autowired
    private CorporateManager corporateManager;
    @Autowired
    private UserManager userManager;
    @Autowired
    private LobManager lobManager;
    @Autowired
    private RoleManager roleManager;
    @Autowired
    private CampaignManager campaignManager;
    @Autowired
    private DomainToFormTranslater domainToFormtranslater;
    @Autowired
    FormToDomainTranslater formToDomainTranslater;
    @Autowired
    private UserLogsManager userLogsManger;

    /**
     * Show create user form. (added by Hasala)
     *
     * @param request - Current HTTP Request
     * @param model - map controller values to the jap page
     * @return - String to be rendered by the relevant jsp page
     */
    @RequestMapping(value = "/createuser", method = RequestMethod.GET)
    public String showCreateUserForm(HttpServletRequest request, Map model) {
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.MANAGE_TAB);
        try {
            CreateUserDTO createuser = new CreateUserDTO();
            User user = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            model.put("lobs", lobManager.findLobByCorporateId(user.getCorporate().getCorporateId()));
            model.put("rolePrivilegesList", corporateManager.findPrivilegesByRoleId("3"));
            model.put(CU_FORM, createuser);
        } catch (QrException ex) {
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            log.error(ex);
            return QrConstants.ERROR;
        } catch (Exception ex) {
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            log.error(ex);
            return QrConstants.ERROR;
        }

        return CU_FORM;
    }

    /**
     * Controller method for Saving new user and display relevant message and jsp page accordingly. (added by Hasala)
     *
     * @param request - current HTTP request
     * @param createuser - current data transfer object with user input values in create user form
     * @param result - bind the form input values for validations
     * @param model - map the controller values to the jap and jsp input fields values to the controller
     * @return - String of the relevant jsp page
     */
    @RequestMapping(value = "/createuser", method = RequestMethod.POST)
    public String saveCreateUser(HttpServletRequest request, @Valid @ModelAttribute("createuser") CreateUserDTO createuser, BindingResult result,
            Map model) {
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.MANAGE_TAB);
        try {
            model.put("createuser", createuser);

            User admin = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            model.put("lobs", lobManager.findLobByCorporateId(admin.getCorporate().getCorporateId()));
            model.put("rolePrivilegesList", corporateManager.findPrivilegesByRoleId(QrConstants.CORPORATE_USER_ROLE));

            //check user with same username exists within the corporate
            User checkuser = corporateManager.findUserByCorpIDandUserName(admin.getCorporate(), createuser.getUserName());
            if (checkuser != null) {
                model.put("errorMessage", "Another user exists with this user name: " + createuser.getUserName());
                return CU_FORM;
            }

            /*
             * Here check whether user is either assign to a campaign or has the privilege of create campaign; althogh
             * Earlier it was check with below code now this check is already done with jquery validation.
             *
             * When creating a user he must be either assign to a campaign or needs to have 'create campaign' privilege
             * createuser.getCampaignId() == null -> means user not assigned to a campaign createuser.getPrivileges() ->
             * array of user privilegs; 10=Privilege Id of 'create campaign. in privileges table of the DB
             */
            if (createuser.getCampaignId() == null && !Arrays.asList(createuser.getPrivileges()).contains(10)) {
                model.put("errorMessage", "To create a user either he needs to have \"Create Campaigns\" privilege or assign him to a campaign under Lob Group.");
                return CU_FORM;
            }
            User user = formToDomainTranslater.handleCorporateUserBean(createuser);
            user.setCorporate(admin.getCorporate());
            user.setRole(roleManager.findRoleByID(QrConstants.CORPORATE_USER_ROLE));
            user.setCreatedUserId(admin.getUserId());
            user.setDateCreated(Calendar.getInstance().getTime());
            user.setLobGroup(lobManager.findLobByLobId(createuser.getLobId()));

            corporateManager.saveUser(user);
            //assign user privilege
            if (createuser.getPrivileges() != null) {
                if (createuser.getPrivileges().length != 0) {
                    userManager.prepareAndSetPrivilages(createuser.getPrivileges(), user);
                }
            }
            //assign campaigns
            if (createuser.getCampaignId() != null) {
                if (createuser.getCampaignId().length != 0) {
                    userManager.prepareAndSetCampaigns(createuser.getCampaignId(), user);
                }
            }

            corporateManager.saveUser(user);

            model.put(QrConstants.SUCCESSMESSAGE, QrConstants.USER_CREATED);
            return showCreateUserForm(request, model);

        } catch (QrException ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
    }

    /**
     * Show all corporate users to Corporate Admin. (added by Dewmini)
     *
     * @param request - current HTTP request
     * @param model - map controller values to jsp page and vise versa
     * @return - String of the jsp page rendered by tiles.xml
     */
    @RequestMapping(value = "/allCorporateUsers", method = {RequestMethod.POST, RequestMethod.GET})
    public String showAllCUs(HttpServletRequest request, Map model) {
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.MANAGE_TAB);
        try {
            //get cuurrent logged in user.
            User admin = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            model.put("users", corporateManager.findCorporateUsersByCorporateAndRole(admin.getCorporate().getCorporateId(), QrConstants.CORPORATE_USER_ROLE, QrConstants.ACTIVE_AND_INACTIVE));

        } catch (QrException ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
        return ALL_CORPORATE_USER;
    }

    /**
     * Show edit user form with the input fields are filled with the editing user's values. (added by Dewmini)
     *
     * @param request - Current HTTP request
     * @param model - map controller values to the jsp page and vise versa
     * @return - String to be rendered by the tiles.xml to display relevant jsp page
     */
    @RequestMapping(value = "/editUser", method = RequestMethod.GET)
    public String showEditCorporateUser(HttpServletRequest request, Map model) {
        CreateUserDTO createUserDTO;
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.MANAGE_TAB);
        try {

            List<Campaign> lobCamapigns = new ArrayList<Campaign>();
            User admin = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));

            model.put("lobs", lobManager.findLobByCorporateId(admin.getCorporate().getCorporateId()));
            model.put("rolePrivilegesList", corporateManager.findPrivilegesByRoleId("3"));

            //p includes currently assigned privileges for the user
            List<Privileges> p = corporateManager.findPrivilegesByUserId(Long.valueOf(request.getParameter("userId")));
            User user = userManager.findUserByID(Long.valueOf(request.getParameter("userId")));

            //this lobCamapigns includes the list of all the campaigns awailable for the slected user
            //find the lob of the user getting edited, then passing that lobid; return all campaigns belong to that lob.
            if (user.getLobGroup() != null) {
                lobCamapigns = campaignManager.findCampaignsByLob(user.getLobGroup().getLobId(), QrConstants.ACTIVE);
            }
            //this userCampaigns includes the list of campaigns assigend for the user
            //by passing the user id of the user getting edited; return his currently assigned campaigns
            List<Campaign> userCampaigns = campaignManager.findCampaignsByUserId(Long.valueOf(request.getParameter("userId")), QrConstants.ACTIVE);
            //Corporate corporate = corporateManager.findCorporateByID(Integer.valueOf(request.getParameter("corporateId")));
            createUserDTO = domainToFormtranslater.translateDataToEditCU(p, user, userCampaigns);

            //model.put("currentCorp", corporate);
            model.put("lobCamapigns", lobCamapigns);

            model.put("editUser", createUserDTO);
            model.put("userId", Long.valueOf(request.getParameter("userId")));
        } catch (QrException ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
        return EDIT_USER;
    }

    /**
     * Get the user changed values and update the user is being edited. (added by Hasala)
     *
     * @param request - current HTTP request
     * @param updateUser - DTO for edit user form
     * @param result - bind the DTO values with the validations
     * @param model - map controller values to the jsp page and vise versa
     * @return - relevant String to be rendered by tiles.xml
     */
    @RequestMapping(value = "/editUser", method = RequestMethod.POST)
    public String submitEditCorporateUser(HttpServletRequest request, @Valid @ModelAttribute("editUser") CreateUserDTO updateUser, BindingResult result,
            Map model) {
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.MANAGE_TAB);
        try {
            if ((updateUser.getCampaignId() == null || updateUser.getCampaignId().length == 0) && !Arrays.asList(updateUser.getPrivileges()).contains(QrConstants.CREATE_CAMPAIGN_PRIVILEGE)) {

                model.put("errorMessage", "User must be either assign to a campaign or grant \"Create Campaigns\" privilege.");
                String userId = request.getParameter("userId");
                request.setAttribute("userId", userId);
                return this.showEditCorporateUser(request, model);
            }

            User admin = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            model.put("editUser", updateUser);

            Corporate corp = admin.getCorporate();
            Role userRole = roleManager.findRoleByID(QrConstants.CORPORATE_USER_ROLE);
            User user = corporateManager.findUserByCorpIDandUserName(corp, updateUser.getUserName());
            corporateManager.deleteCorpInUserPrivileges(user);

            userManager.prepareAndSetPrivilages(updateUser.getPrivileges(), user);

            campaignManager.deleteUserCampaignsByUser(user);
            userManager.prepareAndSetCampaigns(updateUser.getCampaignId(), user);

            corporateManager.updateCorparateUser(updateUser, corp, updateUser.getUserName(), userRole);
            userLogsManger.saveEntityActionInLogs(admin, String.valueOf(user.getUserId()), QrConstants.LOG_EDIT_USER, "edit cu");
        } catch (QrException ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {

            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
        model.put(QrConstants.SUCCESSMESSAGE, "You have successfully updated the user");
        return showEditCorporateUser(request, model);
    }

    /**
     * Controller method to display information for the selected user. (added by Dewmini)
     *
     * @param request - Current HTTP request
     * @param model - map controller and jsp values
     * @return - String to be rendered by the tiles.xml
     */
    @RequestMapping(value = "/viewUser", method = RequestMethod.GET)
    public String showViewCorporateUser(HttpServletRequest request, Map model) {
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.MANAGE_TAB);
        try {
            User user = userManager.findUserByID(Long.valueOf(request.getParameter("userId")));
            model.put("user", user);
        } catch (QrException ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
        return VIEW_USER;
    }

    /**
     * View form which display selected corporate admin information. (added by Dewmini)
     *
     * @param request - Current HTTP request
     * @param model - map Controller and jsp values
     * @return - String to be rendered by tiles.xml
     */
    @RequestMapping(value = "/viewCorporateAdmin", method = RequestMethod.GET)
    public String showViewCorporateAdmin(HttpServletRequest request, Map model) {
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.MANAGE_TAB);
        try {
            User ca = userManager.findUserByID(Long.valueOf(request.getParameter("userId")));
            model.put("user", ca);
        } catch (QrException ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {

            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
        return VIEW_CORPORATE_ADMIN;
    }

    /**
     * Get the display of all corporate users to CSA. (added by Dewmini)
     *
     * @param request - Current HTTP request
     * @param model - map controller and jsp
     * @return - String to be rendered by tiles.xml
     */
    @RequestMapping(value = "/corporateUsers", method = {RequestMethod.POST, RequestMethod.GET})
    public String showAllCUsForCSA(HttpServletRequest request, Map model) {
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.MANAGE_TAB);
        try {

            User admin = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            model.put("id", admin.getCorporate().getCorporateId());
            model.put("allUsers", corporateManager.findCorporateUsersByCorporateAndRole(admin.getCorporate().getCorporateId(), QrConstants.CORPORATE_USER_ROLE, QrConstants.ACTIVE_AND_INACTIVE));

        } catch (QrException ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
        return CORPORATE_USERS;
    }

    /**
     * Get the display of all corporate admins to CSA. (added by Dewmini)
     *
     * @param request - Current HTTP request
     * @param model - map controller and jsp
     * @return - String to be rendered by tiles.xml
     */
    @RequestMapping(value = "/corporateAdmins", method = {RequestMethod.POST, RequestMethod.GET})
    public String showAllCAsForCSA(HttpServletRequest request, Map model) {
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.MANAGE_TAB);
        try {
            User admin = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            model.put("id", admin.getCorporate().getCorporateId());
            model.put("allAdmins", corporateManager.findCorporateUsersByCorporateAndRole(admin.getCorporate().getCorporateId(), QrConstants.COPORATE_ADMIN_ROLE, QrConstants.ACTIVE_AND_INACTIVE));

        } catch (QrException ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {

            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
        return CORPORATE_ADMINS;
    }

    /**
     * delete Corporate user by ajax call (added by Dewmini)
     *
     * @param request - Current HTTP Request
     * @param userId - id of the user is going to be deleted
     * @return - relevant message according to the deletion
     */
    @RequestMapping(value = "/deleteUser", method = RequestMethod.GET)
    public @ResponseBody
    String deleteUser(HttpServletRequest request, @RequestParam Long userId) {

        String msg = "";
        try {
            User currentUser = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            User user = userManager.findUserByID(userId);
            if (user != null) {
                user.setUserStatus(QrConstants.DELETED);
                userManager.updateUser(user);
                userLogsManger.saveEntityActionInLogs(currentUser, String.valueOf(userId), QrConstants.LOG_DELETE_USER, "delete cu");
                msg = "yes";
            }
        } catch (QrException ex) {
            log.error(ex);
            msg = "no";

        } catch (Exception ex) {
            log.error(ex);
            msg = "no";
        }
        return msg;
    }

    /**
     * delete Corporate admin by ajax call (added by Dewmini)
     *
     * @param request - Current HTTP Request
     * @param userId - id of the user is going to be deleted
     * @return - relevant message according to the deletion
     */
    @RequestMapping(value = "/deleteCorporateAdmin", method = RequestMethod.GET)
    public @ResponseBody
    String deleteCA(HttpServletRequest request, @RequestParam Long userId) {
        String msg = "";
        try {
            User currentUser = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            User user = userManager.findUserByID(userId);
            if (user != null) {
                user.setUserStatus(QrConstants.DELETED);
                userManager.updateUser(user);
                userLogsManger.saveEntityActionInLogs(currentUser, String.valueOf(userId), QrConstants.LOG_DELETE_USER, "delete ca");
                msg = "yes";
            }
        } catch (QrException ex) {
            log.error(ex);
            msg = "no";

        } catch (Exception ex) {
            log.error(ex);
            msg = "no";

        }
        return msg;
    }

    /**
     * this is to give user the facility of changing their own user profile,added by Dilusha
     *
     * @param request
     * @param model bind the DTO
     * @return user profile webpage
     * @throws QrException
     */
    @RequestMapping(value = "/userProfile", method = RequestMethod.GET)
    public String showUserProfile(HttpServletRequest request, Map model) throws QrException {
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.PROFILE_TAB);
        try {
            UserProfileDTO userProfileDTO;
            User CorporateAdmin = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            Corporate corporate = corporateManager.findCorporateByAccount((String) request.getSession().getAttribute("account"));
            userProfileDTO = domainToFormtranslater.translateUserProfile(corporate, CorporateAdmin);
            model.put("userProfile", userProfileDTO);

        } catch (QrException ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {

            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
        return USERPROFILE;
    }

    /**
     * to handle user profile modification process. added by Dilusha
     *
     * @param request
     * @param userProfileDTO relevant DTO
     * @param result
     * @param map bind the DTO
     * @param model bind the messages
     * @return userprofile web page,or error page in case of unsuccessful operation
     * @throws QrException
     */
    @RequestMapping(value = "/userProfile", method = RequestMethod.POST)
    public String userprofileProcess(HttpServletRequest request, @Valid @ModelAttribute("userProfile") UserProfileDTO userProfileDTO, BindingResult result,
            Map map, Model model) throws QrException {
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.PROFILE_TAB);
        try {
            map.put("userProfile", userProfileDTO);
            if (result.hasErrors()) {
                log.debug(result.getAllErrors().get(0));
            }
            corporateManager.updateuserProfile(userProfileDTO, corporateManager.findCorporateByAccount(userProfileDTO.getCorporateAccount()), userProfileDTO.getUserName());
        } catch (QrException ex) {
            log.error(ex);
            map.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {

            log.error(ex);
            map.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }

        model.addAttribute(QrConstants.SUCCESSMESSAGE, "Successfully Updated your Profile");


        return USERPROFILE;

    }

    /**
     * password change in the user profile,which is avialble for all the users including DSA,Added by Dilusha
     *
     * @param request
     * @param model to bind the relevant DTO
     * @return change password web page
     * @throws QrException
     */
    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    public String showChangePassword(HttpServletRequest request, Map model) throws QrException {
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.PROFILE_TAB);
        UserProfileDTO userProfileDTO;
        try {
            User user = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            Corporate corporate = corporateManager.findCorporateByAccount((String) request.getSession().getAttribute("account"));
            userProfileDTO = domainToFormtranslater.translateUserProfile(corporate, user);
            model.put("changePassword", userProfileDTO);
        } catch (QrException ex) {
            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {

            log.error(ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
        return CHANGEPASSWORD;
    }

    /**
     * once the user submit the change password this method handle the process,added by Dilusha
     *
     * @param request
     * @param userProfileDTO
     * @param result
     * @param map use to bind the DTO
     * @param model to bind the messages
     * @return change password web page with a message
     * @throws QrException
     */
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public String changePassword(HttpServletRequest request, @Valid @ModelAttribute("changePassword") UserProfileDTO userProfileDTO, BindingResult result,
            Map map, Model model) throws QrException {
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.PROFILE_TAB);
        try {
            map.put("changePassword", userProfileDTO);
            User user = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            if (user == null) {
            } else {
                if ((userProfileDTO.getOldpassword() != "") & (userProfileDTO.getNewpassword() != "") & (userProfileDTO.getRetypepassword() != "")) {
                    if (userProfileDTO.getOldpassword().equals(user.getPassword())) {
                        if (userProfileDTO.getNewpassword().length() > 10) {
                            model.addAttribute(QrConstants.ERRORMESSAGE, "Password length should be less than 10");
                            return CHANGEPASSWORD;
                        }
                        if (userProfileDTO.getNewpassword().equals(userProfileDTO.getRetypepassword())) {

                            corporateManager.updatePassword(userProfileDTO, (String) request.getSession().getAttribute("account"), user.getUserName());
                            model.addAttribute(QrConstants.SUCCESSMESSAGE, "You have successfully changed your password");
                        } else {
                            model.addAttribute(QrConstants.ERRORMESSAGE, "Passwords dont match");
                            return CHANGEPASSWORD;
                        }
                    } else {
                        model.addAttribute(QrConstants.ERRORMESSAGE, "Wrong password;Please enter the correct password");
                        return CHANGEPASSWORD;
                    }


                } else {
                    model.addAttribute(QrConstants.ERRORMESSAGE, "Please Enter valid values");
                    return CHANGEPASSWORD;
                }
            }
        } catch (QrException ex) {
            log.error(ex);
            map.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {

            log.error(ex);
            map.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
        return CHANGEPASSWORD;
    }

    @RequestMapping(value = "/checkOldPassword", method = RequestMethod.GET)
    public @ResponseBody
    String checkOldPassword(HttpServletRequest request, @RequestParam String oldpassword, Map model) {
        String msg = "";
        try {
            User user = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            if (oldpassword != null && !oldpassword.equals(user.getPassword())) {
                //msg = "Old password is incorrect!";
                msg = "true";
            }
        } catch (QrException ex) {
            log.error(ex);
            msg = "Modify password was unsuccessfull. Try Again!";
            return msg;

        } catch (Exception ex) {

            log.error(ex);
            msg = "Modify password was unsuccessfull. Try Again!";
            return msg;

        }
        return msg;
    }

    /**
     * When creating new user check the username given is already exists by ajax call (added by Dewmini)
     *
     * @param request - Current HTTP request
     * @param userName - given username to be check
     * @return - true if username exists
     */
    @RequestMapping(value = "/checkUserName", method = RequestMethod.GET)
    public @ResponseBody
    String checkUserName(HttpServletRequest request, @RequestParam String userName) {
        String msg = "";
        try {
            //User user = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));
            String account = (String) request.getSession().getAttribute("account");
            User userCheck = userManager.findUserByUserNameAndAccount(userName, account);
            if (userCheck != null) {
                //msg = "Old password is incorrect!";
                msg = "true";
            }
        } catch (QrException ex) {
            log.error(ex);
            msg = "User name exist check was unsuccessfull. Try Again!";
            return msg;

        } catch (Exception ex) {

            log.error(ex);
            msg = "User name exist check was unsuccessfull. Try Again!";
            return msg;

        }
        return msg;
    }
}

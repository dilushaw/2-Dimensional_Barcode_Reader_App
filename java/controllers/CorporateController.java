/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lk.dialog.corporate.Qr.data.*;
import lk.dialog.corporate.Qr.dto.*;
import lk.dialog.corporate.Qr.exception.QrException;
import lk.dialog.corporate.Qr.manager.*;
import lk.dialog.corporate.Qr.translator.DomainToFormTranslater;
import lk.dialog.corporate.Qr.translator.FormToDomainTranslater;
import lk.dialog.corporate.Qr.utils.QrConstants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * This class handles all the corporate based functions
 *
 * @author Dilusha Weeraddana
 * @version 2.0
 */
@Controller
public class CorporateController {

    static Logger log = Logger.getLogger(CorporateController.class);
    @Autowired
    private DomainToFormTranslater domainToFormtranslater;
    @Autowired
    private FormToDomainTranslater formtoDomaintranslater;
    @Autowired
    private FormToDomainTranslater formtoDomainCreateCorporate;
    @Autowired
    private FormToDomainTranslater formtoDomainEditAdmin;
    @Autowired
    private CorporateManager corporateManager;
    @Autowired
    private RoleManager roleManager;
    @Autowired
    private Role role;
    @Autowired
    private UserManagerImpl userManager;
    @Autowired
    UserLogsManager userLogsManager;
    private Vector<Privileges> privileges;
    private Vector<Object> corperateCreationVector;
    private static final String CREATECORPORATE = "createCorporate";
    private static final String EDITCORPORATE = "editCorporate";
    private static final String VIEWCORPORATE = "viewCorporate";
    private static final String CREATECORPORATEADMIN = "createCorporateAdmin";
    private static final String EDITCORPORATEADMIN = "editCorporateAdmin";
    private static final String DSA_DASHBOARD = "dsaDashboard";

    /**
     * This method displays the create corporate admin web form to the user.added by Dilusha
     *
     * @param map to add the relevant DTO to the form
     *
     * @return corporate creation admin form
     */
    @RequestMapping(value = "/createCorporateAdmin", method = RequestMethod.GET)
    public String showCreateCorporateAdmin(HttpServletRequest request, Map map, Model model) {
        CoporateAdminCreationDTO createCorporateAdminDTO = null;
        try {
            String currentUserRole = (String) request.getSession().getAttribute(QrConstants.USER_ROLE);
            request.setAttribute(QrConstants.CURRENT_TAB, currentUserRole.equals("DSA") ? QrConstants.CORPORATE_TAB : QrConstants.MANAGE_TAB);

            Corporate corporate = corporateManager.findCorporateByID(Integer.valueOf(request.getParameter("id")));
            createCorporateAdminDTO = domainToFormtranslater.translateDataToCorporateAdminCreation(corporate);
            map.put("id", corporate.getCorporateId());
            map.put("corporateAdminCreation", createCorporateAdminDTO);
            map.put("rolePrivileges", corporateManager.findPrivilegesByRoleId("2"));
        } catch (Exception ex) {
            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            model.addAttribute(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.ERROR;
        }

        return CREATECORPORATEADMIN;
    }

    /**
     * This method process the create corporate admin web form once it is submitted by the user.added by Dilusha
     *
     * @param map to add the relevant DTO to the form
     * @param CoporateAdminCreationDTO corporate admin creation DTO object
     * @param model to add the relevant messages
     * @return corporate creation admin form
     */
    @RequestMapping(value = "/createCorporateAdmin", method = RequestMethod.POST)
    public String createCorporateAdmin(HttpServletRequest request, @Valid @ModelAttribute("corporateAdminCreation") CoporateAdminCreationDTO corporateAdminCreation, BindingResult result,
            Map map, Model model) {
        try {
            String currentUserRole = (String) request.getSession().getAttribute(QrConstants.USER_ROLE);
            request.setAttribute(QrConstants.CURRENT_TAB, currentUserRole.equals("DSA") ? QrConstants.CORPORATE_TAB : QrConstants.MANAGE_TAB);

            privileges = new Vector<Privileges>();
            map.put("corporateAdminCreation", corporateAdminCreation);
            if (result.hasErrors()) {
                model.addAttribute(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
                return QrConstants.ERROR;

            }
            String[] corporateAdminPriv = corporateAdminCreation.getPrivileges();


            for (int i = 0; i < corporateAdminPriv.length; i++) {
                String privilegeName = corporateAdminPriv[i];
                privileges.add(corporateManager.getPrivileges(privilegeName));

            }

            Corporate corporate = corporateManager.findCorporateByAccount(corporateAdminCreation.getCorporateAccount());
            List<User> users = corporateManager.findCorporateUsersByCorporate(corporate.getCorporateId());
            boolean userExists = false;
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUserName().equalsIgnoreCase(corporateAdminCreation.getUserName())) {

                    userExists = true;
                }

            }
            if (userExists == false) {


                corperateCreationVector = formtoDomaintranslater.translateCorporateAdminCreationForm(corporateAdminCreation, privileges, corporate, roleManager.findRoleByID("2"));
                User user = (User) corperateCreationVector.get(1);
                User currentUser = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));//-line added by dewmini
                user.setCreatedUserId(currentUser.getUserId());//line added by dewmini
                user.setDateCreated(Calendar.getInstance().getTime());//line added by dewmini

                corporateManager.saveUser(user);

                //Create a new privilage set to set for user
                Set<UserPrivileges> priviSet = new HashSet<UserPrivileges>();

                for (int i = 2; i < corperateCreationVector.size(); i++) {


                    priviSet.add((UserPrivileges) corperateCreationVector.get(i)); // Add each privilages to set so that it can be used in to set user privilages
                    corporateManager.saveUserPrivileges((UserPrivileges) corperateCreationVector.get(i));
                }


                user.setUserPrivilegeses(priviSet); // Set privilages to user and used it to user-line added by dewmini
                corporateManager.saveUser(user); // Update user object after setting user privilages otherwise it will not persisting on current session -line added by dewmini
                model.addAttribute(QrConstants.SUCCESSMESSAGE, QrConstants.CORPORATE_ADMIN_CREATED);
                request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.CORPORATE_TAB);
                return QrConstants.SUCCESS;
            } else {
                request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.CORPORATE_TAB);
                model.addAttribute(QrConstants.ERRORMESSAGE, "Another user exists with the same User Name :" + corporateAdminCreation.getUserName());
                map.put("id", corporate.getCorporateId());
                map.put("rolePrivileges", corporateManager.findPrivilegesByRoleId("2"));
                return CREATECORPORATEADMIN;
            }
        } catch (QrException ex) {

            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            model.addAttribute(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.ERROR;
        } catch (Exception ex) {

            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            model.addAttribute(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.ERROR;
        }
    }

    /**
     * This method process the reset the corporate admin web form once it is reset by the user.added by Dilusha
     *
     * @param map to add the relevant DTO to the form
     * @param CoporateAdminCreationDTO corporate admin creation DTO object
     * @param model to add the relevant messages
     * @return corporate creation admin form
     */
    @RequestMapping(value = "/createCorporateAdmin", params = "reset", method = RequestMethod.POST)
    public String resetCorporateAdmin(HttpServletRequest request, @Valid @ModelAttribute("corporateAdminCreation") CoporateAdminCreationDTO corporateAdminCreation, BindingResult result,
            Map map, Model model) {

        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.CORPORATE_TAB);
        CoporateAdminCreationDTO createCorporateAdminDTO = null;

        try {

            Corporate corporate = corporateManager.findCorporateByAccount(corporateAdminCreation.getCorporateAccount());
            createCorporateAdminDTO = domainToFormtranslater.translateDataToCorporateAdminCreation(corporate);
            map.put("id", corporate.getCorporateId());
            map.put("corporateAdminCreation", createCorporateAdminDTO);
            map.put("rolePrivileges", corporateManager.findPrivilegesByRoleId("2"));
        } catch (QrException ex) {

            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            model.addAttribute(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.ERROR;
        } catch (Exception ex) {

            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            model.addAttribute(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.ERROR;
        }

        return CREATECORPORATEADMIN;
    }

    /**
     * This method displays the create corporate web form to the user.added by Dilusha
     *
     * @param map to add the relevant DTO to the form
     *
     * @return corporate creation form
     */
    @RequestMapping(value = "/createCorporate", method = RequestMethod.GET)
    public String showCreateCorporate(HttpServletRequest request, Map map) {
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.CORPORATE_TAB);
        CoporateCreationDTO corporateCreation = new CoporateCreationDTO();
        map.put("corporateCreation", corporateCreation);
        return CREATECORPORATE;
    }

    /**
     * This method process the create corporate web form once it is submitted by the user.added by Dilusha
     *
     * @param map to add the relevant DTO to the form
     * @param CoporateCreationDTO corporate creation DTO object
     * @param model to add the relevant messages
     * @return corporate creation form
     */
    @RequestMapping(value = "/createCorporate", params = "buttonSave", method = RequestMethod.POST)
    public String createCorporate(HttpServletRequest request, @Valid @ModelAttribute("corporateCreation") CoporateCreationDTO corporateCreation, BindingResult result,
            Map map, Model model) {
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.CORPORATE_TAB);
        List<String> corporatePrivileges = null;
        try {
            corporatePrivileges = this.getRolePrivilges(QrConstants.SUPER_ADMIN_ROLE);

            map.put("corporateCreation", corporateCreation);

            if (result.hasErrors()) {
                model.addAttribute(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
                return QrConstants.ERROR;
            }

            //here validate Liscence dates for firefox
            DateFormat formatter = new SimpleDateFormat(QrConstants.DATEFORMAT);
            Date currentdate = new Date();
            Date startDate = null;
            Date endDate = null;

            startDate = formatter.parse(corporateCreation.getLicenseStartDate());
            endDate = formatter.parse(corporateCreation.getLicenseEndDate());

            if (endDate.before(startDate)) {
                model.addAttribute(QrConstants.ERRORMESSAGE, "Please ensure that the License End Date is greater than or equals to the License Start Date");
                return CREATECORPORATE;
            }
            if (endDate.before(currentdate)) {
                model.addAttribute(QrConstants.ERRORMESSAGE, "Please ensure that the License End Date is greater than or equals to the Current Date");
                return CREATECORPORATE;
            }

            String message = this.handleCreateCorporate(corporateCreation, corporatePrivileges);
            if (message.equalsIgnoreCase(QrConstants.SUCCESS)) {
                request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.CORPORATE_TAB);
                model.addAttribute(QrConstants.SUCCESSMESSAGE, QrConstants.CORPORATE_CREATED);
                return message;
            } else if (message.equalsIgnoreCase("createCorporateAccountExist")) {

                model.addAttribute(QrConstants.ERRORMESSAGE, "Another Corporate Exists with this Account Name :" + corporateCreation.getCorporateAccount());
                return CREATECORPORATE;
            } else if (message.equalsIgnoreCase("createCorporate")) {

                return CREATECORPORATE;
            } else {
                model.addAttribute(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
                return QrConstants.ERROR;
            }
        } catch (QrException ex) {
            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            model.addAttribute(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.ERROR;
        } catch (Exception ex) {
            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            model.addAttribute(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.ERROR;
        }
    }

    /**
     * This method process the reset the corporate web form once it is reset by the user.added by Dilusha
     *
     * @param map to add the relevant DTO to the form
     * @param CoporateCreationDTO corporate creation DTO object
     * @param model to add the relevant messages
     * @return corporate creation form
     */
    @RequestMapping(value = "/createCorporate", params = "reset", method = RequestMethod.POST)
    public String resetCorporate(HttpServletRequest request, @Valid @ModelAttribute("corporateCreation") CoporateCreationDTO corporateCreation, BindingResult result,
            Map map, Model model) {
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.CORPORATE_TAB);


        CoporateCreationDTO corporateCreationRest = new CoporateCreationDTO();

        map.put("corporateCreation", corporateCreationRest);
        return CREATECORPORATE;
    }

    /**
     * This method to get the privileges of the user by roleId.added by Dilusha
     *
     * @param roleId assigned ID to each role
     * @return list of all the privileges assigned to the role
     */
    private List<String> getRolePrivilges(String roleID) throws QrException {


        role.setRoleId(roleID);
        List<RolePrivilege> rolePrivilege = null;
        List<String> PrivilegesList = new ArrayList<String>();

        rolePrivilege = corporateManager.getRolePrivilege(role);
        for (int i = 0; i < rolePrivilege.size(); i++) {

            PrivilegesList.add((corporateManager.getPrivileges(rolePrivilege.get(i).getPrivileges().getPrivilegeId())).getPrivilegeName());
        }
        return PrivilegesList;

    }

    /**
     * This method checks whether a corporate account is exists.added by Dilusha
     *
     * @param corpAccount corporate account of the user
     * @return whether it is exists or not
     */
    private boolean checkCorporateAccountExist(String corpAccount) throws QrException {
        Corporate corporate = null;

        corporate = corporateManager.checkCorporateAccountExist(corpAccount);

        if (corporate == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * This method handles corporate creation process further.added by Dilusha
     *
     * @param CoporateCreationDTO corporateCreation DTO object
     * @param list of all the privileges of CSA(for now all the privileges are given to CSA)
     * @return Success Page
     */
    private String handleCreateCorporate(CoporateCreationDTO corporateCreation, List<String> corporatePrivileges) throws QrException {

        privileges = new Vector<Privileges>();
        for (int i = 0; i < corporatePrivileges.size(); i++) {


            privileges.add(corporateManager.getPrivileges(corporatePrivileges.get(i)));

        }

        Role role = null;

        role = roleManager.findRoleByID(QrConstants.SUPER_ADMIN_ROLE);

        corperateCreationVector = formtoDomainCreateCorporate.translateCorporateCreationForm(corporateCreation, privileges, role);
        boolean corporateAccountExist = this.checkCorporateAccountExist(((Corporate) corperateCreationVector.get(0)).getCorporateAccount());

        if (corporateAccountExist == true) {

            return "createCorporateAccountExist";
        } else {

            if (corperateCreationVector.size() > 0) {
                corporateManager.saveCorporate((Corporate) corperateCreationVector.get(0));

                User user = (User) corperateCreationVector.get(1);
                corporateManager.saveUser(user);

                Set<UserPrivileges> priviSet = new HashSet<UserPrivileges>();
                for (int i = 2; i < corperateCreationVector.size(); i++) {
                    priviSet.add((UserPrivileges) corperateCreationVector.get(i));
                    corporateManager.saveUserPrivileges((UserPrivileges) corperateCreationVector.get(i));

                }
                user.setUserPrivilegeses(priviSet);
                corporateManager.saveUser(user);

            }


            return QrConstants.SUCCESS;
        }

    }

    /**
     * This method handles corporate deletion process.this inside the edit corporate form.added by Dilusha
     *
     * @param map to add the relevant DTO to the form
     * @param CoporateCreationDTO corporate creation DTO object is used for edit and deletions
     * @param model to add the relevant messages
     * @return Success Page or error page in case of exception occurs
     */
    @RequestMapping(value = "/editCorporate", params = "Delete Corporate", method = RequestMethod.POST)
    public String deleteCorporate(HttpServletRequest request, @ModelAttribute("editCorporate") CoporateCreationDTO corporateCreation, BindingResult result,
            Map map, Model model) {
        try {
            request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.CORPORATE_TAB);
            map.put("deleteCorporate", corporateCreation);
            //get corporate from corporate account
            Corporate corporate = corporateManager.checkCorporateAccountExist(corporateCreation.getCorporateAccount());
            corporate.setCorporateStatus(3);//deleted flag


            this.deleteCorporate(corporate);

            model.addAttribute(QrConstants.SUCCESSMESSAGE, "Successfully deleted");
            return QrConstants.SUCCESS;
        } catch (QrException ex) {
            model.addAttribute(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            return QrConstants.ERROR;

        } catch (Exception ex) {
            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            model.addAttribute(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.ERROR;
        }
    }

    /**
     * This method handles corporate editing process.added by Dilusha
     *
     * @param map to add the relevant DTO to the form
     * @param CoporateCreationDTO corporate creation DTO object is used for edit and deletions
     * @param model to add the relevant messages
     * @return Success Page or error page in case of exception occurs
     */
    @RequestMapping(value = "/editCorporate", params = "Save", method = RequestMethod.POST)
    public String editCorporate(HttpServletRequest request, @Valid @ModelAttribute("editCorporate") CoporateCreationDTO corporateCreation, BindingResult result,
            Map map, Model model) {
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.CORPORATE_TAB);
        map.put("editCorporate", corporateCreation);
        Corporate corporate = null;
        User user = null;
        List<User> ca = null;
        try {
            corporate = corporateManager.findCorporateByAccount(corporateCreation.getCorporateAccount());
            user = corporateManager.findAdminsByCoporateId(corporate.getCorporateId());
            ca = corporateManager.findCorporateUsersByCorporateAndRole(corporate.getCorporateId(), QrConstants.COPORATE_ADMIN_ROLE, QrConstants.ACTIVE_AND_INACTIVE);

            //here validate Liscence dates for firefox
            DateFormat formatter = new SimpleDateFormat(QrConstants.DATEFORMAT);
            Date currentdate = new Date();
            Date startDate = null;
            Date endDate = null;

            startDate = formatter.parse(corporateCreation.getLicenseStartDate());
            endDate = formatter.parse(corporateCreation.getLicenseEndDate());

            log.debug("0 st dt: " + startDate);
            log.debug("0 end dt: " + endDate);
            if (endDate.before(startDate)) {
                model.addAttribute(QrConstants.ERRORMESSAGE, "Please ensure that the License End Date is greater than or equals to the License Start Date");
                map.put("id", corporate.getCorporateId());
                if (user != null) {
                    map.put("coporateAdmins", ca);
                }
                return EDITCORPORATE;
            }
            if (endDate.before(currentdate)) {
                model.addAttribute(QrConstants.ERRORMESSAGE, "Please ensure that the License End Date is greater than or equals to the Current Date");
                map.put("id", corporate.getCorporateId());
                if (user != null) {
                    map.put("coporateAdmins", ca);
                }
                return EDITCORPORATE;
            }
            log.debug("1 st dt: " + corporateCreation.getLicenseStartDate());
            log.debug("1 end dt: " + corporateCreation.getLicenseEndDate());
            corporateManager.updateCorpSuperadminUser(corporateCreation);
            corporateManager.updateCorporate(corporateCreation);

        } catch (QrException ex) {
            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            model.addAttribute(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.ERROR;
        } catch (Exception ex) {
            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            model.addAttribute(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.ERROR;
        }
        model.addAttribute(QrConstants.SUCCESSMESSAGE, "Successfully Updated");
        return QrConstants.SUCCESS;

    }

    /**
     * This method handles corporate admin editing process.added by Dilusha
     *
     * @param map to add the relevant DTO to the form
     * @param CoporateAdminCreationDTO corporate admin creation DTO object is used for edit and deletions
     * @param model to add the relevant messages
     * @return Success Page or error page in case of exception occurs
     */
    @RequestMapping(value = "/editCorporateAdmin", params = "buttonSave", method = RequestMethod.POST)
    public String editCorporateAdmin(HttpServletRequest request, @Valid @ModelAttribute("editCorporate") CoporateAdminCreationDTO corporateAdminCreation, BindingResult result,
            Map map, Model model) {
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.CORPORATE_TAB);
        map.put("editCorporateAdmin", corporateAdminCreation);
        try {
            privileges = new Vector<Privileges>();
            String[] corporateAdminPriv = corporateAdminCreation.getPrivileges();
            for (int i = 0; i < corporateAdminPriv.length; i++) {
                String privilegeName = corporateAdminPriv[i];
                privileges.add(corporateManager.getPrivileges(privilegeName));


            }
            Corporate corporate = corporateManager.findCorporateByAccount(corporateAdminCreation.getCorporateAccount());

            User user = corporateManager.findUserByCorpIDandUserName(corporate, corporateAdminCreation.getUserName());

            corporateManager.deleteCorpInUserPrivileges(user);

            Vector<UserPrivileges> userPrivilegesList = formtoDomainEditAdmin.HandleUserPrivilegeBean(privileges, user);

            Set<UserPrivileges> userPriviSet = new HashSet<UserPrivileges>(); //new  Defined user privilages set-by dewmini

            for (int i = 0; i < userPrivilegesList.size(); i++) {
                userPriviSet.add(userPrivilegesList.get(i)); //new add user privilages to defiend set-by dewmini
                corporateManager.saveUserPrivileges(userPrivilegesList.get(i));
            }

            //new add userPriviSet-by dewmini
            corporateManager.updateCorpadminUser(corporateAdminCreation, corporate, corporateAdminCreation.getUserName(), userPriviSet);
            User currentUser = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));//-line added by dewmini
            userLogsManager.saveEntityActionInLogs(currentUser, String.valueOf(user.getUserId()), QrConstants.LOG_EDIT_USER, "edit ca"); //line added by dewmini


        } catch (QrException ex) {
            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            model.addAttribute(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.ERROR;

        } catch (Exception ex) {
            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            model.addAttribute(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.ERROR;

        }

        model.addAttribute(QrConstants.SUCCESSMESSAGE, "Successfully Updated");
        return QrConstants.SUCCESS;

    }

    /**
     * This method handles corporate admin deletion process.added by Dilusha
     *
     * @param map to add the relevant DTO to the form
     * @param CoporateAdminCreationDTO corporate admin creation DTO object is used for edit and deletions
     * @param model to add the relevant messages
     * @return Success Page or error page in case of exception occurs
     */
    @RequestMapping(value = "/editCorporateAdmin", params = "Delete", method = RequestMethod.POST)
    public String deleteCorporateAdmin(HttpServletRequest request, @ModelAttribute("editCorporate") CoporateAdminCreationDTO corporateAdminCreation, BindingResult result,
            Map map, Model model) {
        request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.CORPORATE_TAB);
        map.put("deleteCorporateAdmin", corporateAdminCreation);
        Corporate corporate;
        try {
            corporate = corporateManager.findCorporateByAccount(corporateAdminCreation.getCorporateAccount());
            User user = corporateManager.findUserByCorpIDandUserName(corporate, corporateAdminCreation.getUserName());
            corporateManager.deleteCorpInUserPrivileges(user);
            corporateManager.deleteUser(user);
            User currentUser = userManager.findUserByUserNameAndAccount((String) request.getSession().getAttribute("userName"), (String) request.getSession().getAttribute("account"));//-line added by dewmini
            userLogsManager.saveEntityActionInLogs(currentUser, String.valueOf(user.getUserId()), QrConstants.LOG_DELETE_USER, "delete ca"); //line added by dewmini

        } catch (QrException ex) {

            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            model.addAttribute(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.ERROR;

        } catch (Exception ex) {

            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            model.addAttribute(QrConstants.ERRORMESSAGE, QrConstants.UNEXPECTEDERROR);
            return QrConstants.ERROR;

        }
        model.addAttribute(QrConstants.SUCCESSMESSAGE, "Successfully Deleted");
        return QrConstants.SUCCESS;
    }

    /**
     * This method handles corporate deletion further.added by Dilusha
     *
     * @param corporate to add the relevant DTO to the form
     * @return Success Page or error page in case of exception occurs
     */
    private void deleteCorporate(Corporate corporate) throws QrException {

        corporateManager.saveCorporate(corporate);
        List<User> users = corporateManager.getUserByCorpID(corporate);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            //  corporateManager.deleteCorpInUserPrivileges(user);
            if (user.getRole().getRoleId().equalsIgnoreCase(QrConstants.CORPORATE_USER_ROLE)) {//if user is corporate user a set flag
                user.setUserStatus(3);
                corporateManager.saveUser(user);
            } else if (user.getRole().getRoleId().equalsIgnoreCase(QrConstants.SUPER_ADMIN_ROLE)) {//if user is the corporate super admin a set flag

                user.setUserStatus(3);
                corporateManager.saveUser(user);
            } else if (user.getRole().getRoleId().equalsIgnoreCase(QrConstants.COPORATE_ADMIN_ROLE)) {//if user is the corporate admin a set flag

                user.setUserStatus(3);
                corporateManager.saveUser(user);
            }
        }
    }

    /**
     * Show dashboard for Dialog Super Admin-DSA which summarizes all corporate , execute when DSA click Dashboard tab
     * and loading DSA's home page(RequestMethod.GET is used to show page), To support with pagination(prev, next)
     * RequestMethod.POST also used (added by dewmini)
     *
     * @param request - current HTTP request
     * @param model - mapping form actions to the controller and set form values
     * @return the name to be rendered by the relevant JSP page(in tiles.xml)
     */
    @RequestMapping(value = "/dsaDashboard", method = {RequestMethod.POST, RequestMethod.GET})
    public String showDSADashboard(HttpServletRequest request, Map model) {
        try {
            request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.DASHBOARD_TAB);

            //List all active and inactive corporate
            model.put("corporates", corporateManager.findAllCorporatesByStatus(QrConstants.ACTIVE_AND_INACTIVE));

            /*
             * get only active and inactive user count for the corporates as when delete a user status is changed to
             * 3,we need to eleminate such users from the user count. hence only calculate for user with status 1/0
             * (active/inactive)
             */
            Map<Integer, Integer> corpUserMap = corporateManager.findCorporateUserCount(QrConstants.ACTIVE_AND_INACTIVE, QrConstants.ACTIVE_AND_INACTIVE);
            model.put("corpUserCount", corpUserMap);
        } catch (QrException ex) {
            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {
            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        }
        return DSA_DASHBOARD;
    }

    /**
     * Show edit form for the particular corporate (added by dewmini)
     *
     * @param request - current HTTP request
     * @param model - mapping form actions to the controller and set form values
     * @return the name to be rendered by the relevant JSP page(in tiles.xml)
     */
    @RequestMapping(value = "/editCorporate", method = RequestMethod.GET)
    public String showEditDeleteCorporateForm(HttpServletRequest request, Map model) {
        CoporateCreationDTO createCorporateDTO;
        try {
            request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.CORPORATE_TAB);
            //id = corporateId passed through the request
            Corporate corporate = corporateManager.findCorporateByID(Integer.valueOf(request.getParameter("id")));
            User user = corporateManager.findAdminsByCoporateId(Integer.valueOf(request.getParameter("id")));
            List<User> ca = corporateManager.findCorporateUsersByCorporateAndRole(Integer.valueOf(request.getParameter("id")), QrConstants.COPORATE_ADMIN_ROLE, QrConstants.ACTIVE_AND_INACTIVE);
            // List<Privileges> p = corporateManager.findPrivilegesByUserId(user.getUserId());
            if (user != null) {
                createCorporateDTO = domainToFormtranslater.translateDataToEditCorporate(user, corporate);

                model.put("id", corporate.getCorporateId());
                model.put("editCorporate", createCorporateDTO);
                model.put("coporateAdmins", ca);
                //model.put("rolePrivileges", corporateManager.findPrivilegesByRoleId("2"));
            } else {
                model.put(QrConstants.ERRORMESSAGE, "No Corporate Super Admin found.");
                return showDSADashboard(request, model);
            }

        } catch (QrException ex) {
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            return showDSADashboard(request, model);
        } catch (Exception ex) {
            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return showDSADashboard(request, model);
        }
        return EDITCORPORATE;
    }

    /**
     * View particular corporate (added by Dewmini)
     *
     * @param request - current HTTP request
     * @param model - mapping form actions to the controller and set form values
     * @return the name to be rendered by the relevant JSP page(in tiles.xml)
     */
    @RequestMapping(value = "/viewCorporate", method = RequestMethod.GET)
    public String showViewCorporateForm(HttpServletRequest request, Map model) {
        try {
            request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.CORPORATE_TAB);
            Corporate corporate = corporateManager.findCorporateByID(Integer.valueOf(request.getParameter("id")));
            User user = corporateManager.findAdminsByCoporateId(Integer.valueOf(request.getParameter("id")));

            model.put("viewCorporate", corporate);
            if (user != null) {
                model.put("userPrivileges", corporateManager.findPrivilegesByUserId(user.getUserId()));
            } else {
                model.put(QrConstants.ERRORMESSAGE, " Cannot process your request at this moment :");
                return showDSADashboard(request, model);
            }


        } catch (QrException ex) {
            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return showDSADashboard(request, model);
        } catch (Exception ex) {

            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return showDSADashboard(request, model);

        }
        return VIEWCORPORATE;
    }

    /**
     * Show form for edit corporate admin (added by dewmini)
     *
     * @param request - current HTTP request
     * @param model - mapping form actions to the controller and set form values
     * @return the name to be rendered by the relevant JSP page(in tiles.xml)
     */
    @RequestMapping(value = "/editCorporateAdmin", method = RequestMethod.GET)
    public String showEditCorporateAdmin(HttpServletRequest request, Map model) throws QrException {
        CoporateAdminCreationDTO coporateAdminCreateDTO;
        try {
            request.setAttribute(QrConstants.CURRENT_TAB, QrConstants.CORPORATE_TAB);
            List<Privileges> p = corporateManager.findPrivilegesByUserId(Long.valueOf(request.getParameter("userId")));
            User user = userManager.findUserByID(Long.valueOf(request.getParameter("userId")));
            Corporate corporate = corporateManager.findCorporateByID(Integer.valueOf(request.getParameter("corporateId")));
            coporateAdminCreateDTO = domainToFormtranslater.translateDataToEditCorporateAdmin(p, user, corporate);
            model.put("currentCorp", corporate);
            model.put("editCorporateAdmin", coporateAdminCreateDTO);
            model.put("rolePrivileges", corporateManager.findPrivilegesByRoleId("2"));
        } catch (QrException ex) {

            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;
        } catch (Exception ex) {

            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            model.put(QrConstants.ERRORMESSAGE, QrConstants.COULDNT_PROCESS);
            return QrConstants.ERROR;

        }
        return EDITCORPORATEADMIN;
    }

    /**
     * This method check the user entered account name is already exists or not(account name is unique within the
     * system).added by Dewmini
     *
     * @param request - current HTTP request
     * @param accountName - user entered account name
     * @param model - mapping form actions to the controller and set form values
     * @return - message if account name already exists or relevant message if error occurred
     */
    @RequestMapping(value = "/checkCorpAccount", method = RequestMethod.GET)
    public @ResponseBody
    String checkCorpAccount(HttpServletRequest request, @RequestParam String accountName, Map model) {
        String msg = "";
        try {
            Corporate corp = corporateManager.findCorporateByAccount(accountName);
            if (corp != null) {

                msg = "true";
            }
        } catch (QrException ex) {
            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            msg = "accountName exists;check was unsuccessfull. Try Again!";
            return msg;

        } catch (Exception ex) {

            log.error("ip:" + (String) request.getSession().getAttribute(QrConstants.IP_ADDRESS), ex);
            msg = "accountName exists;check was unsuccessfull. Try Again!";
            return msg;

        }

        return msg;
    }
}

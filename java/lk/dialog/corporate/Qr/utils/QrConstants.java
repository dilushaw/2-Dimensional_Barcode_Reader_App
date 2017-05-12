/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.utils;

/**
 * This class is to maintain all the constants variables
 *
 * @author Dewmini
 * @version 2.1
 */
public class QrConstants {
//default screen page

    public static final String DEFAULT_SCREEN = "defaultscreen";
    public static final String FORGOTPASSWORD = "forgotPassword";
    public static final String RESETPASSWORD = "resetPassword";
    //constants variables set for the session 
    public static final String USER_NAME = "userName";
    public static final String ACCOUNT = "account";
    public static final String LOGGED_USER = "isLoggedIn";
    public static final String USER_ROLE = "userRole";
    public static final String PRIVILEGE_MAP = "priviMap";
    public static final String IP_ADDRESS = "ipAddress";
    //user roles constants
    public static final String DSA_ROLE = "1";
    public static final String CORPORATE_USER_ROLE = "3";
    public static final String SUPER_ADMIN_ROLE = "4";
    public static final String COPORATE_ADMIN_ROLE = "2";
    public static final String DIALOG_SUPER_ADMIN = "DSA";
    public static final String CORPORATE_SUPER_ADMIN = "CSA";
    public static final String CORPORATE_ADMIN = "CA";
    public static final String CORPORATE_USER = "CU";
    //Entity status constants
    public static final Integer ACTIVE_AND_INACTIVE = -1;//for entity staus=active/inctive (Corporate, user, lob, campaign)
    public static final Integer ACTIVE = 1;//for entity staus=active (Corporate, user, lob, campaign)
    public static final Integer INACTIVE = 0;//for entity staus=inactive (Corporate, user, lob, campaign)
    public static final Integer EXPIRED = 2;//for entity staus=expired (Corporate, user, lob, campaign)
    public static final Integer DELETED = 3;//for entity staus=deleted (Corporate, user, lob, campaign)
    public static final Integer PASSWORDRESET = 4;//for entity staus=deleted (Corporate, user, lob, campaign)
    public static final String SUCCESS = "success";
    public static final String SUCCESSMESSAGE = "successMessage";
    public static final String ERROR = "error";
    public static final String ERRORMESSAGE = "errorMessage";
    public static final String UNEXPECTEDERROR = "Unexpected Error has occurred";
    public static final String COULDNT_PROCESS = "Couldn't process your request at the moment. Please try again later!";
    public static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
    //constants for menu bar tab highlighting
    public static final String CURRENT_TAB = "currentTab";
    public static final String DASHBOARD_TAB = "dashboard";
    public static final String CORPORATE_TAB = "corporate";
    public static final String MANAGE_TAB = "manage";
    public static final String BARCODE_TAB = "barcodes";
    public static final String REPORTS_TAB = "reports";
    public static final String PROFILE_TAB = "profile";
    //constants for user actions
    public static final Integer LOG_EDIT_CORPORATE = 1;
    public static final Integer LOG_DELETE_CORPORATE = 2;
    public static final Integer LOG_EDIT_USER = 11;
    public static final Integer LOG_DELETE_USER = 12;
    public static final Integer LOG_EDIT_LOB = 21;
    public static final Integer LOG_DELETE_LOB = 22;
    public static final Integer LOG_EDIT_CAMPAIGN = 31;
    public static final Integer LOG_DELETE_CAMPAIGN = 32;
    public static final Integer LOG_EDIT_BARCODE = 41;
    public static final Integer LOG_DELETE_BARCODE = 42;
    public static final String SHOW_DSA_REPORT = "showDSAReport";
    public static final String SHOW_CORPORATE = "showCorporateColumn";
    public static final String SHOW_LOB = "showLobColumn";
    public static final String SHOW_CAMPAIGN = "showCampaignColumn";
    public static final String SHOW_CODENAME = "showCodeNameColumn";
    public static final String SHOW_CODESIZE = "showCodeSizeColumn";
    public static final String SHOW_DATE = "showDateColumn";
    public static final String SHOW_USER = "showUserColumn";
    public static final String SHOW_DESIGNATION = "showDesignationColumn";
    public static final String SHOW_URL_HIT = "showUrlHitColumn";
    //constant for privileges
    public static final Integer CREATE_CAMPAIGN_PRIVILEGE = 10;
    //user create messages
    public static final String USER_CREATED = "User successfully created.\n Login password is \"dialog\".\n User will be prompted to change the login password at the time of first login. ";
    public static final String CORPORATE_ADMIN_CREATED = "Corporate Admin successfully created.\n Login password is \"dialog\".\n User will be prompted to change the login password at the time of first login. ";
    public static final String CORPORATE_CREATED = "Corporate and Corporate Super Admin account was successfully created.\n Login password is \"dialog\".\n User will be prompted to change the login password at the time of first login. ";
}

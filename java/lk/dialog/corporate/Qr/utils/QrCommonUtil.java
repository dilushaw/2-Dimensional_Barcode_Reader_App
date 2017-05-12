
package lk.dialog.corporate.Qr.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * This class store common utility behavior throughout the 2dbc functionalities.
 *
 * @author Dewmini
 */
public class QrCommonUtil {
    public static final String IP = "ip:";
    public static final String _ACCOUNT = ", account:";
    public static final String _MESSAGE = ", message:";
    public static final String _USERNAME = ", username:";
    public static final String _USER_ROLE = ", userRole:";

    /**
     * return bellow sample format message and this is going to be used to log meaningful message in to log file
     *
     * ex : - ip:192.168.2.34, account:default, userRole:default, username:default,A new user accessed the site
     *
     * @param ipAddress - current user IP address
     * @param corporateAccount - logged in user's corporate account if not available value is default.
     * @param userRole - logged in user's role if not available value is default.
     * @param userName - logged in user's userName if not available value is default.
     * @param message - Specific text to be logged in to the file (2dbc.log).
     * @return - concatenate message with above fields
     */
    public static String getLogMessage(String ipAddress, String corporateAccount, String userRole, String userName, String message) {

        String logMsg = (IP + ipAddress + _ACCOUNT + corporateAccount + _USER_ROLE + userRole + _USERNAME + userName + _MESSAGE + message);
        return logMsg;
    }
    
    public static String getErrorLogMessage(String ipAddress, String message) {

        String logMsg = (IP + ipAddress + _MESSAGE + message);
        return logMsg;
    }
    
    public String getHostIP(){

    String server_ip="";
    try{
    File url_file = new File("qrServer.xml");
                    FileInputStream fileInput = new FileInputStream(url_file);
                    Properties properties = new Properties();
                    properties.loadFromXML(fileInput);
                    server_ip=properties.getProperty("server_url");

        }

    catch(Exception e){}

    return server_ip;


    }

}

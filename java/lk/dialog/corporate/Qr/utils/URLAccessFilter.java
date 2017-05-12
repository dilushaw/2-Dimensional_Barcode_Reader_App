/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.utils;

import java.io.IOException;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lk.dialog.corporate.Qr.data.User;
import lk.dialog.corporate.Qr.data.UserPrivileges;
import lk.dialog.corporate.Qr.exception.QrException;
import lk.dialog.corporate.Qr.manager.UserManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Dewmini
 */
@Component
public class URLAccessFilter implements Filter {

    static Logger log = Logger.getLogger(URLAccessFilter.class);
    @Autowired
    UserManager userManager;
    private Map<String, String> priviPageKeyUrlMap;
    private ArrayList<String> avoidList;
    private ArrayList<String> dsaUrlList;
    private ArrayList<String> csaUrlList;
    private ArrayList<String> caUrlList;
    private ArrayList<String> cuUrlList;
    private Map<String, List<String>> roleUrlMap;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        String url = httpRequest.getServletPath();
        //avoidList contains the urls that should be avoided from checking, eg: urls common to all user type,ajax calls urls, etc. 
        avoidList = new ArrayList<String>();
        avoidList.add("/defaultscreen.htm");
        avoidList.add("/login.htm");
        avoidList.add("/forgotPassword.htm");
        avoidList.add("/resetPassword.htm");
        avoidList.add("/home.htm");
        avoidList.add("/logout.htm");
        avoidList.add("/unauthorized.htm");
        avoidList.add("/getBarcodeParamsDefault.htm");
        avoidList.add("/getBarcodeParamsLogged.htm");
        avoidList.add("/loadCampaigns.htm");
        avoidList.add("/loadCampaignsCheckBox.htm");
        avoidList.add("/loadCampaignsDropDown.htm");
        avoidList.add("/loadlobsDropDown.htm");
        avoidList.add("/checkCodeName.htm");
        avoidList.add("/checkBulkCodeName.htm");
        avoidList.add("/loadBarcodesByCampaignAndCodeName.htm");
        avoidList.add("/checkUserPrivileges.htm");
        avoidList.add("/checkCampaignName.htm");
        avoidList.add("/loadLobsForReport.htm");
        avoidList.add("/loadCampaignsForReport.htm");
        avoidList.add("/loadCodeNamesForReport.htm");
        avoidList.add("/reportDSAResult.htm");
        avoidList.add("/reportCSAResult.htm");
        avoidList.add("/checkOldPassword.htm");
        avoidList.add("/checkUserName.htm");
        avoidList.add("/checkCorpAccount.htm");
        avoidList.add("/checkLOBName.htm");

        if (null == session) {
            log.info("ip:" + (String) session.getAttribute(QrConstants.IP_ADDRESS) + " Session seems to be expired. or session is null");
            httpResponse.sendRedirect("login.htm");
        } else {
            //if (!url.contains("defaultscreen.htm") && !url.contains("login.htm") && !url.contains("home.htm") && !url.contains("logout.htm") && !url.contains("unauthorized.htm")) {
            if (!avoidList.contains(url)) {
                if (session.getAttribute("userName") != null) {
                    Map<String, String> priviMap = (HashMap<String, String>) session.getAttribute("priviMap");
                    //log.info("url accessing is: " + url);
                    log.debug(QrCommonUtil.getLogMessage((String) session.getAttribute(QrConstants.IP_ADDRESS), (String) session.getAttribute(QrConstants.ACCOUNT), (String) session.getAttribute(QrConstants.USER_ROLE), (String) session.getAttribute(QrConstants.USER_NAME), "url accessing is:" + url));
                    if (url.endsWith(".htm") && (!priviMap.containsKey(priviPageKeyUrlMap.get(url)) && !roleUrlMap.get((String) session.getAttribute(QrConstants.USER_ROLE)).contains(url))) {

                        log.debug(priviMap);
                        log.debug("check.... " + priviPageKeyUrlMap.get(url));
                        // session.setAttribute("isUnautorized", "yes");
                        log.info((String) session.getAttribute(QrConstants.IP_ADDRESS)+" access unauthorized");
                        httpResponse.sendRedirect("unauthorized.htm");
                        return;
                    }
                } else {
                    if (url.endsWith(".htm")) {
                        log.debug("user null-----" + url);
                        httpResponse.sendRedirect("login.htm");

                        return;
                    }
                }
            }
        }


        chain.doFilter(request, response);

    }

    public void init(FilterConfig filterConfig) throws ServletException {

        if (priviPageKeyUrlMap == null) {
            priviPageKeyUrlMap = new HashMap<String, String>();
            priviPageKeyUrlMap.put("/createLob.htm", "createlob");

            priviPageKeyUrlMap.put("/createCampaign.htm", "createcampaigns");
            priviPageKeyUrlMap.put("/userCreateCampaign.htm", "createcampaigns");
            priviPageKeyUrlMap.put("/editCampaign.htm", "editcampaigns");
            priviPageKeyUrlMap.put("/userEditCampaign.htm", "editcampaigns");
            priviPageKeyUrlMap.put("/viewCampaign.htm", "viewcampaigns");
            priviPageKeyUrlMap.put("/deleteCampaign.htm", "deletecamapaigns");

            priviPageKeyUrlMap.put("/createBarcode.htm", "createbarcodes");
            priviPageKeyUrlMap.put("/userCreateBarcode.htm", "createbarcodes");
            priviPageKeyUrlMap.put("/createBulkBC.htm", "createbarcodes");
            priviPageKeyUrlMap.put("/upload.htm", "createbarcodes");
            priviPageKeyUrlMap.put("/downloadBulkArchives.htm", "createbarcodes");
            priviPageKeyUrlMap.put("/viewBarcode.htm", "viewbarcodes");
            priviPageKeyUrlMap.put("/viewAllBarcodes.htm", "viewbarcodes");
            priviPageKeyUrlMap.put("/deleteBarcode.htm", "deletebarcodes");
            priviPageKeyUrlMap.put("/loadDynamicUrlDropDown.htm", "createbarcodes");
            priviPageKeyUrlMap.put("/loadCampaignsDropDownforDynamicUrl.htm", "createbarcodes");
            priviPageKeyUrlMap.put("/dynamicURL.htm", "createbarcodes");
            priviPageKeyUrlMap.put("/processDynamicURL.htm", "createbarcodes");

            priviPageKeyUrlMap.put("/createuser.htm", "createuser");
            priviPageKeyUrlMap.put("/editUser.htm", "edituser");
            priviPageKeyUrlMap.put("/viewUser.htm", "viewuser");
            priviPageKeyUrlMap.put("/deleteUser.htm", "deleteuser");

            priviPageKeyUrlMap.put("/createCorporateAdmin.htm", "createca");
            priviPageKeyUrlMap.put("/editCorporateAdmin.htm", "editca");
            priviPageKeyUrlMap.put("/viewCorporateAdmin.htm", "viewca");
            priviPageKeyUrlMap.put("/deleteCorporateAdmin.htm", "deleteca");

            priviPageKeyUrlMap.put("/createCorporate.htm", "createcorporate");
            priviPageKeyUrlMap.put("/editCorporate.htm", "editcorporate");
            priviPageKeyUrlMap.put("/viewCorporate.htm", "viewcorporate");
            priviPageKeyUrlMap.put("/deleteCorporate.htm", "deletecorporate");

        }

        if (roleUrlMap == null) {
            roleUrlMap = new HashMap<String, List<String>>();
            dsaUrlList = new ArrayList<String>();
            csaUrlList = new ArrayList<String>();
            caUrlList = new ArrayList<String>();
            cuUrlList = new ArrayList<String>();
        }
        //dsa

        String dsaurls = filterConfig.getInitParameter("dsa-urls");
        StringTokenizer dsatoken = new StringTokenizer(dsaurls, ",");

        while (dsatoken.hasMoreTokens()) {
            dsaUrlList.add("/" + dsatoken.nextToken());
        }
        roleUrlMap.put(QrConstants.DIALOG_SUPER_ADMIN, dsaUrlList);


        String csaurls = filterConfig.getInitParameter("csa-urls");
        StringTokenizer csatoken = new StringTokenizer(csaurls, ",");

        while (csatoken.hasMoreTokens()) {
            csaUrlList.add("/" + csatoken.nextToken());
        }
        roleUrlMap.put(QrConstants.CORPORATE_SUPER_ADMIN, csaUrlList);


        String caurls = filterConfig.getInitParameter("ca-urls");
        StringTokenizer catoken = new StringTokenizer(caurls, ",");

        while (catoken.hasMoreTokens()) {
            caUrlList.add("/" + catoken.nextToken());
        }
        roleUrlMap.put(QrConstants.CORPORATE_ADMIN, caUrlList);


        String cuurls = filterConfig.getInitParameter("cu-urls");
        StringTokenizer cutoken = new StringTokenizer(cuurls, ",");

        while (cutoken.hasMoreTokens()) {
            cuUrlList.add("/" + cutoken.nextToken());
        }
        roleUrlMap.put(QrConstants.CORPORATE_USER, cuUrlList);

        //csa
//            String dsaurls = filterConfig.getInitParameter("csa-urls");
//            StringTokenizer dsatoken = new StringTokenizer(dsaurls, ",");
//            urlList = new ArrayList<String>();
//            while (dsatoken.hasMoreTokens()) {
//                dsaurlList.add(dsatoken.nextToken());
//            }
        //ca

        //cu

    }

    public void destroy() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}

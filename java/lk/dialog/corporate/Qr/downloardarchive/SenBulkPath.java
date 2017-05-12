/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.downloardarchive;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lk.dialog.corporate.Qr.utils.QrConstants;
import logic.barcodes.Barcode;
import org.apache.log4j.Logger;

/**
 *
 * @author Anuradha
 * @version 1.1
 */
public class SenBulkPath extends HttpServlet {

    static Logger log = Logger.getLogger(SenBulkPath.class);

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {

            HttpSession session = request.getSession();

            String imagePaths = session.getAttribute("imagePaths").toString();

            log.debug("Bulk image path*****************" + session.getAttribute("imagePaths").toString());

            Barcode create_barcode = new Barcode();

            String[] list = imagePaths.split("\\$");

            ArrayList<String> imgList = new ArrayList<String>(Arrays.asList(list));
            log.debug("Bulk Ayrray List*******" + imgList.get(0));


            String user = (String) session.getAttribute("userName");
            String userAccessLevel = (String) session.getAttribute("userRole");
            String ipAddress = (String) session.getAttribute("ipAddress");
            String corporateAccount = (String) session.getAttribute(QrConstants.ACCOUNT);

            String sZipPath = (imgList.get(0));
            create_barcode.createArchive(user, userAccessLevel, ipAddress, imgList, "../webapps/Qr/" + sZipPath + ".zip", corporateAccount);//userName

            out.print(sZipPath + ".zip");

        } finally {

            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}

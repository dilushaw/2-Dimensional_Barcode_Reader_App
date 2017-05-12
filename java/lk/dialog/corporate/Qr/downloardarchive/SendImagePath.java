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
import logic.barcodes.Barcode;
import org.apache.log4j.Logger;

/**
 *
 * @author Anuradha
 * @version 1.1
 */
public class SendImagePath extends HttpServlet {

    static Logger log = Logger.getLogger(SendImagePath.class);

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();


        try {

            HttpSession session = request.getSession();
            String imagePaths = session.getAttribute("imagePaths").toString();

            log.debug("image path*****************" + session.getAttribute("imagePaths").toString());


            Barcode create_barcode = new Barcode();

            String[] list = imagePaths.split("\\$");
            ArrayList<String> imgList = new ArrayList<String>(Arrays.asList(list));
            log.debug("Ayrray List*******" + imgList.get(0));

            String user = "default";
            String userAccessLevel = "default";
            String ipAddress = "";
            String corporateAccount = "default";


            String sZipPath = (imgList.get(0));


            ipAddress = session.getAttribute("ipAddress").toString();
            create_barcode.createArchive(user, userAccessLevel, ipAddress, imgList, "../webapps/Qr/" + sZipPath + ".zip", corporateAccount);//userNam

            out.print(sZipPath + ".zip");


        } catch (Exception e) {
            log.error(e);
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

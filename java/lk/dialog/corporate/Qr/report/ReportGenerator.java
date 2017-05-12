package lk.dialog.corporate.Qr.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lk.dialog.corporate.Qr.dataaccess.Barcode_category_dao;
import net.sf.jasperreports.engine.JasperRunManager;

/**
 *
 * @author Anuradha
 */
public class ReportGenerator extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//      response.setContentType("text/html;charset=UTF-8");
//      PrintWriter out = response.getWriter();

        response.setContentType("application/pdf");
        InputStream in_stream = null;

        try {

            String datefrm = request.getParameter("datefrom");


            String dateto = request.getParameter("dateto");

            File file = new File("reportdatabase.xml");
            FileInputStream fileInput = new FileInputStream(file);
            Properties properties = new Properties();
            properties.loadFromXML(fileInput);


            Class.forName("com.mysql.jdbc.Driver").newInstance();
// Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/barcodeapplication", "root", "123456");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/barcodeapplication", properties.getProperty("username"), properties.getProperty("password"));

            ServletOutputStream set = response.getOutputStream();
            Map<String, Object> param_map = new HashMap<String, Object>();

            Barcode_category_dao bar_cat_dao = new Barcode_category_dao();
            bar_cat_dao.prepareBarcodeCategoryTable();
            // bar_cat_dao.getBarcodes("2010-09-29 09:43:22","2010-12-29 09:43:22");
            bar_cat_dao.getBarcodes(datefrm, dateto);


            in_stream = getServletConfig().getServletContext().getResourceAsStream("jasper/barcodes_monthly_count.jasper");

            param_map.put("from", datefrm);
            param_map.put("to", dateto);
            JasperRunManager.runReportToPdfStream(in_stream, set, param_map, con);

            set.flush();
            set.close();
            in_stream = null;

        } catch (Exception e) {
        }


        //out.print("prasad");


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

package lk.dialog.corporate.Qr.dataaccess;

import java.util.ArrayList;
import lk.dialog.corporate.Qr.data.Barcode;
import lk.dialog.corporate.Qr.data.BarcodeCategoryJasper;
import lk.dialog.corporate.Qr.report.BarcodeCategory_graph;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Anuradha
 */
public class Barcode_category_dao {

    Transaction tx = null;
    Session session = null;
    ArrayList<String> category_name = null;
    ArrayList<Barcode> barcode_obj = null;
    BarcodeCategory_graph barcde_category_graph = null;

    /*
     * update  barcode quentity in barcode_category_jasper table befor create new report
     * all quentitys are asigned to zero
     */
    public void prepareBarcodeCategoryTable() {

        startTransaction();

        Query q1 = session.createQuery("select  barcodecategory.category from BarcodeCategoryJasper barcodecategory");

        category_name = (ArrayList<String>) q1.list();

        if (category_name.size() > 0) {

            for (int i = 0; i < category_name.size(); i++) {
                try {
                    BarcodeCategoryJasper bar_cagry_row = (BarcodeCategoryJasper) session.get(BarcodeCategoryJasper.class, category_name.get(i));

                    bar_cagry_row.setQuentity(0);
                    updateTableRow(bar_cagry_row);

                } catch (HibernateException e) {
                    tx.rollback();
                    // logger.error(e.toString());
                }
            }
        }

        commitTransaction();
    }

    /*
     * insert data to barcode_category_jasper in respect to "Category Vs Barcode Count" graph
     */
    public void fillBarcodeCategoryTable(int[] quentity) {

        

        String[] types = {"Url", "Sms", "Text", "Contact", "Initiate_Call"};
          //String[] types = {"1", "2","3","4","5"};

        for (int i = 0; i < 5; i++) {

            startTransaction();
            try {
                BarcodeCategoryJasper bar_cagry_row = (BarcodeCategoryJasper) session.get(BarcodeCategoryJasper.class, types[i]);

                bar_cagry_row.setQuentity(quentity[i]);
                updateTableRow(bar_cagry_row);

            } catch (HibernateException e) {
                tx.rollback();
                // logger.error(e.toString());
            }


            commitTransaction();
        }

    }

    /*
     * get barcode list according to the particular time belt
     */
    public void getBarcodes(String start_time, String end_time) {
        java.sql.Timestamp st_time = java.sql.Timestamp.valueOf(start_time);
        java.sql.Timestamp ed_time = java.sql.Timestamp.valueOf(end_time);

        startTransaction();

        Query q1 = session.createQuery("select  barcode from Barcode barcode where barcode.dateCreated<'" + ed_time + "' and barcode.dateCreated>'" + st_time + "'");

        barcode_obj = (ArrayList<Barcode>) q1.list();
        if (barcode_obj.size() > 0) {

            barcde_category_graph = new BarcodeCategory_graph();

             

            for (int i = 0; i < barcode_obj.size(); i++) {

               barcde_category_graph.cheakCategory(barcode_obj.get(i).getType());

            }
            commitTransaction();

           int[] total_quentities = barcde_category_graph.getBarcodeQuentity();

            fillBarcodeCategoryTable(total_quentities);

        }

    }

    /*
     * get barcode types that existed
     * does not use in this pharse
     */
    public ArrayList<String> getBarcodesTypes() {
        ArrayList<String> barcode_types = null;
        startTransaction();

        Query q1 = session.createQuery("select distinct(barcode.type) from Barcode barcode");

        barcode_types = (ArrayList<String>) q1.list();
        return barcode_types;


    }

    /*
     * start tranaction
     */
    public void startTransaction() {


        try {
            if (session == null || !session.isOpen()) {
                HibernateSession hib_sess = new HibernateSession();
                session = hib_sess.getSession();
            }
            tx = session.beginTransaction();
        } catch (HibernateException e) {
            //logger.error(e.toString());

        } catch (RuntimeException e) {
            //logger.error(e.toString());
        }

    }
    /*
     * insert data for table
     */

    public void saveTableRow(Object o) {
        try {
            session.save(o);
        } catch (HibernateException e) {
            tx.rollback();
            //logger.error(e.toString());
        }
    }
    /*
     * commit tranaction
     */

    public void commitTransaction() {
        try {
            tx.commit();
        } catch (HibernateException e) {
            // logger.error(e.toString());
        }

    }

    public void updateTableRow(Object o) {
        try {

            session.update(o);

        } catch (HibernateException e) {
            tx.rollback();
            // logger.error(e.toString());
        }
    }
}

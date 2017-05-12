package lk.dialog.corporate.Qr.dataaccess;

import java.util.ArrayList;
import java.util.Calendar;
import javax.persistence.NonUniqueResultException;
import lk.dialog.corporate.Qr.data.Barcode;
import lk.dialog.corporate.Qr.data.UrlHitCount;
import lk.dialog.corporate.Qr.data.UrlHitTime;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Anuradha
 */
public class MesureHitCount_Dao {

    static Logger log = Logger.getLogger(MesureHitCount_Dao.class);
    Transaction tx = null;
    Session session = null;

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
            log.error(e);
        } catch (RuntimeException e) {
            log.error(e);
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
            log.error(e);
        }
    }
    /*
     * commit tranaction
     */

    public void commitTransaction() {
        try {
            tx.commit();
        } catch (HibernateException e) {
            log.error(e);
        }

    }

    public void updateTableRow(Object o) {
        try {

            session.update(o);

        } catch (HibernateException e) {
            tx.rollback();
            log.error(e);
        }

    }

    /*
     * get hit count
     */
    public void addHitCountRow(String bardodeId) {
        try {
            log.info("inside MesureHitCount_Dao : addHitCountRow");
            startTransaction();
            Query q1 = session.createQuery("select barcodedata.attribute from BarcodeData barcodedata where barcodedata.barcode.barcodeId='" + bardodeId + "'");
            ArrayList<String> attrib_list = (ArrayList<String>) q1.list();

            commitTransaction();

            if (attrib_list != null) {//&& attrib_list.size() == 2 || attrib_list.size() == 4   ----> romoved

                //if ((attrib_list.get(0).equals("shortcode") || attrib_list.get(0).equals("Shortcode")) && (attrib_list.get(1).equals("Redirecturl") || attrib_list.get(1).equals("Redirecturl"))) {
                startTransaction();
                String urlAttribute = (attrib_list.size() == 2 ? "Redirecturl" : "OriginalUrl");
                String sql = "select barcodedata.value from BarcodeData barcodedata where barcodedata.attribute='" + urlAttribute + "' and barcodedata.barcode.barcodeId='" + bardodeId + "'";
                Query q2 = session.createQuery(sql);
                ArrayList<String> url_list = (ArrayList<String>) q2.list();
                String url = url_list.get(0);

                commitTransaction();
                isAvaliableUrl(url, bardodeId);

            }
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            log.error("More result found", ex);
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            log.error("Hibernate exception occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            log.error("Exception occured", ex);
        }

    }

    /**
     * When barcode is scanned - add hit count for the particular url. If record is already available in the DB for the particular URL, hit count
     * will be incremented. otherwise it will insert as a new record.
     *
     * @param url - url scanning (by the smart phone)
     * @param barcodeId - barcode id of the particular barcode
     */
    public void isAvaliableUrl(String url, String barcodeId) {

        ArrayList<UrlHitCount> hit_count_obj = null;

        startTransaction();
        Query q2 = session.createQuery("select urlhitcount from UrlHitCount  urlhitcount  where urlhitcount.realUrl='" + url + "' and urlhitcount.barcode.barcodeId='" + barcodeId + "' ");
        hit_count_obj = (ArrayList<UrlHitCount>) q2.list();
        Barcode bc;
        BarcodeDAO bcDAO = new BarcodeDAOImpl();
        bc = bcDAO.findBarcodeByID(barcodeId);
        /*
         * below is to check : for that particular barcode id, UrlHitCount table already have a count value. If it is so
         * it will update the table row by inctrmenting the value. Otherwise it will insert in to table as a new record
         */
        if (hit_count_obj != null && hit_count_obj.size() > 0) {

            long hit_count = hit_count_obj.get(0).getHitCount();

            UrlHitCount urlhit_count_obj = hit_count_obj.get(0);
            urlhit_count_obj.setHitCount(hit_count + 1);
            updateTableRow(urlhit_count_obj);

            commitTransaction();

        } else {
            startTransaction();
            UrlHitCount urlhit_count = new UrlHitCount();
            urlhit_count.setRealUrl(url);
            urlhit_count.setHitCount(1);
            urlhit_count.setBarcode(bc);
            saveTableRow(urlhit_count);
            //bc.getUrlHitCounts().add(urlhit_count);//new
            //updateTableRow(bc);//new
            commitTransaction();
        }
    }

    public void addURLHitTimeRow(String barcodeId) {
        try {
            log.info("inside MesureHitCount_Dao : addURLHitTimeRow");
            startTransaction();
            String sql = "select barcodedata.attribute from BarcodeData barcodedata where barcodedata.barcode.barcodeId='" + barcodeId + "'";
            Query q1 = session.createQuery(sql);

            ArrayList<String> attribList = (ArrayList<String>) q1.list();
            Barcode bc;
            BarcodeDAO bcDAO = new BarcodeDAOImpl();
            bc = bcDAO.findBarcodeByID(barcodeId);
            commitTransaction();

            if (attribList != null) {

                //if ((attribList.get(0).equals("shortcode") || attribList.get(0).equals("Shortcode")) && (attribList.get(1).equals("Redirecturl") || attribList.get(1).equals("Redirecturl"))) {

                startTransaction();
                String sql2 = "select barcodedata.value from BarcodeData barcodedata where barcodedata.attribute='Redirecturl' and barcodedata.barcode.barcodeId='" + barcodeId + "'";
                Query q2 = session.createQuery(sql2);
                ArrayList<String> url_list = (ArrayList<String>) q2.list();
                String url = url_list.get(0);
                commitTransaction();

                startTransaction();
                UrlHitTime urlhitTime = new UrlHitTime();
                urlhitTime.setBarcode(bc);
                urlhitTime.setRealUrl(url);
                urlhitTime.setHitTime(Calendar.getInstance().getTime());
                saveTableRow(urlhitTime);
                commitTransaction();
            }
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            log.error("More result found", ex);
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            log.error("Hibernate exception occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            log.error("Exception occured", ex);
        }
    }
}

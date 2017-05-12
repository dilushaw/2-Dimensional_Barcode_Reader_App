package lk.dialog.corporate.Qr.dataaccess;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * @author Anuradha
 */
public class UrlRedirection_Dao {

    static Logger log = Logger.getLogger(UrlRedirection_Dao.class);
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

    public String getRedirectUrl(String shortcode) {
        log.debug("inside UrlRedirection_Dao: getRedirectUrl");
        MesureHitCount_Dao hitCount = new MesureHitCount_Dao();
        //UrlHitTimeDAO hitTime = new UrlHitTimeDAOImpl();
        ArrayList<String> barcode_id = null;
        ArrayList<String> re_dir_url_obj = null;
        String re_dir_url = null;


        startTransaction();


        Query q1 = session.createQuery("select  barcodedata.barcode.barcodeId from BarcodeData barcodedata where barcodedata.attribute='Shortcode' and barcodedata.value='" + shortcode + "'");
        barcode_id = (ArrayList<String>) q1.list();
        commitTransaction();
        if (barcode_id != null) {

            startTransaction();
            Query q2 = session.createQuery("select  barcodedata.value from BarcodeData barcodedata where barcodedata.attribute='Redirecturl' and barcodedata.barcode.barcodeId='" + barcode_id.get(0) + "'");
            re_dir_url_obj = (ArrayList<String>) q2.list();
            commitTransaction();
            re_dir_url = re_dir_url_obj.get(0);
            log.debug("BEFORE call addhitcount/time");
            hitCount.addHitCountRow(barcode_id.get(0));
            hitCount.addURLHitTimeRow(barcode_id.get(0));
            log.debug("AFTER call addhitcount/time");
        }

        //commitTransaction();

        log.debug("end of UrlRedirection_Dao: getRedirectUrl : re_dir_url: " + re_dir_url);

        return re_dir_url;
    }
}

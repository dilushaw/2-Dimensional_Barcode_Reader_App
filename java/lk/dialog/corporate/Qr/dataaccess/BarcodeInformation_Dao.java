package lk.dialog.corporate.Qr.dataaccess;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lk.dialog.corporate.Qr.data.*;
import lk.dialog.corporate.Qr.direction.UrlRedirection;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * This class is used to save barcodes, when barcodes are created by not logged in users and using csv file.This class
 * was developed under phase1.
 *
 * @author Anuradha
 * @version 1.2
 */
public class BarcodeInformation_Dao {

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
//                session = HibernateUtil.getSessionFactory().getCurrentSession();
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

    /*
     * add data for Barcode Table
     */
    public String addBarcodeRow(String barcodeId, String userName, String bcAction, List<String> bcAttributes, Date dateCreated, String imgeType, String type, String errorCorrectionLevel, int width, int len, int campaignId, String title, String CodeName, String logType) {
        UrlRedirection url_redirection = null;
        String result = "0";
        startTransaction();
        Query q1 = session.createQuery("select  user from User user where user.userName='" + userName + "'");

        ArrayList<User> user_list = (ArrayList<User>) q1.list();

        User user = user_list.get(0);
        commitTransaction();

        startTransaction();
        Query q2 = session.createQuery("select  sizes from Sizes sizes where sizes.width='" + width + "' and sizes.length='" + len + "'");

        ArrayList<Sizes> sizes_list = (ArrayList<Sizes>) q2.list();
        Sizes sizes = sizes_list.get(0);
        commitTransaction();


        startTransaction();

        try {
            Barcode barcode = new Barcode();

            barcode.setBarcodeId(barcodeId);

            barcode.setBcActionId(bcAction);

            //  barcode.setBcActionId(bcActionId_and_Attributes[0]);
            barcode.setDateCreated(dateCreated);
            barcode.setErrorCorrectionLevel(errorCorrectionLevel);
            barcode.setExpDate(dateCreated);
            barcode.setImgeType(imgeType);
            barcode.setSizes(sizes);
            barcode.setType(type);
            barcode.setUser(user);
            barcode.setCodeName(CodeName);
            barcode.setTitle(title);
            saveTableRow(barcode);
            if (logType.equals("csv")) {
                /*
                 * added by dewmini on 17/8/2012 to add a capaign id to the generated barcode - in csv file requirement
                 * : when logged in user creates barcode he should create it under a assigned campaign
                 */
                HibernateUtil.beginTransaction();
                CampaignDAO campDAO = new CampaignDAOImpl();
                Campaign campaign = campDAO.findByID(Campaign.class, campaignId);
                HibernateUtil.commitTransaction();
                barcode.setCampaign(campaign);//added(17/8/2012) : set campaign object for the barcode 
                campaign.getBarcodes().add(barcode);
                campDAO.saveCampaign(campaign);
            }
            //campaign.getBarcodes().add(barcode);
            //campDAO.saveCampaign(campaign);

            commitTransaction();

            int barcode_action_id = Integer.parseInt(bcAction);

            /**
             * Get key
             */
            url_redirection = new UrlRedirection();



            //int barcode_action_id = Integer.parseInt(bcActionId_and_Attributes[0]);

            if (barcode_action_id == 1) {
                /*
                 * String [] bcActionId_and_Attributes bcActionId_and_Attributes[0] -->actionId
                 * bcActionId_and_Attributes[1] -->real redirect url
                 */


                String[] attribute_name = new String[2];
                /*
                 * describe attributes
                 */
                attribute_name[0] = "Shortcode";
                attribute_name[1] = "Redirecturl";


                /*
                 * if (url_redirection == null) { url_redirection = new UrlRedirection(); }
                 *
                 * short_urls = url_redirection.getShortCode();
                 */

                /*
                 * insert Short code
                 */
                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute_name[0], bcAttributes.get(0));

                /*
                 * insert real redirection url
                 */
                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute_name[1], bcAttributes.get(1));



            } else if (barcode_action_id == 2) {

                /*
                 * if (url_redirection == null) { url_redirection = new UrlRedirection(); } // short_urls = url_redirection.getShortCode();
                 */

                String attribute = "Textmessage";

                /*
                 * insert Text massage
                 */


                //addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute, bcActionId_and_Attributes[1]);

                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute, bcAttributes.get(0));



            } else if (barcode_action_id == 3) {

                /*
                 * if (url_redirection == null) { url_redirection = new UrlRedirection(); } short_urls = url_redirection.getShortCode();
                 */

                String[] attribute_name = new String[3];

                /*
                 * describe attributes
                 */
                attribute_name[0] = "Name";
                attribute_name[1] = "Telephone No";
                attribute_name[2] = "Email";


                /*
                 * insert Name
                 */
                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute_name[0], bcAttributes.get(0));

                /*
                 * insert Telephone
                 */
                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute_name[1], bcAttributes.get(1));

                /*
                 * insert Email
                 */
                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute_name[2], bcAttributes.get(2));


            } else if (barcode_action_id == 4) {

                /*
                 * if (url_redirection == null) { url_redirection = new UrlRedirection(); } short_urls = url_redirection.getShortCode();
                 */

                String[] attribute_name = new String[2];

                attribute_name[0] = "Sendsms";
                attribute_name[1] = "Massage";


                //  String attribute = "Sendsms";

                /*
                 * insert Send SMS
                 */
                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute_name[0], bcAttributes.get(0));

                /*
                 * insert Sms massage
                 */
                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute_name[1], bcAttributes.get(1));

            } else if (barcode_action_id == 5) {

                /*
                 * if (url_redirection == null) { url_redirection = new UrlRedirection(); } short_urls = url_redirection.getShortCode();
                 */
                String attribute = "Initiatecall ";

                /*
                 * insert Initiate call
                 */
                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute, bcAttributes.get(0));
            }




        } catch (Exception e) {
            result = "1";
            HibernateUtil.rollbackTransaction();//new line added by dewmini
        }

        return result;
    }

    public void addBarcodedataRow(String id, String barcodeId, String attribute, String value) {
        startTransaction();

        Barcode barcode = (Barcode) session.get(Barcode.class, barcodeId);
        commitTransaction();

        startTransaction();

        BarcodeData barcodedata = new BarcodeData();
        barcodedata.setId(id);
        barcodedata.setBarcode(barcode);
        barcodedata.setAttribute(attribute);
        barcodedata.setValue(value);
        saveTableRow(barcodedata);

        commitTransaction();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.manager;

import java.util.*;
import javax.persistence.NonUniqueResultException;
import lk.dialog.corporate.Qr.data.*;
import lk.dialog.corporate.Qr.dataaccess.*;
import lk.dialog.corporate.Qr.direction.UrlRedirection;
import lk.dialog.corporate.Qr.dto.BarCodeDataStoreDTO;
import lk.dialog.corporate.Qr.exception.QrException;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handle the business logic between controller and Data Access Objects
 *
 * @author Dewmini
 * @version 2.1
 */
@Component
public class BarcodeManagerImpl implements BarcodeManager {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private SizesDAO sizesDAO;
    @Autowired
    private BarcodeDAO barcodeDAO;
    @Autowired
    private BarcodeDataDAO barcodeDataDAO;
    @Autowired
    logic.barcodes.Barcode barcodeService;
    @Autowired
    CampaignDAO campDAO;
    @Autowired
    UrlHitCountDAO urlHitCountDAO;

    /**
     * Add entered(logged in user entered) barcode information to barcode table of the DB
     *
     * @param barcodeId -Id of the barcode
     * @param userName - creted username
     * @param bcAction - brcode action type i.e. Web,Contact,SMS....
     * @param bcAttributes - relevent dat according to bcAction, if action=Web, Attribute = WebUrl=www.xxxxxxx.xx
     * @param dateCreated - current date time
     * @param imgeType - specified when creating jpg,png....
     * @param type - QR / DM
     * @param errorCorrectionLevel - Low,High
     * @param width - width of barcode image
     * @param len - length of barcode image
     * @param campaignId - Id of the campaign which barcode is creating under
     * @param account - current corporate account of the user belongs
     * @param codeName - codename gibe for barcode
     * @param title - title given for barcode - which may appear under barcode image
     * @return - return 0 if sucessfully created
     */
    public String addBarcodeRow(String barcodeId, String userName, String bcAction, List<String> bcAttributes, Date dateCreated, String imgeType, String type, String errorCorrectionLevel, int width, int len, int campaignId, String account, String codeName, String title) {

        UrlRedirection url_redirection = null;
        String result = "0";
        try {
            HibernateUtil.beginTransaction();

            User user = userDAO.findUserByUserNameAndAccount(userName, account);

            Sizes sizes = sizesDAO.findSize(width, len);

            /*
             * added by dewmini on 17/8/2012 to add a campaign id to the generated barcode requirement : when logged in
             * user creates barcode he should create it under a assigned campaign
             */
            Campaign campaign = campDAO.findByID(Campaign.class, campaignId);

            Barcode barcode = new Barcode();
            //try {


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
            barcode.setCodeName(codeName);
            barcode.setTitle(title);
            barcode.setCampaign(campaign);
            barcodeDAO.save(barcode);
            //added(17/8et012) : set campaign object for the barcode 
            campaign.getBarcodes().add(barcode);
            campDAO.saveCampaign(campaign);
            //HibernateUtil.commitTransaction();

            //HibernateUtil.beginTransaction();
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
                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute_name[0], bcAttributes.get(0), barcode);

                /*
                 * insert real redirection url
                 */
                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute_name[1], bcAttributes.get(1), barcode);

            } else if (barcode_action_id == 2) {

                /*
                 * if (url_redirection == null) { url_redirection = new UrlRedirection(); } // short_urls =
                 * url_redirection.getShortCode();
                 */

                String attribute = "Textmessage";

                /*
                 * insert Text massage
                 */

                //addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute, bcActionId_and_Attributes[1]);

                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute, bcAttributes.get(0), barcode);

            } else if (barcode_action_id == 3) {

                /*
                 * if (url_redirection == null) { url_redirection = new UrlRedirection(); } short_urls =
                 * url_redirection.getShortCode();
                 */

                String[] attribute_name = new String[4];

                /*
                 * describe attributes
                 */
                attribute_name[0] = "Name";
                attribute_name[1] = "Telephone No";
                attribute_name[2] = "Email";
                attribute_name[3] = "Organization";

                /*
                 * insert Name
                 */
                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute_name[0], bcAttributes.get(0), barcode);

                /*
                 * insert Telephone
                 */
                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute_name[1], bcAttributes.get(1), barcode);

                /*
                 * insert Email
                 */
                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute_name[2], bcAttributes.get(2), barcode);
                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute_name[3], bcAttributes.get(3), barcode);


            } else if (barcode_action_id == 4) {

                /*
                 * if (url_redirection == null) { url_redirection = new UrlRedirection(); } short_urls =
                 * url_redirection.getShortCode();
                 */

                String[] attribute_name = new String[2];

                attribute_name[0] = "Sendsms";
                attribute_name[1] = "Massage";


                //  String attribute = "Sendsms";

                /*
                 * insert Send SMS
                 */
                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute_name[0], bcAttributes.get(0), barcode);

                /*
                 * insert Sms massage
                 */
                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute_name[1], bcAttributes.get(1), barcode);

            } else if (barcode_action_id == 5) {

                /*
                 * if (url_redirection == null) { url_redirection = new UrlRedirection(); } short_urls =
                 * url_redirection.getShortCode();
                 */
                String attribute = "Initiatecall ";

                /*
                 * insert Initiate call
                 */
                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute, bcAttributes.get(0), barcode);
            } else if (barcode_action_id == 6) {
                String[] attribute_name = new String[4];

                attribute_name[0] = "Shortcode";
                attribute_name[1] = "Redirecturl";
                attribute_name[2] = "AppendingString";
                attribute_name[3] = "OriginalUrl";
                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute_name[0], bcAttributes.get(0), barcode);

                /*
                 * insert real redirection url
                 */
                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute_name[1], bcAttributes.get(1), barcode);
                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute_name[2], bcAttributes.get(2), barcode);
                addBarcodedataRow(url_redirection.getKey(), barcodeId, attribute_name[3], bcAttributes.get(3), barcode);

            }



        } catch (Throwable e) {

            result = "1";
            e.printStackTrace();
            //transaction.rollback();
            HibernateUtil.rollbackTransaction();
        }
        HibernateUtil.commitTransaction();
        //transaction.commit();
        return result;
        //return "result";
    }

    public void addBarcodedataRow(String id, String barcodeId, String attribute, String value, Barcode barcode) {


        //    HibernateUtil.beginTransaction();

        BarcodeData barcodedata = new BarcodeData();
        barcodedata.setId(id);
        barcodedata.setBarcode(barcode);
        barcodedata.setAttribute(attribute);
        barcodedata.setValue(value);
        barcodeDataDAO.save(barcodedata);

        //     HibernateUtil.commitTransaction();
    }

    /**
     *
     * @param barcodes
     * @return
     * @throws QrException
     */
    public List<BarCodeDataStoreDTO> loadBarcodeImages(Collection<Barcode> barcodes) throws QrException {

        List<BarCodeDataStoreDTO> barcodesStore = new ArrayList<BarCodeDataStoreDTO>();

        for (Barcode barcode : barcodes) {
            byte[] imageByte = barcodeService.loadImageStream("dialog", barcode.getBarcodeId());
            BarCodeDataStoreDTO barcodDataStore = new BarCodeDataStoreDTO();
            System.out.print("imageByte " + imageByte);
            if (imageByte != null) {
                byte encodedImageByte[] = new org.apache.commons.codec.binary.Base64().encode(imageByte);
                barcodDataStore.setBarcodeId(barcode.getBarcodeId());
                barcodDataStore.setImage(new String(encodedImageByte));//set image byte stream
                barcodDataStore.setImageType(barcode.getImgeType());
                barcodDataStore.setTitle(barcode.getTitle());
                barcodDataStore.setCodeName(barcode.getCodeName());
                barcodesStore.add(barcodDataStore);
            }
        }
        return barcodesStore;
    }

    public Map<String, String[]> loadBarcodeDetails(Integer campaignId) throws QrException {
        Map<String, String[]> map = new HashMap<String, String[]>();
        try {
            List<String> barcodesNames = barcodeDAO.findBarcodeNamesByCampaign(campaignId);//contains only barcode names

            for (String bc : barcodesNames) {
                int hitCount = 0;
                int bcAction = 0;
                String[] bcDataArr = new String[3];

                List<Barcode> bcData = barcodeDAO.findBarcodeByCampaignAndCodeName(bc, campaignId);
                for (Barcode b : bcData) {
                    hitCount = hitCount + barcodeURLHitCount(b.getBarcodeId()).intValue();
                    bcAction = Integer.parseInt(b.getBcActionId());
                }

                String typeStr = bcAction == 1 ? "Static Web" : bcAction == 2 ? "Message" : bcAction == 3 ? "Contact Details" : bcAction == 4 ? "Send SMS" : bcAction == 5 ? "Initiate Call" : bcAction == 6 ? "Dynamic Web" : "NA";
                bcDataArr[0] = typeStr;
                bcDataArr[1] = String.valueOf(bcData.size());
                bcDataArr[2] = String.valueOf(hitCount);
                map.put(bc, bcDataArr);
            }
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {

            throw new QrException("Hibernate exception occured", ex);
        }
        return map;
    }

    public String findBarcodeName(String codeName, int campaignId) throws QrException {
        String bcName = null;
        try {
            HibernateUtil.beginTransaction();
            bcName = barcodeDAO.findBarcodeName(codeName, campaignId);
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {

            throw new QrException("Hibernate exception occured", ex);
        }
        return bcName;
    }

    public String findBulkBarcodeName(String codeName, int campaignId) throws QrException {
        String bcName = null;
        try {
            HibernateUtil.beginTransaction();
            bcName = barcodeDAO.findBulkBarcodeName(codeName, campaignId);
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {

            throw new QrException("Hibernate exception occured", ex);
        }
        return bcName;
    }

    private Long barcodeURLHitCount(String barcodeId) throws QrException {
        Long hitCount = 0L;
        try {
            HibernateUtil.beginTransaction();
            UrlHitCount uhc = urlHitCountDAO.findHitCountByBarcodeId(barcodeId);
            HibernateUtil.commitTransaction();
            if (uhc != null) {
                hitCount = uhc.getHitCount();
            }
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {

            throw new QrException("Hibernate exception occured", ex);
        }
        return hitCount;
    }

    public List<Barcode> findBarcodeByCampaignAndCodeName(String codeName, Integer campaignId) throws QrException {
        List<Barcode> barcodeSet = null;
        try {
            HibernateUtil.beginTransaction();
            barcodeSet = barcodeDAO.findBarcodeByCampaignAndCodeName(codeName, campaignId);
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {

            throw new QrException("Hibernate exception occured", ex);
        }
        return barcodeSet;
    }

    /**
     * to delete a barcode,added by Dilusha
     *
     * @param barcode barcode object
     */
    public void deleteBarcode(Barcode barcode) {

        List<BarcodeData> bcdata = barcodeDataDAO.findBarcodeDataByBarcodeId(barcode);
        for (int i = 0; i < bcdata.size(); i++) {
            barcodeDataDAO.deleteBarcode(bcdata.get(i));
        }
        barcodeDAO.deleteBarcode(barcodeDAO.findBarcodeByID(barcode.getBarcodeId()));
    }
    //added by Dilusha

    public Barcode findBarcodeByID(String barcodeId) {
        return barcodeDAO.findBarcodeByID(barcodeId);
    }

    public List<String> findBarcodeByCampaignAndBcAction(String bcActionId, Integer campaignId) throws QrException {
        List<String> barcodes = null;
        try {
            barcodes = barcodeDAO.findBarcodeByCampaignAndBcAction(bcActionId, campaignId);
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {

            throw new QrException("Hibernate exception occured", ex);
        }
        return barcodes;
    }

    public List<Barcode> findBarcodeByCodename(String codeName, Integer campaignId) {
        List<Barcode> barcodes = null;
        try {
            barcodes = barcodeDAO.findBarcodeByCampaignAndCodeName(codeName, campaignId);
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            // throw new QrException("More result found", ex);
        } catch (HibernateException ex) {
            // throw new QrException("Hibernate exception occured", ex);
        }
        return barcodes;
    }

    public BarcodeData findBarcodedataByBarcodeId(Barcode barcode) {//returns only attrubutes which are equals to OriginalUrl;
        BarcodeData bcdata = barcodeDataDAO.findRedirectURLByBarcodeId(barcode);
        return bcdata;
    }

    /**
     * this method is used to modify the dynamic url barcode backend.redirectURL parameter in barcode data, Added by
     * Dilusha table has been modified
     *
     * @param appendingString the new dynamic url
     * @param barcode
     */
    public void modifyRedirectUrl(String appendingString, Barcode barcode) {


        try {
            HibernateUtil.beginTransaction();
            BarcodeData bcdataShortcode = barcodeDataDAO.findShortcodeByBarcodeId(barcode);
            List<BarcodeData> manySizeBarcodes = barcodeDataDAO.findBarcodeDataByShortCode(bcdataShortcode.getValue());
            for (int i = 0; i < manySizeBarcodes.size(); i++) {
                BarcodeData bcdataRedirect = barcodeDataDAO.findRedirectURLByBarcodeId(manySizeBarcodes.get(i).getBarcode());
                bcdataRedirect.setValue(appendingString);

                barcodeDataDAO.editRedirectUrl(bcdataRedirect);

            }

            HibernateUtil.commitTransaction();

        } catch (HibernateException ex) {
            ex.printStackTrace();

            HibernateUtil.rollbackTransaction();
        } catch (Exception ex) {
            ex.printStackTrace();

            HibernateUtil.rollbackTransaction();

        }
    }

    public List<String> findBarcodeNamesByCampaign(Integer campaignId) throws QrException {
        List<String> codeNames = null;
        try {
            HibernateUtil.beginTransaction();
            codeNames = barcodeDAO.findBarcodeNamesByCampaign(campaignId);
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {

            throw new QrException("Hibernate exception occured", ex);
        }
        return codeNames;
    }
}

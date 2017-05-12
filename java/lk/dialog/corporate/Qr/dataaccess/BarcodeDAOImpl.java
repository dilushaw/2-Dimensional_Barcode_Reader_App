/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

import java.util.List;
import java.util.Set;
import lk.dialog.corporate.Qr.data.Barcode;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import org.hibernate.Query;
import org.springframework.stereotype.Component;

/**
 * This class is to communicate with the database using hibernate regarding the 'barcode' entity. Although there was a
 * class(BarcodeInformation_Dao) used to save barcodes, when refactoring the code under phase 2 developmet, this class
 * was developed to maintain the consistency of coding under Spring MVC by enhancing the way of using data to save with
 * DB by calling common utility class (HibernateUtil)
 *
 * @author Dewmini
 */
@Component
public class BarcodeDAOImpl extends GenericDAOImpl<Barcode, String> implements BarcodeDAO {

    /**
     * For a particular campaign it will check the given barcode name already exixts; assuming that code name is unique
     * within the campaign(added by Dewmini)
     *
     * @param codeName - given barcode name
     * @param campaignId - unique id of the given campaign
     * @return - barcode name matching with above parameters; return null if no matches found
     */
    public String findBarcodeName(String codeName, int campaignId) {
        String bcName = null;
        String sql = "FROM Barcode b WHERE b.codeName = :codeName AND b.campaign.campaignId = :campaignId";
        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("codeName", codeName).setParameter("campaignId", campaignId);
        List<Barcode> bc = findMany(query);
        if (bc != null && !bc.isEmpty()) {
            bcName = bc.get(0).getCodeName();
        }
        return bcName;
    }

    /**
     * For a particular campaign it will check the given bulk barcode name already exixts; assuming that code name is
     * unique within the campaign. When creating bulk barcodes using a CSV file each line of barcode defined in the CSV,
     * will be assigned an unique code name in an incremental fashion. Eg. If Bulk_Code_Name = test_bulk and if there
     * are 3 barcode in the CSV file each code will have the code name as test_bulk_1,test_bulk_2,test_bulk_3. As
     * assigning incremental value is happen at time of barcode is saving here we check the code name like 'test_bulk'
     * already exists within the campaign.(added by Dewmini)
     *
     * @param codeName - bulk code name. Eg. test_bulk
     * @param campaignId - id of the campaign which barcode is creating
     * @return - if match with the parameters, return the code name given; otherwise returns null
     */
    public String findBulkBarcodeName(String codeName, int campaignId) {
        String bcName = null;
        String sql = "FROM Barcode b WHERE  b.campaign.campaignId = :campaignId AND b.codeName like '%" + codeName + "%'";
        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("campaignId", campaignId);
        List<Barcode> bc = findMany(query);
        if (bc != null && !bc.isEmpty()) {
            bcName = codeName;
        }
        return bcName;
    }

    /**
     * For a given barcode id it will find the matching information relevant to the barcode id. as barcode id is unique
     * this should return one particular barcode object (not a list)(added by Dewmini)
     *
     * @param barcodeId - id of the barcode
     * @return - barcode object matching the id; ow- null
     */
    public Barcode findBarcodeByID(String barcodeId) {
        Barcode bc = null;
        String sql = "FROM Barcode b WHERE b.barcodeId = :barcodeId";
        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("barcodeId", barcodeId);
        bc = findOne(query);
        return bc;
    }

    /**
     * For given cmapign it will find all the barcodes created under the campaign(added by Dewmini)
     *
     * @param campaignId - id of the campaign
     * @return - list of barcode matching the parameters; ow-null
     */
    public List<Barcode> findBarcodeByCampaign(Integer campaignId) {
        List<Barcode> bc = null;
        String sql = "FROM Barcode b WHERE b.campaign.campaignId = :campaignId";
        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("campaignId", campaignId);
        bc = findMany(query);
        return bc;
    }

    /**
     * When Campaign and barcode name is known it will give the relevant barcodes, althogu we conside code name is
     * unique within the campaign actually it is not. When we creating particular barcode under 3 different sizes it
     * will actually create as 3 different barcodes. But by giving the same code name for all 3 barcodes we consider
     * those 3 records as a one record. So again we can consider code name is unique within the campaign. For the moment
     * this method aim is to display barcode under 'View Campaign' page and click on the code name.(added by Dewmini)
     *
     * @param codeName - barcode name
     * @param campaignId - id of the campaign
     * @return - list if barcodes matching; ow-null
     */
    public List<Barcode> findBarcodeByCampaignAndCodeName(String codeName, Integer campaignId) {
        List<Barcode> bc = null;
        String sql = "FROM Barcode b WHERE b.campaign.campaignId = :campaignId AND b.codeName = :codeName";
        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("campaignId", campaignId).setParameter("codeName", codeName);
        bc = findMany(query);
        return bc;
    }

    /**
     * Here we assume code name is unique within the campaign. But when same code has 3 different sizes(4x4,8x8,12x12)
     * those 3 will save in the db in 3 different rows and code name is same for those 3. but it represent the same
     * barcode and as only difference is the size, we consider all those 3 as a one. So when we want to find code names
     * for a particular campaign we need to take distinct code names; otherwise it will returns duplicates.(added by
     * Dewmini)
     *
     * @param campaignId - id of the given campaign
     * @return - List of codeNames(String) for the campaign
     */
    public List<String> findBarcodeNamesByCampaign(Integer campaignId) {
        List<String> bc = null;
        String sql = "SELECT DISTINCT b.codeName FROM Barcode b WHERE b.campaign.campaignId = :campaignId";
        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("campaignId", campaignId);
        bc = (List<String>) query.list();
        return bc;
    }

    //added by Dilusha
    public List<String> findBarcodeByCampaignAndBcAction(String bcActionId, Integer campaignId) {
        List<String> bc = null;
        String sql = "SELECT DISTINCT b.codeName FROM Barcode b WHERE b.campaign.campaignId = :campaignId AND b.bcActionId = :bcActionId";
        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("campaignId", campaignId).setParameter("bcActionId", bcActionId);
        bc = (List<String>) query.list();
        return bc;
    }

    //added by Dilusha
    public void deleteBarcode(Barcode barcode) {
        delete(barcode);
    }
}

/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

import lk.dialog.corporate.Qr.data.ReportView;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import org.hibernate.Query;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Component;

/**
 * This class will communicate with created view (report_view) in the database. As other DAOs this is not extending
 * GenericDAOImplas we cannot define the entity type here always as ReportView as we need JRDataSource type as well
 *
 * @author Dewmini
 * @version 2.1
 */
@Component
public class ReportViewDAOImpl implements ReportViewDAO {

    //not in use
    public List<ReportView> getAllReportView() {
        List<ReportView> r = null;
        String sql = "FROM ReportView r where r.corporateName not in (:corporateName1,:corporateName2)";
        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("corporateName1", "dialog").setParameter("corporateName2", "default");
        r = (List<ReportView>) query.list();
        return r;
    }

    //not in use
    public JRDataSource getDataSource() {
        // Create an array list of ReportView 
        List<ReportView> items = new ArrayList<ReportView>();

        // get ReportView items
        items = getAllReportView();

        // Wrap the collection in a JRBeanCollectionDataSource
        // This is one of the collections that Jasper understands
        JRDataSource ds = new JRBeanCollectionDataSource(items);

        // Return the wrapped collection
        return ds;
    }

    /**
     * get the List of ReportView objects to be used as the input for creating datasource to be used by jasper
     * @param corporateId
     * @param lobId
     * @param campaignId
     * @param codeName
     * @param dateCreated
     * @param userName
     * @param designation
     * @return - List<ReportView>
     */
    public List<ReportView> getCustomizedReportView(int corporateId, String lobId, String campaignId, String codeName, String dateCreated, String userName, String designation) {
        List<ReportView> report = null;
        String sql = "";
        if (corporateId == -1 && lobId.isEmpty() && campaignId.isEmpty()) {
            sql = "FROM ReportView r where r.codeName like '%" + codeName + "%' and r.dateCreated like '%" + dateCreated + "%' and r.userName like '%" + userName + "%' and r.designation like '%" + designation + "%'";
        } else if (lobId.isEmpty() && campaignId.isEmpty()) {
            sql = "FROM ReportView r where r.corporateId= " + corporateId + " and r.codeName like '%" + codeName + "%' and r.dateCreated like '%" + dateCreated + "%' and r.userName like '%" + userName + "%' and r.designation like '%" + designation + "%'";
        } else if (campaignId.isEmpty()) {
            sql = "FROM ReportView r where r.corporateId= " + corporateId + " and r.lobId='" + lobId + "' and r.codeName like '%" + codeName + "%' and r.dateCreated like '%" + dateCreated + "%' and r.userName like '%" + userName + "%' and r.designation like '%" + designation + "%'";
        } else {
            sql = "FROM ReportView r where r.corporateId= " + corporateId + " and r.lobId='" + lobId + "' and r.campaignId='" + campaignId + "' and r.codeName like '%" + codeName + "%' and r.dateCreated like '%" + dateCreated + "%' and r.userName like '%" + userName + "%' and r.designation like '%" + designation + "%'";
        }
        //sql += " and ";
        Query query = HibernateUtil.getSession().createQuery(sql);
        report = (List<ReportView>) query.list();

        return report;
    }

    /**
     * Our datasource. Retrieves a collection of data that is wrapped by a JRBeanCollectionDataSource.
     *
     * @param corporateId
     * @param lobId
     * @param campaignId
     * @param codeName
     * @param dateCreated
     * @param userName
     * @param designation
     * @return - a JRBeanCollectionDataSource collection
     */
    public JRDataSource getCustomizedDataSource(int corporateId, String lobId, String campaignId, String codeName, String dateCreated, String userName, String designation) {
        List<ReportView> items = new ArrayList<ReportView>();

        items = getCustomizedReportView(corporateId, lobId, campaignId, codeName, dateCreated, userName, designation);
        // Wrap the collection in a JRBeanCollectionDataSource
        // This is one of the collections that Jasper understands
        JRDataSource ds = new JRBeanCollectionDataSource(items);

        return ds;
    }
//    public List<ReportView> getCorporateReportView(Integer corporateId) {
//        List<ReportView> r = null;
//        String sql = "FROM ReportView r where r.corporateId=:corpId";
//        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("corpId", corporateId);
//        r = (List<ReportView>) query.list();
//        return r;
//    }
//
//    public List<ReportView> getCustomizedCorporateReportView(Integer corporateId, String lobId, String campaignId, String codeName, String dateCreated, String userName, String designation) {
//        List<ReportView> report = null;
//        String sql = "FROM ReportView r where r.corporateId=:corpId and r.lobId='" + lobId + "' and r.campaignId='" + campaignId + "' and r.codeName like '%" + codeName + "%' and r.dateCreated like '%" + dateCreated + "%' and r.userName like '%" + userName + "%' and r.designation like '%" + designation + "%'";
//        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("corpId", corporateId);
//        report = (List<ReportView>) query.list();
//
//        return report;
//    }
//
//    public JRDataSource getCustomizedCorporateDataSource(Integer corporateId, String lobId, String campaignId, String codeName, String dateCreated, String userName, String designation) {
//        List<ReportView> items = new ArrayList<ReportView>();
//
//        items = getCustomizedCorporateReportView(corporateId, lobId, campaignId, codeName, dateCreated, userName, designation);
//
//        JRDataSource ds = new JRBeanCollectionDataSource(items);
//
//        return ds;
//    }
}

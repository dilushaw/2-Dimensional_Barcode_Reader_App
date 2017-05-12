/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

import java.util.List;
import lk.dialog.corporate.Qr.data.ReportView;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * This is to highlight the methods needs to communicate with created view (report_view) in the database. As previous
 * DAOs this is not extends GenericDAO. As this is to communicate with DB view not happen any save, update, delete
 * activities. only for retrieving information. And here we cannot define exact entity type as in GenericDAO as
 * sometimes we need 'JRDataSource' type to be returned other than the ReportView type
 *
 * @author Dewmini
 * @version 2.1
 */
public interface ReportViewDAO {

    List<ReportView> getAllReportView();

    public JRDataSource getDataSource();

    List<ReportView> getCustomizedReportView(int corporateName, String lobName, String campaignName, String codeName, String dateCreated, String userName, String designation);

    public JRDataSource getCustomizedDataSource(int corporateId, String lobId, String campaignId, String codeName, String dateCreated, String userName, String designation);
    //public List<ReportView> getCorporateReportView(Integer corporateId);
    //public List<ReportView> getCustomizedCorporateReportView(Integer corporateId, String lobName, String campaignName, String codeName, String dateCreated, String userName, String designation);
    //public JRDataSource getCustomizedCorporateDataSource(Integer corporateId, String lobName, String campaignName, String codeName, String dateCreated, String userName, String designation);
}

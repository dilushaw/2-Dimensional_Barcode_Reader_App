/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.manager;

import java.util.List;
import lk.dialog.corporate.Qr.data.ReportView;
import lk.dialog.corporate.Qr.exception.QrException;
import net.sf.jasperreports.engine.JRDataSource;

/**
 *
 * @author Dewmini
 */
public interface ReportViewManager {
    List<ReportView> getAllReportView() throws QrException;
    
    public List<ReportView> getCustomizedReportView(int corporateName, String lobName, String campaignName, String codeName, String dateCreated, String userName, String designation) throws QrException;

    public JRDataSource getCustomizedDataSource(int corporateId, String lobId, String campaignId, String codeName, String dateCreated, String userName, String designation) throws QrException;

    //public List<ReportView> getCorporateReportView(Integer corporateId) throws QrException;

    //public JRDataSource getCustomizedCorporateDataSource(Integer corporateId, String lobName, String campaignName, String codeName, String dateCreated, String userName, String designation) throws QrException;

}

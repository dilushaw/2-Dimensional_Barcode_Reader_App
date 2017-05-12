/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.manager;

import java.util.List;
import javax.persistence.NonUniqueResultException;
import lk.dialog.corporate.Qr.data.ReportView;
import lk.dialog.corporate.Qr.dataaccess.ReportViewDAO;
import lk.dialog.corporate.Qr.exception.QrException;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import net.sf.jasperreports.engine.JRDataSource;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Dewmini
 */
@Component
public class ReportViewManagerImpl implements ReportViewManager {

    @Autowired
    ReportViewDAO reportViewDAO;

    public List<ReportView> getAllReportView() throws QrException {
        List<ReportView> report = null;
        try {
            HibernateUtil.beginTransaction();
            report = reportViewDAO.getAllReportView();
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate exception occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return report;
    }

    public JRDataSource getCustomizedDataSource(int corporateId, String lobId, String campaignId, String codeName, String dateCreated, String userName, String designation) throws QrException {
        JRDataSource ds = null;
        try {
            HibernateUtil.beginTransaction();
            ds = reportViewDAO.getCustomizedDataSource(corporateId, lobId, campaignId, codeName, dateCreated, userName, designation);
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate exception occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return ds;

    }

//    public List<ReportView> getCorporateReportView(Integer corporateId) throws QrException {
//        List<ReportView> report = null;
//        try {
//            HibernateUtil.beginTransaction();
//            report = reportViewDAO.getCorporateReportView(corporateId);
//            HibernateUtil.commitTransaction();
//        } catch (NonUniqueResultException ex) {
//            HibernateUtil.rollbackTransaction();
//            throw new QrException("More result found", ex);
//        } catch (HibernateException ex) {
//            throw new QrException("Hibernate exception occured", ex);
//        } catch (Exception e) {
//            throw new QrException("Couldn't process your request", e);
//        }
//        return report;
//    }
//
//    public JRDataSource getCustomizedCorporateDataSource(Integer corporateId, String lobName, String campaignName, String codeName, String dateCreated, String userName, String designation) throws QrException {
//        JRDataSource ds = null;
//        try {
//            HibernateUtil.beginTransaction();
//            ds = reportViewDAO.getCustomizedCorporateDataSource(corporateId, lobName, campaignName, codeName, dateCreated, userName, designation);
//            HibernateUtil.commitTransaction();
//        } catch (NonUniqueResultException ex) {
//            HibernateUtil.rollbackTransaction();
//            throw new QrException("More result found", ex);
//        } catch (HibernateException ex) {
//            throw new QrException("Hibernate exception occured", ex);
//        } catch (Exception e) {
//            throw new QrException("Couldn't process your request", e);
//        }
//        return ds;
//    }
    public List<ReportView> getCustomizedReportView(int corporateName, String lobName, String campaignName, String codeName, String dateCreated, String userName, String designation) throws QrException {
        List<ReportView> reportView = null;
        try {
            HibernateUtil.beginTransaction();
            reportView = reportViewDAO.getCustomizedReportView(corporateName, lobName, campaignName, codeName, dateCreated, userName, designation);
            HibernateUtil.commitTransaction();
        } catch (NonUniqueResultException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("More result found", ex);
        } catch (HibernateException ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Hibernate exception occured", ex);
        } catch (Exception ex) {
            HibernateUtil.rollbackTransaction();
            throw new QrException("Exception occured", ex);
        }
        return reportView;
    }
}

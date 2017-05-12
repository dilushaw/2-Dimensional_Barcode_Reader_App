/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.dataaccess;

import lk.dialog.corporate.Qr.data.UrlHitCount;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import org.hibernate.Query;
import org.springframework.stereotype.Component;

/**
 *
 * @author Dewmini
 */
@Component
public class UrlHitCountDAOImpl extends GenericDAOImpl<UrlHitCount, Long> implements UrlHitCountDAO{

    public UrlHitCount findHitCountByBarcodeId(String barcodeId) {
        UrlHitCount hitCount = null;
        String sql = "FROM UrlHitCount u WHERE u.barcode.barcodeId = :barcodeId";
        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("barcodeId", barcodeId);
        hitCount = findOne(query);
        return hitCount;
    }
    
}

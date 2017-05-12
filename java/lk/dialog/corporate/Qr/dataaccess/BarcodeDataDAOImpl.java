/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

import java.util.List;
import lk.dialog.corporate.Qr.data.Barcode;
import lk.dialog.corporate.Qr.data.BarcodeData;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import org.hibernate.Query;
import org.springframework.stereotype.Component;

/**
 *
 * @author Dewmini
 */
@Component
public class BarcodeDataDAOImpl extends GenericDAOImpl<BarcodeData, String> implements BarcodeDataDAO {

    //added by Dilusha
    public List<BarcodeData> findBarcodeDataByBarcodeId(Barcode barcode) {
        List<BarcodeData> bcdata = null;
        String sql = "FROM BarcodeData b WHERE b.barcode = :barcode";
        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("barcode", barcode);
        bcdata = findMany(query);
        return bcdata;
    }

    //added by Dilusha
    public void deleteBarcode(BarcodeData barcodedata) {
        delete(barcodedata);
    }

    public BarcodeData findRedirectURLByBarcodeId(Barcode barcode) {
        BarcodeData bcdata = null;
        String attribute = "Redirecturl";
        String sql = "FROM BarcodeData b WHERE (b.barcode = :barcode and b.attribute=:attribute )";
        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("barcode", barcode).setParameter("attribute", attribute);
        bcdata = findOne(query);
        return bcdata;


    }

    public BarcodeData findAppendingStringByBarcodeId(Barcode barcode) {
        BarcodeData bcdata = null;
        String attribute = "AppendingString";
        String sql = "FROM BarcodeData b WHERE (b.barcode = :barcode and b.attribute=:attribute )";
        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("barcode", barcode).setParameter("attribute", attribute);
        bcdata = findOne(query);
        return bcdata;
    }

    public BarcodeData findOriginalUrlByBarcodeId(Barcode barcode) {
        BarcodeData bcdata = null;
        String attribute = "OriginalUrl";
        String sql = "FROM BarcodeData b WHERE (b.barcode = :barcode and b.attribute=:attribute )";
        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("barcode", barcode).setParameter("attribute", attribute);
        bcdata = findOne(query);
        return bcdata;
    }

    public BarcodeData findShortcodeByBarcodeId(Barcode barcode) {
        BarcodeData bcdata = null;
        String attribute = "Shortcode";
        String sql = "FROM BarcodeData b WHERE (b.barcode = :barcode and b.attribute=:attribute )";
        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("barcode", barcode).setParameter("attribute", attribute);
        bcdata = findOne(query);
        return bcdata;
    }

    public List<BarcodeData> findBarcodeDataByShortCode(String shortcodeValue) {
        List<BarcodeData> bcDataList = null;
        String attribute = "Shortcode";
        String sql = "FROM BarcodeData b WHERE (b.attribute=:attribute and b.value=:shortcodeValue )";
        Query query = HibernateUtil.getSession().createQuery(sql).setParameter("attribute", attribute).setParameter("shortcodeValue", shortcodeValue);
        bcDataList = findMany(query);


        return bcDataList;
    }

    public void editRedirectUrl(BarcodeData barcodedata) {
        update(barcodedata);
    }
}

/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

import java.util.List;
import lk.dialog.corporate.Qr.data.Barcode;
import lk.dialog.corporate.Qr.data.BarcodeData;

/**
 *
 * @author Dewmini
 */
public interface BarcodeDataDAO extends GenericDAO<BarcodeData, String> {

    List<BarcodeData> findBarcodeDataByBarcodeId(Barcode barcode);//added by Dilusha

    void deleteBarcode(BarcodeData barcodedata);//added by Dilusha

    void editRedirectUrl(BarcodeData barcodedata);//added by Dilusha

    BarcodeData findRedirectURLByBarcodeId(Barcode barcode);

    BarcodeData findAppendingStringByBarcodeId(Barcode barcode);//added by Dilusha

    BarcodeData findOriginalUrlByBarcodeId(Barcode barcode);//added by Dilusha
    BarcodeData findShortcodeByBarcodeId(Barcode barcode);

    List<BarcodeData> findBarcodeDataByShortCode(String shortcodeValue);
}

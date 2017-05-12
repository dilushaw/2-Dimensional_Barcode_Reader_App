/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.dataaccess;

import lk.dialog.corporate.Qr.data.UrlHitCount;

/**
 *
 * @author Dewmini
 */
public interface UrlHitCountDAO  extends GenericDAO<UrlHitCount, Long> {
    public UrlHitCount findHitCountByBarcodeId(String barcodeId);
}

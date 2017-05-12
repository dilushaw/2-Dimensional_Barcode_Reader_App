/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.manager;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lk.dialog.corporate.Qr.data.Barcode;
import lk.dialog.corporate.Qr.data.BarcodeData;
import lk.dialog.corporate.Qr.dto.BarCodeDataStoreDTO;
import lk.dialog.corporate.Qr.exception.QrException;

/**
 *
 * @author Dewmini
 */
public interface BarcodeManager {

    String addBarcodeRow(String barcodeId, String userName, String bcAction, List<String> bcAttributes, Date dateCreated, String imgeType, String type, String errorCorrectionLevel, int width, int len, int campaignId, String account, String codeName, String title);
    void addBarcodedataRow(String id, String barcodeId, String attribute, String value, Barcode barcode);
    List<BarCodeDataStoreDTO> loadBarcodeImages(Collection<Barcode> barcodes) throws QrException;
    String findBarcodeName(String codeName, int campaignId) throws QrException;
    String findBulkBarcodeName(String codeName, int campaignId) throws QrException;
    Map<String, String[]> loadBarcodeDetails(Integer campaignId) throws QrException;
    List<Barcode> findBarcodeByCampaignAndCodeName(String codeName, Integer campaignId) throws QrException;
    List<String> findBarcodeByCampaignAndBcAction(String bcActionId, Integer campaignId) throws QrException;
    List<Barcode> findBarcodeByCodename(String codeName,Integer campaignId);
    List<String> findBarcodeNamesByCampaign(Integer campaignId) throws QrException;
    void deleteBarcode( Barcode barcode);//added by Dilusha
    BarcodeData findBarcodedataByBarcodeId(Barcode barcode);
    Barcode findBarcodeByID(String barcodeId);//added by Dilusha
    void modifyRedirectUrl(String appendingString,Barcode barcode );//added by Dilusha
    
}

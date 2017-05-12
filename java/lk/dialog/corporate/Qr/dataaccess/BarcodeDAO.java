/*
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory.  All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

import java.util.List;
import java.util.Set;
import lk.dialog.corporate.Qr.data.Barcode;

/**
 *
 * @author Dewmini
 */
public interface BarcodeDAO extends GenericDAO<Barcode, String> {

    String findBarcodeName(String codeName, int campaignId);

    String findBulkBarcodeName(String codeName, int campaignId);

    Barcode findBarcodeByID(String barcodeId);

    List<Barcode> findBarcodeByCampaign(Integer campaignId);

    List<Barcode> findBarcodeByCampaignAndCodeName(String codeName, Integer campaignId);

    List<String> findBarcodeByCampaignAndBcAction(String bcActionId, Integer campaignId);

    List<String> findBarcodeNamesByCampaign(Integer campaignId);

    //added by Dilusha
    void deleteBarcode(Barcode barcode);
}

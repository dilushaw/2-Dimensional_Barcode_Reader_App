/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Scheduler;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lk.dialog.corporate.Qr.data.Campaign;
import lk.dialog.corporate.Qr.data.Corporate;
import lk.dialog.corporate.Qr.exception.QrException;
import lk.dialog.corporate.Qr.manager.CampaignManager;
import lk.dialog.corporate.Qr.manager.CorporateManager;
import lk.dialog.corporate.Qr.manager.CorporateManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * This class Schedules the expire dates of corporate license and campaigns,currently campaign
 * license is not scheduled it can be done similar to corporate license
 *
 * @author Dilusha Weeraddana
 * @version 2.0
 */
@Service
public class Scheduler {

    @Autowired
    private CorporateManager corporateManager;
    @Autowired
    private CampaignManager campaignManager;

    /**
     * This method schedules the license expire date of a corporate,scheduler runs once per every 12
     * hours
     *
     * @return Nothing
     */
    @Scheduled(fixedDelay = 43200000) //12 hours
    public void checkLicenseExpireDate() {
        System.out.println("this is a schedular to check License Expire Date for corporates");
        List<Corporate> corporates = null;
        Date currentdate = new Date();

        try {
            corporates = corporateManager.loadCorporateDashboard();
            for (int i = 0; i < corporates.size(); i++) {
                Date endDate = corporates.get(i).getLicenseEndDate();
                if (endDate != null && endDate.before(currentdate)) {
                    corporateManager.updateCorporateandUserStatus(corporates.get(i), 0);
                }

            }

        } catch (QrException ex) {
            Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method schedules the license expire date of a campaign
     *
     * @return Nothing
     */
    public void checkCampaignExpireDate() {
        System.out.println("this is a schedular to check Expire Date for campaigns");
        List<Campaign> campaigns = null;
        Date currentdate = new Date();

        try {
            campaigns = campaignManager.loadAllCampaigns();
            for (int i = 0; i < campaigns.size(); i++) {
                Date endDate = campaigns.get(i).getExpireDate();
                if (endDate != null && endDate.before(currentdate)) {
                    campaignManager.updateExpiredCampaign(campaigns.get(i), 0);
                }

            }

        } catch (QrException ex) {
            Logger.getLogger(Scheduler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

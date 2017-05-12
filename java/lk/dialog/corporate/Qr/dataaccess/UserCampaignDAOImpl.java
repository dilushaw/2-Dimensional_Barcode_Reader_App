/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

import java.util.List;

import lk.dialog.corporate.Qr.data.User;
import lk.dialog.corporate.Qr.data.UserCampaign;
import lk.dialog.corporate.Qr.data.UserPrivileges;
import lk.dialog.corporate.Qr.exception.QrException;
import lk.dialog.corporate.Qr.manager.CampaignManager;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * handle user-campaign entity communications, i.e. get user assigned campaigns.
 *
 * @author Dewmini
 * @version 2.1
 */
@Component
public class UserCampaignDAOImpl extends GenericDAOImpl<UserCampaign, Integer> implements UserCampaignDAO {

    static Logger log = Logger.getLogger(UserCampaignDAOImpl.class);

    public void deleteUserCampaignsByUser(User user) {
        try {
            List<UserCampaign> userCampaign = this.findUserCampaignsByUser(user);
            log.debug("inside delete user campaign");
            for (int i = 0; i < userCampaign.size(); i++) {
                log.debug(userCampaign.get(i).getUserCampaignId());
                delete(userCampaign.get(i));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<UserCampaign> findUserCampaignsByUser(User user) {
        List<UserCampaign> userCampaignList = null;
        try {


            String sql = "FROM UserCampaign u WHERE u.user = :user ";

            Query query = HibernateUtil.getSession().createQuery(sql).setParameter("user", user);
            userCampaignList = findMany(query);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return userCampaignList;
    }
}
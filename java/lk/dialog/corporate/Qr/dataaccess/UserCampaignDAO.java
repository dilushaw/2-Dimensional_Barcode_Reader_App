/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

import java.util.List;
import lk.dialog.corporate.Qr.data.User;
import lk.dialog.corporate.Qr.data.UserCampaign;

/**
 * handle user-campaign entity communications, i.e. get user assigned campaigns.
 *
 * @author Dewmini
 * @version 2.1
 */
public interface UserCampaignDAO extends GenericDAO<UserCampaign, Integer> {

    void deleteUserCampaignsByUser(User user);

    List<UserCampaign> findUserCampaignsByUser(User user);
}

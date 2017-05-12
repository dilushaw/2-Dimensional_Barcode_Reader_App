/**
 * Copyright(c) 2012 Dialog-University of Moratuwa Mobile Communications Research Laboratory. All Rights Reserved.
 */
package lk.dialog.corporate.Qr.dataaccess;

import lk.dialog.corporate.Qr.data.UserLogs;
import org.springframework.stereotype.Component;

/**
 * communicate regarding user_logs entity; mainly for saving user logs.(save method is inherited from GenericDAOImpl so
 * no need to implement again)
 *
 * @author Dewmini
 * @version 2.1
 */
@Component
public class UserLogsDAOImpl extends GenericDAOImpl<UserLogs, Integer> implements UserLogsDAO {
}

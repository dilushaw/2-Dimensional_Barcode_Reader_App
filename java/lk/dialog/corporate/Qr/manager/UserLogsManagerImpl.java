/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.manager;

import java.util.Calendar;
import lk.dialog.corporate.Qr.data.User;
import lk.dialog.corporate.Qr.data.UserLogs;
import lk.dialog.corporate.Qr.dataaccess.UserDAO;
import lk.dialog.corporate.Qr.dataaccess.UserLogsDAO;
import lk.dialog.corporate.Qr.exception.QrException;
import lk.dialog.corporate.Qr.utils.QrConstants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Dewmini
 */
@Component
public class UserLogsManagerImpl implements UserLogsManager {

    static Logger log = Logger.getLogger(UserLogsManagerImpl.class);
    @Autowired
    UserLogsDAO userLogsDAO;
    @Autowired
    User user;
    @Autowired
    UserDAO userDAO;

    public void saveEntityActionInLogs(User currentUser, String entityId, Integer actionId, String comment) throws QrException {
        log.debug("inside save logs");
        UserLogs userLogs = new UserLogs();
        userLogs.setUser(currentUser);
        userLogs.setActionDate(Calendar.getInstance().getTime());
        userLogs.setEntityId(entityId);
        userLogs.setActionId(actionId);
        userLogs.setComment(comment);
        userLogsDAO.save(userLogs);
        log.debug("before update user side");
//        user.getUserLogses().add(userLogs);
//        userDAO.save(user);
        log.debug("after save in logs");
    }
}

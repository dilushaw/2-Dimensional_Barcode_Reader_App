/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.manager;

import lk.dialog.corporate.Qr.data.User;
import lk.dialog.corporate.Qr.exception.QrException;

/**
 *
 * @author Dewmini
 */
public interface UserLogsManager {

    public void saveEntityActionInLogs(User currentUser, String entityId, Integer actionId, String comment) throws QrException;
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.dataaccess;

import java.util.List;
import lk.dialog.corporate.Qr.data.*;

/**
 *
 * @author Dilusha
 * @version 2.0
 */
public interface CorporateDAO extends GenericDAO<Corporate, Long> {

    void saveinCorporate(Corporate corporate);

    void updateinCorporate(Corporate corporate);

    List<Corporate> loadAllCorporates();//added by Dewmini

    Corporate findCorporate(String account);//added by Dewmini

    List<Corporate> loadAllCorporatesByStatus(Integer corpStatus);//added by Dewmini
}

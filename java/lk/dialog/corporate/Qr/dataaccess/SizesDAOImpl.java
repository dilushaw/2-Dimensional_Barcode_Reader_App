/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.dataaccess;

import java.util.ArrayList;
import lk.dialog.corporate.Qr.data.Sizes;
import lk.dialog.corporate.Qr.utils.HibernateUtil;
import org.hibernate.Query;
import org.springframework.stereotype.Component;

/**
 *
 * @author Dewmini
 * @version 2.1
 */
@Component
public class SizesDAOImpl extends GenericDAOImpl<Sizes, String> implements SizesDAO {

    /**
     * As barcode sizes are mapped to sizes define by using size id, when we want to get the Sizes object matching width
     * and length
     *
     * @param width
     * @param length
     * @return - Sizes
     */
    public Sizes findSize(int width, int length) {
        String hql = "from Sizes sizes where sizes.width='" + width + "' and sizes.length='" + length + "'";
        Query query = HibernateUtil.getSession().createQuery(hql);
        ArrayList<Sizes> sizes_list = (ArrayList<Sizes>) query.list();
        Sizes sizes = sizes_list.get(0);
        return sizes;
    }
}

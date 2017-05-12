/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.dialog.corporate.Qr.dataaccess;

import lk.dialog.corporate.Qr.data.Sizes;

/**
 *
 * @author Dewmini
 * @version 2.1
 */
public interface SizesDAO  extends GenericDAO<Sizes, String> {
    
    Sizes findSize(int width,int length);
    
}

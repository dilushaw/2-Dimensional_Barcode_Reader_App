package lk.dialog.corporate.Qr.direction;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

/**
 * This class is to generate the shortcode value which will be using when url redirection process
 * This class was created under 2DBC-phase1 
 *
 * @author Anuradha
 * @version 1.1
 */
public class UrlRedirection {

    private Random randBCId;
    /*
     * return redirection url
     */

    public String getShortCode() {

        String short_code = getKey();
        String show_url = short_code;

        return show_url;
    }

    /*
     * generate short code
     */
    public String getKey() {
        String key = "0";
        String sRand;
        randBCId = new Random();



        int nRand = randBCId.nextInt(9999);
        if (nRand < 10) {
            sRand = "000" + String.valueOf(nRand);
        } else if (nRand < 100) {
            sRand = "00" + String.valueOf(nRand);
        } else if (nRand < 1000) {
            sRand = "0" + String.valueOf(nRand);
        } else {
            sRand = String.valueOf(nRand);
        }

        /*
         * Get the current date time yyyymmddHHMMSS.mS.mS.mS
         */
        Calendar calendar = new GregorianCalendar();
        String sMonth;
        int nMonth = calendar.get(Calendar.MONTH);
        if (nMonth < 10) {
            sMonth = "0" + String.valueOf(nMonth);
        } else {
            sMonth = String.valueOf(nMonth);
        }
        String sDate;
        int nDate = calendar.get(Calendar.DATE);
        if (nDate < 10) {
            sDate = "0" + String.valueOf(nDate);
        } else {
            sDate = String.valueOf(nDate);
        }
        String sHour;
        int nHour = calendar.get(Calendar.HOUR_OF_DAY);
        if (nHour < 10) {
            sHour = "0" + String.valueOf(nHour);
        } else {
            sHour = String.valueOf(nHour);
        }
        String sMinute;
        int nMinute = calendar.get(Calendar.MINUTE);
        if (nMinute < 10) {
            sMinute = "0" + String.valueOf(nMinute);
        } else {
            sMinute = String.valueOf(nMinute);
        }
        String sSecond;
        int nSecond = calendar.get(Calendar.SECOND);
        if (nSecond < 10) {
            sSecond = "0" + String.valueOf(nSecond);
        } else {
            sSecond = String.valueOf(nSecond);
        }
        String sMilliSecond;
        int nMilliSecond = calendar.get(Calendar.MILLISECOND);
        if (nMilliSecond < 10) {
            sMilliSecond = "00" + String.valueOf(nMilliSecond);
        } else if (nMilliSecond < 100) {
            sMilliSecond = "0" + String.valueOf(nMilliSecond);
        } else {
            sMilliSecond = String.valueOf(nMilliSecond);
        }
        key = calendar.get(Calendar.YEAR) + sMonth + sDate + sHour + sMinute + sSecond + sMilliSecond + sRand;

        return key;
    }
}

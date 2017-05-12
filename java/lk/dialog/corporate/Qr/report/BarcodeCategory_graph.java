package lk.dialog.corporate.Qr.report;

/**
 *
 * @author Anuradha
 */
public class BarcodeCategory_graph {

    int url_count = 0;
    int sms_count = 0;
    int text_count = 0;
    int contact_count = 0;
    int initiate_call_count = 0;

    /*
     * Cheak barcode category and count it
     */
    public void cheakCategory(String category) {

        if (category.equals("1")) {
            url_count += 1;
        } else if (category.equals("4")) {
            sms_count += 1;
        } else if (category.equals("2")) {
            text_count += 1;
        } else if (category.equals("3")) {
            contact_count += 1;
        } else if (category.equals("5")) {
            initiate_call_count += 1;
        }

    }

    /*
     * return total quentity of each barcode type
     */
    public int[] getBarcodeQuentity() {

        int[] quentities = new int[5];

        quentities[0] = url_count;
        quentities[1] = sms_count;
        quentities[2] = text_count;
        quentities[3] = contact_count;
        quentities[4] = initiate_call_count;

        return quentities;
    }
}

package lk.dialog.corporate.Qr.dto;

/**
 * Store specific barcode relevant information. By using this DTO we can store the image byte stream for a particular
 * barcode which is returned by calling the web service. And also there is no limitation of the field to be defined
 * under this class. As per the current requiremnt it was only sufficient to store information about barcodeId, its byte
 * stream, image type, code name and title.
 *
 * @author Dewmini
 * @version 2.1
 */
public class BarCodeDataStoreDTO {

    private String barcodeId;
    private String image;//image byte stream
    private String imageType;
    private String title;
    private String codeName;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        if (imageType != null || !imageType.isEmpty()) {
            if (imageType.equals("1")) {
                this.imageType = "JPG";
            } else if (imageType.equals("2")) {
                this.imageType = "GIF";
            } else if (imageType.equals("3")) {
                this.imageType = "PNG";
            } else if (imageType.equals("4")) {
                this.imageType = "EPS";
            }
        } else {
            this.imageType = "Unknown";
        }

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getBarcodeId() {
        return barcodeId;
    }

    public void setBarcodeId(String barcodeId) {
        this.barcodeId = barcodeId;
    }
}

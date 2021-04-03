package shopesecond.com.shopesecond.models;


import shopesecond.com.shopesecond.activities.Base;

public class AdvertFashion {

    private String productID;
    private String imageUri;
    private String productTitle;
    private double productPrice;
    private String productType;
    private String productSize;
    private String productLocation;
    private String productDescription;
    private String userEmail;

    public AdvertFashion() {

    }

    public AdvertFashion(String productID, String imageUri, String productTitle, double productPrice, String productType, String productSize,
                         String productLocation, String productDescription, String userEmail) {
        this.productID = productID;
        this.imageUri = imageUri;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.productType = productType;
        this.productSize = productSize;
        this.productLocation = productLocation;
        this.productDescription = productDescription;
        this.userEmail = userEmail;
    }

    public AdvertFashion(String imageUri, String productTitle, double productPrice, String productType, String productSize, String productLocation,
                         String productDescription, String userEmail) {
        this.productID = Base.databaseFashionAds.push().getKey();
        this.imageUri = imageUri;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.productType = productType;
        this.productSize = productSize;
        this.productLocation = productLocation;
        this.productDescription = productDescription;
        this.userEmail = userEmail;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    public String getProductLocation() {
        return productLocation;
    }

    public void setProductLocation(String productLocation) {
        this.productLocation = productLocation;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public String toString() {
        return "AdvertFashion{" +
                "productID='" + productID + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", productTitle='" + productTitle + '\'' +
                ", productPrice=" + productPrice +
                ", productType='" + productType + '\'' +
                ", productSize='" + productSize + '\'' +
                ", productLocation='" + productLocation + '\'' +
                ", productDescription='" + productDescription + '\'' +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}
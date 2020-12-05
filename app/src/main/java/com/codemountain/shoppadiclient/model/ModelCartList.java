package com.codemountain.shoppadiclient.model;

public class ModelCartList {
    private String image, productID, price, quantity;

    public ModelCartList() {
    }

    public ModelCartList(String image, String productID, String price, String quantity) {
        this.image = image;
        this.productID = productID;
        this.price = price;
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}

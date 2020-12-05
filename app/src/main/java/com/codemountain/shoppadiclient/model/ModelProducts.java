package com.codemountain.shoppadiclient.model;

public class ModelProducts {

    private String name, categoryID, rating, price, date, description, image, likes;

    public ModelProducts() {
    }

    public ModelProducts(String name, String categoryID, String rating, String price, String date, String description, String image, String likes) {
        this.name = name;
        this.categoryID = categoryID;
        this.rating = rating;
        this.price = price;
        this.date = date;
        this.description = description;
        this.image = image;
        this.likes = likes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }
}

package com.codemountain.shoppadiclient.model;

public class ModelOrder {

    private String order_ID, state, date;

    public ModelOrder() {
    }

    public ModelOrder(String order_ID, String state, String date) {
        this.order_ID = order_ID;
        this.state = state;
        this.date = date;
    }

    public String getOrder_ID() {
        return order_ID;
    }

    public void setOrder_ID(String order_ID) {
        this.order_ID = order_ID;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

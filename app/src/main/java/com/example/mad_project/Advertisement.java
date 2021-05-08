package com.example.vesloo;

public class Advertisement {
    private String id;
    private String prodId;
    private String prodName;
    private String title;
    private String desc;
    private double discount;
    private String image;


    public Advertisement(String id, String prodId, String prodName, String title, String desc, double discount, String image) {
        this.id = id;
        this.prodId = prodId;
        this.prodName = prodName;
        this.title = title;
        this.desc = desc;
        this.discount = discount;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}

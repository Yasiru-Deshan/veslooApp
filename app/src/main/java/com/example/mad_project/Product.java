package com.example.vesloo;

import com.google.firebase.firestore.FirebaseFirestore;

public class Product {
    private String id;
    private String name;
    private String description;
    private double price;
    private String image;
    private int qty;
    private String code;
    private String supplier;

    public Product(String id, String name, String description, double price, String image, int qty, String code, String supplier) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.qty = qty;
        this.code = code;
        this.supplier = supplier;
    }

    public Product(String id, String name, String description, double price, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean deleteProduct(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        boolean deleted = false;

        //todo delete item from database
        return deleted;
    }
}

package com.example.myapplication;

public class Store {
    private String name;
    private String price;
    private String link;

    public Store(String name, String price, String link) {
        this.name = name;
        this.price = price;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getLink() {
        return link;
    }
}


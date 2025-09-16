package com.example.pizzaapp;

public class MenuItemModel {
    public String title;
    public String subtitle;
    public String category;
    public String imageUrl;
    public double price;
    public boolean isAvailable;

    // Firestore / SQLite need a no-arg constructor
    public MenuItemModel() {}

    public MenuItemModel(String title, String subtitle, String category,
                         String imageUrl, double price, boolean isAvailable) {
        this.title = title;
        this.subtitle = subtitle;
        this.category = category;
        this.imageUrl = imageUrl;
        this.price = price;
        this.isAvailable = isAvailable;
    }
}

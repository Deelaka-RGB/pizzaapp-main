package com.example.pizzaapp;

/** Model used across customer & admin screens.
 *  Fields are PUBLIC so MainActivity can access item.title, item.imageRes, item.liked, etc.
 *  Getters are provided for adapters that prefer them.
 */
public class FoodItem {

    // Public fields (match how MainActivity uses them)
    public String title;
    public String subtitle;
    public float  rating;    // 0..5
    public int    imageRes;  // drawable resource id
    public int    price;     // in rupees
    public boolean liked;    // MainActivity toggles this

    // Required empty constructor (handy for Firebase / deserialization)
    public FoodItem() {}

    public FoodItem(String title, String subtitle, float rating, int imageRes, int price) {
        this(title, subtitle, rating, imageRes, price, false);
    }

    public FoodItem(String title, String subtitle, float rating, int imageRes, int price, boolean liked) {
        this.title = title;
        this.subtitle = subtitle;
        this.rating = rating;
        this.imageRes = imageRes;
        this.price = price;
        this.liked = liked;
    }

    // Getters (so adapters can keep using m.getTitle(), etc.)
    public String getTitle()    { return title; }
    public String getSubtitle() { return subtitle; }
    public float  getRating()   { return rating; }
    public int    getImageRes() { return imageRes; }
    public int    getPrice()    { return price; }
    public boolean isLiked()    { return liked; }
}

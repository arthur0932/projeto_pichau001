package com.example.projetopichau;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    public static class Product {
        public String name;
        public String price;
        public int imageResId;

        public Product(String name, String price, int imageResId) {
            this.name = name;
            this.price = price;
            this.imageResId = imageResId;
        }
    }

    private static List<Product> cartItems = new ArrayList<>();

    public static void addProduct(Product product) {
        cartItems.add(product);
    }

    public static List<Product> getCartItems() {
        return cartItems;
    }

    public static void clearCart() {
        cartItems.clear();
    }
}
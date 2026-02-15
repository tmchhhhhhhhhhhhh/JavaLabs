package ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.model;

import java.io.Serializable;

public abstract class Dish implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected String name;
    protected double price;
    protected int calories;
    protected String ingredients;
    
    public Dish(String name, double price, int calories, String ingredients) {
        this.name = name;
        this.price = price;
        this.calories = calories;
        this.ingredients = ingredients;
    }
    
    public abstract void prepare();
    
    // Геттеры и сеттеры
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public int getCalories() {
        return calories;
    }
    
    public void setCalories(int calories) {
        this.calories = calories;
    }
    
    public String getIngredients() {
        return ingredients;
    }
    
    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
    
    @Override
    public String toString() {
        return name + " | " + price + " ₽ | " + calories + " kcal | " + ingredients;
    }
}

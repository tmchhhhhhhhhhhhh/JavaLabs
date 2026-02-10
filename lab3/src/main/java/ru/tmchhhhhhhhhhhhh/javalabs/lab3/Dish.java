package ru.tmchhhhhhhhhhhhh.javalabs.lab3;

import java.io.Serializable;

public abstract class Dish implements Serializable {
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

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getCalories() { return calories; }
    public String getIngredients() { return ingredients; }

    public void setPrice(double price) { this.price = price; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    @Override
    public String toString() {
        return name + " | " + price + " ₽ | " + calories + " kcal | " + ingredients;
    }
}

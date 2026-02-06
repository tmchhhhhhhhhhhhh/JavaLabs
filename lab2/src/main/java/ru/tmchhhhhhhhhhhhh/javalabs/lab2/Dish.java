package ru.tmchhhhhhhhhhhhh.javalabs.lab2;

import java.io.Serializable;
import java.util.List;

public abstract class Dish implements Serializable {
    private String name;
    private double price;
    private int calories;
    private List<String> ingredients;

    public Dish(String name, double price, int calories, List<String> ingredients) {
        this.name = name;
        this.price = price;
        this.calories = calories;
        this.ingredients = ingredients;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getCalories() { return calories; }
    public List<String> getIngredients() { return ingredients; }

    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setCalories(int calories) { this.calories = calories; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public abstract String getType();

    public void display() {
        System.out.println(getType() + ": " + name + ", Price: " + price +
                ", Calories: " + calories + ", Ingredients: " + ingredients);
    }
}

package ru.tmchhhhhhhhhhhhh.javalabs.lab2;

import java.util.List;

public class Dessert extends Dish {
    public Dessert(String name, double price, int calories, List<String> ingredients) {
        super(name, price, calories, ingredients);
    }

    @Override
    public String getType() {
        return "Десерт";
    }
}

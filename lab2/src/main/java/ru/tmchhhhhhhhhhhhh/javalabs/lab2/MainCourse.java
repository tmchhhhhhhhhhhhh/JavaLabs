package ru.tmchhhhhhhhhhhhh.javalabs.lab2;

import java.util.List;

public class MainCourse extends Dish {
    public MainCourse(String name, double price, int calories, List<String> ingredients) {
        super(name, price, calories, ingredients);
    }

    @Override
    public String getType() {
        return "Основное блюдо";
    }
}

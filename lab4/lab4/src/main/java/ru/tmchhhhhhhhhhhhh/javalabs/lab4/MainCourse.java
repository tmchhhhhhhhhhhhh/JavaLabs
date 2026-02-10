package ru.tmchhhhhhhhhhhhh.javalabs.lab4;

import ru.tmchhhhhhhhhhhhh.javalabs.lab4.interfaces.Cookable;
import ru.tmchhhhhhhhhhhhh.javalabs.lab4.interfaces.Orderable;

public class MainCourse extends Dish implements Cookable, Orderable {
    public MainCourse(String name, double price, int calories, String ingredients) {
        super(name, price, calories, ingredients);
    }

    @Override
    public void prepare() {
        System.out.println("Готовим основное блюдо: " + name);
    }

    @Override
    public void order() {
        System.out.println("Заказано основное блюдо: " + name);
    }
}


package ru.tmchhhhhhhhhhhhh.javalabs.lab4;

import ru.tmchhhhhhhhhhhhh.javalabs.lab4.interfaces.Cookable;
import ru.tmchhhhhhhhhhhhh.javalabs.lab4.interfaces.Orderable;

public class Dessert extends Dish implements Cookable, Orderable {
    public Dessert(String name, double price, int calories, String ingredients) {
        super(name, price, calories, ingredients);
    }

    @Override
    public void prepare() {
        System.out.println("Готовим десерт: " + name);
    }

    @Override
    public void order() {
        System.out.println("Заказан десерт: " + name);
    }
}
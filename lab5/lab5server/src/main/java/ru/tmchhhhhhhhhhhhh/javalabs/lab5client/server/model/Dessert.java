package ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.model;

import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.model.interfaces.Cookable;
import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.model.interfaces.Orderable;

public class Dessert extends Dish implements Cookable, Orderable {
    private static final long serialVersionUID = 1L;
    
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

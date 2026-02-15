package ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.model;

import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.model.interfaces.Cookable;
import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.model.interfaces.Orderable;

public class MainCourse extends Dish implements Cookable, Orderable {
    private static final long serialVersionUID = 1L;
    
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

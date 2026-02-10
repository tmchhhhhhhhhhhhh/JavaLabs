package ru.tmchhhhhhhhhhhhh.javalabs.lab5server.server.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dish implements Serializable {
    private String id;
    private String name;
    private double price;
    private int calories;
    private String ingredients;
    private String type; // "Starter", "MainCourse", "Dessert"

    @Override
    public String toString() {
        return name + " | " + price + " ₽ | " + calories + " kcal | " + ingredients;
    }
}
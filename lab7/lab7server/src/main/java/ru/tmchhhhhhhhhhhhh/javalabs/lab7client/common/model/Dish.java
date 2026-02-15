package ru.tmchhhhhhhhhhhhh.javalabs.lab7client.common.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "dishes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dish implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(length = 36)
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(nullable = false)
    private Integer calories;
    
    @Column(columnDefinition = "TEXT")
    private String ingredients;
    
    @Column(nullable = false, length = 50)
    private String type;  // Starter, MainCourse, Dessert
    
    @Column(name = "created_at")
    private transient LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    // Конструктор без id и createdAt (для создания новых блюд)
    public Dish(String name, Double price, Integer calories, String ingredients, String type) {
        this.name = name;
        this.price = price;
        this.calories = calories;
        this.ingredients = ingredients;
        this.type = type;
    }
}

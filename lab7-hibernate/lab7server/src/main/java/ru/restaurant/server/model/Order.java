package ru.restaurant.server.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.restaurant.common.model.Dish;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish;
    
    @Column(name = "order_time")
    private LocalDateTime orderTime;
    
    @PrePersist
    protected void onCreate() {
        if (orderTime == null) {
            orderTime = LocalDateTime.now();
        }
    }
    
    public Order(Dish dish) {
        this.dish = dish;
    }
}

package ru.tmchhhhhhhhhhhhh.javalabs.lab7client.server.util;

import ru.tmchhhhhhhhhhhhh.javalabs.lab7client.common.model.Dish;
import ru.tmchhhhhhhhhhhhh.javalabs.lab7client.server.dao.DishDAO;

public class DataInitializer {
    
    public static void loadTestData() {
        DishDAO dishDAO = new DishDAO();
        
        // Проверяем, есть ли уже данные
        if (!dishDAO.findAll().isEmpty()) {
            System.out.println("[INIT] Тестовые данные уже загружены\n");
            return;
        }
        
        System.out.println("[INIT] Загрузка тестовых данных...");
        
        // Создаем тестовые блюда
        dishDAO.create(new Dish("1", "Салат Цезарь", 350.0, 280, "Салат, курица, соус Цезарь, пармезан", "Starter", null));
        dishDAO.create(new Dish("2", "Стейк рибай", 1200.0, 680, "Говядина рибай, специи", "MainCourse", null));
        dishDAO.create(new Dish("3", "Тирамису", 420.0, 520, "Маскарпоне, кофе, какао", "Dessert", null));
        dishDAO.create(new Dish("4", "Борщ", 280.0, 350, "Свекла, капуста, мясо, сметана", "MainCourse", null));
        dishDAO.create(new Dish("5", "Паста Карбонара", 550.0, 650, "Паста, бекон, яйца, пармезан", "MainCourse", null));
        dishDAO.create(new Dish("6", "Брускетта", 320.0, 180, "Хлеб, томаты, базилик, чеснок", "Starter", null));
        
        System.out.println("[INIT] ✓ Загружено 6 тестовых блюд\n");
    }
}

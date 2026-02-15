package ru.tmchhhhhhhhhhhhh.javalabs.lab2;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class DishFactory {

    public static Dish createDish(Scanner sc, DishType type) {
        System.out.println("\n=== Создание " + type.getDisplayName() + " ===");

        String name = promptName(sc);
        double price = promptPrice(sc);
        int calories = promptCalories(sc);
        List<String> ingredients = promptIngredients(sc);

        return createDishByType(type, name, price, calories, ingredients);
    }

    private static String promptName(Scanner sc) {
        System.out.print("Название: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Название не может быть пустым");
        }
        return name;
    }

    private static double promptPrice(Scanner sc) {
        while (true) {
            try {
                System.out.print("Цена: ");
                double price = Double.parseDouble(sc.nextLine());
                if (price <= 0) {
                    System.out.println("Цена должна быть положительной!");
                    continue;
                }
                return price;
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод цены!");
            }
        }
    }

    private static int promptCalories(Scanner sc) {
        while (true) {
            try {
                System.out.print("Калории: ");
                int calories = Integer.parseInt(sc.nextLine());
                if (calories <= 0) {
                    System.out.println("Калории должны быть положительными!");
                    continue;
                }
                return calories;
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод калорий!");
            }
        }
    }

    private static List<String> promptIngredients(Scanner sc) {
        System.out.print("Ингредиенты (через запятую): ");
        String line = sc.nextLine();
        return Arrays.stream(line.split(","))
                .map(String::trim)
                .filter(ing -> !ing.isEmpty())
                .collect(Collectors.toList());
    }

    private static Dish createDishByType(DishType type, String name, double price,
                                         int calories, List<String> ingredients) {
        return switch (type) {
            case STARTER -> new Starter(name, price, calories, ingredients);
            case MAIN_COURSE -> new MainCourse(name, price, calories, ingredients);
            case DESSERT -> new Dessert(name, price, calories, ingredients);
        };
    }

    // Перегруженный метод для создания без Scanner (например, для тестов)
    public static Dish createDish(String name, double price, int calories,
                                  List<String> ingredients, DishType type) {
        return createDishByType(type, name, price, calories, ingredients);
    }
}
package ru.tmchhhhhhhhhhhhh.javalabs.lab2;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final String ADMIN_PASS = "1234";

    public static void main(String[] args) {
        MenuManager manager = new MenuManager();
        manager.loadFromFile();

        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== МЕНЮ РЕСТОРАНА ===");
            System.out.println("1. Добавить закуску");
            System.out.println("2. Добавить основное блюдо");
            System.out.println("3. Добавить десерт");
            System.out.println("4. Просмотреть меню");
            System.out.println("5. Поиск по названию");
            System.out.println("6. Поиск по ингредиентам");
            System.out.println("7. Фильтр по типу блюда");
            System.out.println("8. Фильтр по цене");
            System.out.println("9. Фильтр по калориям");
            System.out.println("10. Сортировка по названию");
            System.out.println("11. Сортировка по цене");
            System.out.println("12. Статистика по категориям");
            System.out.println("13. Удалить блюдо (админ)");
            System.out.println("14. Редактировать блюдо (админ)");
            System.out.println("15. Сохранить меню");
            System.out.println("16. Выход");
            System.out.print("Выберите действие: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Некорректный выбор.");
                continue;
            }

            switch (choice) {
                case 1 -> manager.addDish(createDish(sc, "Starter"));
                case 2 -> manager.addDish(createDish(sc, "MainCourse"));
                case 3 -> manager.addDish(createDish(sc, "Dessert"));
                case 4 -> manager.showAll();
                case 5 -> {
                    System.out.print("Введите название для поиска: ");
                    String kw = sc.nextLine();
                    manager.searchByName(kw);
                }
                case 6 -> {
                    System.out.print("Введите ингредиент для поиска: ");
                    String ing = sc.nextLine();
                    manager.searchByIngredient(ing);
                }
                case 7 -> {
                    System.out.println("Типы: Starter, MainCourse, Dessert");
                    System.out.print("Введите тип для фильтра: ");
                    String type = sc.nextLine();
                    switch (type.toLowerCase()) {
                        case "starter" -> manager.filterByType(Starter.class);
                        case "maincourse" -> manager.filterByType(MainCourse.class);
                        case "dessert" -> manager.filterByType(Dessert.class);
                        default -> System.out.println("Некорректный тип.");
                    }
                }
                case 8 -> {
                    System.out.print("Минимальная цена: ");
                    double min = Double.parseDouble(sc.nextLine());
                    System.out.print("Максимальная цена: ");
                    double max = Double.parseDouble(sc.nextLine());
                    manager.filterByPrice(min, max);
                }
                case 9 -> {
                    System.out.print("Минимальные калории: ");
                    int minC = Integer.parseInt(sc.nextLine());
                    System.out.print("Максимальные калории: ");
                    int maxC = Integer.parseInt(sc.nextLine());
                    manager.filterByCalories(minC, maxC);
                }
                case 10 -> manager.sortByName();
                case 11 -> manager.sortByPrice();
                case 12 -> manager.statistics();
                case 13 -> {
                    System.out.print("Введите пароль администратора: ");
                    String pass = sc.nextLine();
                    if (pass.equals(ADMIN_PASS)) {
                        manager.showAll();
                        System.out.print("Введите номер блюда для удаления: ");
                        int idx = Integer.parseInt(sc.nextLine()) - 1;
                        manager.removeDish(idx);
                    } else System.out.println("Неверный пароль!");
                }
                case 14 -> {
                    System.out.print("Введите пароль администратора: ");
                    String pass = sc.nextLine();
                    if (pass.equals(ADMIN_PASS)) {
                        manager.showAll();
                        System.out.print("Введите номер блюда для редактирования: ");
                        int idx = Integer.parseInt(sc.nextLine()) - 1;
                        manager.editDish(idx, sc);
                    } else System.out.println("Неверный пароль!");
                }
                case 15 -> manager.saveToFile();
                case 16 -> {
                    manager.saveToFile();
                    System.out.println("Выход...");
                    running = false;
                }
                default -> System.out.println("Некорректный выбор.");
            }
        }
        sc.close();
    }

    private static Dish createDish(Scanner sc, String type) {
        System.out.print("Название: ");
        String name = sc.nextLine();
        System.out.print("Цена: ");
        double price = Double.parseDouble(sc.nextLine());
        System.out.print("Калории: ");
        int calories = Integer.parseInt(sc.nextLine());
        System.out.print("Ингредиенты через запятую: ");
        String line = sc.nextLine();
        List<String> ingredients = Arrays.stream(line.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        return switch (type) {
            case "Starter" -> new Starter(name, price, calories, ingredients);
            case "MainCourse" -> new MainCourse(name, price, calories, ingredients);
            case "Dessert" -> new Dessert(name, price, calories, ingredients);
            default -> throw new IllegalArgumentException("Неизвестный тип блюда");
        };
    }
}

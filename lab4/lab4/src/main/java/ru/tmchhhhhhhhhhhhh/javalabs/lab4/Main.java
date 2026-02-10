package ru.tmchhhhhhhhhhhhh.javalabs.lab4;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String ADMIN_PASS = "1234";

    public static void main(String[] args) {
        MenuManager manager = new MenuManager();
        manager.loadFromFile();

        Scanner sc = new Scanner(System.in);
        boolean running = true;

        System.out.println("        СИСТЕМА УПРАВЛЕНИЯ МЕНЮ РЕСТОРАНА       ");

        while (running) {
            try {
                displayMainMenu();
                int choice = getIntInput(sc, "Выберите действие: ");

                switch (choice) {
                    case 1 -> addDishMenu(sc, manager);
                    case 2 -> manager.showMenu();
                    case 3 -> searchAndFilterMenu(sc, manager);
                    case 4 -> sortingMenu(sc, manager);
                    case 5 -> statisticsMenu(sc, manager);
                    case 6 -> orderDishMenu(sc, manager);
                    case 7 -> prepareDishMenu(sc, manager);
                    case 8 -> manager.showOrderStatistics();
                    case 9 -> manager.showCaloriesMap();
                    case 10 -> {
                        int n = getIntInput(sc, "Сколько записей показать? ");
                        manager.showMenuChangeLog(n);
                    }
                    case 11 -> adminMenu(sc, manager);
                    case 12 -> fileOperationsMenu(sc, manager);
                    case 13 -> {
                        running = false;
                        manager.saveToFile();
                        System.out.println("\n✓ Данные сохранены. До свидания!");
                    }
                    default -> System.out.println("✗ Некорректный выбор. Попробуйте снова.");
                }
            } catch (InputMismatchException e) {
                System.out.println("✗ Ошибка ввода! Пожалуйста, вводите числа.");
                sc.nextLine(); // Очистка буфера
            } catch (Exception e) {
                System.out.println("✗ Произошла ошибка: " + e.getMessage());
            }
        }
        sc.close();
    }

    private static void displayMainMenu() {
        System.out.println("\n│            ГЛАВНОЕ МЕНЮ                        │\n");
        System.out.println("│ 1.  Добавить блюдо                             │");
        System.out.println("│ 2.  Просмотреть меню                           │");
        System.out.println("│ 3.  Поиск и фильтры                            │");
        System.out.println("│ 4.  Сортировка меню                            │");
        System.out.println("│ 5.  Статистика                                 │");
        System.out.println("│ 6.  Заказать блюдо                             │");
        System.out.println("│ 7.  Приготовить блюдо                          │");
        System.out.println("│ 8.  Статистика заказов                         │");
        System.out.println("│ 9.  Карта калорийности                         │");
        System.out.println("│ 10. Журнал изменений меню                      │");
        System.out.println("│ 11. Администрирование (требуется пароль)       │");
        System.out.println("│ 12. Операции с файлами                         │");
        System.out.println("│ 13. Выход и сохранение                         │");

    }

    private static void addDishMenu(Scanner sc, MenuManager manager) {
        System.out.println("\n=== ДОБАВЛЕНИЕ БЛЮДА ===");
        System.out.println("1. Закуска (Starter)");
        System.out.println("2. Основное блюдо (Main Course)");
        System.out.println("3. Десерт (Dessert)");

        int type = getIntInput(sc, "Выберите тип блюда: ");
        sc.nextLine();

        System.out.print("Название: ");
        String name = sc.nextLine();

        double price = getDoubleInput(sc, "Цена (₽): ");

        int calories = getIntInput(sc, "Калории (kcal): ");
        sc.nextLine();

        System.out.print("Ингредиенты: ");
        String ingredients = sc.nextLine();

        Dish dish = switch (type) {
            case 1 -> new Starter(name, price, calories, ingredients);
            case 2 -> new MainCourse(name, price, calories, ingredients);
            case 3 -> new Dessert(name, price, calories, ingredients);
            default -> null;
        };

        if (dish != null) {
            manager.addDish(dish);
        } else {
            System.out.println("✗ Неверный тип блюда!");
        }
    }

    private static void searchAndFilterMenu(Scanner sc, MenuManager manager) {
        System.out.println("\n=== ПОИСК И ФИЛЬТРЫ ===");
        System.out.println("1. Фильтр по типу блюда");
        System.out.println("2. Фильтр по цене");
        System.out.println("3. Фильтр по калорийности");
        System.out.println("4. Комбинированный фильтр (тип + цена)");
        System.out.println("5. Поиск по названию");

        int choice = getIntInput(sc, "Выберите действие: ");
        sc.nextLine();

        List<Dish> results = null;

        switch (choice) {
            case 1 -> {
                System.out.println("Типы: 1-Starter, 2-MainCourse, 3-Dessert");
                int type = getIntInput(sc, "Выберите тип: ");
                Class<?> cls = switch (type) {
                    case 1 -> Starter.class;
                    case 2 -> MainCourse.class;
                    case 3 -> Dessert.class;
                    default -> null;
                };
                if (cls != null) {
                    results = manager.filterByType(cls);
                }
            }
            case 2 -> {
                double min = getDoubleInput(sc, "Минимальная цена: ");
                double max = getDoubleInput(sc, "Максимальная цена: ");
                results = manager.filterByPrice(min, max);
            }
            case 3 -> {
                int min = getIntInput(sc, "Минимум калорий: ");
                int max = getIntInput(sc, "Максимум калорий: ");
                results = manager.filterByCalories(min, max);
            }
            case 4 -> {
                System.out.println("Типы: 1-Starter, 2-MainCourse, 3-Dessert");
                int type = getIntInput(sc, "Выберите тип: ");
                Class<?> cls = switch (type) {
                    case 1 -> Starter.class;
                    case 2 -> MainCourse.class;
                    case 3 -> Dessert.class;
                    default -> null;
                };
                double min = getDoubleInput(sc, "Минимальная цена: ");
                double max = getDoubleInput(sc, "Максимальная цена: ");
                if (cls != null) {
                    results = manager.filterByTypeAndPrice(cls, min, max);
                }
            }
            case 5 -> {
                System.out.print("Введите название: ");
                String name = sc.nextLine();
                Dish found = manager.findDishByName(name);
                if (found != null) {
                    System.out.println("\n✓ Найдено: " + found);
                } else {
                    System.out.println("✗ Блюдо не найдено");
                }
                return;
            }
        }

        if (results != null) {
            if (results.isEmpty()) {
                System.out.println("✗ По заданным критериям ничего не найдено");
            } else {
                System.out.println("\n=== РЕЗУЛЬТАТЫ ПОИСКА (" + results.size() + " блюд) ===");
                results.forEach(System.out::println);
            }
        }
    }

    private static void sortingMenu(Scanner sc, MenuManager manager) {
        System.out.println("\n=== СОРТИРОВКА МЕНЮ ===");
        System.out.println("1. По названию");
        System.out.println("2. По цене");
        System.out.println("3. По калорийности");

        int choice = getIntInput(sc, "Выберите критерий: ");

        System.out.print("По возрастанию? (true/false): ");
        boolean ascending = sc.nextBoolean();

        switch (choice) {
            case 1 -> manager.sortByName(ascending);
            case 2 -> manager.sortByPrice(ascending);
            case 3 -> manager.sortByCalories(ascending);
            default -> System.out.println("✗ Неверный выбор");
        }
    }

    private static void statisticsMenu(Scanner sc, MenuManager manager) {
        System.out.println("\n=== СТАТИСТИКА ===");
        System.out.println("1. Общая статистика меню");
        System.out.println("2. ТОП-N популярных блюд");

        int choice = getIntInput(sc, "Выберите действие: ");

        switch (choice) {
            case 1 -> manager.showStatistics();
            case 2 -> {
                int n = getIntInput(sc, "Сколько блюд показать? ");
                manager.showTopNPopularDishes(n);
            }
            default -> System.out.println("✗ Неверный выбор");
        }
    }

    private static void orderDishMenu(Scanner sc, MenuManager manager) {
        if (manager.getMenuSize() == 0) {
            System.out.println("Меню пусто!");
            return;
        }

        manager.showMenu();
        sc.nextLine();
        System.out.print("\nВведите название блюда для заказа: ");
        String dishName = sc.nextLine();
        manager.orderDish(dishName);
    }

    private static void prepareDishMenu(Scanner sc, MenuManager manager) {
        if (manager.getMenuSize() == 0) {
            System.out.println("Меню пусто!");
            return;
        }

        manager.showMenu();
        sc.nextLine();
        System.out.print("\nВведите название блюда для приготовления: ");
        String dishName = sc.nextLine();
        manager.prepareDish(dishName);
    }

    private static void adminMenu(Scanner sc, MenuManager manager) {
        sc.nextLine();
        System.out.print("Введите пароль администратора: ");
        String password = sc.nextLine();

        if (!password.equals(ADMIN_PASS)) {
            System.out.println("✗ Неверный пароль!");
            return;
        }

        System.out.println("\n=== МЕНЮ АДМИНИСТРАТОРА ===");
        System.out.println("1. Удалить блюдо");
        System.out.println("2. Редактировать блюдо");

        int choice = getIntInput(sc, "Выберите действие: ");

        switch (choice) {
            case 1 -> {
                if (manager.getMenuSize() == 0) {
                    System.out.println("Меню пусто!");
                    return;
                }
                manager.showMenu();
                int idx = getIntInput(sc, "Номер блюда для удаления: ") - 1;
                manager.removeDish(idx);
            }
            case 2 -> {
                if (manager.getMenuSize() == 0) {
                    System.out.println("Меню пусто!");
                    return;
                }
                manager.showMenu();
                int idx = getIntInput(sc, "Номер блюда для редактирования: ") - 1;
                sc.nextLine();

                System.out.println("Что изменить? 1-Цена, 2-Ингредиенты, 3-Оба");
                int editChoice = getIntInput(sc, "Выбор: ");
                sc.nextLine();

                if (editChoice == 1 || editChoice == 3) {
                    double price = getDoubleInput(sc, "Новая цена: ");
                    manager.editDishPrice(idx, price);
                }

                if (editChoice == 2 || editChoice == 3) {
                    System.out.print("Новые ингредиенты: ");
                    String ingredients = sc.nextLine();
                    manager.editDishIngredients(idx, ingredients);
                }
            }
            default -> System.out.println("Неверный выбор");
        }
    }

    private static void fileOperationsMenu(Scanner sc, MenuManager manager) {
        System.out.println("\n=== ОПЕРАЦИИ С ФАЙЛАМИ ===");
        System.out.println("1. Сохранить меню (бинарный формат)");
        System.out.println("2. Загрузить меню (бинарный формат)");
        System.out.println("3. Экспорт в CSV");
        System.out.println("4. Импорт из CSV");
        System.out.println("5. Экспорт в JSON");
        System.out.println("6. Импорт из JSON");

        int choice = getIntInput(sc, "Выберите действие: ");

        switch (choice) {
            case 1 -> manager.saveToFile();
            case 2 -> manager.loadFromFile();
            case 3 -> manager.exportToCSV();
            case 4 -> manager.importFromCSV();
            case 5 -> manager.exportToJSON();
            case 6 -> manager.importFromJSON();
            default -> System.out.println("Неверный выбор");
        }
    }

    // Вспомогательные методы для безопасного ввода
    private static int getIntInput(Scanner sc, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Ошибка! Введите целое число.");
                sc.nextLine();
            }
        }
    }

    private static double getDoubleInput(Scanner sc, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return sc.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("Ошибка! Введите число.");
                sc.nextLine();
            }
        }
    }
}
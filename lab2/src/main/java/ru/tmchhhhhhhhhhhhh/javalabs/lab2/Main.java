package ru.tmchhhhhhhhhhhhh.javalabs.lab2;

import java.util.*;

public class Main {
    private static final String ADMIN_PASS = "1234";

    public static void main(String[] args) {
        MenuManager manager = new MenuManager();
        manager.loadFromFile();

        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMenu();

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Некорректный выбор.");
                continue;
            }

            try {
                running = processChoice(choice, sc, manager);
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
        sc.close();
    }

    private static void printMenu() {
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
    }

    private static boolean processChoice(int choice, Scanner sc, MenuManager manager) {
        switch (choice) {
            case 1 -> manager.addDish(DishFactory.createDish(sc, DishType.STARTER));
            case 2 -> manager.addDish(DishFactory.createDish(sc, DishType.MAIN_COURSE));
            case 3 -> manager.addDish(DishFactory.createDish(sc, DishType.DESSERT));
            case 4 -> manager.showAll();
            case 5 -> handleSearchByName(sc, manager);
            case 6 -> handleSearchByIngredient(sc, manager);
            case 7 -> handleFilterByType(sc, manager);
            case 8 -> handleFilterByPrice(sc, manager);
            case 9 -> handleFilterByCalories(sc, manager);
            case 10 -> manager.sortByName();
            case 11 -> manager.sortByPrice();
            case 12 -> manager.statistics();
            case 13 -> handleRemoveDish(sc, manager);
            case 14 -> handleEditDish(sc, manager);
            case 15 -> manager.saveToFile();
            case 16 -> {
                manager.saveToFile();
                System.out.println("Выход...");
                return false;
            }
            default -> System.out.println("Некорректный выбор.");
        }
        return true;
    }

    private static void handleSearchByName(Scanner sc, MenuManager manager) {
        System.out.print("Введите название для поиска: ");
        String kw = sc.nextLine();
        manager.searchByName(kw);
    }

    private static void handleSearchByIngredient(Scanner sc, MenuManager manager) {
        System.out.print("Введите ингредиент для поиска: ");
        String ing = sc.nextLine();
        manager.searchByIngredient(ing);
    }

    private static void handleFilterByType(Scanner sc, MenuManager manager) {
        System.out.println("Доступные типы:");
        for (DishType type : DishType.values()) {
            System.out.println("- " + type.name() + " (" + type.getDisplayName() + ")");
        }
        System.out.print("Введите тип для фильтра: ");

        try {
            String typeInput = sc.nextLine();
            DishType type = DishType.fromString(typeInput);

            switch (type) {
                case STARTER -> manager.filterByType(Starter.class);
                case MAIN_COURSE -> manager.filterByType(MainCourse.class);
                case DESSERT -> manager.filterByType(Dessert.class);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Некорректный тип: " + e.getMessage());
        }
    }

    private static void handleFilterByPrice(Scanner sc, MenuManager manager) {
        try {
            System.out.print("Минимальная цена: ");
            double min = Double.parseDouble(sc.nextLine());
            System.out.print("Максимальная цена: ");
            double max = Double.parseDouble(sc.nextLine());
            manager.filterByPrice(min, max);
        } catch (NumberFormatException e) {
            System.out.println("Некорректный ввод цены!");
        }
    }

    private static void handleFilterByCalories(Scanner sc, MenuManager manager) {
        try {
            System.out.print("Минимальные калории: ");
            int minC = Integer.parseInt(sc.nextLine());
            System.out.print("Максимальные калории: ");
            int maxC = Integer.parseInt(sc.nextLine());
            manager.filterByCalories(minC, maxC);
        } catch (NumberFormatException e) {
            System.out.println("Некорректный ввод калорий!");
        }
    }

    private static void handleRemoveDish(Scanner sc, MenuManager manager) {
        System.out.print("Введите пароль администратора: ");
        String pass = sc.nextLine();

        if (pass.equals(ADMIN_PASS)) {
            if (manager.getDishCount() == 0) {
                System.out.println("Меню пусто!");
                return;
            }

            manager.showAll();
            System.out.print("Введите номер блюда для удаления: ");

            try {
                int idx = Integer.parseInt(sc.nextLine()) - 1;
                manager.removeDish(idx);
            } catch (NumberFormatException e) {
                System.out.println("Некорректный номер!");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Блюда с таким номером не существует!");
            }
        } else {
            System.out.println("Неверный пароль!");
        }
    }

    private static void handleEditDish(Scanner sc, MenuManager manager) {
        System.out.print("Введите пароль администратора: ");
        String pass = sc.nextLine();

        if (pass.equals(ADMIN_PASS)) {
            if (manager.getDishCount() == 0) {
                System.out.println("Меню пусто!");
                return;
            }

            manager.showAll();
            System.out.print("Введите номер блюда для редактирования: ");

            try {
                int idx = Integer.parseInt(sc.nextLine()) - 1;
                manager.editDish(idx, sc);
            } catch (NumberFormatException e) {
                System.out.println("Некорректный номер!");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Блюда с таким номером не существует!");
            }
        } else {
            System.out.println("Неверный пароль!");
        }
    }
}
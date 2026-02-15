package ru.tmchhhhhhhhhhhhh.javalabs.lab5client;

import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.enums.Operation;
import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.exceptions.NoConnectionException;
import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.network.Request;
import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.network.Response;
import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.network.ServerClient;
import java.util.Scanner;

public class Main {
    private static ServerClient client;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("     КЛИЕНТ СИСТЕМЫ УПРАВЛЕНИЯ РЕСТОРАНОМ");
        System.out.println("=".repeat(60));

        try {
            client = ServerClient.getInstance();
            System.out.println("\n✓ Соединение с сервером установлено успешно!\n");

            boolean running = true;

            while (running) {
                displayMainMenu();

                try {
                    int choice = getIntInput("Выберите действие: ");

                    switch (choice) {
                        case 1 -> addDishMenu();
                        case 2 -> viewAllDishes();
                        case 3 -> searchDishByName();
                        case 4 -> updateDishPrice();
                        case 5 -> updateDishIngredients();
                        case 6 -> deleteDish();
                        case 7 -> orderDish();
                        case 8 -> prepareDish();
                        case 9 -> viewOrderStatistics();
                        case 10 -> filterMenu();
                        case 11 -> sortMenu();
                        case 12 -> viewStatistics();
                        case 13 -> viewCaloriesMap();
                        case 14 -> viewTopPopularDishes();
                        case 15 -> {
                            running = false;
                            disconnect();
                        }
                        default -> System.out.println("✗ Некорректный выбор!");
                    }

                } catch (Exception e) {
                    System.out.println("✗ Ошибка: " + e.getMessage());
                    scanner.nextLine(); // Очистка буфера
                }
            }

        } catch (NoConnectionException e) {
            System.err.println("✗ Не удалось подключиться к серверу!");
            System.err.println("Убедитесь, что сервер запущен на localhost:6666");
        } finally {
            scanner.close();
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("│                    ГЛАВНОЕ МЕНЮ                          │");
        System.out.println("─".repeat(60));
        System.out.println("│  УПРАВЛЕНИЕ БЛЮДАМИ:                                     │");
        System.out.println("│  1.  Добавить блюдо                                      │");
        System.out.println("│  2.  Просмотреть все блюда                               │");
        System.out.println("│  3.  Найти блюдо по названию                             │");
        System.out.println("│  4.  Обновить цену блюда                                 │");
        System.out.println("│  5.  Обновить ингредиенты блюда                          │");
        System.out.println("│  6.  Удалить блюдо                                       │");
        System.out.println("│                                                          │");
        System.out.println("│  ЗАКАЗЫ:                                                 │");
        System.out.println("│  7.  Заказать блюдо                                      │");
        System.out.println("│  8.  Приготовить блюдо                                   │");
        System.out.println("│  9.  Просмотреть статистику заказов                      │");
        System.out.println("│                                                          │");
        System.out.println("│  ФИЛЬТРЫ И СТАТИСТИКА:                                   │");
        System.out.println("│  10. Фильтровать меню                                    │");
        System.out.println("│  11. Сортировать меню                                    │");
        System.out.println("│  12. Просмотреть статистику меню                         │");
        System.out.println("│  13. Просмотреть карту калорийности                      │");
        System.out.println("│  14. ТОП популярных блюд                                 │");
        System.out.println("│                                                          │");
        System.out.println("│  15. Выход                                               │");
        System.out.println("─".repeat(60));
    }

    private static void addDishMenu() {
        System.out.println("\n=== ДОБАВЛЕНИЕ БЛЮДА ===");
        System.out.println("Типы блюд:");
        System.out.println("1. Закуска (Starter)");
        System.out.println("2. Основное блюдо (MainCourse)");
        System.out.println("3. Десерт (Dessert)");

        int typeChoice = getIntInput("Выберите тип блюда: ");
        scanner.nextLine();

        String type = switch (typeChoice) {
            case 1 -> "Starter";
            case 2 -> "MainCourse";
            case 3 -> "Dessert";
            default -> null;
        };

        if (type == null) {
            System.out.println("✗ Неверный тип блюда!");
            return;
        }

        System.out.print("Название: ");
        String name = scanner.nextLine();

        double price = getDoubleInput("Цена (₽): ");
        int calories = getIntInput("Калории (kcal): ");
        scanner.nextLine();

        System.out.print("Ингредиенты: ");
        String ingredients = scanner.nextLine();

        String data = type + "|" + name + "|" + price + "|" + calories + "|" + ingredients;
        Request request = new Request(Operation.ADD_DISH, data);

        Response response = client.sendRequest(request);

        if (response != null) {
            if (response.isSuccess()) {
                System.out.println("✓ " + response.getMessage());
            } else {
                System.out.println("✗ " + response.getMessage());
            }
        }
    }

    private static void viewAllDishes() {
        Request request = new Request(Operation.GET_ALL_DISHES);
        Response response = client.sendRequest(request);

        if (response != null && response.isSuccess()) {
            System.out.println("\n=== ВСЕ БЛЮДА В МЕНЮ ===");
            String data = response.getData();

            if (data == null || data.isEmpty()) {
                System.out.println("Меню пусто");
                return;
            }

            String[] dishes = data.split(";;");
            for (int i = 0; i < dishes.length; i++) {
                String[] parts = dishes[i].split("\\|");
                if (parts.length >= 5) {
                    System.out.printf("%d. [%s] %s | %.2f ₽ | %s kcal | %s\n",
                            i + 1, parts[0], parts[1], Double.parseDouble(parts[2]),
                            parts[3], parts[4]);
                }
            }
        } else {
            System.out.println("✗ Ошибка получения меню");
        }
    }

    private static void searchDishByName() {
        scanner.nextLine();
        System.out.print("Введите название блюда: ");
        String name = scanner.nextLine();

        Request request = new Request(Operation.GET_DISH_BY_NAME, name);
        Response response = client.sendRequest(request);

        if (response != null) {
            if (response.isSuccess()) {
                String[] parts = response.getData().split("\\|");
                if (parts.length >= 5) {
                    System.out.println("\n✓ Блюдо найдено:");
                    System.out.printf("[%s] %s\n", parts[0], parts[1]);
                    System.out.printf("Цена: %.2f ₽\n", Double.parseDouble(parts[2]));
                    System.out.printf("Калории: %s kcal\n", parts[3]);
                    System.out.printf("Ингредиенты: %s\n", parts[4]);
                }
            } else {
                System.out.println("✗ " + response.getMessage());
            }
        }
    }

    private static void updateDishPrice() {
        scanner.nextLine();
        System.out.print("Введите название блюда: ");
        String name = scanner.nextLine();

        double newPrice = getDoubleInput("Новая цена (₽): ");

        String data = name + "|" + newPrice;
        Request request = new Request(Operation.UPDATE_DISH_PRICE, data);

        Response response = client.sendRequest(request);

        if (response != null) {
            if (response.isSuccess()) {
                System.out.println("✓ " + response.getMessage());
            } else {
                System.out.println("✗ " + response.getMessage());
            }
        }
    }

    private static void updateDishIngredients() {
        scanner.nextLine();
        System.out.print("Введите название блюда: ");
        String name = scanner.nextLine();

        System.out.print("Новые ингредиенты: ");
        String ingredients = scanner.nextLine();

        String data = name + "|" + ingredients;
        Request request = new Request(Operation.UPDATE_DISH_INGREDIENTS, data);

        Response response = client.sendRequest(request);

        if (response != null) {
            if (response.isSuccess()) {
                System.out.println("✓ " + response.getMessage());
            } else {
                System.out.println("✗ " + response.getMessage());
            }
        }
    }

    private static void deleteDish() {
        scanner.nextLine();
        System.out.print("Введите название блюда для удаления: ");
        String name = scanner.nextLine();

        Request request = new Request(Operation.DELETE_DISH, name);
        Response response = client.sendRequest(request);

        if (response != null) {
            if (response.isSuccess()) {
                System.out.println("✓ " + response.getMessage());
            } else {
                System.out.println("✗ " + response.getMessage());
            }
        }
    }

    private static void orderDish() {
        scanner.nextLine();
        System.out.print("Введите название блюда для заказа: ");
        String name = scanner.nextLine();

        Request request = new Request(Operation.ORDER_DISH, name);
        Response response = client.sendRequest(request);

        if (response != null) {
            if (response.isSuccess()) {
                System.out.println("✓ " + response.getMessage());
            } else {
                System.out.println("✗ " + response.getMessage());
            }
        }
    }

    private static void prepareDish() {
        scanner.nextLine();
        System.out.print("Введите название блюда для приготовления: ");
        String name = scanner.nextLine();

        Request request = new Request(Operation.PREPARE_DISH, name);
        Response response = client.sendRequest(request);

        if (response != null) {
            if (response.isSuccess()) {
                System.out.println("✓ " + response.getMessage());
            } else {
                System.out.println("✗ " + response.getMessage());
            }
        }
    }

    private static void viewOrderStatistics() {
        Request request = new Request(Operation.GET_ORDER_STATISTICS);
        Response response = client.sendRequest(request);

        if (response != null && response.isSuccess()) {
            System.out.println("\n=== СТАТИСТИКА ЗАКАЗОВ ===");
            String data = response.getData();

            if (data == null || data.isEmpty()) {
                System.out.println("Заказов пока не было");
                return;
            }

            String[] orders = data.split(";;");

            for (String order : orders) {
                String[] parts = order.split("\\|");
                if (parts.length >= 2) {
                    System.out.printf("%-30s | Заказов: %s\n", parts[0], parts[1]);
                }
            }
        }
    }

    private static void filterMenu() {
        System.out.println("\n=== ФИЛЬТРАЦИЯ МЕНЮ ===");
        System.out.println("1. По типу блюда");
        System.out.println("2. По цене");
        System.out.println("3. По калорийности");

        int choice = getIntInput("Выберите тип фильтра: ");

        Request request = null;

        switch (choice) {
            case 1 -> {
                scanner.nextLine();
                System.out.println("Типы: Starter, MainCourse, Dessert");
                System.out.print("Введите тип: ");
                String type = scanner.nextLine();
                request = new Request(Operation.FILTER_BY_TYPE, type);
            }
            case 2 -> {
                double minPrice = getDoubleInput("Минимальная цена: ");
                double maxPrice = getDoubleInput("Максимальная цена: ");
                String data = minPrice + "|" + maxPrice;
                request = new Request(Operation.FILTER_BY_PRICE, data);
            }
            case 3 -> {
                int minCal = getIntInput("Минимум калорий: ");
                int maxCal = getIntInput("Максимум калорий: ");
                String data = minCal + "|" + maxCal;
                request = new Request(Operation.FILTER_BY_CALORIES, data);
            }
            default -> {
                System.out.println("✗ Неверный выбор!");
                return;
            }
        }

        if (request != null) {
            Response response = client.sendRequest(request);

            if (response != null && response.isSuccess()) {
                System.out.println("\n=== РЕЗУЛЬТАТЫ ФИЛЬТРАЦИИ ===");
                String data = response.getData();

                if (data == null || data.isEmpty()) {
                    System.out.println("По заданным критериям ничего не найдено");
                    return;
                }

                String[] dishes = data.split(";;");
                for (int i = 0; i < dishes.length; i++) {
                    String[] parts = dishes[i].split("\\|");
                    if (parts.length >= 5) {
                        System.out.printf("%d. [%s] %s | %.2f ₽ | %s kcal | %s\n",
                                i + 1, parts[0], parts[1], Double.parseDouble(parts[2]),
                                parts[3], parts[4]);
                    }
                }
            } else {
                System.out.println("✗ Ошибка фильтрации");
            }
        }
    }

    private static void sortMenu() {
        System.out.println("\n=== СОРТИРОВКА МЕНЮ ===");
        System.out.println("1. По названию");
        System.out.println("2. По цене");
        System.out.println("3. По калорийности");

        int choice = getIntInput("Выберите критерий: ");
        System.out.print("По возрастанию? (true/false): ");
        boolean ascending = scanner.nextBoolean();

        Operation operation = switch (choice) {
            case 1 -> Operation.SORT_BY_NAME;
            case 2 -> Operation.SORT_BY_PRICE;
            case 3 -> Operation.SORT_BY_CALORIES;
            default -> null;
        };

        if (operation == null) {
            System.out.println("✗ Неверный выбор!");
            return;
        }

        Request request = new Request(operation, String.valueOf(ascending));
        Response response = client.sendRequest(request);

        if (response != null && response.isSuccess()) {
            System.out.println("\n=== ОТСОРТИРОВАННОЕ МЕНЮ ===");
            String data = response.getData();

            if (data == null || data.isEmpty()) {
                System.out.println("Меню пусто");
                return;
            }

            String[] dishes = data.split(";;");
            for (int i = 0; i < dishes.length; i++) {
                String[] parts = dishes[i].split("\\|");
                if (parts.length >= 5) {
                    System.out.printf("%d. [%s] %s | %.2f ₽ | %s kcal | %s\n",
                            i + 1, parts[0], parts[1], Double.parseDouble(parts[2]),
                            parts[3], parts[4]);
                }
            }
        }
    }

    private static void viewStatistics() {
        Request request = new Request(Operation.GET_STATISTICS);
        Response response = client.sendRequest(request);

        if (response != null && response.isSuccess()) {
            System.out.println("\n" + response.getData());
        }
    }

    private static void viewCaloriesMap() {
        Request request = new Request(Operation.GET_CALORIES_MAP);
        Response response = client.sendRequest(request);

        if (response != null && response.isSuccess()) {
            System.out.println("\n=== КАРТА КАЛОРИЙНОСТИ ===");
            String data = response.getData();

            if (data == null || data.isEmpty()) {
                System.out.println("Карта пуста");
                return;
            }

            String[] items = data.split(";;");
            for (String item : items) {
                String[] parts = item.split("\\|");
                if (parts.length >= 2) {
                    System.out.printf("%-30s | %s kcal\n", parts[0], parts[1]);
                }
            }
        }
    }

    private static void viewTopPopularDishes() {
        int n = getIntInput("Сколько блюд показать? ");

        Request request = new Request(Operation.GET_TOP_POPULAR_DISHES, String.valueOf(n));
        Response response = client.sendRequest(request);

        if (response != null && response.isSuccess()) {
            System.out.println("\n=== ТОП-" + n + " ПОПУЛЯРНЫХ БЛЮД ===");
            String data = response.getData();

            if (data == null || data.isEmpty()) {
                System.out.println("Заказов пока не было");
                return;
            }

            String[] dishes = data.split(";;");
            for (int i = 0; i < dishes.length; i++) {
                String[] parts = dishes[i].split("\\|");
                if (parts.length >= 2) {
                    System.out.printf("%d. %-30s | Заказов: %s\n", i + 1, parts[0], parts[1]);
                }
            }
        }
    }

    private static void disconnect() {
        Request request = new Request(Operation.DISCONNECT);
        Response response = client.sendRequest(request);

        if (response != null && response.isSuccess()) {
            System.out.println("\n✓ " + response.getMessage());
        }

        client.disconnect();
        System.out.println("До свидания!");
    }

    // Вспомогательные методы
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return scanner.nextInt();
            } catch (Exception e) {
                System.out.println("✗ Ошибка! Введите целое число.");
                scanner.nextLine();
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return scanner.nextDouble();
            } catch (Exception e) {
                System.out.println("✗ Ошибка! Введите число.");
                scanner.nextLine();
            }
        }
    }
}

package ru.tmchhhhhhhhhhhhh.javalabs.lab6client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import ru.tmchhhhhhhhhhhhh.javalabs.lab6client.server.enums.Operation;
import ru.tmchhhhhhhhhhhhh.javalabs.lab6client.server.exceptions.NoConnectionException;
import ru.tmchhhhhhhhhhhhh.javalabs.lab6client.server.model.entities.Dish;
import ru.tmchhhhhhhhhhhhh.javalabs.lab6client.server.network.Request;
import ru.tmchhhhhhhhhhhhh.javalabs.lab6client.server.network.Response;
import ru.tmchhhhhhhhhhhhh.javalabs.lab6client.server.network.ServerClient;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    private static ServerClient client;
    private static final Gson gson = new Gson();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║   КЛИЕНТ СИСТЕМЫ УПРАВЛЕНИЯ РЕСТОРАНОМ         ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        try {
            client = ServerClient.getInstance();
            System.out.println();
        } catch (NoConnectionException e) {
            System.err.println(e.getMessage());
            System.err.println("Убедитесь, что сервер запущен и доступен.");
            return;
        }

        mainMenu();
        disconnectFromServer();
    }



    private static void mainMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n┌────────────────────────────────────────────────┐");
            System.out.println("│            ГЛАВНОЕ МЕНЮ КЛИЕНТА                │");
            System.out.println("├────────────────────────────────────────────────┤");
            System.out.println("│ 1.  Просмотреть все блюда                      │");
            System.out.println("│ 2.  Создать блюдо                              │");
            System.out.println("│ 3.  Обновить блюдо                             │");
            System.out.println("│ 4.  Удалить блюдо                              │");
            System.out.println("│ 5.  Фильтр по типу                             │");
            System.out.println("│ 6.  Фильтр по цене                             │");
            System.out.println("│ 7.  Фильтр по калориям                         │");
            System.out.println("│ 8.  Сортировка по названию                     │");
            System.out.println("│ 9.  Сортировка по цене                         │");
            System.out.println("│ 10. Сортировка по калориям                     │");
            System.out.println("│ 11. Заказать блюдо                             │");
            System.out.println("│ 12. Статистика заказов                         │");
            System.out.println("│ 13. Общая статистика                           │");
            System.out.println("│ 14. Карта калорийности                         │");
            System.out.println("│ 15. Журнал изменений                           │");
            System.out.println("│ 0.  Выход                                      │");
            System.out.println("└────────────────────────────────────────────────┘");
            System.out.print("Выберите действие: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1 -> getAllDishes();
                    case 2 -> createDishInteractive();
                    case 3 -> updateDishInteractive();
                    case 4 -> deleteDishInteractive();
                    case 5 -> filterByTypeInteractive();
                    case 6 -> filterByPriceInteractive();
                    case 7 -> filterByCaloriesInteractive();
                    case 8 -> sortByNameInteractive();
                    case 9 -> sortByPriceInteractive();
                    case 10 -> sortByCaloriesInteractive();
                    case 11 -> orderDishInteractive();
                    case 12 -> getOrderStatistics();
                    case 13 -> getStatistics();
                    case 14 -> getCaloriesMap();
                    case 15 -> getMenuChangeLogInteractive();
                    case 0 -> running = false;
                    default -> System.out.println("✗ Неверный выбор");
                }
            } catch (NumberFormatException e) {
                System.out.println("✗ Введите число!");
            }
        }
    }

    private static void getAllDishes() {
        Response response = client.sendRequest(new Request(Operation.GET_ALL_DISHES));

        if (response.isSuccess()) {
            List<Dish> dishes = gson.fromJson(response.getData(),
                    new TypeToken<List<Dish>>(){}.getType());

            System.out.println("\n=== МЕНЮ РЕСТОРАНА ===");
            if (dishes.isEmpty()) {
                System.out.println("Меню пусто");
            } else {
                for (int i = 0; i < dishes.size(); i++) {
                    Dish dish = dishes.get(i);
                    System.out.printf("%d. [%s] %s | %.2f ₽ | %d kcal | %s%n",
                            i + 1, dish.getType(), dish.getName(),
                            dish.getPrice(), dish.getCalories(), dish.getIngredients());
                }
            }
        } else {
            System.out.println("✗ " + response.getMessage());
        }
    }

    private static void createDish(Dish dish) {
        Request request = new Request(Operation.CREATE_DISH, gson.toJson(dish));
        Response response = client.sendRequest(request);

        if (response.isSuccess()) {
            System.out.println("✓ " + response.getMessage());
            Dish created = gson.fromJson(response.getData(), Dish.class);
            System.out.println("  Создано: " + created.getName());
        } else {
            System.out.println("✗ " + response.getMessage());
        }
    }

    private static void createDishInteractive() {
        System.out.println("\n=== СОЗДАНИЕ БЛЮДА ===");

        System.out.print("Название: ");
        String name = scanner.nextLine();

        System.out.print("Цена (₽): ");
        double price = Double.parseDouble(scanner.nextLine());

        System.out.print("Калории (kcal): ");
        int calories = Integer.parseInt(scanner.nextLine());

        System.out.print("Ингредиенты: ");
        String ingredients = scanner.nextLine();

        System.out.println("Тип: 1-Starter, 2-MainCourse, 3-Dessert");
        System.out.print("Выберите: ");
        int typeChoice = Integer.parseInt(scanner.nextLine());

        String type = switch (typeChoice) {
            case 1 -> "Starter";
            case 2 -> "MainCourse";
            case 3 -> "Dessert";
            default -> "MainCourse";
        };

        Dish dish = new Dish(UUID.randomUUID().toString(), name, price, calories, ingredients, type);
        createDish(dish);
    }

    private static void updateDishInteractive() {
        getAllDishes();

        System.out.print("\nВведите ID блюда для обновления: ");
        String id = scanner.nextLine();

        System.out.print("Новое название: ");
        String name = scanner.nextLine();

        System.out.print("Новая цена: ");
        double price = Double.parseDouble(scanner.nextLine());

        System.out.print("Новые калории: ");
        int calories = Integer.parseInt(scanner.nextLine());

        System.out.print("Новые ингредиенты: ");
        String ingredients = scanner.nextLine();

        System.out.print("Новый тип (Starter/MainCourse/Dessert): ");
        String type = scanner.nextLine();

        Dish dish = new Dish(id, name, price, calories, ingredients, type);

        Request request = new Request(Operation.UPDATE_DISH, gson.toJson(dish));
        Response response = client.sendRequest(request);

        System.out.println(response.isSuccess() ? "✓ " + response.getMessage() : "✗ " + response.getMessage());
    }

    private static void deleteDishInteractive() {
        getAllDishes();

        System.out.print("\nВведите ID блюда для удаления: ");
        String id = scanner.nextLine();

        JsonObject json = new JsonObject();
        json.addProperty("id", id);

        Request request = new Request(Operation.DELETE_DISH, gson.toJson(json));
        Response response = client.sendRequest(request);

        System.out.println(response.isSuccess() ? "✓ " + response.getMessage() : "✗ " + response.getMessage());
    }

    private static void filterByType(String type) {
        JsonObject json = new JsonObject();
        json.addProperty("type", type);

        Request request = new Request(Operation.FILTER_BY_TYPE, gson.toJson(json));
        Response response = client.sendRequest(request);

        if (response.isSuccess()) {
            List<Dish> dishes = gson.fromJson(response.getData(),
                    new TypeToken<List<Dish>>(){}.getType());

            System.out.println("\n=== ФИЛЬТР ПО ТИПУ: " + type + " ===");
            dishes.forEach(d -> System.out.println("  " + d));
        } else {
            System.out.println("✗ " + response.getMessage());
        }
    }

    private static void filterByTypeInteractive() {
        System.out.println("Тип: 1-Starter, 2-MainCourse, 3-Dessert");
        System.out.print("Выберите: ");
        int choice = Integer.parseInt(scanner.nextLine());

        String type = switch (choice) {
            case 1 -> "Starter";
            case 2 -> "MainCourse";
            case 3 -> "Dessert";
            default -> "MainCourse";
        };

        filterByType(type);
    }

    private static void filterByPrice(double min, double max) {
        JsonObject json = new JsonObject();
        json.addProperty("minPrice", min);
        json.addProperty("maxPrice", max);

        Request request = new Request(Operation.FILTER_BY_PRICE, gson.toJson(json));
        Response response = client.sendRequest(request);

        if (response.isSuccess()) {
            List<Dish> dishes = gson.fromJson(response.getData(),
                    new TypeToken<List<Dish>>(){}.getType());

            System.out.println("\n=== ФИЛЬТР ПО ЦЕНЕ: " + min + "-" + max + " ₽ ===");
            dishes.forEach(d -> System.out.println("  " + d));
        } else {
            System.out.println("✗ " + response.getMessage());
        }
    }

    private static void filterByPriceInteractive() {
        System.out.print("Минимальная цена: ");
        double min = Double.parseDouble(scanner.nextLine());

        System.out.print("Максимальная цена: ");
        double max = Double.parseDouble(scanner.nextLine());

        filterByPrice(min, max);
    }

    private static void filterByCaloriesInteractive() {
        System.out.print("Минимум калорий: ");
        int min = Integer.parseInt(scanner.nextLine());

        System.out.print("Максимум калорий: ");
        int max = Integer.parseInt(scanner.nextLine());

        JsonObject json = new JsonObject();
        json.addProperty("minCalories", min);
        json.addProperty("maxCalories", max);

        Request request = new Request(Operation.FILTER_BY_CALORIES, gson.toJson(json));
        Response response = client.sendRequest(request);

        if (response.isSuccess()) {
            List<Dish> dishes = gson.fromJson(response.getData(),
                    new TypeToken<List<Dish>>(){}.getType());

            System.out.println("\n=== ФИЛЬТР ПО КАЛОРИЯМ: " + min + "-" + max + " kcal ===");
            dishes.forEach(d -> System.out.println("  " + d));
        } else {
            System.out.println("✗ " + response.getMessage());
        }
    }

    private static void sortByPrice(boolean ascending) {
        JsonObject json = new JsonObject();
        json.addProperty("ascending", ascending);

        Request request = new Request(Operation.SORT_BY_PRICE, gson.toJson(json));
        Response response = client.sendRequest(request);

        if (response.isSuccess()) {
            List<Dish> dishes = gson.fromJson(response.getData(),
                    new TypeToken<List<Dish>>(){}.getType());

            System.out.println("\n=== СОРТИРОВКА ПО ЦЕНЕ (" +
                    (ascending ? "возрастание" : "убывание") + ") ===");
            dishes.forEach(d -> System.out.printf("  %.2f ₽ - %s%n", d.getPrice(), d.getName()));
        } else {
            System.out.println("✗ " + response.getMessage());
        }
    }

    private static void sortByNameInteractive() {
        System.out.print("По возрастанию? (true/false): ");
        boolean ascending = Boolean.parseBoolean(scanner.nextLine());

        JsonObject json = new JsonObject();
        json.addProperty("ascending", ascending);

        Request request = new Request(Operation.SORT_BY_NAME, gson.toJson(json));
        Response response = client.sendRequest(request);

        if (response.isSuccess()) {
            List<Dish> dishes = gson.fromJson(response.getData(),
                    new TypeToken<List<Dish>>(){}.getType());

            System.out.println("\n=== СОРТИРОВКА ПО НАЗВАНИЮ ===");
            dishes.forEach(d -> System.out.println("  " + d.getName()));
        } else {
            System.out.println("✗ " + response.getMessage());
        }
    }

    private static void sortByPriceInteractive() {
        System.out.print("По возрастанию? (true/false): ");
        boolean ascending = Boolean.parseBoolean(scanner.nextLine());
        sortByPrice(ascending);
    }

    private static void sortByCaloriesInteractive() {
        System.out.print("По возрастанию? (true/false): ");
        boolean ascending = Boolean.parseBoolean(scanner.nextLine());

        JsonObject json = new JsonObject();
        json.addProperty("ascending", ascending);

        Request request = new Request(Operation.SORT_BY_CALORIES, gson.toJson(json));
        Response response = client.sendRequest(request);

        if (response.isSuccess()) {
            List<Dish> dishes = gson.fromJson(response.getData(),
                    new TypeToken<List<Dish>>(){}.getType());

            System.out.println("\n=== СОРТИРОВКА ПО КАЛОРИЯМ ===");
            dishes.forEach(d -> System.out.printf("  %d kcal - %s%n", d.getCalories(), d.getName()));
        } else {
            System.out.println("✗ " + response.getMessage());
        }
    }

    private static void orderDish(String id) {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);

        Request request = new Request(Operation.ORDER_DISH, gson.toJson(json));
        Response response = client.sendRequest(request);

        if (response.isSuccess()) {
            System.out.println("✓ " + response.getMessage());
            Dish dish = gson.fromJson(response.getData(), Dish.class);
            System.out.println("  Заказано: " + dish.getName());
        } else {
            System.out.println("✗ " + response.getMessage());
        }
    }

    private static void orderDishInteractive() {
        getAllDishes();

        System.out.print("\nВведите ID блюда для заказа: ");
        String id = scanner.nextLine();

        orderDish(id);
    }

    private static void getOrderStatistics() {
        Response response = client.sendRequest(new Request(Operation.GET_ORDER_STATISTICS));

        if (response.isSuccess()) {
            Map<String, Object> stats = gson.fromJson(response.getData(),
                    new TypeToken<Map<String, Object>>(){}.getType());

            System.out.println("\n=== СТАТИСТИКА ЗАКАЗОВ ===");
            stats.forEach((key, value) -> {
                if (!key.equals("totalOrders")) {
                    System.out.printf("  %-30s | Заказов: %.0f%n", key, ((Number) value).doubleValue());
                }
            });

            if (stats.containsKey("totalOrders")) {
                System.out.println("\nВсего заказов: " + ((Number) stats.get("totalOrders")).intValue());
            }
        } else {
            System.out.println("✗ " + response.getMessage());
        }
    }

    private static void getStatistics() {
        Response response = client.sendRequest(new Request(Operation.GET_STATISTICS));

        if (response.isSuccess()) {
            System.out.println("\n=== ОБЩАЯ СТАТИСТИКА ===");
            System.out.println(response.getMessage());
        } else {
            System.out.println("✗ " + response.getMessage());
        }
    }

    private static void getCaloriesMap() {
        Response response = client.sendRequest(new Request(Operation.GET_CALORIES_MAP));

        if (response.isSuccess()) {
            Map<String, Integer> caloriesMap = gson.fromJson(response.getData(),
                    new TypeToken<Map<String, Integer>>(){}.getType());

            System.out.println("\n=== КАРТА КАЛОРИЙНОСТИ ===");
            caloriesMap.forEach((name, cal) ->
                    System.out.printf("  %-30s | %4d kcal%n", name, cal));
        } else {
            System.out.println("✗ " + response.getMessage());
        }
    }

    private static void getMenuChangeLog(int limit) {
        JsonObject json = new JsonObject();
        json.addProperty("limit", limit);

        Request request = new Request(Operation.GET_MENU_CHANGE_LOG, gson.toJson(json));
        Response response = client.sendRequest(request);

        if (response.isSuccess()) {
            List<String> logs = gson.fromJson(response.getData(),
                    new TypeToken<List<String>>(){}.getType());

            System.out.println("\n=== ЖУРНАЛ ИЗМЕНЕНИЙ (последние " + limit + ") ===");
            logs.forEach(log -> System.out.println("  " + log));
        } else {
            System.out.println("✗ " + response.getMessage());
        }
    }

    private static void getMenuChangeLogInteractive() {
        System.out.print("Сколько записей показать? ");
        int limit = Integer.parseInt(scanner.nextLine());
        getMenuChangeLog(limit);
    }

    private static void disconnectFromServer() {
        System.out.println("\nОтключение от сервера...");

        Request disconnectRequest = new Request(Operation.DISCONNECT);
        client.sendRequest(disconnectRequest);

        client.disconnect();
        System.out.println("До свидания!");
    }
}
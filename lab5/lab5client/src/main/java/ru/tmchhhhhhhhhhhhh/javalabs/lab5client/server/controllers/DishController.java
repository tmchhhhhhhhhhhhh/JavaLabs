package ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.controllers;


import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.network.Request;
import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.network.Response;
import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.model.entities.Dish;
import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.exceptions.ResponseException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DishController {
    private final Map<String, Dish> menu = new HashMap<>();
    private final Map<String, Integer> orderCount = new HashMap<>();
    private final Map<String, Integer> caloriesMap = new HashMap<>();
    private final LinkedList<String> menuChangeLog = new LinkedList<>();
    private final Gson gson = new Gson();

    public DishController() {
        initTestData();
    }

    private void initTestData() {
        createDish(new Dish("1", "Салат Цезарь", 350.0, 280, "Салат, курица, соус", "Starter"));
        createDish(new Dish("2", "Стейк рибай", 1200.0, 680, "Говядина, специи", "MainCourse"));
        createDish(new Dish("3", "Тирамису", 420.0, 520, "Маскарпоне, кофе", "Dessert"));
        createDish(new Dish("4", "Борщ", 280.0, 350, "Свекла, капуста, мясо", "MainCourse"));
    }

    private void createDish(Dish dish) {
        menu.put(dish.getId(), dish);
        orderCount.put(dish.getId(), 0);
        caloriesMap.put(dish.getName(), dish.getCalories());
        addToLog("Добавлено блюдо: " + dish.getName() + " (Тип: " + dish.getType() + ")");
    }

    public Response createDishFromRequest(Request request) {
        try {
            Dish dish = gson.fromJson(request.getData(), Dish.class);

            if (dish.getId() == null || dish.getId().isEmpty()) {
                dish.setId(UUID.randomUUID().toString());
            }

            if (menu.containsKey(dish.getId())) {
                throw new ResponseException("Блюдо с таким ID уже существует");
            }

            createDish(dish);
            return new Response(true, "Блюдо успешно добавлено", gson.toJson(dish));
        } catch (Exception e) {
            throw new ResponseException("Ошибка создания блюда: " + e.getMessage());
        }
    }

    public Response readDish(Request request) {
        try {
            JsonObject json = gson.fromJson(request.getData(), JsonObject.class);
            String id = json.get("id").getAsString();

            Dish dish = menu.get(id);
            if (dish == null) {
                throw new ResponseException("Блюдо не найдено");
            }

            return new Response(true, "Блюдо найдено", gson.toJson(dish));
        } catch (Exception e) {
            throw new ResponseException("Ошибка чтения блюда: " + e.getMessage());
        }
    }

    public Response updateDish(Request request) {
        try {
            Dish updatedDish = gson.fromJson(request.getData(), Dish.class);

            if (!menu.containsKey(updatedDish.getId())) {
                throw new ResponseException("Блюдо не найдено");
            }

            Dish oldDish = menu.get(updatedDish.getId());
            menu.put(updatedDish.getId(), updatedDish);
            caloriesMap.put(updatedDish.getName(), updatedDish.getCalories());

            addToLog("Обновлено блюдо: " + updatedDish.getName() +
                    " (Цена: " + oldDish.getPrice() + " → " + updatedDish.getPrice() + ")");

            return new Response(true, "Блюдо успешно обновлено", gson.toJson(updatedDish));
        } catch (Exception e) {
            throw new ResponseException("Ошибка обновления блюда: " + e.getMessage());
        }
    }

    public Response deleteDish(Request request) {
        try {
            JsonObject json = gson.fromJson(request.getData(), JsonObject.class);
            String id = json.get("id").getAsString();

            Dish dish = menu.remove(id);
            if (dish == null) {
                throw new ResponseException("Блюдо не найдено");
            }

            orderCount.remove(id);
            caloriesMap.remove(dish.getName());

            addToLog("Удалено блюдо: " + dish.getName() + " (Тип: " + dish.getType() + ")");

            return new Response(true, "Блюдо успешно удалено", gson.toJson(dish));
        } catch (Exception e) {
            throw new ResponseException("Ошибка удаления блюда: " + e.getMessage());
        }
    }

    public Response getAllDishes() {
        List<Dish> dishes = new ArrayList<>(menu.values());
        return new Response(true, "Получено " + dishes.size() + " блюд", gson.toJson(dishes));
    }

    public Response filterByType(Request request) {
        try {
            JsonObject json = gson.fromJson(request.getData(), JsonObject.class);
            String type = json.get("type").getAsString();

            List<Dish> filtered = menu.values().stream()
                    .filter(d -> d.getType().equals(type))
                    .collect(Collectors.toList());

            return new Response(true, "Найдено " + filtered.size() + " блюд типа " + type,
                    gson.toJson(filtered));
        } catch (Exception e) {
            throw new ResponseException("Ошибка фильтрации: " + e.getMessage());
        }
    }

    public Response filterByPrice(Request request) {
        try {
            JsonObject json = gson.fromJson(request.getData(), JsonObject.class);
            double minPrice = json.get("minPrice").getAsDouble();
            double maxPrice = json.get("maxPrice").getAsDouble();

            List<Dish> filtered = menu.values().stream()
                    .filter(d -> d.getPrice() >= minPrice && d.getPrice() <= maxPrice)
                    .collect(Collectors.toList());

            return new Response(true, "Найдено " + filtered.size() + " блюд в диапазоне цен",
                    gson.toJson(filtered));
        } catch (Exception e) {
            throw new ResponseException("Ошибка фильтрации: " + e.getMessage());
        }
    }

    public Response filterByCalories(Request request) {
        try {
            JsonObject json = gson.fromJson(request.getData(), JsonObject.class);
            int minCal = json.get("minCalories").getAsInt();
            int maxCal = json.get("maxCalories").getAsInt();

            List<Dish> filtered = menu.values().stream()
                    .filter(d -> d.getCalories() >= minCal && d.getCalories() <= maxCal)
                    .collect(Collectors.toList());

            return new Response(true, "Найдено " + filtered.size() + " блюд в диапазоне калорий",
                    gson.toJson(filtered));
        } catch (Exception e) {
            throw new ResponseException("Ошибка фильтрации: " + e.getMessage());
        }
    }

    public Response sortByName(Request request) {
        try {
            JsonObject json = gson.fromJson(request.getData(), JsonObject.class);
            boolean ascending = json.get("ascending").getAsBoolean();

            List<Dish> sorted = menu.values().stream()
                    .sorted(ascending ?
                            Comparator.comparing(Dish::getName) :
                            Comparator.comparing(Dish::getName).reversed())
                    .collect(Collectors.toList());

            return new Response(true, "Меню отсортировано по названию", gson.toJson(sorted));
        } catch (Exception e) {
            throw new ResponseException("Ошибка сортировки: " + e.getMessage());
        }
    }

    public Response sortByPrice(Request request) {
        try {
            JsonObject json = gson.fromJson(request.getData(), JsonObject.class);
            boolean ascending = json.get("ascending").getAsBoolean();

            List<Dish> sorted = menu.values().stream()
                    .sorted(ascending ?
                            Comparator.comparingDouble(Dish::getPrice) :
                            Comparator.comparingDouble(Dish::getPrice).reversed())
                    .collect(Collectors.toList());

            return new Response(true, "Меню отсортировано по цене", gson.toJson(sorted));
        } catch (Exception e) {
            throw new ResponseException("Ошибка сортировки: " + e.getMessage());
        }
    }

    public Response sortByCalories(Request request) {
        try {
            JsonObject json = gson.fromJson(request.getData(), JsonObject.class);
            boolean ascending = json.get("ascending").getAsBoolean();

            List<Dish> sorted = menu.values().stream()
                    .sorted(ascending ?
                            Comparator.comparingInt(Dish::getCalories) :
                            Comparator.comparingInt(Dish::getCalories).reversed())
                    .collect(Collectors.toList());

            return new Response(true, "Меню отсортировано по калорийности", gson.toJson(sorted));
        } catch (Exception e) {
            throw new ResponseException("Ошибка сортировки: " + e.getMessage());
        }
    }

    public Response orderDish(Request request) {
        try {
            JsonObject json = gson.fromJson(request.getData(), JsonObject.class);
            String id = json.get("id").getAsString();

            if (!menu.containsKey(id)) {
                throw new ResponseException("Блюдо не найдено");
            }

            orderCount.put(id, orderCount.getOrDefault(id, 0) + 1);
            Dish dish = menu.get(id);

            addToLog("Заказано блюдо: " + dish.getName() +
                    " (Всего заказов: " + orderCount.get(id) + ")");

            return new Response(true, "Заказ принят", gson.toJson(dish));
        } catch (Exception e) {
            throw new ResponseException("Ошибка заказа: " + e.getMessage());
        }
    }

    public Response getOrderStatistics() {
        Map<String, Object> stats = new HashMap<>();

        for (Map.Entry<String, Integer> entry : orderCount.entrySet()) {
            if (entry.getValue() > 0) {
                Dish dish = menu.get(entry.getKey());
                if (dish != null) {
                    stats.put(dish.getName(), entry.getValue());
                }
            }
        }

        int totalOrders = orderCount.values().stream().mapToInt(Integer::intValue).sum();
        stats.put("totalOrders", totalOrders);

        return new Response(true, "Статистика заказов", gson.toJson(stats));
    }

    public Response getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        Map<String, List<Dish>> grouped = menu.values().stream()
                .collect(Collectors.groupingBy(Dish::getType));

        Map<String, Map<String, Object>> typeStats = new HashMap<>();
        for (Map.Entry<String, List<Dish>> entry : grouped.entrySet()) {
            DoubleSummaryStatistics priceStats = entry.getValue().stream()
                    .mapToDouble(Dish::getPrice)
                    .summaryStatistics();

            Map<String, Object> typeStat = new HashMap<>();
            typeStat.put("count", entry.getValue().size());
            typeStat.put("avgPrice", priceStats.getAverage());
            typeStat.put("minPrice", priceStats.getMin());
            typeStat.put("maxPrice", priceStats.getMax());

            typeStats.put(entry.getKey(), typeStat);
        }

        DoubleSummaryStatistics overallPrice = menu.values().stream()
                .mapToDouble(Dish::getPrice)
                .summaryStatistics();

        DoubleSummaryStatistics overallCalories = menu.values().stream()
                .mapToDouble(Dish::getCalories)
                .summaryStatistics();

        stats.put("typeStats", typeStats);
        stats.put("avgPrice", overallPrice.getAverage());
        stats.put("minPrice", overallPrice.getMin());
        stats.put("maxPrice", overallPrice.getMax());
        stats.put("avgCalories", overallCalories.getAverage());
        stats.put("minCalories", overallCalories.getMin());
        stats.put("maxCalories", overallCalories.getMax());
        stats.put("totalDishes", menu.size());

        List<Dish> top3 = menu.values().stream()
                .sorted(Comparator.comparingDouble(Dish::getPrice).reversed())
                .limit(3)
                .collect(Collectors.toList());
        stats.put("top3Expensive", top3);

        return new Response(true, "Общая статистика", gson.toJson(stats));
    }

    public Response getCaloriesMap() {
        return new Response(true, "Карта калорийности", gson.toJson(caloriesMap));
    }

    public Response getMenuChangeLog(Request request) {
        try {
            JsonObject json = gson.fromJson(request.getData(), JsonObject.class);
            int limit = json.get("limit").getAsInt();

            List<String> logs = menuChangeLog.stream()
                    .limit(limit)
                    .collect(Collectors.toList());

            return new Response(true, "Журнал изменений", gson.toJson(logs));
        } catch (Exception e) {
            return new Response(true, "Журнал изменений", gson.toJson(menuChangeLog));
        }
    }

    private void addToLog(String message) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        menuChangeLog.addFirst("[" + timestamp + "] " + message);

        if (menuChangeLog.size() > 100) {
            menuChangeLog.removeLast();
        }
    }
}
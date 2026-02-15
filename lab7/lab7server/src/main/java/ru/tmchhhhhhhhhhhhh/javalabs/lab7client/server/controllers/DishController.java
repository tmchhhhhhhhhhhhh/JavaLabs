package ru.tmchhhhhhhhhhhhh.javalabs.lab7client.server.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ru.tmchhhhhhhhhhhhh.javalabs.lab7client.common.exceptions.ResponseException;
import ru.tmchhhhhhhhhhhhh.javalabs.lab7client.common.model.Dish;
import ru.tmchhhhhhhhhhhhh.javalabs.lab7client.common.network.Request;
import ru.tmchhhhhhhhhhhhh.javalabs.lab7client.common.network.Response;
import ru.tmchhhhhhhhhhhhh.javalabs.lab7client.server.dao.DishDAO;
import ru.tmchhhhhhhhhhhhh.javalabs.lab7client.server.dao.OrderDAO;

import java.util.*;

public class DishController {
    private final DishDAO dishDAO;
    private final OrderDAO orderDAO;
    private final Gson gson;
    
    public DishController() {
        this.dishDAO = new DishDAO();
        this.orderDAO = new OrderDAO();
        this.gson = new Gson();
    }
    
    public Response processRequest(Request request) {
        try {
            return switch (request.getOperation()) {
                case CREATE_DISH -> createDish(request);
                case GET_ALL_DISHES -> getAllDishes(request);
                case UPDATE_DISH -> updateDish(request);
                case DELETE_DISH -> deleteDish(request);
                case FILTER_BY_TYPE -> filterByType(request);
                case FILTER_BY_PRICE -> filterByPrice(request);
                case FILTER_BY_CALORIES -> filterByCalories(request);
                case SORT_BY_NAME -> sortByName(request);
                case SORT_BY_PRICE -> sortByPrice(request);
                case SORT_BY_CALORIES -> sortByCalories(request);
                case ORDER_DISH -> orderDish(request);
                case GET_ORDER_STATISTICS -> getOrderStatistics();
                case GET_STATISTICS -> getStatistics();
                case GET_CALORIES_MAP -> getCaloriesMap();
                case DISCONNECT -> new Response(true, "Отключение", null);
                default -> new Response(false, "Неизвестная операция", null);
            };
        } catch (ResponseException e) {
            return new Response(false, e.getMessage(), null);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Ошибка сервера: " + e.getMessage(), null);
        }
    }
    
    private Response createDish(Request request) {
        Dish dish = gson.fromJson(request.getData(), Dish.class);
        Dish created = dishDAO.create(dish);
        return new Response(true, "Блюдо создано", gson.toJson(created));
    }
    
    private Response getAllDishes(Request request) {
        List<Dish> dishes = dishDAO.findAll();
        return new Response(true, "Получено " + dishes.size() + " блюд", gson.toJson(dishes));
    }
    
    private Response updateDish(Request request) {
        Dish dish = gson.fromJson(request.getData(), Dish.class);
        Dish updated = dishDAO.update(dish);
        return new Response(true, "Блюдо обновлено", gson.toJson(updated));
    }
    
    private Response deleteDish(Request request) {
        JsonObject json = gson.fromJson(request.getData(), JsonObject.class);
        String id = json.get("id").getAsString();
        boolean deleted = dishDAO.delete(id);
        return new Response(deleted, deleted ? "Удалено" : "Не найдено", null);
    }
    
    private Response filterByType(Request request) {
        JsonObject json = gson.fromJson(request.getData(), JsonObject.class);
        String type = json.get("type").getAsString();
        List<Dish> dishes = dishDAO.findByType(type);
        return new Response(true, "Найдено " + dishes.size(), gson.toJson(dishes));
    }
    
    private Response filterByPrice(Request request) {
        JsonObject json = gson.fromJson(request.getData(), JsonObject.class);
        double min = json.get("minPrice").getAsDouble();
        double max = json.get("maxPrice").getAsDouble();
        List<Dish> dishes = dishDAO.findByPriceRange(min, max);
        return new Response(true, "Найдено " + dishes.size(), gson.toJson(dishes));
    }
    
    private Response filterByCalories(Request request) {
        JsonObject json = gson.fromJson(request.getData(), JsonObject.class);
        int min = json.get("minCalories").getAsInt();
        int max = json.get("maxCalories").getAsInt();
        List<Dish> dishes = dishDAO.findByCaloriesRange(min, max);
        return new Response(true, "Найдено " + dishes.size(), gson.toJson(dishes));
    }
    
    private Response sortByName(Request request) {
        JsonObject json = gson.fromJson(request.getData(), JsonObject.class);
        boolean asc = json.get("ascending").getAsBoolean();
        List<Dish> dishes = dishDAO.findAllSortedByName(asc);
        return new Response(true, "Отсортировано", gson.toJson(dishes));
    }
    
    private Response sortByPrice(Request request) {
        JsonObject json = gson.fromJson(request.getData(), JsonObject.class);
        boolean asc = json.get("ascending").getAsBoolean();
        List<Dish> dishes = dishDAO.findAllSortedByPrice(asc);
        return new Response(true, "Отсортировано", gson.toJson(dishes));
    }
    
    private Response sortByCalories(Request request) {
        JsonObject json = gson.fromJson(request.getData(), JsonObject.class);
        boolean asc = json.get("ascending").getAsBoolean();
        List<Dish> dishes = dishDAO.findAllSortedByCalories(asc);
        return new Response(true, "Отсортировано", gson.toJson(dishes));
    }
    
    private Response orderDish(Request request) {
        JsonObject json = gson.fromJson(request.getData(), JsonObject.class);
        String dishId = json.get("id").getAsString();
        Dish dish = dishDAO.findById(dishId);
        if (dish == null) {
            return new Response(false, "Блюдо не найдено", null);
        }
        orderDAO.createOrder(dish);
        return new Response(true, "Заказ принят", gson.toJson(dish));
    }
    
    private Response getStatistics() {
        Object[] stats = dishDAO.getStatistics();
        if (stats == null || stats[0] == null) {
            return new Response(false, "Меню пусто", null);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("avgPrice", stats[0]);
        result.put("minPrice", stats[1]);
        result.put("maxPrice", stats[2]);
        result.put("avgCalories", stats[3]);
        result.put("minCalories", stats[4]);
        result.put("maxCalories", stats[5]);
        result.put("totalDishes", stats[6]);
        
        List<Dish> top3 = dishDAO.findTopExpensive(3);
        result.put("top3Expensive", top3);
        
        return new Response(true, "Статистика", gson.toJson(result));
    }
    
    private Response getOrderStatistics() {
        Map<String, Long> orderCounts = orderDAO.getOrderCountByDish();
        Long total = orderDAO.getTotalOrders();
        
        Map<String, Object> result = new HashMap<>();
        orderCounts.forEach(result::put);
        result.put("totalOrders", total);
        
        return new Response(true, "Статистика заказов", gson.toJson(result));
    }
    
    private Response getCaloriesMap() {
        List<Dish> dishes = dishDAO.findAll();
        Map<String, Integer> caloriesMap = new HashMap<>();
        for (Dish dish : dishes) {
            caloriesMap.put(dish.getName(), dish.getCalories());
        }
        return new Response(true, "Карта калорийности", gson.toJson(caloriesMap));
    }
}

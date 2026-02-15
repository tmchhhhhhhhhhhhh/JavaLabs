package ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.controllers;


import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.network.Request;
import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.network.Response;
import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.services.MenuService;
import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.model.*;
import java.util.*;

public class DishController {
    private final MenuService menuService;
    
    public DishController() {
        this.menuService = MenuService.getInstance();
    }
    
    public Response addDish(Request request) {
        try {
            // Парсим данные из request.getData()
            // Формат: "тип|название|цена|калории|ингредиенты"
            String data = request.getData();
            String[] parts = data.split("\\|");
            
            if (parts.length < 5) {
                return new Response(false, "Неверный формат данных", null);
            }
            
            String type = parts[0];
            String name = parts[1];
            double price = Double.parseDouble(parts[2]);
            int calories = Integer.parseInt(parts[3]);
            String ingredients = parts[4];
            
            Dish dish = switch (type.toLowerCase()) {
                case "starter" -> new Starter(name, price, calories, ingredients);
                case "maincourse" -> new MainCourse(name, price, calories, ingredients);
                case "dessert" -> new Dessert(name, price, calories, ingredients);
                default -> null;
            };
            
            if (dish == null) {
                return new Response(false, "Неверный тип блюда", null);
            }
            
            menuService.addDish(dish);
            return new Response(true, "Блюдо успешно добавлено", name);
            
        } catch (Exception e) {
            return new Response(false, "Ошибка добавления блюда: " + e.getMessage(), null);
        }
    }
    
    public Response getAllDishes() {
        try {
            List<Dish> dishes = menuService.getAllDishes();
            StringBuilder result = new StringBuilder();
            
            for (int i = 0; i < dishes.size(); i++) {
                Dish dish = dishes.get(i);
                result.append(dish.getClass().getSimpleName()).append("|")
                      .append(dish.getName()).append("|")
                      .append(dish.getPrice()).append("|")
                      .append(dish.getCalories()).append("|")
                      .append(dish.getIngredients());
                
                if (i < dishes.size() - 1) {
                    result.append(";;");
                }
            }
            
            return new Response(true, "Получено " + dishes.size() + " блюд", result.toString());
            
        } catch (Exception e) {
            return new Response(false, "Ошибка получения меню: " + e.getMessage(), null);
        }
    }
    
    public Response getDishByName(Request request) {
        try {
            String dishName = request.getData();
            Dish dish = menuService.getDishByName(dishName);
            
            if (dish == null) {
                return new Response(false, "Блюдо не найдено", null);
            }
            
            String result = dish.getClass().getSimpleName() + "|" +
                          dish.getName() + "|" +
                          dish.getPrice() + "|" +
                          dish.getCalories() + "|" +
                          dish.getIngredients();
            
            return new Response(true, "Блюдо найдено", result);
            
        } catch (Exception e) {
            return new Response(false, "Ошибка поиска блюда: " + e.getMessage(), null);
        }
    }
    
    public Response updateDishPrice(Request request) {
        try {
            // Формат: "название|новая_цена"
            String[] parts = request.getData().split("\\|");
            if (parts.length < 2) {
                return new Response(false, "Неверный формат данных", null);
            }
            
            String name = parts[0];
            double newPrice = Double.parseDouble(parts[1]);
            
            boolean updated = menuService.updateDishPrice(name, newPrice);
            
            if (updated) {
                return new Response(true, "Цена обновлена", null);
            } else {
                return new Response(false, "Блюдо не найдено", null);
            }
            
        } catch (Exception e) {
            return new Response(false, "Ошибка обновления цены: " + e.getMessage(), null);
        }
    }
    
    public Response updateDishIngredients(Request request) {
        try {
            // Формат: "название|новые_ингредиенты"
            String[] parts = request.getData().split("\\|", 2);
            if (parts.length < 2) {
                return new Response(false, "Неверный формат данных", null);
            }
            
            String name = parts[0];
            String newIngredients = parts[1];
            
            boolean updated = menuService.updateDishIngredients(name, newIngredients);
            
            if (updated) {
                return new Response(true, "Ингредиенты обновлены", null);
            } else {
                return new Response(false, "Блюдо не найдено", null);
            }
            
        } catch (Exception e) {
            return new Response(false, "Ошибка обновления ингредиентов: " + e.getMessage(), null);
        }
    }
    
    public Response deleteDish(Request request) {
        try {
            String dishName = request.getData();
            boolean deleted = menuService.deleteDish(dishName);
            
            if (deleted) {
                return new Response(true, "Блюдо удалено", null);
            } else {
                return new Response(false, "Блюдо не найдено", null);
            }
            
        } catch (Exception e) {
            return new Response(false, "Ошибка удаления блюда: " + e.getMessage(), null);
        }
    }
    
    public Response orderDish(Request request) {
        try {
            String dishName = request.getData();
            boolean ordered = menuService.orderDish(dishName);
            
            if (ordered) {
                return new Response(true, "Заказ принят", null);
            } else {
                return new Response(false, "Блюдо не найдено в меню", null);
            }
            
        } catch (Exception e) {
            return new Response(false, "Ошибка заказа: " + e.getMessage(), null);
        }
    }
    
    public Response prepareDish(Request request) {
        try {
            String dishName = request.getData();
            boolean prepared = menuService.prepareDish(dishName);
            
            if (prepared) {
                return new Response(true, "Блюдо готовится", null);
            } else {
                return new Response(false, "Блюдо не найдено", null);
            }
            
        } catch (Exception e) {
            return new Response(false, "Ошибка приготовления: " + e.getMessage(), null);
        }
    }
    
    public Response getOrderStatistics() {
        try {
            Map<String, Integer> stats = menuService.getOrderStatistics();
            StringBuilder result = new StringBuilder();
            
            List<Map.Entry<String, Integer>> sortedStats = new ArrayList<>(stats.entrySet());
            sortedStats.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
            
            for (int i = 0; i < sortedStats.size(); i++) {
                Map.Entry<String, Integer> entry = sortedStats.get(i);
                if (entry.getValue() > 0) {
                    result.append(entry.getKey()).append("|").append(entry.getValue());
                    if (i < sortedStats.size() - 1) {
                        result.append(";;");
                    }
                }
            }
            
            return new Response(true, "Статистика заказов", result.toString());
            
        } catch (Exception e) {
            return new Response(false, "Ошибка получения статистики: " + e.getMessage(), null);
        }
    }
    
    public Response filterByType(Request request) {
        try {
            String type = request.getData();
            List<Dish> filtered = menuService.filterByType(type);
            
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < filtered.size(); i++) {
                Dish dish = filtered.get(i);
                result.append(dish.getClass().getSimpleName()).append("|")
                      .append(dish.getName()).append("|")
                      .append(dish.getPrice()).append("|")
                      .append(dish.getCalories()).append("|")
                      .append(dish.getIngredients());
                
                if (i < filtered.size() - 1) {
                    result.append(";;");
                }
            }
            
            return new Response(true, "Найдено " + filtered.size() + " блюд", result.toString());
            
        } catch (Exception e) {
            return new Response(false, "Ошибка фильтрации: " + e.getMessage(), null);
        }
    }
    
    public Response filterByPrice(Request request) {
        try {
            // Формат: "минимальная_цена|максимальная_цена"
            String[] parts = request.getData().split("\\|");
            double minPrice = Double.parseDouble(parts[0]);
            double maxPrice = Double.parseDouble(parts[1]);
            
            List<Dish> filtered = menuService.filterByPrice(minPrice, maxPrice);
            
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < filtered.size(); i++) {
                Dish dish = filtered.get(i);
                result.append(dish.getClass().getSimpleName()).append("|")
                      .append(dish.getName()).append("|")
                      .append(dish.getPrice()).append("|")
                      .append(dish.getCalories()).append("|")
                      .append(dish.getIngredients());
                
                if (i < filtered.size() - 1) {
                    result.append(";;");
                }
            }
            
            return new Response(true, "Найдено " + filtered.size() + " блюд", result.toString());
            
        } catch (Exception e) {
            return new Response(false, "Ошибка фильтрации по цене: " + e.getMessage(), null);
        }
    }
    
    public Response filterByCalories(Request request) {
        try {
            // Формат: "минимальные_калории|максимальные_калории"
            String[] parts = request.getData().split("\\|");
            int minCalories = Integer.parseInt(parts[0]);
            int maxCalories = Integer.parseInt(parts[1]);
            
            List<Dish> filtered = menuService.filterByCalories(minCalories, maxCalories);
            
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < filtered.size(); i++) {
                Dish dish = filtered.get(i);
                result.append(dish.getClass().getSimpleName()).append("|")
                      .append(dish.getName()).append("|")
                      .append(dish.getPrice()).append("|")
                      .append(dish.getCalories()).append("|")
                      .append(dish.getIngredients());
                
                if (i < filtered.size() - 1) {
                    result.append(";;");
                }
            }
            
            return new Response(true, "Найдено " + filtered.size() + " блюд", result.toString());
            
        } catch (Exception e) {
            return new Response(false, "Ошибка фильтрации по калориям: " + e.getMessage(), null);
        }
    }
    
    public Response sortByName(Request request) {
        try {
            boolean ascending = Boolean.parseBoolean(request.getData());
            List<Dish> sorted = menuService.sortByName(ascending);
            
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < sorted.size(); i++) {
                Dish dish = sorted.get(i);
                result.append(dish.getClass().getSimpleName()).append("|")
                      .append(dish.getName()).append("|")
                      .append(dish.getPrice()).append("|")
                      .append(dish.getCalories()).append("|")
                      .append(dish.getIngredients());
                
                if (i < sorted.size() - 1) {
                    result.append(";;");
                }
            }
            
            return new Response(true, "Меню отсортировано", result.toString());
            
        } catch (Exception e) {
            return new Response(false, "Ошибка сортировки: " + e.getMessage(), null);
        }
    }
    
    public Response sortByPrice(Request request) {
        try {
            boolean ascending = Boolean.parseBoolean(request.getData());
            List<Dish> sorted = menuService.sortByPrice(ascending);
            
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < sorted.size(); i++) {
                Dish dish = sorted.get(i);
                result.append(dish.getClass().getSimpleName()).append("|")
                      .append(dish.getName()).append("|")
                      .append(dish.getPrice()).append("|")
                      .append(dish.getCalories()).append("|")
                      .append(dish.getIngredients());
                
                if (i < sorted.size() - 1) {
                    result.append(";;");
                }
            }
            
            return new Response(true, "Меню отсортировано", result.toString());
            
        } catch (Exception e) {
            return new Response(false, "Ошибка сортировки: " + e.getMessage(), null);
        }
    }
    
    public Response sortByCalories(Request request) {
        try {
            boolean ascending = Boolean.parseBoolean(request.getData());
            List<Dish> sorted = menuService.sortByCalories(ascending);
            
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < sorted.size(); i++) {
                Dish dish = sorted.get(i);
                result.append(dish.getClass().getSimpleName()).append("|")
                      .append(dish.getName()).append("|")
                      .append(dish.getPrice()).append("|")
                      .append(dish.getCalories()).append("|")
                      .append(dish.getIngredients());
                
                if (i < sorted.size() - 1) {
                    result.append(";;");
                }
            }
            
            return new Response(true, "Меню отсортировано", result.toString());
            
        } catch (Exception e) {
            return new Response(false, "Ошибка сортировки: " + e.getMessage(), null);
        }
    }
    
    public Response getStatistics() {
        try {
            Map<String, Object> stats = menuService.getStatistics();
            
            if (stats.containsKey("error")) {
                return new Response(false, (String) stats.get("error"), null);
            }
            
            StringBuilder result = new StringBuilder();
            result.append("Всего блюд: ").append(stats.get("totalDishes")).append("\n");
            result.append("Средняя цена: ").append(String.format("%.2f", stats.get("avgPrice"))).append(" ₽\n");
            result.append("Мин. цена: ").append(String.format("%.2f", stats.get("minPrice"))).append(" ₽\n");
            result.append("Макс. цена: ").append(String.format("%.2f", stats.get("maxPrice"))).append(" ₽\n");
            result.append("Средняя калорийность: ").append(String.format("%.0f", stats.get("avgCalories"))).append(" kcal\n");
            result.append("Мин. калорийность: ").append(String.format("%.0f", stats.get("minCalories"))).append(" kcal\n");
            result.append("Макс. калорийность: ").append(String.format("%.0f", stats.get("maxCalories"))).append(" kcal");
            
            return new Response(true, "Статистика меню", result.toString());
            
        } catch (Exception e) {
            return new Response(false, "Ошибка получения статистики: " + e.getMessage(), null);
        }
    }
    
    public Response getCaloriesMap() {
        try {
            Map<String, Integer> caloriesMap = menuService.getCaloriesMap();
            StringBuilder result = new StringBuilder();
            
            List<Map.Entry<String, Integer>> sortedCalories = new ArrayList<>(caloriesMap.entrySet());
            sortedCalories.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
            
            for (int i = 0; i < sortedCalories.size(); i++) {
                Map.Entry<String, Integer> entry = sortedCalories.get(i);
                result.append(entry.getKey()).append("|").append(entry.getValue());
                if (i < sortedCalories.size() - 1) {
                    result.append(";;");
                }
            }
            
            return new Response(true, "Карта калорийности", result.toString());
            
        } catch (Exception e) {
            return new Response(false, "Ошибка получения карты калорийности: " + e.getMessage(), null);
        }
    }
    
    public Response getTopPopularDishes(Request request) {
        try {
            int n = Integer.parseInt(request.getData());
            List<Map.Entry<String, Integer>> topDishes = menuService.getTopPopularDishes(n);
            
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < topDishes.size(); i++) {
                Map.Entry<String, Integer> entry = topDishes.get(i);
                result.append(entry.getKey()).append("|").append(entry.getValue());
                if (i < topDishes.size() - 1) {
                    result.append(";;");
                }
            }
            
            return new Response(true, "ТОП-" + n + " популярных блюд", result.toString());
            
        } catch (Exception e) {
            return new Response(false, "Ошибка получения топа: " + e.getMessage(), null);
        }
    }
}

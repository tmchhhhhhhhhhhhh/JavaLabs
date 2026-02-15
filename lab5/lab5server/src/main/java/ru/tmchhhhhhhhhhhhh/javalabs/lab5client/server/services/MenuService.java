package ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.services;


import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.model.interfaces.Cookable;
import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.model.interfaces.Orderable;
import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.model.*;
import java.util.*;
import java.util.stream.Collectors;

public class MenuService {
    private static MenuService instance;
    private List<Dish> menu = new ArrayList<>();
    private Map<String, Integer> orderCount = new HashMap<>();
    private Map<String, Integer> caloriesMap = new HashMap<>();
    
    private MenuService() {
        addDish(new Starter("Брускетта", 350.0, 180, "Хлеб, томаты, базилик, оливковое масло"));
        addDish(new Starter("Салат Цезарь", 450.0, 320, "Салат, курица, пармезан, соус"));
        addDish(new MainCourse("Стейк Рибай", 1200.0, 580, "Говядина, специи"));
        addDish(new MainCourse("Паста Карбонара", 550.0, 650, "Паста, бекон, яйца, пармезан"));
        addDish(new Dessert("Тирамису", 380.0, 450, "Маскарпоне, кофе, какао"));
        addDish(new Dessert("Чизкейк", 420.0, 520, "Сливочный сыр, печенье, ягоды"));
    }
    
    public static synchronized MenuService getInstance() {
        if (instance == null) {
            instance = new MenuService();
        }
        return instance;
    }
    
    // CRUD операции
    public synchronized void addDish(Dish dish) {
        menu.add(dish);
        caloriesMap.put(dish.getName(), dish.getCalories());
        orderCount.put(dish.getName(), 0);
        System.out.println("✓ Блюдо добавлено: " + dish.getName());
    }
    
    public synchronized List<Dish> getAllDishes() {
        return new ArrayList<>(menu);
    }
    
    public synchronized Dish getDishByName(String name) {
        return menu.stream()
                .filter(d -> d.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
    
    public synchronized boolean updateDishPrice(String name, double newPrice) {
        Dish dish = getDishByName(name);
        if (dish != null) {
            dish.setPrice(newPrice);
            System.out.println("✓ Цена блюда '" + name + "' обновлена: " + newPrice + " ₽");
            return true;
        }
        return false;
    }
    
    public synchronized boolean updateDishIngredients(String name, String newIngredients) {
        Dish dish = getDishByName(name);
        if (dish != null) {
            dish.setIngredients(newIngredients);
            System.out.println("✓ Ингредиенты блюда '" + name + "' обновлены");
            return true;
        }
        return false;
    }
    
    public synchronized boolean deleteDish(String name) {
        Dish dish = getDishByName(name);
        if (dish != null) {
            menu.remove(dish);
            caloriesMap.remove(name);
            orderCount.remove(name);
            System.out.println("✓ Блюдо удалено: " + name);
            return true;
        }
        return false;
    }
    
    // Операции с заказами
    public synchronized boolean orderDish(String dishName) {
        if (orderCount.containsKey(dishName)) {
            orderCount.put(dishName, orderCount.get(dishName) + 1);
            
            Dish dish = getDishByName(dishName);
            if (dish instanceof Orderable) {
                ((Orderable) dish).order();
            }
            
            System.out.println("✓ Заказ принят: " + dishName + " (всего заказов: " + orderCount.get(dishName) + ")");
            return true;
        }
        return false;
    }
    
    public synchronized boolean prepareDish(String dishName) {
        Dish dish = getDishByName(dishName);
        if (dish != null && dish instanceof Cookable) {
            ((Cookable) dish).prepare();
            return true;
        }
        return false;
    }
    
    public synchronized Map<String, Integer> getOrderStatistics() {
        return new HashMap<>(orderCount);
    }
    
    // Фильтрация
    public synchronized List<Dish> filterByType(String type) {
        return menu.stream()
                .filter(d -> d.getClass().getSimpleName().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }
    
    public synchronized List<Dish> filterByPrice(double minPrice, double maxPrice) {
        return menu.stream()
                .filter(d -> d.getPrice() >= minPrice && d.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }
    
    public synchronized List<Dish> filterByCalories(int minCalories, int maxCalories) {
        return menu.stream()
                .filter(d -> d.getCalories() >= minCalories && d.getCalories() <= maxCalories)
                .collect(Collectors.toList());
    }
    
    // Сортировка
    public synchronized List<Dish> sortByName(boolean ascending) {
        List<Dish> sorted = new ArrayList<>(menu);
        sorted.sort(Comparator.comparing(Dish::getName));
        if (!ascending) Collections.reverse(sorted);
        return sorted;
    }
    
    public synchronized List<Dish> sortByPrice(boolean ascending) {
        List<Dish> sorted = new ArrayList<>(menu);
        sorted.sort(Comparator.comparingDouble(Dish::getPrice));
        if (!ascending) Collections.reverse(sorted);
        return sorted;
    }
    
    public synchronized List<Dish> sortByCalories(boolean ascending) {
        List<Dish> sorted = new ArrayList<>(menu);
        sorted.sort(Comparator.comparingInt(Dish::getCalories));
        if (!ascending) Collections.reverse(sorted);
        return sorted;
    }
    
    // Статистика
    public synchronized Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        if (menu.isEmpty()) {
            stats.put("error", "Меню пусто");
            return stats;
        }
        
        DoubleSummaryStatistics priceStats = menu.stream()
                .mapToDouble(Dish::getPrice)
                .summaryStatistics();
        
        DoubleSummaryStatistics caloriesStats = menu.stream()
                .mapToDouble(Dish::getCalories)
                .summaryStatistics();
        
        stats.put("totalDishes", menu.size());
        stats.put("avgPrice", priceStats.getAverage());
        stats.put("minPrice", priceStats.getMin());
        stats.put("maxPrice", priceStats.getMax());
        stats.put("avgCalories", caloriesStats.getAverage());
        stats.put("minCalories", caloriesStats.getMin());
        stats.put("maxCalories", caloriesStats.getMax());
        
        return stats;
    }
    
    public synchronized Map<String, Integer> getCaloriesMap() {
        return new HashMap<>(caloriesMap);
    }
    
    public synchronized List<Map.Entry<String, Integer>> getTopPopularDishes(int n) {
        return orderCount.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(n)
                .collect(Collectors.toList());
    }
}

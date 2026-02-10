package ru.tmchhhhhhhhhhhhh.javalabs.lab4;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MenuManager implements Serializable {
    // Основное меню - ArrayList
    private List<Dish> menu = new ArrayList<>();

    // HashMap для учёта заказов (блюдо → количество заказов)
    private Map<String, Integer> orderCount = new HashMap<>();

    // HashMap для калорийности (название блюда → калорийность)
    private Map<String, Integer> caloriesMap = new HashMap<>();

    // LinkedList для журнала изменений меню
    private LinkedList<String> menuChangeLog = new LinkedList<>();

    private final String FILE_NAME = "src/main/java/ru/tmchhhhhhhhhhhhh/javalabs/lab3/menu.dat";
    private final String CSV_FILE = "src/main/java/ru/tmchhhhhhhhhhhhh/javalabs/lab3/menu.csv";
    private final String JSON_FILE = "src/main/java/ru/tmchhhhhhhhhhhhh/javalabs/lab3/menu.json";

    // CRUD операции
    public void addDish(Dish dish) {
        menu.add(dish);
        caloriesMap.put(dish.getName(), dish.getCalories());
        orderCount.put(dish.getName(), 0);

        String logMessage = String.format("Добавлено блюдо: %s (Тип: %s, Цена: %.2f ₽, Калории: %d)",
                dish.getName(), dish.getClass().getSimpleName(), dish.getPrice(), dish.getCalories());

        addToMenuChangeLog(logMessage);
        LoggerManager.log(logMessage);
        System.out.println("✓ Блюдо успешно добавлено в меню");
    }

    public void removeDish(int index) {
        if (index >= 0 && index < menu.size()) {
            Dish dish = menu.get(index);
            String dishName = dish.getName();

            String logMessage = String.format("Удалено блюдо: %s (Тип: %s)",
                    dishName, dish.getClass().getSimpleName());

            menu.remove(index);
            caloriesMap.remove(dishName);
            orderCount.remove(dishName);

            addToMenuChangeLog(logMessage);
            LoggerManager.log(logMessage);
            System.out.println("✓ Блюдо успешно удалено");
        } else {
            System.out.println("✗ Ошибка: неверный индекс блюда");
        }
    }

    public void editDishPrice(int index, double price) {
        if (index >= 0 && index < menu.size()) {
            Dish dish = menu.get(index);
            double oldPrice = dish.getPrice();
            dish.setPrice(price);

            String logMessage = String.format("Изменена цена блюда '%s': %.2f ₽ → %.2f ₽",
                    dish.getName(), oldPrice, price);

            addToMenuChangeLog(logMessage);
            LoggerManager.log(logMessage);
            System.out.println("✓ Цена успешно изменена");
        } else {
            System.out.println("✗ Ошибка: неверный индекс блюда");
        }
    }

    public void editDishIngredients(int index, String ingredients) {
        if (index >= 0 && index < menu.size()) {
            Dish dish = menu.get(index);
            dish.setIngredients(ingredients);

            String logMessage = String.format("Изменены ингредиенты блюда '%s'", dish.getName());

            addToMenuChangeLog(logMessage);
            LoggerManager.log(logMessage);
            System.out.println("✓ Ингредиенты успешно изменены");
        } else {
            System.out.println("✗ Ошибка: неверный индекс блюда");
        }
    }

    // Метод для заказа блюда
    public void orderDish(String dishName) {
        if (orderCount.containsKey(dishName)) {
            orderCount.put(dishName, orderCount.get(dishName) + 1);

            Dish dish = menu.stream()
                    .filter(d -> d.getName().equals(dishName))
                    .findFirst()
                    .orElse(null);

            if (dish instanceof ru.tmchhhhhhhhhhhhh.javalabs.lab4.interfaces.Orderable) {
                ((ru.tmchhhhhhhhhhhhh.javalabs.lab4.interfaces.Orderable) dish).order();
            }

            String logMessage = String.format("Заказано блюдо '%s' (всего заказов: %d)",
                    dishName, orderCount.get(dishName));
            LoggerManager.log(logMessage);
            System.out.println("✓ Заказ принят");
        } else {
            System.out.println("✗ Ошибка: блюдо не найдено в меню");
        }
    }

    // Метод для приготовления блюда
    public void prepareDish(String dishName) {
        Dish dish = menu.stream()
                .filter(d -> d.getName().equals(dishName))
                .findFirst()
                .orElse(null);

        if (dish != null && dish instanceof ru.tmchhhhhhhhhhhhh.javalabs.lab4.interfaces.Cookable) {
            ((ru.tmchhhhhhhhhhhhh.javalabs.lab4.interfaces.Cookable) dish).prepare();
        } else {
            System.out.println("✗ Ошибка: блюдо не найдено");
        }
    }

    // Добавление записи в журнал изменений
    private void addToMenuChangeLog(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        menuChangeLog.addFirst("[" + timestamp + "] " + message);

        // Ограничиваем размер журнала (храним последние 100 записей)
        if (menuChangeLog.size() > 100) {
            menuChangeLog.removeLast();
        }
    }

    // Просмотр журнала изменений
    public void showMenuChangeLog(int lastN) {
        System.out.println("\n=== ЖУРНАЛ ИЗМЕНЕНИЙ МЕНЮ (последние " + lastN + " записей) ===");
        int count = 0;
        for (String entry : menuChangeLog) {
            if (count >= lastN) break;
            System.out.println(entry);
            count++;
        }
    }

    // Просмотр всех заказов
    public void showOrderStatistics() {
        System.out.println("\n=== СТАТИСТИКА ЗАКАЗОВ ===");
        if (orderCount.isEmpty()) {
            System.out.println("Заказов пока не было");
            return;
        }

        // Сортируем по количеству заказов (от большего к меньшему)
        orderCount.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(entry -> {
                    if (entry.getValue() > 0) {
                        System.out.printf("%-30s | Заказов: %d%n", entry.getKey(), entry.getValue());
                    }
                });

        int totalOrders = orderCount.values().stream().mapToInt(Integer::intValue).sum();
        System.out.println("\nВсего заказов: " + totalOrders);
    }

    // Просмотр карты калорийности
    public void showCaloriesMap() {
        System.out.println("\n=== КАРТА КАЛОРИЙНОСТИ БЛЮД ===");
        caloriesMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry ->
                        System.out.printf("%-30s | %4d kcal%n", entry.getKey(), entry.getValue())
                );
    }

    public void showMenu() {
        if (menu.isEmpty()) {
            System.out.println("Меню пусто");
            return;
        }

        System.out.println("\n=== ТЕКУЩЕЕ МЕНЮ ===");
        for (int i = 0; i < menu.size(); i++) {
            System.out.println((i + 1) + ". " + menu.get(i));
        }
    }

    // Фильтры (Stream API)
    public List<Dish> filterByType(Class<?> type) {
        return menu.stream()
                .filter(type::isInstance)
                .collect(Collectors.toList());
    }

    public List<Dish> filterByPrice(double min, double max) {
        return menu.stream()
                .filter(d -> d.getPrice() >= min && d.getPrice() <= max)
                .collect(Collectors.toList());
    }

    public List<Dish> filterByCalories(int min, int max) {
        return menu.stream()
                .filter(d -> d.getCalories() >= min && d.getCalories() <= max)
                .collect(Collectors.toList());
    }

    // Комбинированный фильтр
    public List<Dish> filterByTypeAndPrice(Class<?> type, double minPrice, double maxPrice) {
        return menu.stream()
                .filter(type::isInstance)
                .filter(d -> d.getPrice() >= minPrice && d.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    // Сортировка
    public void sortByName(boolean ascending) {
        menu.sort(Comparator.comparing(Dish::getName));
        if (!ascending) Collections.reverse(menu);
        System.out.println("✓ Меню отсортировано по названию (" + (ascending ? "А-Я" : "Я-А") + ")");
    }

    public void sortByPrice(boolean ascending) {
        menu.sort(Comparator.comparingDouble(Dish::getPrice));
        if (!ascending) Collections.reverse(menu);
        System.out.println("✓ Меню отсортировано по цене (" + (ascending ? "возрастание" : "убывание") + ")");
    }

    public void sortByCalories(boolean ascending) {
        menu.sort(Comparator.comparingInt(Dish::getCalories));
        if (!ascending) Collections.reverse(menu);
        System.out.println("✓ Меню отсортировано по калорийности (" + (ascending ? "возрастание" : "убывание") + ")");
    }

    // Статистика с использованием DoubleSummaryStatistics
    public void showStatistics() {
        if (menu.isEmpty()) {
            System.out.println("Меню пусто, статистика недоступна");
            return;
        }

        System.out.println("\n=== ОБЩАЯ СТАТИСТИКА МЕНЮ ===");

        // Группировка по типам
        Map<String, List<Dish>> grouped = menu.stream()
                .collect(Collectors.groupingBy(d -> d.getClass().getSimpleName()));

        System.out.println("\n1. Статистика по типам блюд:");
        for (String type : grouped.keySet()) {
            List<Dish> list = grouped.get(type);
            DoubleSummaryStatistics priceStats = list.stream()
                    .mapToDouble(Dish::getPrice)
                    .summaryStatistics();

            System.out.printf("%-15s | Количество: %2d | Средняя цена: %6.2f ₽ | Мин: %6.2f ₽ | Макс: %6.2f ₽%n",
                    type, list.size(), priceStats.getAverage(), priceStats.getMin(), priceStats.getMax());
        }

        // Общая статистика по ценам
        DoubleSummaryStatistics overallPriceStats = menu.stream()
                .mapToDouble(Dish::getPrice)
                .summaryStatistics();

        System.out.println("\n2. Общая статистика по ценам:");
        System.out.printf("Средняя цена: %.2f ₽%n", overallPriceStats.getAverage());
        System.out.printf("Минимальная цена: %.2f ₽%n", overallPriceStats.getMin());
        System.out.printf("Максимальная цена: %.2f ₽%n", overallPriceStats.getMax());
        System.out.printf("Общее количество блюд: %d%n", overallPriceStats.getCount());

        // Статистика по калорийности
        DoubleSummaryStatistics caloriesStats = menu.stream()
                .mapToDouble(Dish::getCalories)
                .summaryStatistics();

        System.out.println("\n3. Статистика по калорийности:");
        System.out.printf("Средняя калорийность: %.0f kcal%n", caloriesStats.getAverage());
        System.out.printf("Минимальная калорийность: %.0f kcal%n", caloriesStats.getMin());
        System.out.printf("Максимальная калорийность: %.0f kcal%n", caloriesStats.getMax());

        // ТОП-3 дорогих блюд используя PriorityQueue
        showTop3ExpensiveDishes();

        // ТОП-3 низкокалорийных блюд
        showTop3LowCalorieDishes();
    }

    // ТОП-3 дорогих блюд (PriorityQueue)
    private void showTop3ExpensiveDishes() {
        System.out.println("\n4. ТОП-3 самых дорогих блюд:");

        // PriorityQueue с компаратором по убыванию цены
        PriorityQueue<Dish> topExpensive = new PriorityQueue<>(
                (d1, d2) -> Double.compare(d2.getPrice(), d1.getPrice())
        );

        topExpensive.addAll(menu);

        int count = 0;
        while (!topExpensive.isEmpty() && count < 3) {
            Dish dish = topExpensive.poll();
            System.out.printf("   %d. %-30s | %.2f ₽%n", ++count, dish.getName(), dish.getPrice());
        }
    }

    // ТОП-3 низкокалорийных блюд
    private void showTop3LowCalorieDishes() {
        System.out.println("\n5. ТОП-3 самых низкокалорийных блюд:");

        menu.stream()
                .sorted(Comparator.comparingInt(Dish::getCalories))
                .limit(3)
                .forEach(d -> System.out.printf("   %-30s | %d kcal%n", d.getName(), d.getCalories()));
    }

    // ТОП-N самых популярных блюд
    public void showTopNPopularDishes(int n) {
        System.out.println("\n=== ТОП-" + n + " САМЫХ ПОПУЛЯРНЫХ БЛЮД ===");

        orderCount.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(n)
                .forEach(entry ->
                        System.out.printf("%-30s | Заказов: %d%n", entry.getKey(), entry.getValue())
                );
    }

    // Сохранение в бинарный файл
    public void saveToFile() {
        try {
            File file = new File(FILE_NAME);
            file.getParentFile().mkdirs();

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(menu);
                oos.writeObject(orderCount);
                oos.writeObject(caloriesMap);
                oos.writeObject(menuChangeLog);
            }

            LoggerManager.log("Меню сохранено в бинарный файл");
            System.out.println("✓ Меню успешно сохранено");
        } catch (IOException e) {
            System.out.println("✗ Ошибка сохранения: " + e.getMessage());
        }
    }

    // Загрузка из бинарного файла
    @SuppressWarnings("unchecked")
    public void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            menu = (List<Dish>) ois.readObject();
            orderCount = (Map<String, Integer>) ois.readObject();
            caloriesMap = (Map<String, Integer>) ois.readObject();
            menuChangeLog = (LinkedList<String>) ois.readObject();

            LoggerManager.log("Меню загружено из бинарного файла");
            System.out.println("✓ Меню загружено (" + menu.size() + " блюд)");
        } catch (FileNotFoundException e) {
            System.out.println("Файл меню не найден. Создано новое меню.");
        } catch (Exception e) {
            System.out.println("Ошибка загрузки меню: " + e.getMessage());
        }
    }

    // Экспорт в CSV
    public void exportToCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE))) {
            pw.println("type,name,price,calories,ingredients,orderCount");

            for (Dish dish : menu) {
                String type = dish.getClass().getSimpleName();
                int orders = orderCount.getOrDefault(dish.getName(), 0);

                pw.printf("%s,%s,%.2f,%d,\"%s\",%d%n",
                        type,
                        dish.getName(),
                        dish.getPrice(),
                        dish.getCalories(),
                        dish.getIngredients().replace("\"", "\"\""),
                        orders);
            }

            LoggerManager.log("Меню экспортировано в CSV");
            System.out.println("✓ Меню экспортировано в CSV: " + CSV_FILE);
        } catch (IOException e) {
            System.out.println("✗ Ошибка экспорта в CSV: " + e.getMessage());
        }
    }

    // Импорт из CSV
    public void importFromCSV() {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            List<Dish> importedDishes = new ArrayList<>();
            Map<String, Integer> importedOrders = new HashMap<>();
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] parts = parseCSVLine(line);
                if (parts.length >= 5) {
                    String type = parts[0];
                    String name = parts[1];
                    double price = Double.parseDouble(parts[2]);
                    int calories = Integer.parseInt(parts[3]);
                    String ingredients = parts[4];
                    int orders = parts.length > 5 ? Integer.parseInt(parts[5]) : 0;

                    Dish dish = createDishByType(type, name, price, calories, ingredients);

                    if (dish != null) {
                        importedDishes.add(dish);
                        importedOrders.put(name, orders);
                    }
                }
            }

            if (!importedDishes.isEmpty()) {
                menu = importedDishes;
                orderCount = importedOrders;

                // Обновляем карту калорийности
                caloriesMap.clear();
                for (Dish dish : menu) {
                    caloriesMap.put(dish.getName(), dish.getCalories());
                }

                LoggerManager.log("Меню импортировано из CSV (" + importedDishes.size() + " блюд)");
                System.out.println("✓ Импортировано " + importedDishes.size() + " блюд из CSV");
            }
        } catch (FileNotFoundException e) {
            System.out.println("✗ CSV файл не найден: " + CSV_FILE);
        } catch (IOException e) {
            System.out.println("✗ Ошибка импорта из CSV: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("✗ Ошибка формата данных в CSV: " + e.getMessage());
        }
    }

    // Экспорт в JSON
    public void exportToJSON() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(JSON_FILE))) {
            pw.println("{");
            pw.println("  \"menu\": [");

            for (int i = 0; i < menu.size(); i++) {
                Dish dish = menu.get(i);
                pw.println("    {");
                pw.println("      \"type\": \"" + dish.getClass().getSimpleName() + "\",");
                pw.println("      \"name\": \"" + escapeJson(dish.getName()) + "\",");
                pw.println("      \"price\": " + dish.getPrice() + ",");
                pw.println("      \"calories\": " + dish.getCalories() + ",");
                pw.println("      \"ingredients\": \"" + escapeJson(dish.getIngredients()) + "\",");
                pw.println("      \"orderCount\": " + orderCount.getOrDefault(dish.getName(), 0));
                pw.print("    }");

                if (i < menu.size() - 1) {
                    pw.println(",");
                } else {
                    pw.println();
                }
            }

            pw.println("  ]");
            pw.println("}");

            LoggerManager.log("Меню экспортировано в JSON");
            System.out.println("✓ Меню экспортировано в JSON: " + JSON_FILE);
        } catch (IOException e) {
            System.out.println("✗ Ошибка экспорта в JSON: " + e.getMessage());
        }
    }

    // Импорт из JSON
    public void importFromJSON() {
        try (BufferedReader br = new BufferedReader(new FileReader(JSON_FILE))) {
            List<Dish> importedDishes = new ArrayList<>();
            Map<String, Integer> importedOrders = new HashMap<>();
            StringBuilder jsonContent = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                jsonContent.append(line.trim());
            }

            String json = jsonContent.toString();

            int startPos = 0;
            while ((startPos = json.indexOf('{', startPos)) != -1) {
                int endPos = findMatchingBrace(json, startPos);
                if (endPos == -1) break;

                String obj = json.substring(startPos + 1, endPos);

                // Пропускаем внешний объект с "menu"
                if (!obj.contains("\"type\"")) {
                    startPos = endPos + 1;
                    continue;
                }

                Dish dish = parseJSONObject(obj, importedOrders);
                if (dish != null) {
                    importedDishes.add(dish);
                }

                startPos = endPos + 1;
            }

            if (!importedDishes.isEmpty()) {
                menu = importedDishes;
                orderCount = importedOrders;

                // Обновляем карту калорийности
                caloriesMap.clear();
                for (Dish dish : menu) {
                    caloriesMap.put(dish.getName(), dish.getCalories());
                }

                LoggerManager.log("Меню импортировано из JSON (" + importedDishes.size() + " блюд)");
                System.out.println("✓ Импортировано " + importedDishes.size() + " блюд из JSON");
            }
        } catch (FileNotFoundException e) {
            System.out.println("✗ JSON файл не найден: " + JSON_FILE);
        } catch (IOException e) {
            System.out.println("✗ Ошибка импорта из JSON: " + e.getMessage());
        }
    }

    // Вспомогательные методы
    private String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());

        return result.toArray(new String[0]);
    }

    private Dish parseJSONObject(String obj, Map<String, Integer> orders) {
        String type = extractValue(obj, "type");
        String name = extractValue(obj, "name");
        String priceStr = extractValue(obj, "price");
        String caloriesStr = extractValue(obj, "calories");
        String ingredients = extractValue(obj, "ingredients");
        String orderCountStr = extractValue(obj, "orderCount");

        if (type != null && name != null && priceStr != null && caloriesStr != null && ingredients != null) {
            try {
                double price = Double.parseDouble(priceStr);
                int calories = Integer.parseInt(caloriesStr);
                int orderCnt = orderCountStr != null ? Integer.parseInt(orderCountStr) : 0;

                orders.put(name, orderCnt);
                return createDishByType(type, name, price, calories, ingredients);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка формата числа в JSON");
            }
        }
        return null;
    }

    private Dish createDishByType(String type, String name, double price, int calories, String ingredients) {
        return switch (type) {
            case "Starter" -> new Starter(name, price, calories, ingredients);
            case "MainCourse" -> new MainCourse(name, price, calories, ingredients);
            case "Dessert" -> new Dessert(name, price, calories, ingredients);
            default -> null;
        };
    }

    private int findMatchingBrace(String json, int start) {
        int depth = 0;
        for (int i = start; i < json.length(); i++) {
            if (json.charAt(i) == '{') depth++;
            else if (json.charAt(i) == '}') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    private String extractValue(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return null;

        start += search.length();
        int end = json.indexOf(',', start);
        if (end == -1) end = json.length();

        String value = json.substring(start, end).trim();

        if (value.startsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }

        return value;
    }

    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // Получение размера меню
    public int getMenuSize() {
        return menu.size();
    }

    // Поиск блюда по имени
    public Dish findDishByName(String name) {
        return menu.stream()
                .filter(d -> d.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
package ru.tmchhhhhhhhhhhhh.javalabs.lab3;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MenuManager implements Serializable {
    private List<Dish> menu = new ArrayList<>();
    private final String FILE_NAME = "src/main/java/ru/tmchhhhhhhhhhhhh/javalabs/lab3/menu.dat";
    private final String CSV_FILE = "src/main/java/ru/tmchhhhhhhhhhhhh/javalabs/lab3/menu.csv";
    private final String JSON_FILE = "src/main/java/ru/tmchhhhhhhhhhhhh/javalabs/lab3/menu.json";


    public void addDish(Dish dish) {
        menu.add(dish);
        LoggerManager.log("Добавлено блюдо: " + dish.getName());
    }

    public void removeDish(int index) {
        if (index >= 0 && index < menu.size()) {
            LoggerManager.log("Удалено блюдо: " + menu.get(index).getName());
            menu.remove(index);
        }
    }

    public void editDishPrice(int index, double price) {
        if (index >= 0 && index < menu.size()) {
            menu.get(index).setPrice(price);
            LoggerManager.log("Изменена цена блюда: " + menu.get(index).getName());
        }
    }

    public void editDishIngredients(int index, String ingredients) {
        if (index >= 0 && index < menu.size()) {
            menu.get(index).setIngredients(ingredients);
            LoggerManager.log("Изменены ингредиенты блюда: " + menu.get(index).getName());
        }
    }

    public void showMenu() {
        for (int i = 0; i < menu.size(); i++) {
            System.out.println((i+1) + ". " + menu.get(i));
        }
    }

    public List<Dish> filterByType(Class<?> type) {
        return menu.stream()
                .filter(d -> type.isInstance(d))
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

    public void sortByName(boolean ascending) {
        menu.sort(Comparator.comparing(Dish::getName));
        if (!ascending) Collections.reverse(menu);
    }

    public void sortByPrice(boolean ascending) {
        menu.sort(Comparator.comparingDouble(Dish::getPrice));
        if (!ascending) Collections.reverse(menu);
    }

    public void showStatistics() {
        Map<String, List<Dish>> grouped = menu.stream()
                .collect(Collectors.groupingBy(d -> d.getClass().getSimpleName()));

        for (String type : grouped.keySet()) {
            List<Dish> list = grouped.get(type);
            double avg = list.stream().mapToDouble(Dish::getPrice).average().orElse(0);
            System.out.println(type + " | Кол-во: " + list.size() + " | Ср. цена: " + avg);
        }

        List<Dish> top3 = menu.stream()
                .sorted((d1, d2) -> Double.compare(d2.getPrice(), d1.getPrice()))
                .limit(3)
                .collect(Collectors.toList());
        System.out.println("ТОП-3 дорогих блюда:");
        top3.forEach(d -> System.out.println(d));
    }

    public void saveToFile() {
        try {
            File file = new File(FILE_NAME);
            file.getParentFile().mkdirs();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(menu);
            oos.close();
            LoggerManager.log("Меню сохранено");
        } catch (IOException e) {
            System.out.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME));
            menu = (List<Dish>) ois.readObject();
            ois.close();
            LoggerManager.log("Меню загружено");
        } catch (Exception e) {
            System.out.println("Нет сохранённого меню");
        }
    }

    public void exportToCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE))) {
            // Заголовок CSV
            pw.println("type,name,price,calories,ingredients");

            // Данные
            for (Dish dish : menu) {
                String type = dish.getClass().getSimpleName(); // Starter, MainCourse, Dessert
                pw.println(type + "," +
                        dish.getName() + "," +
                        dish.getPrice() + "," +
                        dish.getCalories() + "," +
                        "\"" + dish.getIngredients().replace("\"", "\"\"") + "\"");
            }

            LoggerManager.log("Меню экспортировано в CSV");
            System.out.println("Меню успешно экспортировано в CSV файл: " + CSV_FILE);
        } catch (IOException e) {
            System.out.println("Ошибка экспорта в CSV: " + e.getMessage());
        }
    }

    // ПРОСТОЙ ИМПОРТ ИЗ CSV
    public void importFromCSV() {
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            List<Dish> importedDishes = new ArrayList<>();
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue; // Пропускаем заголовок
                }

                // Парсим CSV строку
                String[] parts = parseCSVLine(line);
                if (parts.length >= 5) {
                    String type = parts[0];
                    String name = parts[1];
                    double price = Double.parseDouble(parts[2]);
                    int calories = Integer.parseInt(parts[3]);
                    String ingredients = parts[4];

                    // Создаем объект нужного типа
                    Dish dish = null;
                    switch (type) {
                        case "Starter":
                            dish = new Starter(name, price, calories, ingredients);
                            break;
                        case "MainCourse":
                            dish = new MainCourse(name, price, calories, ingredients);
                            break;
                        case "Dessert":
                            dish = new Dessert(name, price, calories, ingredients);
                            break;
                    }

                    if (dish != null) {
                        importedDishes.add(dish);
                    }
                }
            }

            if (!importedDishes.isEmpty()) {
                menu = importedDishes;
                LoggerManager.log("Меню импортировано из CSV (" + importedDishes.size() + " блюд)");
                System.out.println("Успешно импортировано " + importedDishes.size() + " блюд из CSV");
            }
        } catch (FileNotFoundException e) {
            System.out.println("CSV файл не найден: " + CSV_FILE);
        } catch (IOException e) {
            System.out.println("Ошибка импорта из CSV: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка формата данных в CSV: " + e.getMessage());
        }
    }

    // Вспомогательный метод для парсинга CSV строк (учитывает кавычки)
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

    // ПРОСТОЙ ЭКСПОРТ В JSON
    public void exportToJSON() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(JSON_FILE))) {
            pw.println("[");

            for (int i = 0; i < menu.size(); i++) {
                Dish dish = menu.get(i);
                pw.println("  {");
                pw.println("    \"type\": \"" + dish.getClass().getSimpleName() + "\",");
                pw.println("    \"name\": \"" + escapeJson(dish.getName()) + "\",");
                pw.println("    \"price\": " + dish.getPrice() + ",");
                pw.println("    \"calories\": " + dish.getCalories() + ",");
                pw.println("    \"ingredients\": \"" + escapeJson(dish.getIngredients()) + "\"");
                pw.print("  }");

                if (i < menu.size() - 1) {
                    pw.println(",");
                } else {
                    pw.println();
                }
            }

            pw.println("]");

            LoggerManager.log("Меню экспортировано в JSON");
            System.out.println("Меню успешно экспортировано в JSON файл: " + JSON_FILE);
        } catch (IOException e) {
            System.out.println("Ошибка экспорта в JSON: " + e.getMessage());
        }
    }

    // ПРОСТОЙ ИМПОРТ ИЗ JSON
    public void importFromJSON() {
        try (BufferedReader br = new BufferedReader(new FileReader(JSON_FILE))) {
            List<Dish> importedDishes = new ArrayList<>();
            StringBuilder jsonContent = new StringBuilder();
            String line;

            // Читаем весь файл
            while ((line = br.readLine()) != null) {
                jsonContent.append(line.trim());
            }

            String json = jsonContent.toString();

            // Упрощенный парсинг JSON (для простой структуры)
            // Ищем все объекты { ... }
            int startPos = 0;
            while ((startPos = json.indexOf('{', startPos)) != -1) {
                int endPos = json.indexOf('}', startPos);
                if (endPos == -1) break;

                String obj = json.substring(startPos + 1, endPos);
                parseJSONObject(obj, importedDishes);

                startPos = endPos + 1;
            }

            if (!importedDishes.isEmpty()) {
                menu = importedDishes;
                LoggerManager.log("Меню импортировано из JSON (" + importedDishes.size() + " блюд)");
                System.out.println("Успешно импортировано " + importedDishes.size() + " блюд из JSON");
            }
        } catch (FileNotFoundException e) {
            System.out.println("JSON файл не найден: " + JSON_FILE);
        } catch (IOException e) {
            System.out.println("Ошибка импорта из JSON: " + e.getMessage());
        }
    }

    // Вспомогательный метод для парсинга JSON объекта
    private void parseJSONObject(String obj, List<Dish> dishes) {
        String type = extractValue(obj, "type");
        String name = extractValue(obj, "name");
        String priceStr = extractValue(obj, "price");
        String caloriesStr = extractValue(obj, "calories");
        String ingredients = extractValue(obj, "ingredients");

        if (type != null && name != null && priceStr != null && caloriesStr != null && ingredients != null) {
            try {
                double price = Double.parseDouble(priceStr);
                int calories = Integer.parseInt(caloriesStr);

                Dish dish = null;
                switch (type) {
                    case "Starter":
                        dish = new Starter(name, price, calories, ingredients);
                        break;
                    case "MainCourse":
                        dish = new MainCourse(name, price, calories, ingredients);
                        break;
                    case "Dessert":
                        dish = new Dessert(name, price, calories, ingredients);
                        break;
                }

                if (dish != null) {
                    dishes.add(dish);
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка формата числа в JSON");
            }
        }
    }

    // Вспомогательный метод для извлечения значения из JSON
    private String extractValue(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return null;

        start += search.length();
        int end = json.indexOf(',', start);
        if (end == -1) end = json.length();

        String value = json.substring(start, end).trim();

        // Убираем кавычки для строк
        if (value.startsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }

        return value;
    }

    // Вспомогательный метод для экранирования спецсимволов в JSON
    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

}

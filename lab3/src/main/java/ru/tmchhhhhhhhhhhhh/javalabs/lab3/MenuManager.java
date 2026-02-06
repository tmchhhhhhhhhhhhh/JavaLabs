package ru.tmchhhhhhhhhhhhh.javalabs.lab3;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MenuManager implements Serializable {
    private List<Dish> menu = new ArrayList<>();
    private final String FILE_NAME = "src/main/java/ru/tmchhhhhhhhhhhhh/javalabs/lab3/menu.dat";

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
}

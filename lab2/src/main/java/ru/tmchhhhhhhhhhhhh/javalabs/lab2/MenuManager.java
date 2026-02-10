package ru.tmchhhhhhhhhhhhh.javalabs.lab2;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MenuManager {
    private List<Dish> dishes = new ArrayList<>();
    private final String FILE_NAME = "lab2/menu.dat";

    public void addDish(Dish d) {
        dishes.add(d);
        System.out.println(d.getType() + " добавлено: " + d.getName());
    }

    public void showAll() {
        if (dishes.isEmpty()) {
            System.out.println("Меню пусто.");
            return;
        }
        for (int i = 0; i < dishes.size(); i++) {
            System.out.print((i + 1) + ". ");
            dishes.get(i).display();
        }
    }

    public void removeDish(int index) {
        if (index >= 0 && index < dishes.size()) {
            System.out.println("Удалено: " + dishes.get(index).getName());
            dishes.remove(index);
        } else {
            System.out.println("Некорректный номер.");
        }
    }

    public void editDish(int index, Scanner sc) {
        if (index >= 0 && index < dishes.size()) {
            Dish d = dishes.get(index);
            System.out.print("Введите новую цену: ");
            double price = sc.nextDouble();
            sc.nextLine();
            d.setPrice(price);
            System.out.print("Введите ингредиенты через запятую: ");
            String line = sc.nextLine();
            List<String> ingredients = Arrays.stream(line.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
            d.setIngredients(ingredients);
            System.out.println("Блюдо обновлено.");
        } else {
            System.out.println("Некорректный номер.");
        }
    }

    public void searchByName(String keyword) {
        boolean found = false;
        for (Dish d : dishes) {
            if (d.getName().toLowerCase().contains(keyword.toLowerCase())) {
                d.display();
                found = true;
            }
        }
        if (!found) System.out.println("Совпадений не найдено.");
    }

    public void searchByIngredient(String ingredient) {
        boolean found = false;
        for (Dish d : dishes) {
            if (d.getIngredients().stream().anyMatch(i -> i.equalsIgnoreCase(ingredient))) {
                d.display();
                found = true;
            }
        }
        if (!found) System.out.println("Совпадений не найдено.");
    }

    public void filterByType(Class<? extends Dish> type) {
        boolean found = false;
        for (Dish d : dishes) {
            if (type.isInstance(d)) {
                d.display();
                found = true;
            }
        }
        if (!found) System.out.println("Совпадений не найдено.");
    }

    public void filterByPrice(double min, double max) {
        boolean found = false;
        for (Dish d : dishes) {
            if (d.getPrice() >= min && d.getPrice() <= max) {
                d.display();
                found = true;
            }
        }
        if (!found) System.out.println("Совпадений не найдено.");
    }

    public void filterByCalories(int min, int max) {
        boolean found = false;
        for (Dish d : dishes) {
            if (d.getCalories() >= min && d.getCalories() <= max) {
                d.display();
                found = true;
            }
        }
        if (!found) System.out.println("Совпадений не найдено.");
    }

    public void sortByName() {
        dishes.sort(Comparator.comparing(Dish::getName));
        System.out.println("Сортировка по названию выполнена.");
    }

    public void sortByPrice() {
        dishes.sort(Comparator.comparingDouble(Dish::getPrice));
        System.out.println("Сортировка по цене выполнена.");
    }

    public void statistics() {
        Map<String, List<Dish>> map = dishes.stream().collect(Collectors.groupingBy(Dish::getType));
        for (String type : map.keySet()) {
            List<Dish> list = map.get(type);
            double avg = list.stream().mapToDouble(Dish::getPrice).average().orElse(0);
            System.out.println(type + " - количество: " + list.size() + ", средняя цена: " + avg);
        }
    }

    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(dishes);
            System.out.println("Меню сохранено.");
        } catch (IOException e) {
            System.out.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            dishes = (List<Dish>) ois.readObject();
            System.out.println("Меню загружено.");
        } catch (Exception e) {
            System.out.println("Нет сохранённых данных.");
        }
    }
}

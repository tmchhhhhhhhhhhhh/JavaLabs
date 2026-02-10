package ru.tmchhhhhhhhhhhhh.javalabs.lab3;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        MenuManager manager = new MenuManager();
        manager.loadFromFile();

        Scanner sc = new Scanner(System.in);
        boolean running = true;
        final String ADMIN_PASS = "1234";

        while (running) {
            System.out.println("\n=== МЕНЮ РЕСТОРАНА ===");
            System.out.println("1. Добавить закуску");
            System.out.println("2. Добавить основное блюдо");
            System.out.println("3. Добавить десерт");
            System.out.println("4. Просмотреть меню");
            System.out.println("5. Поиск/фильтры");
            System.out.println("6. Сортировка");
            System.out.println("7. Статистика");
            System.out.println("8. Удалить блюдо (админ)");
            System.out.println("9. Редактировать блюдо (админ)");
            System.out.println("10. Сохранить меню (бинарный формат)");
            System.out.println("11. Экспорт в CSV");
            System.out.println("12. Импорт из CSV");
            System.out.println("13. Экспорт в JSON");
            System.out.println("14. Импорт из JSON");
            System.out.println("15. Выход");
            System.out.print("Выберите действие: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("Название: ");
                    String n = sc.nextLine();
                    System.out.print("Цена: ");
                    double p = sc.nextDouble();
                    System.out.print("Калории: ");
                    int c = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Ингредиенты: ");
                    String i = sc.nextLine();
                    manager.addDish(new Starter(n, p, c, i));
                    System.out.println("Закуска добавлена!");
                }
                case 2 -> {
                    System.out.print("Название: ");
                    String n = sc.nextLine();
                    System.out.print("Цена: ");
                    double p = sc.nextDouble();
                    System.out.print("Калории: ");
                    int c = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Ингредиенты: ");
                    String i = sc.nextLine();
                    manager.addDish(new MainCourse(n, p, c, i));
                    System.out.println("Основное блюдо добавлено!");
                }
                case 3 -> {
                    System.out.print("Название: ");
                    String n = sc.nextLine();
                    System.out.print("Цена: ");
                    double p = sc.nextDouble();
                    System.out.print("Калории: ");
                    int c = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Ингредиенты: ");
                    String i = sc.nextLine();
                    manager.addDish(new Dessert(n, p, c, i));
                    System.out.println("Десерт добавлен!");
                }
                case 4 -> manager.showMenu();
                case 5 -> {
                    System.out.println("Фильтры: 1-тип, 2-цена, 3-калории");
                    int f = sc.nextInt();
                    sc.nextLine();

                    if (f == 1) {
                        System.out.print("Тип (Starter/MainCourse/Dessert): ");
                        String type = sc.nextLine();

                        switch (type) {
                            case "Starter" -> manager.filterByType(Starter.class).forEach(System.out::println);
                            case "MainCourse" -> manager.filterByType(MainCourse.class).forEach(System.out::println);
                            case "Dessert" -> manager.filterByType(Dessert.class).forEach(System.out::println);
                            default -> System.out.println("Неизвестный тип блюда!");
                        }
                    } else if (f == 2) {
                        System.out.print("Мин цена: ");
                        double min = sc.nextDouble();
                        System.out.print("Макс цена: ");
                        double max = sc.nextDouble();
                        manager.filterByPrice(min, max).forEach(System.out::println);
                    } else if (f == 3) {
                        System.out.print("Мин калории: ");
                        int min = sc.nextInt();
                        System.out.print("Макс калории: ");
                        int max = sc.nextInt();
                        manager.filterByCalories(min, max).forEach(System.out::println);
                    }
                }
                case 6 -> {
                    System.out.println("Сортировка: 1-имя, 2-цена");
                    int s = sc.nextInt();
                    System.out.print("По возрастанию? (true/false): ");
                    boolean asc = sc.nextBoolean();

                    if (s == 1) {
                        manager.sortByName(asc);
                    } else if (s == 2) {
                        manager.sortByPrice(asc);
                    }

                    System.out.println("Меню отсортировано!");
                }
                case 7 -> manager.showStatistics();
                case 8 -> {
                    System.out.print("Пароль администратора: ");
                    String pass = sc.nextLine();

                    if (pass.equals(ADMIN_PASS)) {
                        manager.showMenu();
                        System.out.print("Номер для удаления: ");
                        int idx = sc.nextInt() - 1;
                        sc.nextLine();
                        manager.removeDish(idx);
                        System.out.println("Блюдо удалено!");
                    } else {
                        System.out.println("Неверный пароль!");
                    }
                }
                case 9 -> {
                    System.out.print("Пароль администратора: ");
                    String pass = sc.nextLine();

                    if (pass.equals(ADMIN_PASS)) {
                        manager.showMenu();
                        System.out.print("Номер для редактирования: ");
                        int idx = sc.nextInt() - 1;
                        sc.nextLine();
                        System.out.print("Новая цена: ");
                        double p = sc.nextDouble();
                        sc.nextLine();
                        manager.editDishPrice(idx, p);
                        System.out.print("Новые ингредиенты: ");
                        String i = sc.nextLine();
                        manager.editDishIngredients(idx, i);
                        System.out.println("Блюдо обновлено!");
                    } else {
                        System.out.println("Неверный пароль!");
                    }
                }
                case 10 -> manager.saveToFile();
                case 11 -> manager.exportToCSV();
                case 12 -> manager.importFromCSV();
                case 13 -> manager.exportToJSON();
                case 14 -> manager.importFromJSON();
                case 15 -> {
                    running = false;
                    manager.saveToFile();
                    System.out.println("До свидания!");
                }
                default -> System.out.println("Некорректный выбор");
            }
        }
        sc.close();
    }
}
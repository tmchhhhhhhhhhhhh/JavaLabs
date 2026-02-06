package ru.tmchhhhhhhhhhhhh.javalabs.lab1;

import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        boolean useLower = true;
        boolean useUpper = true;
        boolean useDigits = true;
        boolean useSymbols = true;
        int length = 8;

        while (true) {
            System.out.println("\n=== Генератор паролей ===");
            System.out.println("Текущие настройки:");
            System.out.println("s - строчные буквы: " + (useLower ? "Вкл" : "Выкл"));
            System.out.println("z - заглавные буквы: " + (useUpper ? "Вкл" : "Выкл"));
            System.out.println("d - цифры: " + (useDigits ? "Вкл" : "Выкл"));
            System.out.println("c - спец. символы: " + (useSymbols ? "Вкл" : "Выкл"));
            System.out.println("l - длина пароля: " + length);
            System.out.println("g - сгенерировать пароль");
            System.out.println("q - выход");
            System.out.print("Выберите опцию: ");

            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "s":
                    useLower = !useLower;
                    break;
                case "z":
                    useUpper = !useUpper;
                    break;
                case "d":
                    useDigits = !useDigits;
                    break;
                case "c":
                    useSymbols = !useSymbols;
                    break;
                case "l":
                    System.out.print("Введите длину пароля: ");
                    try {
                        length = Integer.parseInt(scanner.nextLine());
                        if (length <= 0) {
                            System.out.println("Длина должна быть больше 0");
                            length = 8;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Некорректное число, оставлена предыдущая длина");
                    }
                    break;
                case "g":
                    try {
                        logger.info("Параметры генерации: length={}, lower={}, upper={}, digits={}, symbols={}",
                                length, useLower, useUpper, useDigits, useSymbols);
                        String password = PasswordGenerator.generate(length, useLower, useUpper, useDigits, useSymbols);
                        System.out.println("Сгенерированный пароль: " + password);
                        logger.info("Сгенерирован пароль: {}", password);
                    } catch (Exception e) {
                        System.out.println("Ошибка: " + e.getMessage());
                        logger.error("Ошибка при генерации пароля", e);
                    }
                    break;
                case "q":
                    System.out.println("Выход...");
                    logger.info("Программа завершена");
                    return;
                default:
                    System.out.println("Некорректная опция, попробуйте снова");
            }
        }
    }
}

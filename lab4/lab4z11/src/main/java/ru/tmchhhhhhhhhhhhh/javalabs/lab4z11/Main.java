package ru.tmchhhhhhhhhhhhh.javalabs.lab4z11;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Лабораторная работа: Криптоанализ двойного шифрования ===");

        // Тестовые данные
        String plaintext = "SecretMessage123";
        int testK1 = 123456;  // Пример первого ключа (20-битный)
        int testK2 = 789012;  // Пример второго ключа (20-битный)

        System.out.println("Исходный текст: " + plaintext);
        System.out.println("Тестовый ключ K1: " + testK1);
        System.out.println("Тестовый ключ K2: " + testK2);

        // Создаем экземпляр взломщика
        DoubleEncryptionCracker cracker = new DoubleEncryptionCracker();

        // Двойное шифрование
        String ciphertext = cracker.doubleEncrypt(plaintext, testK1, testK2);
        System.out.println("Зашифрованный текст: " + ciphertext);

        System.out.println("\n=== Начинаем поиск ключей ===");

        // Запускаем взлом
        long startTime = System.currentTimeMillis();
        List<int[]> foundKeys = cracker.findKeys(plaintext, ciphertext);
        long endTime = System.currentTimeMillis();

        System.out.println("\n=== Результаты поиска ===");
        System.out.println("Время выполнения: " + (endTime - startTime) + " мс");
        System.out.println("Оригинальные ключи: K1=" + testK1 + ", K2=" + testK2);

        if (!foundKeys.isEmpty()) {
            System.out.println("Найдено " + foundKeys.size() + " пар ключей:");
            for (int i = 0; i < foundKeys.size(); i++) {
                int[] pair = foundKeys.get(i);
                System.out.println("Пара " + (i + 1) + ": K1=" + pair[0] + ", K2=" + pair[1]);

                // Проверяем правильность
                if (pair[0] == testK1 && pair[1] == testK2) {
                    System.out.println("   Найдены оригинальные ключи!");
                }

                // Проверяем шифрованием
                String testCipher = cracker.doubleEncrypt(plaintext, pair[0], pair[1]);
                if (testCipher.equals(ciphertext)) {
                    System.out.println("   Ключи корректны (шифрование совпадает)");
                }
            }
        } else {
            System.out.println("Ключи не найдены!");
        }

        // Тестируем оптимизированную версию
        System.out.println("\n=== Тестируем оптимизированную версию ===");
        List<int[]> optimizedKeys = cracker.findKeysOptimized(plaintext, ciphertext, 4);
        System.out.println("Оптимизированная версия нашла: " + optimizedKeys.size() + " пар ключей");
    }
}
package ru.tmchhhhhhhhhhhhh.javalabs.lab4z11;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class DoubleEncryptionCracker {

    /**
     * Функция шифрования (примерная реализация)
     * В реальной задаче должна быть заменена на конкретный алгоритм
     */
    public String encrypt(String data, int key) {
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[bytes.length];

        for (int i = 0; i < bytes.length; i++) {
            // XOR с ключом
            int encryptedByte = bytes[i] ^ (key & 0xFF);

            // Циклический сдвиг в зависимости от ключа
            int shift = (key % 7) + 1;
            encryptedByte = ((encryptedByte << shift) | (encryptedByte >>> (8 - shift))) & 0xFF;

            result[i] = (byte) encryptedByte;
        }

        return new String(result, StandardCharsets.UTF_8);
    }

    /**
     * Функция двойного шифрования
     */
    public String doubleEncrypt(String plaintext, int k1, int k2) {
        String intermediate = encrypt(plaintext, k1);
        return encrypt(intermediate, k2);
    }

    /**
     * Атака "встреча посередине" для нахождения ключей K1 и K2
     * @param plaintext исходный текст
     * @param ciphertext зашифрованный текст
     * @return список найденных пар ключей [K1, K2]
     */
    public List<int[]> findKeys(String plaintext, String ciphertext) {
        List<int[]> results = new ArrayList<>();

        final int MAX_KEY = 1 << 20;

        System.out.println("Поиск по " + MAX_KEY + " возможным ключам...");
        System.out.println("Этап 1: Шифрование со всеми возможными K1...");

        long startTime = System.currentTimeMillis();

        // Этап 1: Сохраняем все промежуточные результаты для каждого K1
        Map<String, Integer> intermediateResults = new HashMap<>(MAX_KEY);

        for (int k1 = 0; k1 < MAX_KEY; k1++) {
            String intermediate = encrypt(plaintext, k1);
            intermediateResults.put(intermediate, k1);

            // Вывод прогресса
            if (k1 % 100000 == 0 && k1 > 0) {
                System.out.printf("Обработано %d ключей (%.1f%%)\n",
                        k1, (k1 * 100.0 / MAX_KEY));
            }
        }

        System.out.println("Этап 2: Поиск совпадений для K2...");

        // Этап 2: Ищем K2, который дает такое же промежуточное значение
        for (int k2 = 0; k2 < MAX_KEY; k2++) {
            // "Расшифровка" ciphertext с помощью K2 должна дать
            // одно из промежуточных значений
            String decryptedWithK2 = encrypt(ciphertext, k2);

            Integer k1 = intermediateResults.get(decryptedWithK2);
            if (k1 != null) {
                results.add(new int[]{k1, k2});
                System.out.println("Найдена пара ключей: K1=" + k1 + ", K2=" + k2);
            }

            // Вывод прогресса
            if (k2 % 100000 == 0 && k2 > 0) {
                System.out.printf("Обработано %d ключей (%.1f%%)\n",
                        k2, (k2 * 100.0 / MAX_KEY));
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Поиск завершен за " + (endTime - startTime) + " мс");

        return results;
    }

    /**
     * Оптимизированная версия с многопоточностью
     * @param plaintext исходный текст
     * @param ciphertext зашифрованный текст
     * @param threadCount количество потоков
     * @return список найденных пар ключей
     */
    public List<int[]> findKeysOptimized(String plaintext, String ciphertext, int threadCount) {
        final List<int[]> results = Collections.synchronizedList(new ArrayList<>());
        final int MAX_KEY = 1 << 20;

        System.out.println("Оптимизированный поиск с " + threadCount + " потоками...");
        long startTime = System.currentTimeMillis();

        // Этап 1: Заполняем карту промежуточных результатов (в одном потоке)
        Map<String, Integer> intermediateMap = new HashMap<>(MAX_KEY);

        for (int k1 = 0; k1 < MAX_KEY; k1++) {
            intermediateMap.put(encrypt(plaintext, k1), k1);
        }

        System.out.println("Этап 1 завершен. Начинаем многопоточный поиск K2...");

        // Этап 2: Многопоточный поиск K2
        List<Thread> threads = new ArrayList<>();
        int keysPerThread = MAX_KEY / threadCount;

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            final int startKey = i * keysPerThread;
            final int endKey = (i == threadCount - 1) ? MAX_KEY : (i + 1) * keysPerThread;

            Thread thread = new Thread(() -> {
                System.out.println("Поток " + threadId + " обрабатывает ключи от " +
                        startKey + " до " + (endKey - 1));

                for (int k2 = startKey; k2 < endKey; k2++) {
                    String decrypted = encrypt(ciphertext, k2);
                    Integer k1 = intermediateMap.get(decrypted);

                    if (k1 != null) {
                        results.add(new int[]{k1, k2});
                        System.out.println("Поток " + threadId +
                                " нашел пару: K1=" + k1 + ", K2=" + k2);
                    }
                }

                System.out.println("Поток " + threadId + " завершил работу");
            });

            threads.add(thread);
            thread.start();
        }

        // Ожидаем завершения всех потоков
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Оптимизированный поиск завершен за " +
                (endTime - startTime) + " мс");

        return results;
    }

    /**
     * Вспомогательный метод для проверки корректности ключей
     */
    public boolean verifyKeys(String plaintext, String ciphertext, int k1, int k2) {
        String testCipher = doubleEncrypt(plaintext, k1, k2);
        return testCipher.equals(ciphertext);
    }
}
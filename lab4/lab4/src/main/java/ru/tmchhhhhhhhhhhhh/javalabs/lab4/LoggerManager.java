package ru.tmchhhhhhhhhhhhh.javalabs.lab4;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerManager {
    private static final String LOG_FILE = "src/main/java/ru/tmchhhhhhhhhhhhh/javalabs/lab3/operations.log";

    public static void log(String message) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            fw.write("[" + time + "] " + message + "\n");
        } catch (IOException e) {
            System.out.println("Ошибка логирования: " + e.getMessage());
        }
    }
}

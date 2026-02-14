package ru.tmchhhhhhhhhhhhh.javalabs.lab6client.server.network;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;

public class Server {
    private static int clientCount = 0;
    private static long lastClientConnectedTime = System.currentTimeMillis();

    public static void main(String[] args) {
        startServer();
    }

    private static void startServer() {
        ResourceBundle bundle = ResourceBundle.getBundle("server");
        int serverPort = Integer.parseInt(bundle.getString("SERVER_PORT"));

        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║   МНОГОПОТОЧНЫЙ СЕРВЕР УПРАВЛЕНИЯ РЕСТОРАНОМ   ║");
        System.out.println("╚════════════════════════════════════════════════╝");
        System.out.println("\nСервер запускается...");

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("Сервер запущен на порте " + serverPort + "!");
            System.out.println("Мониторинг клиентов активирован");
            System.out.println("Ожидание подключений клиентов...\n");

            startMonitoring();

            while (true) {
                System.out.println("═══════════════════════════════════════════════");
                System.out.println("Ожидание подключения...");

                Socket clientAccepted = serverSocket.accept();

                incrementClientCount();
                lastClientConnectedTime = System.currentTimeMillis();

                System.out.println("Соединение установлено от: " + clientAccepted.getInetAddress());
                System.out.println("Текущее количество клиентов: " + clientCount);
                System.out.println("═══════════════════════════════════════════════\n");

                new Thread(new ClientThread(clientAccepted)).start();
            }
        } catch (Exception e) {
            System.err.println("Критическая ошибка сервера:");
            e.printStackTrace();
        }
    }

    private static void startMonitoring() {
        ResourceBundle bundle = ResourceBundle.getBundle("server");
        long monitoringInterval = Long.parseLong(bundle.getString("MONITORING_INTERVAL"));
        long shutdownTime = Long.parseLong(bundle.getString("SHUTDOWN_TIME"));

        new Thread(() -> {
            System.out.println("[MONITORING] Поток мониторинга запущен");
            System.out.println("[MONITORING] Интервал: " + monitoringInterval + " мс");
            System.out.println("[MONITORING] Время автоотключения: " + shutdownTime + " мс\n");

            while (true) {
                try {
                    Thread.sleep(monitoringInterval);

                    long currentTime = System.currentTimeMillis();
                    long idleTime = currentTime - lastClientConnectedTime;

                    if (clientCount == 0 && idleTime >= shutdownTime) {
                        System.out.println("\n╔════════════════════════════════════════════════╗");
                        System.out.println("║         АВТОМАТИЧЕСКОЕ ОТКЛЮЧЕНИЕ              ║");
                        System.out.println("╚════════════════════════════════════════════════╝");
                        System.out.println("Причина: Нет подключенных клиентов более " +
                                (shutdownTime / 1000) + " секунд");
                        System.out.println("Сервер завершает работу...");
                        System.exit(0);
                    }

                    System.out.println("[MONITORING] Активных клиентов: " + clientCount +
                            " | Простой: " + (idleTime / 1000) + " сек | Активных потоков: " +
                            Thread.activeCount());

                } catch (InterruptedException e) {
                    System.err.println("[MONITORING] Поток мониторинга прерван");
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "MonitoringThread").start();
    }

    // ПУБЛИЧНЫЕ методы для управления счетчиком клиентов
    public static synchronized void incrementClientCount() {
        clientCount++;
    }

    public static synchronized void decrementClientCount() {
        clientCount--;
        System.out.println("[SERVER] Клиент отключился. Осталось клиентов: " + clientCount);
    }
}
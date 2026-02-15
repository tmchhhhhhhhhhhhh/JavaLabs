package ru.restaurant.server.network;

import ru.restaurant.server.util.HibernateUtil;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static int clientCount = 0;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║   МНОГОПОТОЧНЫЙ СЕРВЕР С HIBERNATE ORM         ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");
        
        // Инициализация Hibernate
        try {
            HibernateUtil.getSessionFactory();
            
            // Загрузка тестовых данных
            ru.restaurant.server.util.DataInitializer.loadTestData();
            
            System.out.println();
        } catch (Exception e) {
            System.err.println("✗ КРИТИЧЕСКАЯ ОШИБКА: Не удалось инициализировать Hibernate!");
            e.printStackTrace();
            System.exit(1);
        }
        
        startServer();
    }

    private static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(6666)) {
            System.out.println("✓ Сервер запущен на порту 6666!");
            System.out.println("Ожидание подключений клиентов...\n");

            while (true) {
                System.out.println("═".repeat(50));
                System.out.println("Ожидание подключения...");

                Socket clientAccepted = serverSocket.accept();

                incrementClientCount();

                System.out.println("✓ Клиент подключен: " + clientAccepted.getInetAddress());
                System.out.println("Активных клиентов: " + clientCount);
                System.out.println("═".repeat(50) + "\n");

                new Thread(new ClientThread(clientAccepted)).start();
            }
        } catch (Exception e) {
            System.err.println("Критическая ошибка сервера:");
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }

    public static synchronized void incrementClientCount() {
        clientCount++;
    }

    public static synchronized void decrementClientCount() {
        clientCount--;
        System.out.println("[SERVER] Клиент отключился. Осталось клиентов: " + clientCount);
    }
}

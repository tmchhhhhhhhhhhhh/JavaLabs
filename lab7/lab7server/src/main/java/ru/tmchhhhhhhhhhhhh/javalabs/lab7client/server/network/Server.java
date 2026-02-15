package ru.tmchhhhhhhhhhhhh.javalabs.lab7client.server.network;

import ru.tmchhhhhhhhhhhhh.javalabs.lab7client.server.util.HibernateUtil;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;


public class Server {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("server");
    private static final int serverPort = Integer.parseInt(bundle.getString("SERVER_PORT"));
    private static int clientCount = 0;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║   МНОГОПОТОЧНЫЙ СЕРВЕР РЕСТОРАНА               ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");
        
        // Инициализация Hibernate
        try {
            HibernateUtil.getSessionFactory();
            
            // Загрузка тестовых данных
            ru.tmchhhhhhhhhhhhh.javalabs.lab7client.server.util.DataInitializer.loadTestData();
            
            System.out.println();
        } catch (Exception e) {
            System.err.println("✗ КРИТИЧЕСКАЯ ОШИБКА: Не удалось инициализировать Hibernate!");
            e.printStackTrace();
            System.exit(1);
        }
        
        startServer();
    }

    private static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("✓ Сервер запущен на порту " + serverPort +"!");
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

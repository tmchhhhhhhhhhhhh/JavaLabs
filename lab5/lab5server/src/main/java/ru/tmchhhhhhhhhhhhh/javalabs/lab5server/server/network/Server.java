package ru.tmchhhhhhhhhhhhh.javalabs.lab5server.server.network;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;

public class Server {
    public static void main(String[] args) {
        ResourceBundle bundle = ResourceBundle.getBundle("server");
        int serverPort = Integer.parseInt(bundle.getString("SERVER_PORT"));

        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║   СЕРВЕР УПРАВЛЕНИЯ РЕСТОРАНОМ                 ║");
        System.out.println("╚════════════════════════════════════════════════╝");
        System.out.println("\nСервер запускается на порте " + serverPort + "...");

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("Сервер успешно запущен!");
            System.out.println("Ожидание подключений клиентов...\n");

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("═══════════════════════════════════════════════");
                    System.out.println("Новое подключение от: " + clientSocket.getInetAddress());

                    ClientThread clientThread = new ClientThread(clientSocket);
                    clientThread.start();

                    System.out.println("Поток клиента запущен");
                    System.out.println("═══════════════════════════════════════════════\n");
                } catch (Exception e) {
                    System.err.println("Ошибка при принятии подключения: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Критическая ошибка сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
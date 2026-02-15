package ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;

public class Server {
    public static void main(String[] args) {
        ResourceBundle bundle = ResourceBundle.getBundle("server");
        int serverPort = Integer.parseInt(bundle.getString("SERVER_PORT"));
        
        System.out.println("=".repeat(50));
        System.out.println("      СЕРВЕР РЕСТОРАНА - ЗАПУСК");
        System.out.println("=".repeat(50));
        System.out.println("Сервер запускается на порте " + serverPort + "...");
        
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("✓ Сервер успешно запущен!");
            System.out.println("Ожидание подключений клиентов...");
            System.out.println("=".repeat(50));
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("\n>>> Новое подключение от: " + clientSocket.getInetAddress());
                
                // Создаем и запускаем поток для обработки клиента
                ClientThread clientThread = new ClientThread(clientSocket);
                clientThread.start();
            }
            
        } catch (IOException e) {
            System.err.println("✗ Ошибка сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

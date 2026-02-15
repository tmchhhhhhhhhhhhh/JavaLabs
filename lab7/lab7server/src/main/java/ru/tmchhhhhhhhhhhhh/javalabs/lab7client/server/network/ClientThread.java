package ru.restaurant.server.network;

import ru.restaurant.common.network.Request;
import ru.restaurant.common.network.Response;
import ru.restaurant.server.controllers.DishController;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientThread implements Runnable {
    private final Socket clientSocket;
    private final DishController dishController;

    public ClientThread(Socket socket) {
        this.clientSocket = socket;
        this.dishController = new DishController();
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        System.out.println("[" + threadName + "] Новый клиент подключен");

        try (ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream())) {

            output.flush();

            boolean keepRunning = true;

            while (keepRunning) {
                try {
                    Request request = (Request) input.readObject();

                    if (request != null) {
                        System.out.println("[" + threadName + "] Запрос: " + request.getOperation());

                        Response response = dishController.processRequest(request);

                        output.writeObject(response);
                        output.flush();

                        if (request.getOperation() == ru.restaurant.common.enums.Operation.DISCONNECT) {
                            keepRunning = false;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[" + threadName + "] Ошибка обработки запроса: " + e.getMessage());
                    keepRunning = false;
                }
            }

        } catch (Exception e) {
            System.err.println("[" + threadName + "] Ошибка подключения: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                // ignore
            }
            Server.decrementClientCount();
            System.out.println("[" + threadName + "] Клиент отключен");
        }
    }
}

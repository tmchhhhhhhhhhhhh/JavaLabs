package ru.tmchhhhhhhhhhhhh.javalabs.lab6client.server.network;

import ru.tmchhhhhhhhhhhhh.javalabs.lab6client.server.controllers.DishController;
import ru.tmchhhhhhhhhhhhh.javalabs.lab6client.server.enums.Operation;
import ru.tmchhhhhhhhhhhhh.javalabs.lab6client.server.exceptions.ResponseException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientThread implements Runnable {
    private final Socket clientSocket;
    private final DishController dishController;

    public ClientThread(Socket socket) {
        this.clientSocket = socket;
        this.dishController = DishController.getInstance();
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        System.out.println("[" + threadName + "] Новый клиент подключен: " + clientSocket.getInetAddress());

        ObjectOutputStream output = null;
        ObjectInputStream input = null;

        try {
            // ВАЖНО: Сначала создаём OUTPUT, потом INPUT!
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            output.flush(); // Обязательно flush после создания

            input = new ObjectInputStream(clientSocket.getInputStream());

            System.out.println("[" + threadName + "] Потоки ввода-вывода созданы");

            boolean keepRunning = true;

            while (keepRunning) {
                try {
                    Request request = (Request) input.readObject();

                    if (request != null) {
                        System.out.println("[" + threadName + "] Получен запрос: " + request.getOperation());

                        Response response = processRequest(request);

                        if (request.getOperation() == Operation.DISCONNECT) {
                            keepRunning = false;
                        }

                        output.writeObject(response);
                        output.flush();

                        System.out.println("[" + threadName + "] Ответ отправлен: " + response.getMessage());
                    } else {
                        Response errorResponse = new Response(false, "Received invalid object", null);
                        output.writeObject(errorResponse);
                        output.flush();
                    }
                } catch (IOException e) {
                    System.err.println("[" + threadName + "] Connection error: " + e.getMessage());
                    keepRunning = false;
                } catch (ClassNotFoundException e) {
                    System.err.println("[" + threadName + "] Class not found: " + e.getMessage());
                    keepRunning = false;
                } catch (Exception e) {
                    System.err.println("[" + threadName + "] Unexpected error:");
                    e.printStackTrace();

                    try {
                        Response errorResponse = new Response(false, "Server error: " + e.getMessage(), null);
                        output.writeObject(errorResponse);
                        output.flush();
                    } catch (IOException ioException) {
                        System.err.println("[" + threadName + "] Failed to send error response");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[" + threadName + "] Stream initialization error: " + e.getMessage());
        } finally {
            closeConnection(output, input);
        }
    }

    private Response processRequest(Request request) {
        try {
            return switch (request.getOperation()) {
                case CREATE_DISH -> dishController.createDishFromRequest(request);
                case READ_DISH -> dishController.readDish(request);
                case UPDATE_DISH -> dishController.updateDish(request);
                case DELETE_DISH -> dishController.deleteDish(request);
                case GET_ALL_DISHES -> dishController.getAllDishes();

                case FILTER_BY_TYPE -> dishController.filterByType(request);
                case FILTER_BY_PRICE -> dishController.filterByPrice(request);
                case FILTER_BY_CALORIES -> dishController.filterByCalories(request);

                case SORT_BY_NAME -> dishController.sortByName(request);
                case SORT_BY_PRICE -> dishController.sortByPrice(request);
                case SORT_BY_CALORIES -> dishController.sortByCalories(request);

                case ORDER_DISH -> dishController.orderDish(request);
                case GET_ORDER_STATISTICS -> dishController.getOrderStatistics();

                case GET_STATISTICS -> dishController.getStatistics();
                case GET_CALORIES_MAP -> dishController.getCaloriesMap();
                case GET_MENU_CHANGE_LOG -> dishController.getMenuChangeLog(request);

                case DISCONNECT -> new Response(true, "Disconnected successfully", null);

                default -> new Response(false, "Received unknown operation", null);
            };
        } catch (ResponseException e) {
            return new Response(false, e.getMessage(), null);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Something went wrong on the server side!", null);
        }
    }

    private void closeConnection(ObjectOutputStream output, ObjectInputStream input) {
        String threadName = Thread.currentThread().getName();

        try {
            if (output != null) output.close();
        } catch (IOException e) {
            System.err.println("[" + threadName + "] Error closing output stream");
        }

        try {
            if (input != null) input.close();
        } catch (IOException e) {
            System.err.println("[" + threadName + "] Error closing input stream");
        }

        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("[" + threadName + "] Error closing socket");
        }

        Server.decrementClientCount();
        System.out.println("[" + threadName + "] Соединение закрыто");
    }
}
package ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.network;


import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.controllers.DishController;
import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.enums.Operation;
import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.exceptions.ResponseException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientThread extends Thread {
    private final Socket clientSocket;
    private final DishController dishController;

    public ClientThread(Socket socket) {
        this.clientSocket = socket;
        this.dishController = new DishController();
    }

    @Override
    public void run() {
        System.out.println("Новый клиент подключен: " + clientSocket.getInetAddress());

        try (
                ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            boolean keepRunning = true;

            while (keepRunning) {
                try {
                    Request request = (Request) input.readObject();

                    if (request != null) {
                        System.out.println("Получен запрос: " + request.getOperation());

                        Response response = processRequest(request);

                        if (request.getOperation() == Operation.DISCONNECT) {
                            keepRunning = false;
                        }

                        output.writeObject(response);
                        output.flush();

                        System.out.println("Ответ отправлен: " + response.getMessage());
                    } else {
                        Response errorResponse = new Response(false, "Получен некорректный объект", null);
                        output.writeObject(errorResponse);
                        output.flush();
                    }
                } catch (IOException e) {
                    System.err.println("Ошибка соединения: " + e.getMessage());
                    keepRunning = false;
                } catch (ClassNotFoundException e) {
                    System.err.println("Класс не найден: " + e.getMessage());
                    keepRunning = false;
                } catch (Exception e) {
                    System.err.println("Ошибка обработки запроса: " + e.getMessage());
                    e.printStackTrace();
                    keepRunning = false;
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка потока клиента: " + e.getMessage());
        } finally {
            closeConnection();
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

                case DISCONNECT -> new Response(true, "Отключение успешно", null);

                default -> new Response(false, "Неизвестная операция", null);
            };
        } catch (ResponseException e) {
            return new Response(false, e.getMessage(), null);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Ошибка на стороне сервера: " + e.getMessage(), null);
        }
    }

    private void closeConnection() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                System.out.println("Соединение с клиентом закрыто");
            }
        } catch (IOException e) {
            System.err.println("Ошибка закрытия соединения: " + e.getMessage());
        }
    }
}

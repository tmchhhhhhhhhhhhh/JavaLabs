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
        try (
                ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream())
        ) {
            boolean keepRunning = true;
            
            System.out.println("Клиент подключен: " + clientSocket.getInetAddress());
            
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
                        
                        System.out.println("Отправлен ответ: " + response.getMessage());
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
                    System.err.println("Неожиданная ошибка: " + e.getMessage());
                    e.printStackTrace();
                    keepRunning = false;
                }
            }
            
        } catch (IOException e) {
            System.err.println("Ошибка создания потоков: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }
    
    private Response processRequest(Request request) {
        try {
            return switch (request.getOperation()) {
                case ADD_DISH -> dishController.addDish(request);
                case GET_ALL_DISHES -> dishController.getAllDishes();
                case GET_DISH_BY_NAME -> dishController.getDishByName(request);
                case UPDATE_DISH_PRICE -> dishController.updateDishPrice(request);
                case UPDATE_DISH_INGREDIENTS -> dishController.updateDishIngredients(request);
                case DELETE_DISH -> dishController.deleteDish(request);
                case ORDER_DISH -> dishController.orderDish(request);
                case PREPARE_DISH -> dishController.prepareDish(request);
                case GET_ORDER_STATISTICS -> dishController.getOrderStatistics();
                case FILTER_BY_TYPE -> dishController.filterByType(request);
                case FILTER_BY_PRICE -> dishController.filterByPrice(request);
                case FILTER_BY_CALORIES -> dishController.filterByCalories(request);
                case SORT_BY_NAME -> dishController.sortByName(request);
                case SORT_BY_PRICE -> dishController.sortByPrice(request);
                case SORT_BY_CALORIES -> dishController.sortByCalories(request);
                case GET_STATISTICS -> dishController.getStatistics();
                case GET_CALORIES_MAP -> dishController.getCaloriesMap();
                case GET_TOP_POPULAR_DISHES -> dishController.getTopPopularDishes(request);
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
                System.out.println("Соединение закрыто: " + clientSocket.getInetAddress());
            }
        } catch (IOException e) {
            System.err.println("Ошибка закрытия соединения: " + e.getMessage());
        }
    }
}

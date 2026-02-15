package ru.tmchhhhhhhhhhhhh.javalabs.lab7client.client.network;

import ru.tmchhhhhhhhhhhhh.javalabs.lab7client.common.exceptions.NoConnectionException;
import ru.tmchhhhhhhhhhhhh.javalabs.lab7client.common.network.Request;
import ru.tmchhhhhhhhhhhhh.javalabs.lab7client.common.network.Response;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerClient {
    private static ServerClient instance;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 6666;

    private ServerClient() {
        try {
            System.out.println("Подключение к серверу " + SERVER_HOST + ":" + SERVER_PORT + "...");

            socket = new Socket(SERVER_HOST, SERVER_PORT);
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());

            System.out.println("✓ Подключено к серверу!\n");

        } catch (Exception e) {
            throw new NoConnectionException("Не удалось подключиться к серверу: " + e.getMessage());
        }
    }

    public static synchronized ServerClient getInstance() {
        if (instance == null) {
            instance = new ServerClient();
        }
        return instance;
    }

    public synchronized Response sendRequest(Request request) {
        try {
            System.out.println("→ Отправка запроса: " + request.getOperation());

            output.writeObject(request);
            output.flush();

            Response response = (Response) input.readObject();

            System.out.println("← Получен ответ: " + response.getMessage());

            return response;

        } catch (Exception e) {
            System.err.println("✗ Ошибка отправки/получения: " + e.getMessage());
            return null;
        }
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("Отключено от сервера");
        } catch (Exception e) {
            System.err.println("Ошибка при отключении: " + e.getMessage());
        }
    }
}

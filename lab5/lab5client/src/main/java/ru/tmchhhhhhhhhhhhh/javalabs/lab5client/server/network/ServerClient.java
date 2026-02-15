package ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.network;

import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.exceptions.NoConnectionException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ResourceBundle;

public class ServerClient {
    private static ServerClient instance;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private ServerClient() throws NoConnectionException {
        connect();
    }

    public static synchronized ServerClient getInstance() throws NoConnectionException {
        if (instance == null) {
            try {
                instance = new ServerClient();
            } catch (Exception e) {
                e.printStackTrace();
                throw new NoConnectionException("Не удалось подключиться к серверу");
            }
        }
        return instance;
    }

    private void connect() throws NoConnectionException {
        ResourceBundle bundle = ResourceBundle.getBundle("server");
        String serverAddress = bundle.getString("SERVER_IP");
        int serverPort = Integer.parseInt(bundle.getString("SERVER_PORT"));

        try {
            socket = new Socket(serverAddress, serverPort);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            System.out.println("✓ Подключено к серверу: " + serverAddress + ":" + serverPort);

        } catch (IOException e) {
            throw new NoConnectionException("Не удалось подключиться к серверу: " + serverAddress + ":" + serverPort);
        }
    }

    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            instance = null;
            System.out.println("✓ Отключено от сервера");
        } catch (IOException e) {
            System.err.println("Ошибка при отключении: " + e.getMessage());
        }
    }

    public Response sendRequest(Request request) {
        try {
            System.out.println("Отправка запроса: " + request.getOperation());
            out.writeObject(request);
            out.flush();
            return processResponse();
        } catch (IOException e) {
            System.err.println("Ошибка отправки запроса: " + e.getMessage());
            return null;
        }
    }

    private Response processResponse() {
        try {
            Response response = (Response) in.readObject();
            return response;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка получения ответа: " + e.getMessage());
            return null;
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}

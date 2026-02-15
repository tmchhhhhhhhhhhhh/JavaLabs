package ru.tmchhhhhhhhhhhhh.javalabs.lab7client.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.tmchhhhhhhhhhhhh.javalabs.lab7client.client.network.ServerClient;

public class RestaurantClientApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Подключение к серверу
            ServerClient.getInstance();

            // Загрузка FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
            Parent root = loader.load();

            // Создание сцены
            Scene scene = new Scene(root, 1100, 700);

            // Загрузка CSS
            String css = getClass().getResource("/styles/application.css").toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.setTitle("🍽️ Система Управления Рестораном - Hibernate");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);

            // Обработка закрытия
            primaryStage.setOnCloseRequest(event -> {
                ServerClient.getInstance().disconnect();
                System.exit(0);
            });

            primaryStage.show();

        } catch (Exception e) {
            System.err.println("✗ Ошибка запуска клиента:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package ru.tmchhhhhhhhhhhhh.javalabs.lab7client.client.gui.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.tmchhhhhhhhhhhhh.javalabs.lab7client.client.network.ServerClient;
import ru.tmchhhhhhhhhhhhh.javalabs.lab7client.common.enums.Operation;
import ru.tmchhhhhhhhhhhhh.javalabs.lab7client.common.model.Dish;
import ru.tmchhhhhhhhhhhhh.javalabs.lab7client.common.network.Request;
import ru.tmchhhhhhhhhhhhh.javalabs.lab7client.common.network.Response;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MainController {

    @FXML private TableView<Dish> dishTable;
    @FXML private TableColumn<Dish, String> typeColumn;
    @FXML private TableColumn<Dish, String> nameColumn;
    @FXML private TableColumn<Dish, Double> priceColumn;
    @FXML private TableColumn<Dish, Integer> caloriesColumn;
    @FXML private TableColumn<Dish, String> ingredientsColumn;
    @FXML private Label statusLabel;

    private final ServerClient serverClient = ServerClient.getInstance();
    private final Gson gson = new Gson();
    private final ObservableList<Dish> dishList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadAllDishes();
    }

    private void setupTable() {
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        caloriesColumn.setCellValueFactory(new PropertyValueFactory<>("calories"));
        ingredientsColumn.setCellValueFactory(new PropertyValueFactory<>("ingredients"));

        priceColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty || price == null ? null : String.format("%.2f ₽", price));
            }
        });

        caloriesColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer cal, boolean empty) {
                super.updateItem(cal, empty);
                setText(empty || cal == null ? null : cal + " kcal");
            }
        });

        dishTable.setItems(dishList);
    }

    @FXML
    private void handleRefresh() {
        loadAllDishes();
    }

    @FXML
    private void handleAdd() {
        Dialog<Dish> dialog = new Dialog<>();
        dialog.setTitle("Добавить блюдо");
        dialog.setHeaderText("Создание нового блюда");

        ButtonType addButton = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Название");
        TextField priceField = new TextField();
        priceField.setPromptText("Цена");
        TextField caloriesField = new TextField();
        caloriesField.setPromptText("Калории");
        TextField ingredientsField = new TextField();
        ingredientsField.setPromptText("Ингредиенты");
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Starter", "MainCourse", "Dessert");
        typeBox.setValue("MainCourse");

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Название:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Цена:"), 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(new Label("Калории:"), 0, 2);
        grid.add(caloriesField, 1, 2);
        grid.add(new Label("Ингредиенты:"), 0, 3);
        grid.add(ingredientsField, 1, 3);
        grid.add(new Label("Тип:"), 0, 4);
        grid.add(typeBox, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                try {
                    return new Dish(
                            UUID.randomUUID().toString(),
                            nameField.getText(),
                            Double.parseDouble(priceField.getText()),
                            Integer.parseInt(caloriesField.getText()),
                            ingredientsField.getText(),
                            typeBox.getValue(),
                            null
                    );
                } catch (NumberFormatException e) {
                    showError("Неверный формат цены или калорий");
                    return null;
                }
            }
            return null;
        });

        Optional<Dish> result = dialog.showAndWait();
        result.ifPresent(this::createDish);
    }

    @FXML
    private void handleDelete() {
        Dish selected = dishTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Выберите блюдо для удаления");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText("Удалить блюдо?");
        confirm.setContentText(selected.getName());

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteDish(selected.getId());
        }
    }

    @FXML
    private void handleFilterByType() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Starter", "Starter", "MainCourse", "Dessert");
        dialog.setTitle("Фильтр");
        dialog.setHeaderText("Фильтр по типу блюда");
        dialog.setContentText("Выберите тип:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(this::filterByType);
    }

    @FXML
    private void handleSortByPrice() {
        sortByPrice(true);
    }

    @FXML
    private void handleOrder() {
        Dish selected = dishTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Выберите блюдо для заказа");
            return;
        }
        orderDish(selected.getId());
    }

    @FXML
    private void handleStatistics() {
        Response response = serverClient.sendRequest(new Request(Operation.GET_STATISTICS));

        if (response != null && response.isSuccess()) {
            try {
                JsonObject stats = gson.fromJson(response.getData(), JsonObject.class);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Статистика");
                alert.setHeaderText("📊 Статистика меню ресторана");

                TextArea textArea = new TextArea();
                textArea.setEditable(false);
                textArea.setWrapText(true);
                textArea.setPrefRowCount(15);

                StringBuilder content = new StringBuilder();
                content.append(String.format("💰 Средняя цена: %.2f ₽\n", stats.get("avgPrice").getAsDouble()));
                content.append(String.format("💰 Минимальная цена: %.2f ₽\n", stats.get("minPrice").getAsDouble()));
                content.append(String.format("💰 Максимальная цена: %.2f ₽\n\n", stats.get("maxPrice").getAsDouble()));

                content.append(String.format("🔥 Средняя калорийность: %.0f kcal\n", stats.get("avgCalories").getAsDouble()));
                content.append(String.format("🔥 Минимум: %d kcal\n", stats.get("minCalories").getAsInt()));
                content.append(String.format("🔥 Максимум: %d kcal\n\n", stats.get("maxCalories").getAsInt()));

                content.append(String.format("📋 Всего блюд в меню: %d\n\n", stats.get("totalDishes").getAsLong()));

                if (stats.has("top3Expensive")) {
                    content.append("💎 ТОП-3 САМЫХ ДОРОГИХ:\n");
                    List<Dish> top3 = gson.fromJson(
                            stats.get("top3Expensive"),
                            new TypeToken<List<Dish>>(){}.getType()
                    );

                    int i = 1;
                    for (Dish dish : top3) {
                        content.append(String.format("%d. %s - %.2f ₽ (%d kcal)\n",
                                i++, dish.getName(), dish.getPrice(), dish.getCalories()));
                    }
                }

                textArea.setText(content.toString());
                alert.getDialogPane().setContent(textArea);
                alert.showAndWait();

            } catch (Exception e) {
                showError("Ошибка: " + e.getMessage());
            }
        } else {
            showError("Ошибка получения статистики");
        }
    }

    private void loadAllDishes() {
        setStatus("Загрузка меню...");

        new Thread(() -> {
            Response response = serverClient.sendRequest(new Request(Operation.GET_ALL_DISHES));

            Platform.runLater(() -> {
                if (response != null && response.isSuccess()) {
                    List<Dish> dishes = gson.fromJson(response.getData(),
                            new TypeToken<List<Dish>>(){}.getType());

                    dishList.clear();
                    dishList.addAll(dishes);

                    setStatus("Загружено: " + dishes.size() + " блюд");
                } else {
                    showError("Ошибка загрузки меню");
                    setStatus("Ошибка");
                }
            });
        }).start();
    }

    private void createDish(Dish dish) {
        setStatus("Создание блюда...");

        new Thread(() -> {
            Request request = new Request(Operation.CREATE_DISH, gson.toJson(dish));
            Response response = serverClient.sendRequest(request);

            Platform.runLater(() -> {
                if (response != null && response.isSuccess()) {
                    showInfo("Блюдо успешно создано!");
                    loadAllDishes();
                } else {
                    showError(response != null ? response.getMessage() : "Ошибка создания");
                }
            });
        }).start();
    }

    private void deleteDish(String id) {
        setStatus("Удаление...");

        new Thread(() -> {
            JsonObject json = new JsonObject();
            json.addProperty("id", id);

            Request request = new Request(Operation.DELETE_DISH, gson.toJson(json));
            Response response = serverClient.sendRequest(request);

            Platform.runLater(() -> {
                if (response != null && response.isSuccess()) {
                    showInfo("Блюдо удалено");
                    loadAllDishes();
                } else {
                    showError("Ошибка удаления");
                }
            });
        }).start();
    }

    private void filterByType(String type) {
        setStatus("Фильтрация...");

        new Thread(() -> {
            JsonObject json = new JsonObject();
            json.addProperty("type", type);

            Request request = new Request(Operation.FILTER_BY_TYPE, gson.toJson(json));
            Response response = serverClient.sendRequest(request);

            Platform.runLater(() -> {
                if (response != null && response.isSuccess()) {
                    List<Dish> dishes = gson.fromJson(response.getData(),
                            new TypeToken<List<Dish>>(){}.getType());

                    dishList.clear();
                    dishList.addAll(dishes);

                    setStatus("Фильтр: " + type + " (" + dishes.size() + " блюд)");
                } else {
                    showError("Ошибка фильтрации");
                }
            });
        }).start();
    }

    private void sortByPrice(boolean ascending) {
        setStatus("Сортировка...");

        new Thread(() -> {
            JsonObject json = new JsonObject();
            json.addProperty("ascending", ascending);

            Request request = new Request(Operation.SORT_BY_PRICE, gson.toJson(json));
            Response response = serverClient.sendRequest(request);

            Platform.runLater(() -> {
                if (response != null && response.isSuccess()) {
                    List<Dish> dishes = gson.fromJson(response.getData(),
                            new TypeToken<List<Dish>>(){}.getType());

                    dishList.clear();
                    dishList.addAll(dishes);

                    setStatus("Отсортировано по цене");
                } else {
                    showError("Ошибка сортировки");
                }
            });
        }).start();
    }

    private void orderDish(String id) {
        setStatus("Создание заказа...");

        new Thread(() -> {
            JsonObject json = new JsonObject();
            json.addProperty("id", id);

            Request request = new Request(Operation.ORDER_DISH, gson.toJson(json));
            Response response = serverClient.sendRequest(request);

            Platform.runLater(() -> {
                if (response != null && response.isSuccess()) {
                    showInfo("Заказ принят!");
                    setStatus("Заказ создан");
                } else {
                    showError("Ошибка заказа");
                }
            });
        }).start();
    }

    private void setStatus(String text) {
        Platform.runLater(() -> statusLabel.setText(text));
    }

    private void showInfo(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Информация");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void showWarning(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Внимание");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
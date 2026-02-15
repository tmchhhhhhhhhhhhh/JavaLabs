package ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.network;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success; // Успешен ли запрос
    private String message; // Сообщение от сервера (к примеру, информация об ошибке)
    private String data; // Строка с JSON-информацией
}

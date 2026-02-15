package ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.network;

import ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.enums.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    @NonNull
    private Operation operation; // Необходимая операция

    private String data; // Строка с JSON-информацией
}

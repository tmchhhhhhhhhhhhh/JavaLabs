package ru.tmchhhhhhhhhhhhh.javalabs.lab7client.server.common.network;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.tmchhhhhhhhhhhhh.javalabs.lab7client.server.common.enums.Operation;

import java.io.Serializable;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @NonNull
    private Operation operation;
    
    private String data; // JSON данные
}

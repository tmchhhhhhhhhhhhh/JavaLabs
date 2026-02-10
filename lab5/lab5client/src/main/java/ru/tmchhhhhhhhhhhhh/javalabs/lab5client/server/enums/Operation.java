package ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.enums;

public enum Operation {
    // Dish operations
    CREATE_DISH,
    READ_DISH,
    UPDATE_DISH,
    DELETE_DISH,
    GET_ALL_DISHES,

    // Filter operations
    FILTER_BY_TYPE,
    FILTER_BY_PRICE,
    FILTER_BY_CALORIES,

    // Sort operations
    SORT_BY_NAME,
    SORT_BY_PRICE,
    SORT_BY_CALORIES,

    // Order operations
    ORDER_DISH,
    GET_ORDER_STATISTICS,

    // Statistics
    GET_STATISTICS,
    GET_CALORIES_MAP,
    GET_MENU_CHANGE_LOG,

    // System
    DISCONNECT
}
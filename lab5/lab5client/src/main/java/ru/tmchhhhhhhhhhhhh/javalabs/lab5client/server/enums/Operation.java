package ru.tmchhhhhhhhhhhhh.javalabs.lab5client.server.enums;

public enum Operation {
    // Операции с блюдами
    ADD_DISH,
    GET_ALL_DISHES,
    GET_DISH_BY_NAME,
    UPDATE_DISH_PRICE,
    UPDATE_DISH_INGREDIENTS,
    DELETE_DISH,

    // Операции с заказами
    ORDER_DISH,
    PREPARE_DISH,
    GET_ORDER_STATISTICS,

    // Фильтрация и поиск
    FILTER_BY_TYPE,
    FILTER_BY_PRICE,
    FILTER_BY_CALORIES,

    // Сортировка
    SORT_BY_NAME,
    SORT_BY_PRICE,
    SORT_BY_CALORIES,

    // Статистика
    GET_STATISTICS,
    GET_CALORIES_MAP,
    GET_TOP_POPULAR_DISHES,

    // Служебные
    DISCONNECT
}

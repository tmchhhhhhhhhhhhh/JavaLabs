package ru.restaurant.common.enums;

public enum Operation {
    // CRUD операции
    CREATE_DISH,
    GET_ALL_DISHES,
    GET_DISH_BY_ID,
    UPDATE_DISH,
    DELETE_DISH,
    
    // Фильтрация
    FILTER_BY_TYPE,
    FILTER_BY_PRICE,
    FILTER_BY_CALORIES,
    
    // Сортировка
    SORT_BY_NAME,
    SORT_BY_PRICE,
    SORT_BY_CALORIES,
    
    // Заказы
    ORDER_DISH,
    GET_ORDER_STATISTICS,
    
    // Статистика
    GET_STATISTICS,
    GET_CALORIES_MAP,
    GET_CHANGE_LOG,
    
    // Служебные
    DISCONNECT
}

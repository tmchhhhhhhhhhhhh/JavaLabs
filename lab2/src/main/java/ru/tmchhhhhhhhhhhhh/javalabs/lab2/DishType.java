package ru.tmchhhhhhhhhhhhh.javalabs.lab2;

public enum DishType {
    STARTER("Закуска"),
    MAIN_COURSE("Основное блюдо"),
    DESSERT("Десерт");

    private final String displayName;

    DishType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static DishType fromString(String type) {
        return switch (type.toLowerCase()) {
            case "starter", "закуска","з" -> STARTER;
            case "maincourse", "main", "основное","о" -> MAIN_COURSE;
            case "dessert", "десерт","д" -> DESSERT;
            default -> throw new IllegalArgumentException("Неизвестный тип блюда: " + type);
        };
    }
}
package ru.tmchhhhhhhhhhhhh.javalabs.lab4;

import java.util.Comparator;

public class DishComparator implements Comparator<Dish> {
    public enum SortBy {
        NAME, PRICE, CALORIES, PRICE_PER_CALORIE
    }

    private final SortBy sortBy;
    private final boolean ascending;

    public DishComparator(SortBy sortBy, boolean ascending) {
        this.sortBy = sortBy;
        this.ascending = ascending;
    }

    @Override
    public int compare(Dish d1, Dish d2) {
        int result = 0;

        switch (sortBy) {
            case NAME -> result = d1.getName().compareTo(d2.getName());
            case PRICE -> result = Double.compare(d1.getPrice(), d2.getPrice());
            case CALORIES -> result = Integer.compare(d1.getCalories(), d2.getCalories());
            case PRICE_PER_CALORIE -> {
                double ppc1 = d1.getPrice() / d1.getCalories();
                double ppc2 = d2.getPrice() / d2.getCalories();
                result = Double.compare(ppc1, ppc2);
            }
        }

        return ascending ? result : -result;
    }
}
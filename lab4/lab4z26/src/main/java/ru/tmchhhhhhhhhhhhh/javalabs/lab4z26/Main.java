package ru.tmchhhhhhhhhhhhh.javalabs.lab4z26;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Double> numbers = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0);

        System.out.println("Исходный ряд чисел: " + numbers);
        System.out.println("\nПроцесс попарного суммирования:");

        double result = pairwiseSum(numbers);

        System.out.println("\nИтоговый результат: " + result);
    }

    public static double pairwiseSum(List<Double> initialNumbers) {
        Set<Double> currentSet = new LinkedHashSet<>(initialNumbers);
        int stage = 1;

        while (currentSet.size() > 1) {
            System.out.println("Этап " + stage + ": " + currentSet);

            Set<Double> nextSet = new LinkedHashSet<>();

            List<Double> currentList = new ArrayList<>(currentSet);

            for (int i = 0; i < currentList.size(); i += 2) {
                if (i + 1 < currentList.size()) {
                    double sum = currentList.get(i) + currentList.get(i + 1);
                    nextSet.add(sum);
                    System.out.println("  " + currentList.get(i) + " + " +
                            currentList.get(i + 1) + " = " + sum);
                } else {
                    nextSet.add(currentList.get(i));
                    System.out.println("  " + currentList.get(i) + " (без пары)");
                }
            }

            currentSet = nextSet;
            stage++;
        }

        return currentSet.iterator().next();
    }
}

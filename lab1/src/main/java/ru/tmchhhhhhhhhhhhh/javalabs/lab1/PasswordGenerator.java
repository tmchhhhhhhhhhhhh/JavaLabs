package ru.tmchhhhhhhhhhhhh.javalabs.lab1;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    private static final SecureRandom random = new SecureRandom();

    public static String generate(int length, boolean useLower, boolean useUpper, boolean useDigits, boolean useSymbols) {
        StringBuilder chars = new StringBuilder();
        if (useLower) chars.append(LOWER);
        if (useUpper) chars.append(UPPER);
        if (useDigits) chars.append(DIGITS);
        if (useSymbols) chars.append(SYMBOLS);

        if (chars.isEmpty()) throw new IllegalArgumentException("Нет символов для генерации");

        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            password.append(chars.charAt(index));
        }

        return password.toString();
    }
}

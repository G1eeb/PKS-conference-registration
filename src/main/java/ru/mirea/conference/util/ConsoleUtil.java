package ru.mirea.conference.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public final class ConsoleUtil {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private ConsoleUtil() { }

    public static String readString(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    public static String readNonEmptyString(String prompt) {
        while (true) {
            String s = readString(prompt);
            if (!s.isEmpty()) return s;
            System.out.println("Ошибка: значение не может быть пустым.");
        }
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: ID должен быть целым числом.");
            }
        }
    }

    public static Long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                return Long.parseLong(line);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: значение должно быть целым числом.");
            }
        }
    }

    public static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                return LocalDate.parse(line, DATE_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: дата должна быть в формате yyyy-MM-dd.");
            }
        }
    }

    public static void pressEnterToContinue() {
        System.out.print("\nНажмите Enter для продолжения...");
        SCANNER.nextLine();
    }
}
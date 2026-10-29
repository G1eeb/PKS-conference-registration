package ru.mirea.conference.fx.util;

public final class ValidationUtil {
    private ValidationUtil() {}
    public static String required(String value, String field, int max) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException("Поле «"+field+"» обязательно.");
        String v=value.trim();
        if (v.length()>max) throw new IllegalArgumentException("Поле «"+field+"» не должно превышать "+max+" символов.");
        return v;
    }
    public static String optional(String value, String field, int max) {
        if (value == null || value.isBlank()) return null;
        String v=value.trim();
        if (v.length()>max) throw new IllegalArgumentException("Поле «"+field+"» не должно превышать "+max+" символов.");
        return v;
    }
    public static String email(String value) {
        String v=required(value,"Email",150);
        if (!v.matches("^[\\w.!#$%&'*+/=?^`{|}~-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)+$"))
            throw new IllegalArgumentException("Введите корректный email.");
        return v;
    }
}
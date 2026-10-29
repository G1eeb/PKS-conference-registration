package ru.mirea.conference.util;

import ru.mirea.conference.exception.DatabaseException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public final class DatabaseManager {
    private static final String url;
    private static final String user;
    private static final String password;
    static {
        try (InputStream in = DatabaseManager.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) throw new DatabaseException("Файл db.properties не найден");
            Properties p = new Properties();
            p.load(in);
            url = require(p, "db.url"); user = require(p, "db.user"); password = require(p, "db.password");
        } catch (Exception e) {
            if (e instanceof DatabaseException de) throw de;
            throw new DatabaseException("Не удалось загрузить настройки БД", e);
        }
    }
    private static String require(Properties p, String key) {
        String value = p.getProperty(key);
        if (value == null || value.isBlank()) throw new DatabaseException("Не задан параметр " + key);
        return value.trim();
    }
    private DatabaseManager() {}
    public static Connection getConnection() {
        try { return DriverManager.getConnection(url,user,password); }
        catch (SQLException e) { throw new DatabaseException("Не удалось подключиться к PostgreSQL: " + e.getMessage(), e); }
    }
    public static boolean testConnection() {
        try (Connection c=getConnection()) { return c.isValid(2); }
        catch (Exception e) { return false; }
    }
}
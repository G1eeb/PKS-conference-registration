package ru.mirea.conference.util;

import ru.mirea.conference.exception.DatabaseException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseManager {

    private static String url;
    private static String user;
    private static String password;

    static {
        try (InputStream in = DatabaseManager.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in == null) throw new DatabaseException("Файл db.properties не найден");
            Properties props = new Properties();
            props.load(in);
            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");
        } catch (IOException e) {
            throw new DatabaseException("Ошибка чтения db.properties", e);
        }
    }

    private DatabaseManager() { }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка подключения к БД: " + e.getMessage(), e);
        }
    }
}

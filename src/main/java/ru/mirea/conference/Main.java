package ru.mirea.conference;

import ru.mirea.conference.exception.DatabaseException;
import ru.mirea.conference.ui.ConferenceMenu;
import ru.mirea.conference.ui.ParticipantMenu;
import ru.mirea.conference.ui.RegistrationMenu;
import ru.mirea.conference.util.ConsoleUtil;

public class Main {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  СИСТЕМА РЕГИСТРАЦИИ НА КОНФЕРЕНЦИИ");
        System.out.println("=================================================");

        while (true) {
            System.out.println("\n========== ГЛАВНОЕ МЕНЮ ==========");
            System.out.println("1. Участники");
            System.out.println("2. Конференции");
            System.out.println("3. Регистрации");
            System.out.println("0. Выход");
            int choice = ConsoleUtil.readInt("Выберите действие: ");

            try {
                switch (choice) {
                    case 1 -> new ParticipantMenu().show();
                    case 2 -> new ConferenceMenu().show();
                    case 3 -> new RegistrationMenu().show();
                    case 0 -> {
                        System.out.println("Выход...");
                        return;
                    }
                    default -> System.out.println("Неверный пункт меню.");
                }
            } catch (DatabaseException e) {
                System.out.println("Ошибка БД: " + e.getMessage());
                System.out.println("Проверьте подключение и настройки db.properties.");
            }
        }
    }
}
package ru.mirea.conference.ui;

import ru.mirea.conference.exception.BusinessException;
import ru.mirea.conference.exception.DatabaseException;
import ru.mirea.conference.exception.EntityNotFoundException;
import ru.mirea.conference.model.Conference;
import ru.mirea.conference.model.ConferenceTrack;
import ru.mirea.conference.service.ConferenceService;
import ru.mirea.conference.util.ConsoleUtil;

import java.time.LocalDate;
import java.util.List;

public class ConferenceMenu {

    private final ConferenceService service = new ConferenceService();

    public void show() {
        while (true) {
            System.out.println("\n========== КОНФЕРЕНЦИИ ==========");
            System.out.println("1. Создать конференцию");
            System.out.println("2. Все конференции");
            System.out.println("3. Найти по ID");
            System.out.println("4. Изменить");
            System.out.println("5. Удалить");
            System.out.println("6. Поиск по названию");
            System.out.println("7. Фильтр по треку");
            System.out.println("8. Фильтр по диапазону дат");
            System.out.println("9. Сортировка по дате начала");
            System.out.println("10. Сортировка по названию");
            System.out.println("0. Назад");
            int choice = ConsoleUtil.readInt("Выберите действие: ");
            try {
                switch (choice) {
                    case 1 -> create();
                    case 2 -> printList(service.getAll());
                    case 3 -> findById();
                    case 4 -> update();
                    case 5 -> delete();
                    case 6 -> searchByTitle();
                    case 7 -> filterByTrack();
                    case 8 -> filterByDateRange();
                    case 9 -> printList(service.sortByStartDate());
                    case 10 -> printList(service.sortByTitle());
                    case 0 -> { return; }
                    default -> System.out.println("Неверный пункт меню.");
                }
            } catch (BusinessException | EntityNotFoundException | DatabaseException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
            ConsoleUtil.pressEnterToContinue();
        }
    }

    private void create() {
        String title = ConsoleUtil.readNonEmptyString("Название: ");
        ConferenceTrack track = readTrack();
        LocalDate start = ConsoleUtil.readDate("Дата начала (yyyy-MM-dd): ");
        LocalDate end   = ConsoleUtil.readDate("Дата окончания (yyyy-MM-dd): ");
        String location = ConsoleUtil.readString("Место: ");
        Conference c = service.create(new Conference(title, track, start, end, location));
        System.out.println("✔ Конференция создана: " + c);
    }

    private ConferenceTrack readTrack() {
        while (true) {
            System.out.print("Трек (IT/SCIENCE/BUSINESS/MEDICINE/EDUCATION): ");
            try {
                return ConferenceTrack.valueOf(ConsoleUtil.readString("").toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: неверный трек.");
            }
        }
    }

    private void findById() {
        System.out.println(service.getById(ConsoleUtil.readLong("ID: ")));
    }

    private void update() {
        Long id = ConsoleUtil.readLong("ID конференции: ");
        Conference c = service.getById(id);
        c.setTitle(ConsoleUtil.readString("Новое название [" + c.getTitle() + "]: "));
        c.setTrack(readTrack());
        c.setStartDate(ConsoleUtil.readDate("Новая дата начала: "));
        c.setEndDate(ConsoleUtil.readDate("Новая дата окончания: "));
        c.setLocation(ConsoleUtil.readString("Новое место: "));
        service.update(c);
        System.out.println("✔ Конференция обновлена.");
    }

    private void delete() {
        service.delete(ConsoleUtil.readLong("ID для удаления: "));
        System.out.println("✔ Конференция удалена.");
    }

    private void searchByTitle() {
        printList(service.searchByTitle(ConsoleUtil.readNonEmptyString("Часть названия: ")));
    }

    private void filterByTrack() {
        printList(service.filterByTrack(readTrack()));
    }

    private void filterByDateRange() {
        LocalDate from = ConsoleUtil.readDate("С даты: ");
        LocalDate to   = ConsoleUtil.readDate("По дату: ");
        printList(service.filterByDateRange(from, to));
    }

    private void printList(List<Conference> list) {
        if (list.isEmpty()) System.out.println("Список пуст.");
        else list.forEach(System.out::println);
    }
}
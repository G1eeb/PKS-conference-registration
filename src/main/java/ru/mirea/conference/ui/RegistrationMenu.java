package ru.mirea.conference.ui;

import ru.mirea.conference.exception.BusinessException;
import ru.mirea.conference.exception.DatabaseException;
import ru.mirea.conference.exception.EntityNotFoundException;
import ru.mirea.conference.model.Registration;
import ru.mirea.conference.model.RegistrationStatus;
import ru.mirea.conference.service.RegistrationService;
import ru.mirea.conference.util.ConsoleUtil;
import ru.mirea.conference.util.ExcelExporter;

import java.util.List;
import java.util.Map;

public class RegistrationMenu {

    private final RegistrationService service = new RegistrationService();

    public void show() {
        while (true) {
            System.out.println("\n========== РЕГИСТРАЦИИ ==========");
            System.out.println("1. Создать регистрацию");
            System.out.println("2. Все регистрации");
            System.out.println("3. Найти по ID");
            System.out.println("4. Изменить статус");
            System.out.println("5. Удалить");
            System.out.println("6. Поиск по участнику");
            System.out.println("7. Поиск по конференции");
            System.out.println("8. Фильтр по статусу");
            System.out.println("9. Фильтр по конференции");
            System.out.println("10. Сортировка по дате");
            System.out.println("11. Сортировка по статусу");
            System.out.println("12. Статистика");
            System.out.println("13. Экспорт в Excel");
            System.out.println("0. Назад");
            int choice = ConsoleUtil.readInt("Выберите действие: ");
            try {
                switch (choice) {
                    case 1 -> create();
                    case 2 -> printList(service.getAll());
                    case 3 -> findById();
                    case 4 -> updateStatus();
                    case 5 -> delete();
                    case 6 -> searchByParticipant();
                    case 7 -> searchByConference();
                    case 8 -> filterByStatus();
                    case 9 -> filterByConference();
                    case 10 -> printList(service.sortByRegisteredAt());
                    case 11 -> printList(service.sortByStatus());
                    case 12 -> statistics();
                    case 13 -> export();
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
        Long participantId = ConsoleUtil.readLong("ID участника: ");
        Long conferenceId  = ConsoleUtil.readLong("ID конференции: ");
        RegistrationStatus status = readStatusOptional();
        Registration r = service.create(participantId, conferenceId, status);
        System.out.println("✔ Регистрация создана: " + r);
    }

    private RegistrationStatus readStatusOptional() {
        System.out.print("Статус (Enter — CREATED): ");
        String s = ConsoleUtil.readString("").toUpperCase();
        if (s.isEmpty()) return RegistrationStatus.CREATED;
        try {
            return RegistrationStatus.valueOf(s);
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный статус, будет установлен CREATED.");
            return RegistrationStatus.CREATED;
        }
    }

    private RegistrationStatus readStatus() {
        while (true) {
            System.out.print("Статус (CREATED/CONFIRMED/PAID/ATTENDED/CANCELLED): ");
            try {
                return RegistrationStatus.valueOf(ConsoleUtil.readString("").toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: неверный статус.");
            }
        }
    }

    private void findById() {
        System.out.println(service.getById(ConsoleUtil.readLong("ID: ")));
    }

    private void updateStatus() {
        Long id = ConsoleUtil.readLong("ID регистрации: ");
        RegistrationStatus status = readStatus();
        Registration r = service.updateStatus(id, status);
        System.out.println("✔ Статус обновлён: " + r);
    }

    private void delete() {
        service.delete(ConsoleUtil.readLong("ID для удаления: "));
        System.out.println("✔ Регистрация удалена.");
    }

    private void searchByParticipant() {
        printList(service.searchByParticipantName(ConsoleUtil.readNonEmptyString("Часть ФИО: ")));
    }

    private void searchByConference() {
        printList(service.searchByConferenceTitle(ConsoleUtil.readNonEmptyString("Часть названия: ")));
    }

    private void filterByStatus() {
        printList(service.filterByStatus(readStatus()));
    }

    private void filterByConference() {
        printList(service.filterByConference(ConsoleUtil.readLong("ID конференции: ")));
    }

    private void statistics() {
        Map<String, Object> stats = service.getStatistics();
        System.out.println("\n===== СТАТИСТИКА =====");
        stats.forEach((k, v) -> System.out.println(k + ": " + v));
    }

    private void export() {
        String path = ConsoleUtil.readString("Путь к файлу (registrations.xlsx): ");
        if (path.isEmpty()) path = "registrations.xlsx";
        ExcelExporter.exportRegistrations(service.getAll(), path);
    }

    private void printList(List<Registration> list) {
        if (list.isEmpty()) System.out.println("Список пуст.");
        else list.forEach(System.out::println);
    }
}
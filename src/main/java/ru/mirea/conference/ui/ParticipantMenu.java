package ru.mirea.conference.ui;

import ru.mirea.conference.exception.BusinessException;
import ru.mirea.conference.exception.DatabaseException;
import ru.mirea.conference.exception.EntityNotFoundException;
import ru.mirea.conference.model.Participant;
import ru.mirea.conference.service.ParticipantService;
import ru.mirea.conference.util.ConsoleUtil;

import java.util.List;

public class ParticipantMenu {

    private final ParticipantService service = new ParticipantService();

    public void show() {
        while (true) {
            System.out.println("\n========== УЧАСТНИКИ ==========");
            System.out.println("1. Создать участника");
            System.out.println("2. Все участники");
            System.out.println("3. Найти по ID");
            System.out.println("4. Изменить участника");
            System.out.println("5. Удалить участника");
            System.out.println("6. Поиск по ФИО");
            System.out.println("7. Поиск по email");
            System.out.println("8. Сортировка по ФИО");
            System.out.println("9. Сортировка по дате создания");
            System.out.println("0. Назад");
            int choice = ConsoleUtil.readInt("Выберите действие: ");
            try {
                switch (choice) {
                    case 1 -> create();
                    case 2 -> listAll();
                    case 3 -> findById();
                    case 4 -> update();
                    case 5 -> delete();
                    case 6 -> searchByName();
                    case 7 -> searchByEmail();
                    case 8 -> printList(service.sortByName());
                    case 9 -> printList(service.sortByCreatedAt());
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
        String name  = ConsoleUtil.readNonEmptyString("ФИО: ");
        String email = ConsoleUtil.readNonEmptyString("Email: ");
        String org   = ConsoleUtil.readString("Организация: ");
        Participant p = service.create(new Participant(name, email, org));
        System.out.println("✔ Участник создан: " + p);
    }

    private void listAll() { printList(service.getAll()); }

    private void findById() {
        System.out.println(service.getById(ConsoleUtil.readLong("ID: ")));
    }

    private void update() {
        Long id = ConsoleUtil.readLong("ID участника для изменения: ");
        Participant p = service.getById(id);
        p.setFullName(ConsoleUtil.readString("Новое ФИО [" + p.getFullName() + "]: "));
        p.setEmail(ConsoleUtil.readString("Новый email [" + p.getEmail() + "]: "));
        p.setOrganization(ConsoleUtil.readString("Новая организация [" + p.getOrganization() + "]: "));
        service.update(p);
        System.out.println("✔ Участник обновлён.");
    }

    private void delete() {
        service.delete(ConsoleUtil.readLong("ID участника для удаления: "));
        System.out.println("✔ Участник удалён.");
    }

    private void searchByName() {
        printList(service.searchByName(ConsoleUtil.readNonEmptyString("Часть ФИО: ")));
    }

    private void searchByEmail() {
        printList(service.searchByEmail(ConsoleUtil.readNonEmptyString("Часть email: ")));
    }

    private void printList(List<Participant> list) {
        if (list.isEmpty()) System.out.println("Список пуст.");
        else list.forEach(System.out::println);
    }
}
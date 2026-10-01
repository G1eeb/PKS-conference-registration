package ru.mirea.conference.service;

import ru.mirea.conference.exception.BusinessException;
import ru.mirea.conference.exception.EntityNotFoundException;
import ru.mirea.conference.model.*;
import ru.mirea.conference.repository.ConferenceRepository;
import ru.mirea.conference.repository.ParticipantRepository;
import ru.mirea.conference.repository.RegistrationRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class RegistrationService {

    private final RegistrationRepository regRepo = new RegistrationRepository();
    private final ParticipantRepository partRepo = new ParticipantRepository();
    private final ConferenceRepository confRepo = new ConferenceRepository();

    private static final Map<RegistrationStatus, Set<RegistrationStatus>> ALLOWED_TRANSITIONS = Map.of(
            RegistrationStatus.CREATED,   EnumSet.of(RegistrationStatus.CONFIRMED, RegistrationStatus.CANCELLED),
            RegistrationStatus.CONFIRMED, EnumSet.of(RegistrationStatus.PAID, RegistrationStatus.CANCELLED),
            RegistrationStatus.PAID,      EnumSet.of(RegistrationStatus.ATTENDED, RegistrationStatus.CANCELLED),
            RegistrationStatus.ATTENDED,  EnumSet.noneOf(RegistrationStatus.class),
            RegistrationStatus.CANCELLED, EnumSet.noneOf(RegistrationStatus.class)
    );

    public Registration create(Long participantId, Long conferenceId, RegistrationStatus status) {
        Participant p = partRepo.findById(participantId)
                .orElseThrow(() -> new BusinessException("Участник с id=" + participantId + " не существует"));
        Conference c = confRepo.findById(conferenceId)
                .orElseThrow(() -> new BusinessException("Конференция с id=" + conferenceId + " не существует"));

        if (c.getEndDate().isBefore(LocalDate.now()))
            throw new BusinessException("Нельзя регистрироваться на завершённую конференцию: " + c.getTitle());

        regRepo.findByParticipantAndConference(participantId, conferenceId).ifPresent(r -> {
            throw new BusinessException("Участник " + p.getFullName() +
                    " уже зарегистрирован на конференцию «" + c.getTitle() + "»");
        });

        if (status == null) status = RegistrationStatus.CREATED;

        Registration reg = new Registration(participantId, conferenceId, status);
        Registration saved = regRepo.save(reg);
        saved.setParticipantName(p.getFullName());
        saved.setConferenceTitle(c.getTitle());
        return saved;
    }

    public Registration getById(Long id) {
        return regRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Регистрация с id=" + id + " не найдена"));
    }

    public List<Registration> getAll() {
        return regRepo.findAll();
    }

    public Registration updateStatus(Long id, RegistrationStatus newStatus) {
        Registration reg = getById(id);
        RegistrationStatus current = reg.getStatus();
        if (current == newStatus) return reg;
        Set<RegistrationStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
        if (!allowed.contains(newStatus))
            throw new BusinessException("Недопустимый переход статуса: " + current + " → " + newStatus);
        reg.setStatus(newStatus);
        if (!regRepo.update(reg))
            throw new EntityNotFoundException("Регистрация с id=" + id + " не найдена");
        return reg;
    }

    public Registration update(Registration reg) {
        if (reg.getId() == null) throw new BusinessException("ID регистрации не указан");
        if (!regRepo.update(reg))
            throw new EntityNotFoundException("Регистрация с id=" + reg.getId() + " не найдена");
        return reg;
    }

    public void delete(Long id) {
        if (!regRepo.deleteById(id))
            throw new EntityNotFoundException("Регистрация с id=" + id + " не найдена");
    }

    public List<Registration> searchByParticipantName(String query) {
        String q = query.toLowerCase();
        return regRepo.findAll().stream()
                .filter(r -> r.getParticipantName() != null &&
                             r.getParticipantName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public List<Registration> searchByConferenceTitle(String query) {
        String q = query.toLowerCase();
        return regRepo.findAll().stream()
                .filter(r -> r.getConferenceTitle() != null &&
                             r.getConferenceTitle().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public List<Registration> filterByStatus(RegistrationStatus status) {
        return regRepo.findAll().stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Registration> filterByConference(Long conferenceId) {
        return regRepo.findAll().stream()
                .filter(r -> r.getConferenceId().equals(conferenceId))
                .collect(Collectors.toList());
    }

    public List<Registration> sortByRegisteredAt() {
        return regRepo.findAll().stream()
                .sorted(Comparator.comparing(Registration::getRegisteredAt))
                .collect(Collectors.toList());
    }

    public List<Registration> sortByStatus() {
        return regRepo.findAll().stream()
                .sorted(Comparator.comparing(r -> r.getStatus().name()))
                .collect(Collectors.toList());
    }

    // Вспомогательный доступ для статистики (используется в коммите 3)
    public RegistrationRepository getRegistrationRepository() { return regRepo; }
    public ParticipantRepository getParticipantRepository()   { return partRepo; }
    public ConferenceRepository getConferenceRepository()     { return confRepo; }

    // getStatistics() будет добавлен в КОММИТЕ 3 (Участник 2)
}

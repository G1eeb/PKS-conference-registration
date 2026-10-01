package ru.mirea.conference.service;

import ru.mirea.conference.exception.BusinessException;
import ru.mirea.conference.exception.EntityNotFoundException;
import ru.mirea.conference.model.Participant;
import ru.mirea.conference.repository.ParticipantRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ParticipantService {

    private final ParticipantRepository repository = new ParticipantRepository();

    private void validate(Participant p) {
        if (p.getFullName() == null || p.getFullName().isBlank())
            throw new BusinessException("ФИО участника не может быть пустым");
        if (p.getEmail() == null || p.getEmail().isBlank())
            throw new BusinessException("Email участника не может быть пустым");
        if (!p.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$"))
            throw new BusinessException("Некорректный формат email: " + p.getEmail());
    }

    public Participant create(Participant p) {
        validate(p);
        repository.findByEmail(p.getEmail()).ifPresent(existing -> {
            throw new BusinessException("Участник с email " + p.getEmail() + " уже существует");
        });
        return repository.save(p);
    }

    public Participant getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Участник с id=" + id + " не найден"));
    }

    public List<Participant> getAll() {
        return repository.findAll();
    }

    public Participant update(Participant p) {
        if (p.getId() == null) throw new BusinessException("ID участника не указан");
        validate(p);
        repository.findByEmail(p.getEmail()).ifPresent(other -> {
            if (!other.getId().equals(p.getId()))
                throw new BusinessException("Email " + p.getEmail() + " уже занят другим участником");
        });
        if (!repository.update(p))
            throw new EntityNotFoundException("Участник с id=" + p.getId() + " не найден");
        return p;
    }

    public void delete(Long id) {
        if (!repository.deleteById(id))
            throw new EntityNotFoundException("Участник с id=" + id + " не найден");
    }

    public List<Participant> searchByName(String query) {
        String q = query.toLowerCase();
        return repository.findAll().stream()
                .filter(p -> p.getFullName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public List<Participant> searchByEmail(String query) {
        String q = query.toLowerCase();
        return repository.findAll().stream()
                .filter(p -> p.getEmail().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public List<Participant> sortByName() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Participant::getFullName))
                .collect(Collectors.toList());
    }

    public List<Participant> sortByCreatedAt() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Participant::getCreatedAt))
                .collect(Collectors.toList());
    }
}

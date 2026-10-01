package ru.mirea.conference.service;

import ru.mirea.conference.exception.BusinessException;
import ru.mirea.conference.exception.EntityNotFoundException;
import ru.mirea.conference.model.Conference;
import ru.mirea.conference.model.ConferenceTrack;
import ru.mirea.conference.repository.ConferenceRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ConferenceService {

    private final ConferenceRepository repository = new ConferenceRepository();

    private void validate(Conference c) {
        if (c.getTitle() == null || c.getTitle().isBlank())
            throw new BusinessException("Название конференции не может быть пустым");
        if (c.getTrack() == null)
            throw new BusinessException("Трек конференции не указан");
        if (c.getStartDate() == null || c.getEndDate() == null)
            throw new BusinessException("Даты конференции должны быть заполнены");
        if (c.getEndDate().isBefore(c.getStartDate()))
            throw new BusinessException("Дата окончания не может быть раньше даты начала");
    }

    public Conference create(Conference c) {
        validate(c);
        return repository.save(c);
    }

    public Conference getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Конференция с id=" + id + " не найдена"));
    }

    public List<Conference> getAll() {
        return repository.findAll();
    }

    public Conference update(Conference c) {
        if (c.getId() == null) throw new BusinessException("ID конференции не указан");
        validate(c);
        if (!repository.update(c))
            throw new EntityNotFoundException("Конференция с id=" + c.getId() + " не найдена");
        return c;
    }

    public void delete(Long id) {
        if (!repository.deleteById(id))
            throw new EntityNotFoundException("Конференция с id=" + id + " не найдена");
    }

    public List<Conference> searchByTitle(String query) {
        String q = query.toLowerCase();
        return repository.findAll().stream()
                .filter(c -> c.getTitle().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public List<Conference> filterByTrack(ConferenceTrack track) {
        return repository.findAll().stream()
                .filter(c -> c.getTrack() == track)
                .collect(Collectors.toList());
    }

    public List<Conference> filterByDateRange(LocalDate from, LocalDate to) {
        return repository.findAll().stream()
                .filter(c -> !c.getStartDate().isAfter(to) && !c.getEndDate().isBefore(from))
                .collect(Collectors.toList());
    }

    public List<Conference> sortByStartDate() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Conference::getStartDate))
                .collect(Collectors.toList());
    }

    public List<Conference> sortByTitle() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Conference::getTitle))
                .collect(Collectors.toList());
    }
}

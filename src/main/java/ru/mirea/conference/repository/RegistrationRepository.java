package ru.mirea.conference.repository;

import ru.mirea.conference.exception.DatabaseException;
import ru.mirea.conference.model.Registration;
import ru.mirea.conference.model.RegistrationStatus;
import ru.mirea.conference.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RegistrationRepository implements Repository<Registration, Long> {

    private static final String BASE_SELECT =
            "SELECT r.id, r.participant_id, r.conference_id, r.status, r.registered_at, " +
            "       p.full_name AS participant_name, c.title AS conference_title " +
            "FROM registrations r " +
            "JOIN participants p ON r.participant_id = p.id " +
            "JOIN conferences  c ON r.conference_id  = c.id ";

    @Override
    public Registration save(Registration reg) {
        String sql = "INSERT INTO registrations (participant_id, conference_id, status) VALUES (?, ?, ?) RETURNING id, registered_at";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, reg.getParticipantId());
            ps.setLong(2, reg.getConferenceId());
            ps.setString(3, reg.getStatus().name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    reg.setId(rs.getLong("id"));
                    reg.setRegisteredAt(rs.getTimestamp("registered_at").toLocalDateTime());
                }
            }
            return reg;
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка сохранения регистрации: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Registration> findById(Long id) {
        String sql = BASE_SELECT + "WHERE r.id = ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка поиска регистрации: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Registration> findAll() {
        String sql = BASE_SELECT + "ORDER BY r.id";
        List<Registration> list = new ArrayList<>();
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка получения регистраций: " + e.getMessage(), e);
        }
        return list;
    }

    public Optional<Registration> findByParticipantAndConference(Long participantId, Long conferenceId) {
        String sql = BASE_SELECT + "WHERE r.participant_id = ? AND r.conference_id = ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, participantId);
            ps.setLong(2, conferenceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка поиска регистрации: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Registration reg) {
        String sql = "UPDATE registrations SET participant_id=?, conference_id=?, status=? WHERE id=?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, reg.getParticipantId());
            ps.setLong(2, reg.getConferenceId());
            ps.setString(3, reg.getStatus().name());
            ps.setLong(4, reg.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка обновления регистрации: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM registrations WHERE id = ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка удаления регистрации: " + e.getMessage(), e);
        }
    }

    private Registration mapRow(ResultSet rs) throws SQLException {
        Registration r = new Registration(
                rs.getLong("id"),
                rs.getLong("participant_id"),
                rs.getLong("conference_id"),
                RegistrationStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("registered_at").toLocalDateTime()
        );
        r.setParticipantName(rs.getString("participant_name"));
        r.setConferenceTitle(rs.getString("conference_title"));
        return r;
    }
}

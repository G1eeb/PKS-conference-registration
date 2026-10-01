package ru.mirea.conference.repository;

import ru.mirea.conference.exception.DatabaseException;
import ru.mirea.conference.model.Participant;
import ru.mirea.conference.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParticipantRepository implements Repository<Participant, Long> {

    @Override
    public Participant save(Participant p) {
        String sql = "INSERT INTO participants (full_name, email, organization) VALUES (?, ?, ?) RETURNING id, created_at";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getFullName());
            ps.setString(2, p.getEmail());
            ps.setString(3, p.getOrganization());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p.setId(rs.getLong("id"));
                    p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
            }
            return p;
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка сохранения участника: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Participant> findById(Long id) {
        String sql = "SELECT * FROM participants WHERE id = ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка поиска участника: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Participant> findAll() {
        String sql = "SELECT * FROM participants ORDER BY id";
        List<Participant> list = new ArrayList<>();
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка получения участников: " + e.getMessage(), e);
        }
        return list;
    }

    public Optional<Participant> findByEmail(String email) {
        String sql = "SELECT * FROM participants WHERE email = ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка поиска по email: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Participant p) {
        String sql = "UPDATE participants SET full_name=?, email=?, organization=? WHERE id=?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getFullName());
            ps.setString(2, p.getEmail());
            ps.setString(3, p.getOrganization());
            ps.setLong(4, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка обновления участника: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM participants WHERE id = ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка удаления участника: " + e.getMessage(), e);
        }
    }

    private Participant mapRow(ResultSet rs) throws SQLException {
        return new Participant(
                rs.getLong("id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("organization"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}

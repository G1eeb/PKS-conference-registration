package ru.mirea.conference.repository;

import ru.mirea.conference.exception.DatabaseException;
import ru.mirea.conference.model.Conference;
import ru.mirea.conference.model.ConferenceTrack;
import ru.mirea.conference.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConferenceRepository implements Repository<Conference, Long> {

    @Override
    public Conference save(Conference conf) {
        String sql = "INSERT INTO conferences (title, track, start_date, end_date, location) VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, conf.getTitle());
            ps.setString(2, conf.getTrack().name());
            ps.setDate(3, Date.valueOf(conf.getStartDate()));
            ps.setDate(4, Date.valueOf(conf.getEndDate()));
            ps.setString(5, conf.getLocation());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) conf.setId(rs.getLong("id"));
            }
            return conf;
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка сохранения конференции: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Conference> findById(Long id) {
        String sql = "SELECT * FROM conferences WHERE id = ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка поиска конференции: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Conference> findAll() {
        String sql = "SELECT * FROM conferences ORDER BY id";
        List<Conference> list = new ArrayList<>();
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка получения конференций: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean update(Conference conf) {
        String sql = "UPDATE conferences SET title=?, track=?, start_date=?, end_date=?, location=? WHERE id=?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, conf.getTitle());
            ps.setString(2, conf.getTrack().name());
            ps.setDate(3, Date.valueOf(conf.getStartDate()));
            ps.setDate(4, Date.valueOf(conf.getEndDate()));
            ps.setString(5, conf.getLocation());
            ps.setLong(6, conf.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка обновления конференции: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM conferences WHERE id = ?";
        try (Connection c = DatabaseManager.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка удаления конференции: " + e.getMessage(), e);
        }
    }

    private Conference mapRow(ResultSet rs) throws SQLException {
        return new Conference(
                rs.getLong("id"),
                rs.getString("title"),
                ConferenceTrack.valueOf(rs.getString("track")),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate(),
                rs.getString("location")
        );
    }
}

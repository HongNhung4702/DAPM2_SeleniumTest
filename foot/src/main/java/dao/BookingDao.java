package dao;

import model.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public class BookingDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final class BookingRowMapper implements RowMapper<Booking> {
        @Override
        public Booking mapRow(ResultSet rs, int rowNum) throws SQLException {
            Booking booking = new Booking();
            booking.setId(rs.getLong("id"));
            booking.setUserId(rs.getLong("user_id"));
            booking.setStadiumId(rs.getLong("stadium_id"));
            booking.setBookingDate(rs.getDate("booking_date").toLocalDate());
            booking.setStartTime(rs.getTime("start_time").toLocalTime());
            booking.setEndTime(rs.getTime("end_time").toLocalTime());
            booking.setStatus(Booking.Status.valueOf(rs.getString("status")));
            booking.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return booking;
        }
    }

    public List<Booking> findAll() {
        String sql = "SELECT * FROM Booking ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new BookingRowMapper());
    }

    public List<Booking> findByStatus(Booking.Status status) {
        String sql = "SELECT * FROM Booking WHERE status = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new BookingRowMapper(), status.name());
    }

    public Booking findById(Long id) {
        String sql = "SELECT * FROM Booking WHERE id = ?";
        List<Booking> bookings = jdbcTemplate.query(sql, new BookingRowMapper(), id);
        return bookings.isEmpty() ? null : bookings.get(0);
    }

    public List<Booking> findByUserId(Long userId) {
        String sql = "SELECT * FROM Booking WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new BookingRowMapper(), userId);
    }

    public void save(Booking booking) {
        // Check if this is a new booking (ID is null or 0)
        if (booking.getId() == null || booking.getId() == 0) {
            String sql = "INSERT INTO Booking (user_id, stadium_id, booking_date, start_time, end_time, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, booking.getUserId(), booking.getStadiumId(), booking.getBookingDate(),
                    booking.getStartTime(), booking.getEndTime(), booking.getStatus().name(), booking.getCreatedAt());

        } else {
            String sql = "UPDATE Booking SET user_id = ?, stadium_id = ?, booking_date = ?, start_time = ?, end_time = ?, status = ? WHERE id = ?";
            jdbcTemplate.update(sql, booking.getUserId(), booking.getStadiumId(), booking.getBookingDate(),
                    booking.getStartTime(), booking.getEndTime(), booking.getStatus().name(), booking.getId());
        }
    }

    public void updateStatus(Long id, Booking.Status status) {
        String sql = "UPDATE Booking SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status.name(), id);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM Booking WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // Get booking with user and stadium information
    public List<Object[]> findBookingsWithDetails() {
        String sql = "SELECT b.id, b.booking_date, b.start_time, b.end_time, b.status, b.created_at, " +
                "u.username, u.email, s.name as stadium_name, s.address, s.price_per_hour " +
                "FROM Booking b " +
                "JOIN User u ON b.user_id = u.id " +
                "JOIN Stadium s ON b.stadium_id = s.id " +
                "ORDER BY b.created_at DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new Object[] {
                    rs.getLong("id"),
                    rs.getDate("booking_date").toLocalDate(),
                    rs.getTime("start_time").toLocalTime(),
                    rs.getTime("end_time").toLocalTime(),
                    rs.getString("status"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("stadium_name"),
                    rs.getString("address"),
                    rs.getDouble("price_per_hour")
            };
        });
    }

    public List<Object[]> findBookingsWithDetailsByStatus(Booking.Status status) {
        String sql = "SELECT b.id, b.booking_date, b.start_time, b.end_time, b.status, b.created_at, " +
                "u.username, u.email, s.name as stadium_name, s.address, s.price_per_hour " +
                "FROM Booking b " +
                "JOIN User u ON b.user_id = u.id " +
                "JOIN Stadium s ON b.stadium_id = s.id " +
                "WHERE b.status = ? " +
                "ORDER BY b.created_at DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new Object[] {
                    rs.getLong("id"),
                    rs.getDate("booking_date").toLocalDate(),
                    rs.getTime("start_time").toLocalTime(),
                    rs.getTime("end_time").toLocalTime(),
                    rs.getString("status"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("stadium_name"),
                    rs.getString("address"),
                    rs.getDouble("price_per_hour")
            };
        }, status.name());
    }

    public List<Booking> findOverlappingBookings(Long stadiumId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        String sql = "SELECT * FROM Booking WHERE stadium_id = ? AND booking_date = ? " +
                    "AND status != 'CANCELLED' " +
                    "AND ((start_time < ? AND end_time > ?) " +  // Existing booking overlaps with new start time
                    "OR (start_time < ? AND end_time > ?) " +    // Existing booking overlaps with new end time
                    "OR (start_time >= ? AND end_time <= ?))";   // Existing booking is within new time range

        return jdbcTemplate.query(sql, new BookingRowMapper(),
                stadiumId, date,
                endTime, startTime,    // Check start time overlap
                endTime, endTime,      // Check end time overlap
                startTime, endTime     // Check contained bookings
        );
    }

    public List<Booking> findBookingsByStadiumAndDate(Long stadiumId, LocalDate date) {
        String sql = "SELECT * FROM Booking WHERE stadium_id = ? AND booking_date = ? AND status != 'CANCELLED' ORDER BY start_time";
        return jdbcTemplate.query(sql, new BookingRowMapper(), stadiumId, date);
    }

    public boolean hasActiveBookingForStadium(Long stadiumId) {
        String sql = "SELECT COUNT(*) FROM Booking WHERE stadium_id = ? AND (status = 'PENDING' OR status = 'APPROVED')";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, stadiumId);
        return count != null && count > 0;
    }
}

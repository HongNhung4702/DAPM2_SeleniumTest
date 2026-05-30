package dao;

import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Repository
public class UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User findByUsername(String username) {
        String sql = "SELECT * FROM User WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{username}, new UserRowMapper());
        } catch (Exception e) {
            return null;
        }
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM User WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{email}, new UserRowMapper());
        } catch (Exception e) {
            return null;
        }
    }

    public void save(User user) {
        String sql = "INSERT INTO User (username, password, full_name, email, phone, address, role, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                user.getUsername(),
                user.getPassword(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getRole().toString(),
                user.getCreatedAt());
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setFullName(rs.getString("full_name"));
            user.setEmail(rs.getString("email"));
            user.setPhone(rs.getString("phone"));
            user.setAddress(rs.getString("address"));
            user.setRole(User.Role.valueOf(rs.getString("role")));
            user.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
            return user;
        }
    }
}
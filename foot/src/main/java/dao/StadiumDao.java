package dao;

import model.Stadium;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class StadiumDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final class StadiumRowMapper implements RowMapper<Stadium> {
        @Override
        public Stadium mapRow(ResultSet rs, int rowNum) throws SQLException {
            Stadium stadium = new Stadium();
            stadium.setId(rs.getLong("id"));
            stadium.setName(rs.getString("name"));
            stadium.setAddress(rs.getString("address"));
            stadium.setPricePerHour(rs.getDouble("price_per_hour"));
            stadium.setDescription(rs.getString("description"));
            stadium.setImageUrl(rs.getString("image_url"));
            stadium.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            
            String fieldTypeStr = rs.getString("field_type");
            if (fieldTypeStr != null) {
                try {
                    stadium.setFieldType(Stadium.FieldType.valueOf(fieldTypeStr.replace(" ", "_").toUpperCase()));
                } catch (IllegalArgumentException e) {
                    // Handle cases where the string from DB doesn't match enum constants
                    System.err.println("Invalid field_type value from DB: " + fieldTypeStr);
                    stadium.setFieldType(null); // Or set a default
                }
            }
            try {
                stadium.setActive(rs.getBoolean("is_active"));
            } catch (SQLException e) {
                stadium.setActive(true);
            }
            return stadium;
        }
    }

    public List<Stadium> findAll() {
        String sql = "SELECT * FROM Stadium WHERE is_active = TRUE ORDER BY id DESC";
        return jdbcTemplate.query(sql, new StadiumRowMapper());
    }

    public Stadium findById(Long id) {
        String sql = "SELECT * FROM Stadium WHERE id = ? AND is_active = TRUE";
        List<Stadium> stadiums = jdbcTemplate.query(sql, new StadiumRowMapper(), id);
        return stadiums.isEmpty() ? null : stadiums.get(0);
    }

    public void save(Stadium stadium) {
        if (stadium.getId() == null) {
            String sql = "INSERT INTO Stadium (name, address, price_per_hour, description, image_url, field_type, created_at, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, stadium.getName(), stadium.getAddress(), stadium.getPricePerHour(), 
                              stadium.getDescription(), stadium.getImageUrl(), stadium.getFieldType().toString(), stadium.getCreatedAt(), stadium.isActive());
        } else {
            String sql = "UPDATE Stadium SET name = ?, address = ?, price_per_hour = ?, description = ?, image_url = ?, field_type = ?, is_active = ? WHERE id = ?";
            jdbcTemplate.update(sql, stadium.getName(), stadium.getAddress(), stadium.getPricePerHour(), 
                              stadium.getDescription(), stadium.getImageUrl(), stadium.getFieldType().toString(), stadium.isActive(), stadium.getId());
        }
    }

    public void deleteById(Long id) {
        String sql = "UPDATE Stadium SET is_active = FALSE WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}

package model;

import java.time.LocalDateTime;

public class Stadium {

    private Long id;
    private String name;
    private String address;
    private Double pricePerHour;
    private String description;
    private String imageUrl;
    private LocalDateTime createdAt = LocalDateTime.now();
    private FieldType fieldType;
    private boolean isActive = true;

    public enum FieldType {
        SÂN_5("Sân 5"),
        SÂN_7("Sân 7"),
        SÂN_11("Sân 11");

        private final String displayName;

        FieldType(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(Double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Helper method for JSP to format the date
    public String getFormattedCreatedAt() {
        if (createdAt != null) {
            return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return "";
    }
    
    // Helper method to extract area from address
    public String getArea() {
        if (address != null && !address.isEmpty()) {
            // Split by comma and get the last part (area)
            String[] parts = address.split(",");
            if (parts.length > 0) {
                String area = parts[parts.length - 1].trim();
                // Remove common prefixes like "TP.", "Huyện", "Quận", etc.
                area = area.replaceAll("^(TP\\.|Thành phố|Huyện|Quận|Thị xã)\\s*", "");
                return area.trim();
            }
        }
        return "Không xác định";
    }
}
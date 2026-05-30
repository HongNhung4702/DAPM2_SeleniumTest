package model;

import java.time.LocalDateTime;

public class User {

    private Long id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private Role role = Role.USER;
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Role {
        USER, ADMIN
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Helper method for JSP to format the date
    public String getFormattedCreatedAt() {
        if (createdAt != null) {
            return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return "";
    }
}
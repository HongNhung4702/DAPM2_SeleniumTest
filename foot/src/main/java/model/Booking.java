package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Booking {

    private Long id;
    private Long userId;
    private Long stadiumId;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Status status = Status.PENDING;
    private LocalDateTime createdAt = LocalDateTime.now();    public enum Status {
        PENDING, APPROVED, REJECTED, CANCELLED
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStadiumId() {
        return stadiumId ;
    }

    public void setStadiumId(Long stadiumId) {
        this.stadiumId = stadiumId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getFormattedCreatedAt() {
        if (createdAt != null) {
            return createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return "";
    }
    
    public String getFormattedBookingDate() {
        if (bookingDate != null) {
            return bookingDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return "";
    }
    
    public String getFormattedStartTime() {
        if (startTime != null) {
            return startTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        }
        return "";
    }
    
    public String getFormattedEndTime() {
        if (endTime != null) {
            return endTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        }
        return "";
    }
      public String getStatusInVietnamese() {
        if (status == null) return "";
        switch (status) {
            case PENDING: return "Chờ duyệt";
            case APPROVED: return "Đã duyệt";
            case REJECTED: return "Từ chối";
            case CANCELLED: return "Đã hủy";
            default: return status.toString();
        }
    }
    
    // Check if booking can be cancelled
    public boolean canBeCancelled() {
        if (status == Status.CANCELLED || status == Status.REJECTED) {
            return false;
        }
        
        // Can cancel if booking date is in the future
        if (bookingDate != null) {
            LocalDate today = LocalDate.now();
            return bookingDate.isAfter(today) || 
                   (bookingDate.equals(today) && startTime != null && 
                    startTime.isAfter(LocalTime.now()));
        }
        
        return false;
    }
    
    // Get status color class for UI
    public String getStatusColorClass() {
        if (status == null) return "secondary";
        switch (status) {
            case PENDING: return "warning";
            case APPROVED: return "success";
            case REJECTED: return "danger";
            case CANCELLED: return "dark";
            default: return "secondary";
        }
    }
    
    // Calculate duration in hours
    public double getDurationInHours() {
        if (startTime == null || endTime == null) {
            return 0.0;
        }
        long startMinutes = startTime.toSecondOfDay() / 60;
        long endMinutes = endTime.toSecondOfDay() / 60;
        return (endMinutes - startMinutes) / 60.0;
    }
}
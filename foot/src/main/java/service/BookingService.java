package service;

import dao.BookingDao;
import model.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingDao bookingDao;

    public List<Booking> getAllBookings() {
        return bookingDao.findAll();
    }

    public List<Booking> getBookingsByStatus(Booking.Status status) {
        return bookingDao.findByStatus(status);
    }

    public Booking getBookingById(Long id) {
        return bookingDao.findById(id);
    }

    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingDao.findByUserId(userId);
    }

    public void saveBooking(Booking booking) {
        bookingDao.save(booking);
    }

    public void updateBookingStatus(Long id, Booking.Status status) {
        bookingDao.updateStatus(id, status);
    }

    public void deleteBooking(Long id) {
        bookingDao.deleteById(id);
    }

    public List<Object[]> getBookingsWithDetails() {
        return bookingDao.findBookingsWithDetails();
    }

    public List<Object[]> getBookingsWithDetailsByStatus(Booking.Status status) {
        return bookingDao.findBookingsWithDetailsByStatus(status);
    }

    public List<Object[]> getPendingBookingsWithDetails() {
        return bookingDao.findBookingsWithDetailsByStatus(Booking.Status.PENDING);
    }

    @Transactional
    public void createBooking(Booking booking) {
        System.out.println("BookingService.createBooking - Input booking: " + 
                          "id=" + booking.getId() +
                          ", userId=" + booking.getUserId() + 
                          ", stadiumId=" + booking.getStadiumId() + 
                          ", date=" + booking.getBookingDate() + 
                          ", startTime=" + booking.getStartTime() + 
                          ", endTime=" + booking.getEndTime());
        
        // Validate booking time
        if (booking.getStartTime() != null && booking.getEndTime() != null) {
            if (booking.getEndTime().isBefore(booking.getStartTime()) || 
                booking.getEndTime().equals(booking.getStartTime())) {
                throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu");
            }
        }

        // Check for overlapping bookings
        List<Booking> overlappingBookings = bookingDao.findOverlappingBookings(
            booking.getStadiumId(),
            booking.getBookingDate(),
            booking.getStartTime(),
            booking.getEndTime()
        );

        if (!overlappingBookings.isEmpty()) {
            throw new IllegalStateException("Khung giờ này đã được đặt. Vui lòng chọn khung giờ khác.");
        }
        
        if (booking.getStatus() == null) {
            booking.setStatus(Booking.Status.PENDING);
        }
        
        if (booking.getCreatedAt() == null) {
            booking.setCreatedAt(java.time.LocalDateTime.now());
        }
        
        booking.setId(null);
        bookingDao.save(booking);
    }

    public List<Booking> getBookingsByStadiumAndDate(Long stadiumId, LocalDate date) {
        return bookingDao.findBookingsByStadiumAndDate(stadiumId, date);
    }

    public boolean isTimeSlotAvailable(Long stadiumId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<Booking> overlappingBookings = bookingDao.findOverlappingBookings(stadiumId, date, startTime, endTime);
        return overlappingBookings.isEmpty();
    }
}

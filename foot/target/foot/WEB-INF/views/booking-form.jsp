<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="container mt-4">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <!-- Stadium Info -->
            <div class="card mb-4 shadow-sm">
                <div class="card-header bg-success text-white">
                    <h4 class="mb-0">
                        <i class="fas fa-futbol"></i> Thông Tin Sân Bóng
                    </h4>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-4">
                            <c:choose>
                                <c:when test="${not empty stadium.imageUrl && stadium.imageUrl.startsWith('/')}" >
                                    <img src="${pageContext.request.contextPath}${stadium.imageUrl}" class="img-fluid rounded stadium-detail-img" alt="${stadium.name}">
                                </c:when>
                                <c:when test="${not empty stadium.imageUrl}">
                                    <img src="${stadium.imageUrl}" class="img-fluid rounded stadium-detail-img" alt="${stadium.name}">
                                </c:when>
                                <c:otherwise>
                                    <div class="stadium-placeholder d-flex align-items-center justify-content-center bg-light rounded">
                                        <i class="fas fa-futbol fa-3x text-muted"></i>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="col-md-8">
                            <h5 class="text-success mb-3">${stadium.name}</h5>
                            <p class="mb-2">
                                <i class="fas fa-map-marker-alt text-danger"></i>
                                <strong>Địa chỉ:</strong> ${stadium.address}
                            </p>
                            <p class="mb-2">
                                <i class="fas fa-money-bill-wave text-warning"></i>
                                <strong>Giá:</strong> <span class="text-success fw-bold"><fmt:formatNumber value="${stadium.pricePerHour}" pattern="#,###"/> VNĐ/giờ</span>
                            </p>
                            <p class="mb-0">
                                <i class="fas fa-info-circle text-info"></i>
                                <strong>Mô tả:</strong> ${stadium.description}
                            </p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Booking Form -->
            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white">
                    <h4 class="mb-0">
                        <i class="fas fa-calendar-plus"></i> Đặt Sân Bóng
                    </h4>
                </div>
                <div class="card-body">
                    <form:form action="${pageContext.request.contextPath}/stadiums/${stadium.id}/book" 
                               method="post" modelAttribute="booking" class="needs-validation" novalidate="true">
                        
                        <!-- Ensure ID is null for new booking -->
                        <form:hidden path="id" value=""/>
                        
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="bookingDate" class="form-label">
                                    <i class="fas fa-calendar-alt"></i> Ngày đặt sân <span class="text-danger">*</span>
                                </label>
                                <input type="date" class="form-control" id="bookingDate" name="bookingDate" 
                                       min="${minDate}" required>
                                <div class="invalid-feedback">
                                    Vui lòng chọn ngày đặt sân.
                                </div>
                            </div>
                            
                            <div class="col-md-3 mb-3">
                                <label for="startTime" class="form-label">
                                    <i class="fas fa-clock"></i> Giờ bắt đầu <span class="text-danger">*</span>
                                </label>
                                <select class="form-select" id="startTime" name="startTime" required>
                                    <option value="">Chọn giờ</option>
                                </select>
                                <div class="invalid-feedback">
                                    Vui lòng chọn giờ bắt đầu.
                                </div>
                            </div>
                            
                            <div class="col-md-3 mb-3">
                                <label for="endTime" class="form-label">
                                    <i class="fas fa-clock"></i> Giờ kết thúc <span class="text-danger">*</span>
                                </label>
                                <select class="form-select" id="endTime" name="endTime" required>
                                    <option value="">Chọn giờ</option>
                                </select>
                                <div class="invalid-feedback">
                                    Vui lòng chọn giờ kết thúc.
                                </div>
                            </div>
                        </div>

                        <div class="alert alert-info">
                            <i class="fas fa-info-circle"></i>
                            <strong>Lưu ý:</strong> Đơn đặt sân sẽ được gửi đến quản trị viên để xét duyệt. 
                            Bạn sẽ nhận được thông báo khi đơn được xử lý.
                        </div>

                        <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                            <a href="${pageContext.request.contextPath}/stadiums" class="btn btn-secondary me-md-2">
                                <i class="fas fa-arrow-left"></i> Quay lại
                            </a>
                            <button type="submit" class="btn btn-success">
                                <i class="fas fa-paper-plane"></i> Gửi Đơn Đặt Sân
                            </button>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
    </div>
</div>

<style>
.stadium-detail-img {
    width: 100%;
    height: 150px;
    object-fit: cover;
}

.stadium-placeholder {
    width: 100%;
    height: 150px;
}

.form-label {
    font-weight: 600;
    color: #495057;
}

.btn-success {
    background: linear-gradient(45deg, #28a745, #20c997);
    border: none;
    padding: 10px 20px;
    border-radius: 8px;
    font-weight: 500;
    transition: all 0.3s ease;
}

.btn-success:hover {
    background: linear-gradient(45deg, #218838, #1ea488);
    transform: translateY(-1px);
}

.btn-secondary {
    padding: 10px 20px;
    border-radius: 8px;
    font-weight: 500;
}

.card {
    border: none;
    border-radius: 12px;
}

.card-header {
    border-radius: 12px 12px 0 0 !important;
}

.fw-bold {
    font-weight: 700 !important;
}

.time-slot-unavailable {
    opacity: 0.6;
    background-color: #e9ecef;
    color: #6c757d;
    cursor: not-allowed;
}
</style>

<script>
document.addEventListener('DOMContentLoaded', function() {
    const today = new Date().toISOString().split('T')[0];
    const bookingDateInput = document.getElementById('bookingDate');
    const startTimeSelect = document.getElementById('startTime');
    const endTimeSelect = document.getElementById('endTime');
    const stadiumId = "${stadium.id}";
    
    bookingDateInput.setAttribute('min', today);
    
    // Available time slots
    const timeSlots = [
        "06:00", "07:00", "08:00", "09:00", "10:00", "11:00", "12:00",
        "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00",
        "20:00", "21:00", "22:00", "23:00"
    ];

    let bookedRanges = [];

    // Fetch booked time slots for the selected date
    async function fetchBookedTimeSlots(date) {
        try {
            const response = await fetch(`${pageContext.request.contextPath}/api/bookings/check-availability?stadiumId=${stadiumId}&date=${date}`);
            if (!response.ok) throw new Error('Network response was not ok');
            return await response.json();
        } catch (error) {
            console.error('Error fetching booked time slots:', error);
            return [];
        }
    }

    // Check if a time is within any booked range
    function isTimeBooked(time) {
        const t = timeToMinutes(time);
        return bookedRanges.some(range => t >= range.start && t < range.end);
    }

    // Convert HH:mm to minutes since midnight
    function timeToMinutes(t) {
        const [h, m] = t.split(":").map(Number);
        return h * 60 + (m || 0);
    }

    // Update start time options
function updateStartTimeOptions() {
    startTimeSelect.innerHTML = '<option value="">Chọn giờ</option>';
    const selectedDate = bookingDateInput.value;
    const now = new Date();
    const currentTimeStr = now.toTimeString().slice(0,5); // "HH:mm"
    const isToday = selectedDate === now.toISOString().slice(0,10);
    timeSlots.forEach(time => {
        const option = document.createElement('option');
        option.value = time;
        option.textContent = time;
        let shouldDisable = false;
        if (isTimeBooked(time)) {
            shouldDisable = true;
        } else if (isToday && time <= currentTimeStr) {
            shouldDisable = true;
        }
        if (shouldDisable) {
            option.disabled = true;
            option.classList.add('time-slot-unavailable');
        }
        startTimeSelect.appendChild(option);
    });
    endTimeSelect.innerHTML = '<option value="">Chọn giờ</option>';
    endTimeSelect.disabled = true;
}

    // Update end time options based on start time
    function updateEndTimeOptions() {
        const selectedStartTime = startTimeSelect.value;
        if (!selectedStartTime) {
            endTimeSelect.innerHTML = '<option value="">Chọn giờ</option>';
            endTimeSelect.disabled = true;
            return;
        }

        const startIndex = timeSlots.indexOf(selectedStartTime);
        endTimeSelect.innerHTML = '<option value="">Chọn giờ</option>';
        let foundValidEndTime = false;

        // Tìm giờ kết thúc hợp lệ
        for (let i = startIndex + 1; i < timeSlots.length; i++) {
            const currentTime = timeSlots[i];
            const option = document.createElement('option');
            option.value = currentTime;
            option.textContent = currentTime;
            
            if (isTimeBooked(currentTime)) {
                option.disabled = true;
                option.classList.add('time-slot-unavailable');
                break;
            } else {
                foundValidEndTime = true;
            }
            
            endTimeSelect.appendChild(option);
        }

        // Enable/disable dropdown giờ kết thúc
        endTimeSelect.disabled = !foundValidEndTime;
        if (!foundValidEndTime) {
            const option = document.createElement('option');
            option.value = "";
            option.textContent = "Không có khung giờ phù hợp";
            endTimeSelect.innerHTML = '';
            endTimeSelect.appendChild(option);
        }
    }

    // Khi chọn ngày, fetch các khung giờ đã đặt và cập nhật dropdown
    bookingDateInput.addEventListener('change', async function() {
        const selectedDate = bookingDateInput.value;
        if (!selectedDate) {
            startTimeSelect.innerHTML = '<option value="">Chọn giờ</option>';
            endTimeSelect.innerHTML = '<option value="">Chọn giờ</option>';
            startTimeSelect.disabled = true;
            endTimeSelect.disabled = true;
            return;
        }

        startTimeSelect.disabled = true;
        endTimeSelect.disabled = true;
        startTimeSelect.innerHTML = '<option value="">Đang tải...</option>';
        endTimeSelect.innerHTML = '<option value="">Đang tải...</option>';

        const bookedSlots = await fetchBookedTimeSlots(selectedDate);
        bookedRanges = bookedSlots.map(slot => ({
            start: timeToMinutes(slot.startTime),
            end: timeToMinutes(slot.endTime)
        }));

        startTimeSelect.disabled = false;
        updateStartTimeOptions();
    });

    // Tự động load bookings khi trang được load (nếu đã có ngày được chọn)
    if (bookingDateInput.value) {
        bookingDateInput.dispatchEvent(new Event('change'));
    }

    // Khi chọn giờ bắt đầu, cập nhật dropdown giờ kết thúc
    startTimeSelect.addEventListener('change', updateEndTimeOptions);

    // Form validation
    const form = document.querySelector('.needs-validation');
    form.addEventListener('submit', function(event) {
        if (!form.checkValidity()) {
            event.preventDefault();
            event.stopPropagation();
        }
        const startTime = startTimeSelect.value;
        const endTime = endTimeSelect.value;
        if (startTime && endTime && endTime <= startTime) {
            event.preventDefault();
            event.stopPropagation();
            alert('Giờ kết thúc phải sau giờ bắt đầu!');
            return false;
        }
        form.classList.add('was-validated');
    }, false);
});
</script>

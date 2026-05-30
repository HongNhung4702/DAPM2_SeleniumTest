<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="container mt-4">
    <!-- Header -->
    <div class="row mb-4">
        <div class="col-12">
            <div class="d-flex justify-content-between align-items-center">
                <h2 class="mb-0">
                    <i class="fas fa-history text-primary"></i> Lịch Sử Đặt Sân
                </h2>
                <a href="${pageContext.request.contextPath}/stadiums" class="btn btn-success">
                    <i class="fas fa-plus"></i> Đặt Sân Mới
                </a>
            </div>
        </div>
    </div>

    <!-- Success/Error Messages -->
    <c:if test="${not empty success}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle"></i> ${success}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle"></i> ${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <!-- Bookings Content -->
    <c:choose>
        <c:when test="${not empty bookings}">
            <!-- Bookings Table -->
            <div class="card">
                <div class="card-header bg-light">
                    <h5 class="mb-0">
                        <i class="fas fa-list"></i> Danh Sách Đặt Sân (${bookings.size()} đơn)
                    </h5>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>Mã đơn</th>
                                    <th>Ngày đặt</th>
                                    <th>Thời gian</th>
                                    <th>Sân</th>
                                    <th>Tổng tiền</th>
                                    <th>Trạng thái</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="booking" items="${bookings}">
                                    <tr>
                                        <td><span class="badge bg-secondary">#${booking.id}</span></td>
                                        <td><strong>${booking.formattedBookingDate}</strong></td>
                                        <td>${booking.formattedStartTime} - ${booking.formattedEndTime}</td>
                                        <td><span class="badge bg-info">${stadiumNames[booking.stadiumId]}</span></td>
                                        <td>
                                            <c:set var="pricePerHour" value="${stadiumPrices[booking.stadiumId]}" />
                                            <c:if test="${not empty pricePerHour}">
                                                <c:set var="durationHours" value="${booking.durationInHours}" />
                                                <c:set var="totalAmount" value="${pricePerHour * durationHours}" />
                                                <strong class="text-success"><fmt:formatNumber value="${totalAmount}" pattern="#,###"/> VNĐ</strong>
                                            </c:if>
                                        </td>
                                        <td><span class="badge bg-${booking.statusColorClass}">${booking.statusInVietnamese}</span></td>                                        <td>
                                            <c:if test="${booking.canBeCancelled()}">
                                                <button class="btn btn-outline-danger btn-sm cancel-btn" 
                                                        data-booking-id="${booking.id}"
                                                        data-booking-date="${booking.formattedBookingDate}"
                                                        data-booking-time="${booking.formattedStartTime}">
                                                    <i class="fas fa-times"></i> Hủy
                                                </button>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="text-center py-5">
                <i class="fas fa-calendar-times fa-5x text-muted mb-4"></i>
                <h4 class="text-muted mb-3">Chưa có lịch sử đặt sân</h4>
                <a href="${pageContext.request.contextPath}/stadiums" class="btn btn-success btn-lg">
                    <i class="fas fa-futbol"></i> Đặt Sân Ngay
                </a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- Cancel Modal -->
<div class="modal fade" id="cancelModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title">Xác nhận hủy đặt sân</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <p>Bạn có chắc chắn muốn hủy đặt sân này không?</p>
                <div class="alert alert-info">
                    <span id="cancelBookingInfo"></span>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Không</button>
                <form id="cancelForm" method="post" style="display: inline;">
                    <button type="submit" class="btn btn-danger">Có, hủy đặt sân</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
// Event delegation for cancel buttons
document.addEventListener('DOMContentLoaded', function() {
    document.addEventListener('click', function(e) {
        if (e.target.closest('.cancel-btn')) {
            const btn = e.target.closest('.cancel-btn');
            const bookingId = btn.dataset.bookingId;
            const date = btn.dataset.bookingDate;
            const time = btn.dataset.bookingTime;
            confirmCancel(bookingId, date, time);
        }
    });
});

function confirmCancel(bookingId, date, time) {
    document.getElementById('cancelBookingInfo').innerHTML = 
        'Ngày: <strong>' + date + '</strong><br>Giờ: <strong>' + time + '</strong>';
    document.getElementById('cancelForm').action = 
        '${pageContext.request.contextPath}/bookings/' + bookingId + '/cancel';
    new bootstrap.Modal(document.getElementById('cancelModal')).show();
}
</script>

<style>
.booking-card {
    transition: transform 0.2s ease-in-out;
    border: none;
}

.booking-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 20px rgba(0,0,0,0.15) !important;
}

.card-header {
    border-radius: 12px 12px 0 0 !important;
    border-bottom: none;
}

.card {
    border-radius: 12px;
    overflow: hidden;
}

.booking-details p {
    line-height: 1.6;
    font-size: 0.95em;
}

.booking-details strong {
    color: #495057;
}

.alert {
    border: none;
    border-radius: 8px;
}

.btn-success {
    background: linear-gradient(45deg, #28a745, #20c997);
    border: none;
    padding: 12px 24px;
    border-radius: 8px;
    font-weight: 500;
    transition: all 0.3s ease;
}

.btn-success:hover {
    background: linear-gradient(45deg, #218838, #1ea488);
    transform: translateY(-1px);
}

.btn-outline-primary {
    border-radius: 8px;
    padding: 10px 20px;
    font-weight: 500;
}

.fa-5x {
    font-size: 4rem !important;
}

@media (max-width: 768px) {
    .booking-card {
        margin-bottom: 1rem;
    }
}
</style>

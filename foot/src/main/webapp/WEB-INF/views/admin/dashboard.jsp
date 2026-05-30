<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container mt-4">
    <h2>Bảng Điều Khiển Quản Trị</h2>

    <div class="row">
        <!-- Thẻ Thống kê booking chờ xử lý -->
        <div class="col-md-6 mb-4">
            <div class="card border-info">
                <div class="card-header bg-info text-white">
                    <i class="fas fa-clock me-2"></i>Đặt sân chờ xử lý
                </div>
                <div class="card-body">
                    <h3>${pendingBookingsCount}</h3>
                    <p>đặt sân đang chờ phê duyệt</p>
                </div>
            </div>
        </div>
    </div>
</div>

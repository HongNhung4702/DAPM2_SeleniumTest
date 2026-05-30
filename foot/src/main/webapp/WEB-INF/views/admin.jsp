<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="row">
    <div class="col-12">
        <h2 class="mb-4">Bảng Điều Khiển Của Quản Trị Viên</h2>
        <p class="lead">Chào mừng, <strong>${username}</strong>! Bạn đang đăng nhập với quyền quản trị viên.</p>
    </div>
</div>

<!-- Quick Stats -->
<div class="row mb-4">
    <div class="col-md-3">
        <div class="card bg-info text-white">
            <div class="card-body">
                <div class="d-flex justify-content-between">
                    <div>
                        <h5 class="card-title">Đặt sân chờ xử lý</h5>
                        <h3>${pendingBookingsCount}</h3>
                    </div>
                    <div class="align-self-center">
                        <i class="fas fa-clock fa-2x"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Quick Actions -->
<div class="row">
    <div class="col-12">
        <h4 class="mb-3">Thao tác nhanh</h4>
    </div>
    <div class="col-md-6 mb-3">
        <div class="card h-100">
            <div class="card-body text-center">
                <i class="fas fa-futbol fa-3x text-success mb-3"></i>
                <h5 class="card-title">Quản lý sân bóng</h5>
                <p class="card-text">Thêm, chỉnh sửa và quản lý thông tin sân bóng.</p>
                <a href="<c:url value='/admin/stadiums'/>" class="btn btn-success">Quản lý sân bóng</a>
            </div>
        </div>
    </div>
    <div class="col-md-6 mb-3">
        <div class="card h-100">
            <div class="card-body text-center">
                <i class="fas fa-calendar-check fa-3x text-info mb-3"></i>
                <h5 class="card-title">Quản lý đặt sân</h5>
                <p class="card-text">Xem xét và phê duyệt yêu cầu đặt sân bóng.</p>
                <a href="<c:url value='/admin/bookings'/>" class="btn btn-info">Quản lý đặt sân</a>
            </div>
        </div>
    </div>
</div>

<!-- Font Awesome for icons -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
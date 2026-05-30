<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>${pageTitle}</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <!-- Custom Admin CSS -->
    <link rel="stylesheet" href="/css/admin-custom.css">
    <style>
        .sidebar {
            min-height: calc(100vh - 56px);
            background-color: #f8f9fa;
        }
        .sidebar .nav-link {
            color: #333;
            border-radius: 5px;
            margin-bottom: 5px;
        }
        .sidebar .nav-link:hover,
        .sidebar .nav-link.active {
            background-color: #007bff;
            color: white;
        }
        .main-content {
            min-height: calc(100vh - 56px);
        }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container-fluid">
        <a class="navbar-brand" href="<c:url value='/admin'/>">
            <i class="fas fa-futbol me-2"></i>Football Booking Admin
        </a>
        <div class="d-flex align-items-center">
                <span class="me-3 text-white">
                    <i class="fas fa-user-shield me-1"></i>Chào Mừng, ${username}
                </span>
            <a class="btn btn-outline-light btn-sm" href="<c:url value='/logout'/>" onclick="return confirm('Bạn có chắc chắn muốn đăng xuất không?');">
                <i class="fas fa-sign-out-alt me-1"></i>Đăng xuất
            </a>
        </div>
    </div>
</nav>

<div class="container-fluid">
    <div class="row">
        <!-- Sidebar -->
        <nav class="col-md-3 col-lg-2 d-md-block sidebar p-3">
            <div class="position-sticky">
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value='/admin'/>">
                            <i class="fas fa-tachometer-alt me-2"></i>Bảng điều khiển
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value='/admin/stadiums'/>">
                            <i class="fas fa-futbol me-2"></i>Sân bóng
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value='/admin/bookings'/>">
                            <i class="fas fa-calendar-check me-2"></i>Đặt sân
                        </a>
                    </li>
                </ul>
            </div>
        </nav>

        <!-- Main content -->
        <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4 main-content">
            <div class="pt-3">
                <!-- Flash Messages -->
                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle me-2"></i>${successMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-circle me-2"></i>${errorMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <!-- Page Content -->
                <jsp:include page="/WEB-INF/views/${contentPage}.jsp"/>
            </div>
        </main>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>${pageTitle}</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <!-- Custom Admin CSS -->
    <link rel="stylesheet" href="/css/admin-custom.css">
</head>
<body>
    <header class="bg-success text-white py-3 px-4">
        <div class="d-flex justify-content-between align-items-center">
            <h2 class="mb-0">
                <i class="fas fa-futbol"></i> FootballBooking
            </h2>
            <div class="d-flex align-items-center">
                <span class="me-3 text-white">
                    <i class="fas fa-user"></i> Xin chào, ${username}
                </span>
                <a class="btn btn-outline-light btn-sm" href="<c:url value='/logout'/>" onclick="return confirm('Bạn có chắc chắn muốn đăng xuất không?');">
                    <i class="fas fa-sign-out-alt"></i> Đăng xuất
                </a>
            </div>
        </div>
    </header>

    <!-- Navigation Menu -->
    <nav class="navbar navbar-expand-lg navbar-light bg-light border-bottom">
        <div class="container">
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link ${contentPage == 'stadiums' ? 'active' : ''}" href="${pageContext.request.contextPath}/stadiums">
                            <i class="fas fa-futbol"></i> Đặt sân
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${contentPage == 'my-bookings' ? 'active' : ''}" href="${pageContext.request.contextPath}/my-bookings">
                            <i class="fas fa-history"></i> Lịch sử đặt sân
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>


    <main class="container my-4">
        <jsp:include page="/WEB-INF/views/${contentPage}.jsp"/>
    </main>
    <footer class="bg-light pt-4 pb-3 mt-auto border-top">
        <div class="container">
            <div class="row">
                <div class="col-md-7 mb-3">
                    <h5 class="text-uppercase fw-bold mb-3">
                        <i class="fas fa-futbol text-success"></i> Giới thiệu
                    </h5>
                    <p class="text-muted">
                        FootballBooking là nền tảng đặt sân bóng đá hàng đầu, ra đời với sứ mệnh kết nối những người yêu thể thao. Chúng tôi cung cấp một hệ thống đặt sân tiện lợi, nhanh chóng, giúp bạn lan tỏa niềm đam mê sân cỏ.
                    </p>
                </div>
                <div class="col-md-5 mb-3">
                    <h5 class="text-uppercase fw-bold mb-3">Liên hệ</h5>
                    <ul class="list-unstyled text-muted">
                        <li class="mb-2"><i class="fas fa-map-marker-alt fa-fw me-2"></i>170 An Dương Vương, Quy Nhơn, Bình Định</li>
                        <li class="mb-2"><i class="fas fa-envelope fa-fw me-2"></i>bookingfootball@gmail.com</li>
                        <li class="mb-2"><i class="fas fa-phone fa-fw me-2"></i>0334455678</li>
                    </ul>
                </div>
            </div>
            <div class="text-center text-muted pt-2 mt-2 border-top">
                 <p class="mb-0">
                    <i class="fas fa-futbol text-success"></i> FootballBooking © 2025
                </p>
            </div>
        </div>
    </footer>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <style>
        .nav-link.active {
            color: #198754 !important;
            font-weight: 600;
        }
        
        .nav-link:hover {
            color: #198754;
        }
        
        body {
            display: flex;
            flex-direction: column;
            min-height: 100vh;
        }
        
        main {
            flex: 1;
        }
    </style>
</body>
</html>

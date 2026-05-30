<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Login</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
    .input-eye-wrapper {
        position: relative;
    }
    .input-eye-wrapper input[type="password"],
    .input-eye-wrapper input[type="text"] {
        padding-right: 2.5rem;
    }
    .input-eye-btn {
        position: absolute;
        top: 50%;
        right: 0.75rem;
        transform: translateY(-50%);
        border: none;
        background: transparent;
        box-shadow: none;
        outline: none;
        padding: 0;
        display: flex;
        align-items: center;
        cursor: pointer;
    }
    .input-eye-btn:focus {
        box-shadow: none;
        outline: none;
    }
    .input-eye-btn .fa-eye, .input-eye-btn .fa-eye-slash {
        color: #888;
        font-size: 1.1rem;
        transition: color 0.2s;
    }
    .input-eye-btn:hover .fa-eye,
    .input-eye-btn:hover .fa-eye-slash {
        color: #333;
    }
    </style>
</head>
<body class="bg-light">
    <div class="container mt-5">
        <div class="card shadow-sm mx-auto" style="max-width: 400px;">
            <div class="card-body">
                <h3 class="card-title text-center mb-4">Đăng nhập</h3>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>
                <c:if test="${not empty success}">
                    <div class="alert alert-success">${success}</div>
                </c:if>
                <form action="<c:url value='/login'/>" method="post">
                    <div class="mb-3">
                        <label for="username" class="form-label">Tên đăng nhập</label>
                        <input type="text" class="form-control" id="username" name="username" required title="Vui lòng nhập tên đăng nhập (bắt buộc)">
                        <div class="invalid-feedback">Vui lòng nhập tên đăng nhập!</div>
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Mật khẩu</label>
                        <div class="input-eye-wrapper">
                            <input type="password" class="form-control" id="password" name="password" required title="Vui lòng nhập mật khẩu (bắt buộc)">
                            <div class="invalid-feedback">Vui lòng nhập mật khẩu!</div>
                            <button type="button" class="input-eye-btn" tabindex="-1" onclick="togglePassword('password', this)">
                                <span class="fa fa-eye"></span>
                            </button>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary w-100">Đăng nhập</button>
                </form>
                <div class="mt-3 text-center">
                    <small>Chưa có tài khoản? <a href="<c:url value='/register'/>">Đăng ký ngay</a></small>
                </div>
            </div>
        </div>
    </div>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/js/all.min.js"></script>
    <script>
    function togglePassword(inputId, btn) {
        const input = document.getElementById(inputId);
        if (input.type === 'password') {
            input.type = 'text';
            btn.querySelector('span').classList.remove('fa-eye');
            btn.querySelector('span').classList.add('fa-eye-slash');
        } else {
            input.type = 'password';
            btn.querySelector('span').classList.remove('fa-eye-slash');
            btn.querySelector('span').classList.add('fa-eye');
        }
    }
    // Custom Vietnamese required message for login form
    document.querySelectorAll('form input[required]').forEach(input => {
        input.oninvalid = function(e) {
            switch (input.name) {
                case 'username':
                    input.setCustomValidity('Vui lòng nhập tên đăng nhập!');
                    break;
                case 'password':
                    input.setCustomValidity('Vui lòng nhập mật khẩu!');
                    break;
                default:
                    input.setCustomValidity('Vui lòng điền thông tin này!');
            }
        };
        input.oninput = function(e) {
            input.setCustomValidity('');
        };
    });
    </script>
</body>
</html>

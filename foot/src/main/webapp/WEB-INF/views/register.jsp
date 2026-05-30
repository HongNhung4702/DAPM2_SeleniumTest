<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Register</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .input-group .btn-eye {
            border: none;
            background: transparent;
            box-shadow: none;
            outline: none;
            padding: 0 0.75rem;
            display: flex;
            align-items: center;
            border-radius: 0 0.375rem 0.375rem 0;
        }
        .input-group .btn-eye:focus {
            box-shadow: none;
            outline: none;
        }
        .input-group .fa-eye, .input-group .fa-eye-slash {
            color: #888;
            font-size: 1.1rem;
            transition: color 0.2s;
        }
        .input-group .btn-eye:hover .fa-eye,
        .input-group .btn-eye:hover .fa-eye-slash {
            color: #333;
        }
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
        <div class="card shadow-sm mx-auto" style="max-width: 500px;">
            <div class="card-body">
                <h3 class="card-title text-center mb-4">Đăng ký tài khoản</h3>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>
                <form action="<c:url value='/register'/>" method="post" id="registerForm" onsubmit="return validateForm()">
                    <div class="mb-3">
                        <label for="username" class="form-label">Tên đăng nhập</label>
                        <input type="text" class="form-control" id="username" name="username" required pattern="^[a-zA-Z0-9_]{3,20}$" title="Vui lòng nhập tên đăng nhập (bắt buộc) từ 3-20 ký tự, chỉ chữ cái, số hoặc gạch dưới">
                        <div class="invalid-feedback">Vui lòng nhập tên đăng nhập hợp lệ (bắt buộc, 3-20 ký tự, chỉ chữ cái, số, gạch dưới)</div>
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Mật khẩu</label>
                        <div class="input-eye-wrapper">
                            <input type="password" class="form-control" id="password" name="password" required minlength="6" pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{6,}$" title="Vui lòng nhập mật khẩu (bắt buộc, tối thiểu 6 ký tự, gồm chữ hoa, chữ thường và số)">
                            <button type="button" class="input-eye-btn" tabindex="-1" onclick="togglePassword('password', this)">
                                <span class="fa fa-eye"></span>
                            </button>
                        </div>
                        <div class="invalid-feedback">Vui lòng nhập mật khẩu hợp lệ (tối thiểu 6 ký tự, gồm chữ hoa, chữ thường và số)</div>
                    </div>
                    <div class="mb-3">
                        <label for="fullName" class="form-label">Họ tên</label>
                        <input type="text" class="form-control" id="fullName" name="fullName" required minlength="2" maxlength="50" title="Vui lòng nhập họ tên (bắt buộc, 2-50 ký tự)">
                        <div class="invalid-feedback">Vui lòng nhập họ tên (bắt buộc, 2-50 ký tự)</div>
                    </div>
                    <div class="mb-3">
                        <label for="email" class="form-label">Email</label>
                        <input type="email" class="form-control" id="email" name="email" required maxlength="50" pattern="[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$" title="Vui lòng nhập địa chỉ email (bắt buộc)">
                        <div class="invalid-feedback">Vui lòng nhập địa chỉ email hợp lệ (bắt buộc)</div>
                    </div>
                    <div class="mb-3">
                        <label for="phone" class="form-label">Số điện thoại</label>
                        <input type="tel" class="form-control" id="phone" name="phone" required pattern="^0\d{9,10}$" title="Vui lòng nhập số điện thoại (bắt buộc 10-11 số và bắt đầu bằng 0)">
                        <div class="invalid-feedback">Vui lòng nhập số điện thoại hợp lệ (bắt buộc, 10-11 số, bắt đầu bằng 0)</div>
                    </div>
                    <div class="mb-3">
                        <label for="address" class="form-label">Địa chỉ</label>
                        <input type="text" class="form-control" id="address" name="address" required minlength="5" title="Vui lòng nhập địa chỉ (bắt buộc, ít nhất 5 ký tự)">
                        <div class="invalid-feedback">Vui lòng nhập địa chỉ (bắt buộc, ít nhất 5 ký tự)</div>
                    </div>
                    <button type="submit" class="btn btn-success w-100">Đăng ký</button>
                </form>
                <div class="mt-3 text-center">
                    <small>Đã có tài khoản? <a href="<c:url value='/login'/>">Đăng nhập ngay</a></small>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/js/all.min.js"></script>
    <script>
        // Add custom validation
        function validateForm() {
            const form = document.getElementById('registerForm');
            const inputs = form.querySelectorAll('input');
            let isValid = true;
            inputs.forEach(input => {
                if (!input.checkValidity()) {
                    input.classList.add('is-invalid');
                    isValid = false;
                } else {
                    input.classList.remove('is-invalid');
                }
            });
            return isValid;
        }
        // Real-time validation
        document.querySelectorAll('#registerForm input').forEach(input => {
            input.addEventListener('input', function() {
                if (this.checkValidity()) {
                    this.classList.remove('is-invalid');
                }
            });
        });
        // Custom Vietnamese required message for each field
        document.querySelectorAll('#registerForm input[required]').forEach(input => {
            input.oninvalid = function(e) {
                switch (input.name) {
                    case 'username':
                        input.setCustomValidity('Vui lòng nhập tên đăng nhập!');
                        break;
                    case 'password':
                        input.setCustomValidity('Vui lòng nhập mật khẩu!');
                        break;
                    case 'fullName':
                        input.setCustomValidity('Vui lòng nhập họ tên!');
                        break;
                    case 'email':
                        input.setCustomValidity('Vui lòng nhập email!');
                        break;
                    case 'phone':
                        input.setCustomValidity('Vui lòng nhập số điện thoại!');
                        break;
                    case 'address':
                        input.setCustomValidity('Vui lòng nhập địa chỉ!');
                        break;
                    default:
                        input.setCustomValidity('Vui lòng điền thông tin này!');
                }
            };
            input.oninput = function(e) {
                input.setCustomValidity('');
            };
        });
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
    </script>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container mt-4">
    <h3>Thông tin giao hàng</h3>
    <form action="<c:url value='/checkout/confirm'/>" method="post" id="checkoutForm" onsubmit="return validateCheckoutForm()">
        <!-- Giữ productId và quantity từ bước gọi GET /checkout -->
        <input type="hidden" name="productId" value="${product.id}" />
        <input type="hidden" name="quantity" value="${quantity}" />

        <div class="mb-3">
            <label for="shippingName" class="form-label">Họ &amp; Tên</label>
            <input type="text"
                   id="shippingName"
                   name="shippingName"
                   class="form-control"
                   placeholder="Nhập họ & tên người nhận"
                   required
                   minlength="2"
                   maxlength="50"
                   pattern="^[a-zA-ZÀ-ỹ\s]{2,50}$"
                   title="Họ tên phải từ 2-50 ký tự và chỉ chứa chữ cái" />
            <div class="invalid-feedback">Vui lòng nhập họ tên hợp lệ (2-50 ký tự, chỉ chứa chữ cái)</div>
        </div>

        <div class="mb-3">
            <label for="shippingPhone" class="form-label">Số điện thoại</label>
            <input type="tel"
                   id="shippingPhone"
                   name="shippingPhone"
                   class="form-control"
                   placeholder="Nhập số điện thoại người nhận"
                   required
                   pattern="^0\d{9,10}$"
                   title="Số điện thoại phải có 10-11 số và bắt đầu bằng số 0" />
            <div class="invalid-feedback">Vui lòng nhập số điện thoại hợp lệ (10-11 số, bắt đầu bằng số 0)</div>
        </div>

        <div class="mb-3">
            <label for="shippingAddress" class="form-label">Địa chỉ nhận hàng</label>
            <input type="text"
                   id="shippingAddress"
                   name="shippingAddress"
                   class="form-control"
                   placeholder="Nhập địa chỉ giao hàng"
                   required
                   minlength="10"
                   maxlength="200"
                   title="Địa chỉ phải từ 10-200 ký tự" />
            <div class="invalid-feedback">Vui lòng nhập địa chỉ hợp lệ (tối thiểu 10 ký tự)</div>
        </div>

        <div class="mb-3">
            <label for="size" class="form-label">Size (nếu có)</label>
            <input type="text"
                   id="size"
                   name="size"
                   class="form-control"
                   placeholder="Nhập size sản phẩm (ví dụ: S, M, L, XL, 39, 40...)"
                   pattern="^(S|M|L|XL|XXL|[3-4][0-9])$"
                   title="Size phải là một trong các giá trị: S, M, L, XL, XXL hoặc số từ 30-49" />
            <div class="invalid-feedback">Vui lòng nhập size hợp lệ (S, M, L, XL, XXL hoặc số từ 30-49)</div>
        </div>

        <button type="submit" class="btn btn-success">Xác nhận đặt hàng</button>
    </form>
</div>

<script>
    // Hàm kiểm tra form trước khi submit
    function validateCheckoutForm() {
        const form = document.getElementById('checkoutForm');
        const inputs = form.querySelectorAll('input:not([type="hidden"])');
        let isValid = true;

        inputs.forEach(input => {
            // Bỏ qua validate cho trường size nếu không có giá trị
            if (input.id === 'size' && !input.value) {
                input.classList.remove('is-invalid');
                return;
            }

            if (!input.checkValidity()) {
                input.classList.add('is-invalid');
                isValid = false;
            } else {
                input.classList.remove('is-invalid');
            }
        });

        return isValid;
    }

    // Kiểm tra real-time khi người dùng nhập
    document.querySelectorAll('#checkoutForm input').forEach(input => {
        input.addEventListener('input', function() {
            // Bỏ qua validate cho trường size nếu không có giá trị
            if (this.id === 'size' && !this.value) {
                this.classList.remove('is-invalid');
                return;
            }

            if (this.checkValidity()) {
                this.classList.remove('is-invalid');
            }
        });
    });
</script>

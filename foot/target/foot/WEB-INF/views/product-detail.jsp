<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="container mt-4">
    <div class="row">
        <div class="col-md-6">
            <img
                    src="<c:url value='${product.imageUrl}'/>"
                    class="img-fluid rounded"
                    alt="${product.name}"
            />
        </div>
        <div class="col-md-6">
            <h2>${product.name}</h2>
            <p>${product.description}</p>
            <p><strong>Giá:</strong> <span class="text-danger fw-bold"><fmt:formatNumber value="${product.price}" type="currency" currencySymbol="" minFractionDigits="0"/> VNĐ</span></p>
            <p><strong>Kho còn:</strong> ${product.stock}</p>

            <form action="<c:url value='/checkout'/>" method="get" class="mt-4">
                <input type="hidden" name="productId" value="${product.id}" />
                <div class="mb-3">
                    <label for="quantity" class="form-label">Số lượng</label>
                    <input
                            type="number"
                            id="quantity"
                            name="quantity"
                            class="form-control"
                            min="1"
                            max="${product.stock}"
                            value="1"
                            required
                    />
                </div>
                <button type="submit" class="btn btn-success">Đặt Hàng</button>
            </form>
        </div>
    </div>
</div>

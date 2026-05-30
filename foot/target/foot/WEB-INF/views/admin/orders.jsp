<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Header -->
<div class="row mb-4">
    <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
            <h2><i class="fas fa-box-open me-2"></i>Quản lý đơn hàng</h2>
        </div>
    </div>
</div>

<!-- Orders Table Card -->
<div class="card shadow-sm">
    <div class="card-header bg-light">
        <h5 class="mb-0">
            <i class="fas fa-list me-2"></i>Danh sách đơn hàng
        </h5>
        <form class="row g-2 mt-2" method="get" action="${pageContext.request.contextPath}/admin/orders">
            <div class="col-auto">
                <input type="number" min="1" max="31" class="form-control" name="day" placeholder="Ngày" value="${filterDay != null ? filterDay : ''}">
            </div>
            <div class="col-auto">
                <input type="number" min="1" max="12" class="form-control" name="month" placeholder="Tháng" value="${filterMonth != null ? filterMonth : ''}">
            </div>
            <div class="col-auto">
                <input type="number" min="2000" max="2100" class="form-control" name="year" placeholder="Năm" value="${filterYear != null ? filterYear : ''}">
            </div>
            <div class="col-auto">
                <button type="submit" class="btn btn-primary"><i class="fas fa-filter"></i> Lọc</button>
                <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-secondary">Đặt lại</a>
            </div>
        </form>
    </div>
    <div class="card-body p-0">
        <c:choose>
            <c:when test="${empty orders}">
                <div class="text-center py-5">
                    <i class="fas fa-box-open fa-3x text-muted mb-3"></i>
                    <h5 class="text-muted">Không tìm thấy đơn hàng.</h5>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead class="table-dark">
                            <tr>
                                <th>Mã đơn hàng</th>
                                <th>Khách hàng</th>
                                <th>Sản phẩm</th>
                                <th>Kích cỡ</th>
                                <th>Tổng tiền</th>
                                <th>Thanh toán</th>
                                <th>Trạng thái</th>
                                <th>Ngày tạo</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="order" items="${orders}">
                                <tr>
                                    <td><strong>#${order.orderId}</strong></td>
                                    <td>
                                        <div>
                                            <strong>${order.shippingName}</strong>
                                            <br><small class="text-muted">Người dùng: ${order.userName}</small>
                                        </div>
                                    </td>
                                    <td>
                                        <div>
                                            <strong>${order.productName}</strong>
                                            <br><small class="text-muted">Số lượng: ${order.quantity}</small>
                                        </div>
                                    </td>
                                    <td>${order.size}</td>
                                    <td>
                                        <span class="fw-bold text-success">
                                            <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="" minFractionDigits="0"/> VNĐ
                                        </span>
                                    </td>
                                    <td>
                                        <span class="badge bg-${order.paymentStatusBadgeClass}">
                                            ${order.paymentStatus}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="badge bg-${order.statusBadgeClass}">
                                            ${order.status}
                                        </span>
                                    </td>
                                    <td>${order.formattedCreatedAt}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

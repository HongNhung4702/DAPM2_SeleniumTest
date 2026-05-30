<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="row mb-4">
    <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
            <h2><i class="fas fa-calendar-check me-2"></i>Quản lý đặt sân</h2>
            <div class="d-flex gap-2">
                <!-- Status Filter -->
                <div class="dropdown">
                    <button class="btn btn-outline-secondary dropdown-toggle" type="button" id="statusFilter" 
                            data-bs-toggle="dropdown" aria-expanded="false">
                        <i class="fas fa-filter me-2"></i>
                        <c:choose>
                            <c:when test="${selectedStatus == 'PENDING'}">Chờ xử lý</c:when>
                            <c:when test="${selectedStatus == 'APPROVED'}">Đã phê duyệt</c:when>
                            <c:when test="${selectedStatus == 'REJECTED'}">Đã từ chối</c:when>
                            <c:otherwise>Tất cả đặt sân</c:otherwise>
                        </c:choose>
                    </button>
                    <ul class="dropdown-menu" aria-labelledby="statusFilter">
                        <li><a class="dropdown-item" href="<c:url value='/admin/bookings'/>">Tất cả đặt sân</a></li>
                        <li><a class="dropdown-item" href="<c:url value='/admin/bookings?status=PENDING'/>">Chờ xử lý</a></li>
                        <li><a class="dropdown-item" href="<c:url value='/admin/bookings?status=APPROVED'/>">Đã phê duyệt</a></li>
                        <li><a class="dropdown-item" href="<c:url value='/admin/bookings?status=REJECTED'/>">Đã từ chối</a></li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Bookings Table -->
<div class="card">
    <div class="card-header">
        <h5 class="mb-0">
            <i class="fas fa-list me-2"></i>Danh sách đặt sân
            <c:if test="${selectedStatus != null}">
                - Đặt sân <span class="text-capitalize">
                    <c:choose>
                        <c:when test="${selectedStatus == 'PENDING'}">chờ xử lý</c:when>
                        <c:when test="${selectedStatus == 'APPROVED'}">đã phê duyệt</c:when>
                        <c:when test="${selectedStatus == 'REJECTED'}">đã từ chối</c:when>
                        <c:otherwise>${selectedStatus.toLowerCase()}</c:otherwise>
                    </c:choose>
                </span>
            </c:if>
        </h5>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${empty bookings}">
                <div class="text-center py-4">
                    <i class="fas fa-calendar-times fa-3x text-muted mb-3"></i>
                    <h5 class="text-muted">Không tìm thấy đặt sân</h5>
                    <p class="text-muted">
                        <c:choose>
                            <c:when test="${selectedStatus != null}">
                                Không có đặt sân 
                                <c:choose>
                                    <c:when test="${selectedStatus == 'PENDING'}">chờ xử lý</c:when>
                                    <c:when test="${selectedStatus == 'APPROVED'}">đã phê duyệt</c:when>
                                    <c:when test="${selectedStatus == 'REJECTED'}">đã từ chối</c:when>
                                    <c:otherwise>${selectedStatus.toLowerCase()}</c:otherwise>
                                </c:choose>
                                nào.
                            </c:when>
                            <c:otherwise>
                                Chưa có đặt sân nào được tạo.
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead class="table-dark">
                            <tr>
                                <th>ID</th>
                                <th>Khách hàng</th>
                                <th>Sân bóng</th>
                                <th>Ngày</th>
                                <th>Giờ</th>
                                <th>Tổng tiền</th>
                                <th>Trạng thái</th>
                                <th>Ngày tạo</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="booking" items="${bookings}">
                                <tr>
                                    <td><strong>#${booking[0]}</strong></td>
                                    <td>
                                        <div>
                                            <strong>${booking[6]}</strong>
                                            <br><small class="text-muted">${booking[7]}</small>
                                        </div>
                                    </td>
                                    <td>
                                        <div>
                                            <strong>${booking[8]}</strong>
                                            <br><small class="text-muted">${booking[9]}</small>
                                        </div>
                                    </td>                                    <td>
                                        ${booking[1]}
                                    </td>
                                    <td>
                                        ${booking[2]} - ${booking[3]}
                                    </td>
                                    <td>
                                        <c:if test="${not empty booking[10] and not empty booking[2] and not empty booking[3]}">
                                            <c:set var="pricePerHour" value="${booking[10]}" />
                                            <c:set var="startTime" value="${booking[2]}" />
                                            <c:set var="endTime" value="${booking[3]}" />
                                            <%
                                                java.time.LocalTime startTime = (java.time.LocalTime) pageContext.getAttribute("startTime");
                                                java.time.LocalTime endTime = (java.time.LocalTime) pageContext.getAttribute("endTime");
                                                Double pricePerHour = (Double) pageContext.getAttribute("pricePerHour");
                                                if (startTime != null && endTime != null && pricePerHour != null) {
                                                    long startMinutes = startTime.toSecondOfDay() / 60;
                                                    long endMinutes = endTime.toSecondOfDay() / 60;
                                                    double durationHours = (endMinutes - startMinutes) / 60.0;
                                                    double totalAmount = pricePerHour * durationHours;
                                                    pageContext.setAttribute("totalAmount", totalAmount);
                                                }
                                            %>
                                            <c:if test="${not empty totalAmount}">
                                                <strong class="text-success"><fmt:formatNumber value="${totalAmount}" pattern="#,###"/> VNĐ</strong>
                                            </c:if>
                                        </c:if>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${booking[4] == 'PENDING'}">
                                                <span class="badge bg-warning text-dark">
                                                    <i class="fas fa-clock me-1"></i>Chờ xử lý
                                                </span>
                                            </c:when>
                                            <c:when test="${booking[4] == 'CANCELLED'}">
                                                <span class="badge bg-danger">
                                                    <i class="fas fa-times me-1"></i>Đã hủy
                                                </span>
                                            </c:when>
                                            <c:when test="${booking[4] == 'APPROVED'}">
                                                <span class="badge bg-success">
                                                    <i class="fas fa-check me-1"></i>Đã phê duyệt
                                                </span>
                                            </c:when>
                                            <c:when test="${booking[4] == 'REJECTED'}">
                                                <span class="badge bg-danger">
                                                    <i class="fas fa-times me-1"></i>Đã từ chối
                                                </span>
                                            </c:when>
                                        </c:choose>
                                    </td>                                    <td>
                                        <c:set var="dateTime" value="${booking[5].toString()}" />
                                        <c:choose>
                                            <c:when test="${dateTime.length() >= 19}">
                                                <c:set var="date" value="${dateTime.substring(0, 10)}" />
                                                <c:set var="time" value="${dateTime.substring(11, 19)}" />
                                                ${date.replace('-', '/')} ${time}
                                            </c:when>
                                            <c:when test="${dateTime.length() >= 16}">
                                                <c:set var="date" value="${dateTime.substring(0, 10)}" />
                                                <c:set var="time" value="${dateTime.substring(11)}" />
                                                ${date.replace('-', '/')} ${time}:00
                                            </c:when>
                                            <c:otherwise>
                                                ${dateTime}
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:if test="${booking[4] == 'PENDING'}">
                                            <div class="btn-group" role="group">
                                                <form action="<c:url value='/admin/bookings/approve/${booking[0]}'/>" 
                                                      method="post" style="display: inline; margin-right: 10px;">
                                                    <button type="submit" class="btn btn-sm btn-success" 
                                                            title="Phê duyệt đặt sân">
                                                        <i class="fas fa-check"></i>
                                                    </button>
                                                </form>
                                                <form action="<c:url value='/admin/bookings/reject/${booking[0]}'/>" 
                                                      method="post" style="display: inline;">
                                                    <button type="submit" class="btn btn-sm btn-danger" 
                                                            title="Từ chối đặt sân"
                                                            onclick="return confirm('Bạn có chắc chắn muốn từ chối đặt sân này?')">
                                                        <i class="fas fa-times"></i>
                                                    </button>
                                                </form>
                                            </div>
                                        </c:if>
                                        <c:if test="${booking[4] != 'PENDING'}">
                                            <span class="text-muted">Không có thao tác</span>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>


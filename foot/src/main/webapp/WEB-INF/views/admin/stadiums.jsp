<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h2>Quản lý sân bóng</h2>
    <a href="<c:url value='/admin/stadiums/add'/>" class="btn btn-primary">
        <i class="fas fa-plus me-2"></i>Thêm sân bóng mới
    </a>
</div>

<!-- Stadiums Table -->
<div class="table-responsive">
    <table class="table table-striped table-hover">
        <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>Hình ảnh</th>
                <th>Tên</th>
                <th>Địa chỉ</th>
                <th>Giá/Giờ</th>
                <th>Mô tả</th>
                <th>Ngày tạo</th>
                <th>Thao tác</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="stadium" items="${stadiums}">
                <tr>
                    <td>${stadium.id}</td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty stadium.imageUrl}">
                                <img src="${pageContext.request.contextPath}${stadium.imageUrl}" alt="${stadium.name}" class="img-thumbnail" style="width: 50px; height: 50px; object-fit: cover;">
                            </c:when>
                            <c:otherwise>
                                <div class="bg-secondary text-white d-flex align-items-center justify-content-center" style="width: 50px; height: 50px; font-size: 12px;">Không có ảnh</div>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td><strong>${stadium.name}</strong></td>
                    <td>${stadium.address}</td>
                    <td><fmt:formatNumber value="${stadium.pricePerHour}" type="currency" currencySymbol="₫"/></td>
                    <td>${stadium.description}</td>
                    <td>${stadium.formattedCreatedAt}</td>
                    <td>
                        <div class="btn-group" role="group">
                            <a href="<c:url value='/admin/stadiums/edit/${stadium.id}'/>" class="btn btn-sm btn-outline-primary" title="Edit">
                                <i class="fas fa-edit"></i>
                            </a>
                            <button type="button" class="btn btn-sm btn-outline-danger" onclick="confirmDelete('${stadium.id}', '${stadium.name}')" title="Delete">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<c:if test="${empty stadiums}">
    <div class="text-center py-5">
        <i class="fas fa-futbol fa-3x text-muted mb-3"></i>
        <h5 class="text-muted">Không tìm thấy sân bóng</h5>
        <p class="text-muted">Bắt đầu bằng cách thêm sân bóng đầu tiên của bạn.</p>
        <a href="<c:url value='/admin/stadiums/add'/>" class="btn btn-primary">
            <i class="fas fa-plus me-2"></i>Thêm sân bóng
        </a>
    </div>
</c:if>

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="deleteModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Xác nhận xóa</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <p>Bạn có chắc chắn muốn xóa sân bóng "<span id="stadiumName"></span>"?</p>
                <p class="text-danger">Hành động này không thể hoàn tác.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                <form id="deleteForm" method="post" style="display: inline;">
                    <button type="submit" class="btn btn-danger">Xóa</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
function confirmDelete(stadiumId, stadiumName) {
    document.getElementById('stadiumName').textContent = stadiumName;
    document.getElementById('deleteForm').action = '<c:url value="/admin/stadiums/delete/"/>' + stadiumId;
    new bootstrap.Modal(document.getElementById('deleteModal')).show();
}
</script> 
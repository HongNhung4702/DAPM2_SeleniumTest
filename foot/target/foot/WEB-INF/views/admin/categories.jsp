<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="d-flex justify-content-between align-items-center mb-4">
    <h2>Quản lý danh mục</h2>
    <a href="<c:url value='/admin/categories/add'/>" class="btn btn-primary">
        <i class="fas fa-plus me-2"></i>Thêm danh mục mới
    </a>
</div>

<!-- Categories Table -->
<div class="table-responsive">
    <table class="table table-striped table-hover">
        <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>Tên</th>
                <th>Mô tả</th>
                <th>Số sản phẩm</th>
                <th>Thao tác</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="category" items="${categories}">
                <tr>
                    <td>${category.id}</td>
                    <td>
                        <strong>${category.name}</strong>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty category.description}">
                                ${category.description}
                            </c:when>
                            <c:otherwise>
                                <span class="text-muted">Không có mô tả</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <span class="badge bg-info" id="productCount_${category.id}">
                            <!-- Product count will be populated by JavaScript -->
                            Đang tải...
                        </span>
                    </td>
                    <td>
                        <div class="btn-group" role="group">
                            <a href="<c:url value='/admin/categories/edit/${category.id}'/>" 
                               class="btn btn-sm btn-outline-primary" title="Edit">
                                <i class="fas fa-edit"></i>
                            </a>
                            <button type="button" class="btn btn-sm btn-outline-danger" 
                                    onclick="confirmDelete('${category.id}', '${category.name}')" 
                                    title="Delete" id="deleteBtn_${category.id}">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<c:if test="${empty categories}">
    <div class="text-center py-5">
        <i class="fas fa-tags fa-3x text-muted mb-3"></i>
        <h5 class="text-muted">Không tìm thấy danh mục</h5>
        <p class="text-muted">Bắt đầu bằng cách thêm danh mục đầu tiên của bạn.</p>
        <a href="<c:url value='/admin/categories/add'/>" class="btn btn-primary">
            <i class="fas fa-plus me-2"></i>Thêm danh mục
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
                <p>Bạn có chắc chắn muốn xóa danh mục "<span id="categoryName"></span>"?</p>
                <div id="deleteWarning" class="alert alert-warning" style="display: none;">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                    Danh mục này chứa sản phẩm. Vui lòng di chuyển hoặc xóa tất cả sản phẩm trước.
                </div>
                <p class="text-danger">Hành động này không thể hoàn tác.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                <form id="deleteForm" method="post" style="display: inline;">
                    <button type="submit" class="btn btn-danger" id="confirmDeleteBtn">Xóa</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
function confirmDelete(categoryId, categoryName) {
    document.getElementById('categoryName').textContent = categoryName;
    document.getElementById('deleteForm').action = '<c:url value="/admin/categories/delete/"/>' + categoryId;
    new bootstrap.Modal(document.getElementById('deleteModal')).show();
}
</script>

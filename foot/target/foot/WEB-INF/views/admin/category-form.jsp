<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="row justify-content-center">
    <div class="col-md-6">
        <div class="card">
            <div class="card-header">
                <h4 class="mb-0">
                    <c:choose>
                        <c:when test="${isEdit}">
                            <i class="fas fa-edit me-2"></i>Chỉnh sửa danh mục
                        </c:when>
                        <c:otherwise>
                            <i class="fas fa-plus me-2"></i>Thêm danh mục mới
                        </c:otherwise>
                    </c:choose>
                </h4>
            </div>
            <div class="card-body">
                <form action="<c:url value='/admin/categories/save'/>" method="post">
                    <c:if test="${isEdit}">
                        <input type="hidden" name="id" value="${category.id}">
                    </c:if>
                    
                    <div class="mb-3">
                        <label for="name" class="form-label">Tên danh mục <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="name" name="name" 
                               value="${category.name}" required maxlength="100"
                               placeholder="Ví dụ: Giày bóng đá, Bóng, Áo đấu">
                    </div>

                    <div class="mb-3">
                        <label for="description" class="form-label">Mô tả</label>
                        <textarea class="form-control" id="description" name="description" 
                                  rows="3" maxlength="500"
                                  placeholder="Mô tả tùy chọn cho danh mục này">${category.description}</textarea>
                        <div class="form-text">Mô tả danh mục tùy chọn (tối đa 500 ký tự)</div>
                    </div>

                    <div class="d-flex justify-content-between">
                        <a href="<c:url value='/admin/categories'/>" class="btn btn-secondary">
                            <i class="fas fa-arrow-left me-2"></i>Quay lại danh mục
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <c:choose>
                                <c:when test="${isEdit}">
                                    <i class="fas fa-save me-2"></i>Cập nhật danh mục
                                </c:when>
                                <c:otherwise>
                                    <i class="fas fa-plus me-2"></i>Thêm danh mục
                                </c:otherwise>
                            </c:choose>
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Common Categories Suggestions (only for new categories) -->
        <c:if test="${not isEdit}">
            <div class="card mt-4">
                <div class="card-header">
                    <h6 class="mb-0">
                        <i class="fas fa-lightbulb me-2"></i>Danh mục thiết bị bóng đá phổ biến
                    </h6>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-6">
                            <ul class="list-unstyled">
                                <li><a href="#" onclick="fillCategory('Giày bóng đá', 'Giày bóng đá chuyên nghiệp và nghiệp dư')">Giày bóng đá</a></li>
                                <li><a href="#" onclick="fillCategory('Bóng đá', 'Bóng đá chính thức và tập luyện')">Bóng đá</a></li>
                                <li><a href="#" onclick="fillCategory('Áo đấu', 'Áo đấu đội và áo tập luyện')">Áo đấu</a></li>
                                <li><a href="#" onclick="fillCategory('Quần short', 'Quần short bóng đá và quần tập luyện')">Quần short</a></li>
                            </ul>
                        </div>
                        <div class="col-md-6">
                            <ul class="list-unstyled">
                                <li><a href="#" onclick="fillCategory('Tất', 'Tất bóng đá và vớ')">Tất</a></li>
                                <li><a href="#" onclick="fillCategory('Bảo vệ ống chân', 'Bảo vệ ống chân')">Bảo vệ ống chân</a></li>
                                <li><a href="#" onclick="fillCategory('Găng tay', 'Găng tay thủ môn')">Găng tay</a></li>
                                <li><a href="#" onclick="fillCategory('Phụ kiện', 'Thiết bị tập luyện và phụ kiện')">Phụ kiện</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>
    </div>
</div>

<script>
function fillCategory(name, description) {
    document.getElementById('name').value = name;
    document.getElementById('description').value = description;
    event.preventDefault();
}

// Character counter for description
document.getElementById('description').addEventListener('input', function() {
    const maxLength = 500;
    const currentLength = this.value.length;
    const remaining = maxLength - currentLength;
    
    let counter = document.getElementById('descriptionCounter');
    if (!counter) {
        counter = document.createElement('div');
        counter.id = 'descriptionCounter';
        counter.className = 'form-text';
        this.parentNode.appendChild(counter);
    }
    
    counter.textContent = 'Còn lại ' + remaining + ' ký tự';
    counter.className = remaining < 0 ? 'form-text text-danger' : 'form-text text-muted';
});
</script>

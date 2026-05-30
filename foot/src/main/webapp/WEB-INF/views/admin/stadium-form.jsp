<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="row justify-content-center">
    <div class="col-md-8">
        <h2>${isEdit ? 'Chỉnh sửa sân bóng' : 'Thêm sân bóng'}</h2>
        <form action="<c:url value='/admin/stadiums/save'/>" method="post" enctype="multipart/form-data">
            <input type="hidden" name="id" value="${stadium.id}"/>
            <div class="mb-3">
                <label for="name" class="form-label">Tên sân bóng</label>
                <input type="text" class="form-control" id="name" name="name" value="${stadium.name}" required>
            </div>
            <div class="mb-3">
                <label for="address" class="form-label">Địa chỉ</label>
                <input type="text" class="form-control" id="address" name="address" value="${stadium.address}" required>
            </div>
            <div class="mb-3">
                <label for="pricePerHour" class="form-label">Giá mỗi giờ (VNĐ)</label>
                <input type="number" class="form-control" id="pricePerHour" name="pricePerHour" value="${stadium.pricePerHour}" min="0" step="1000" required>
            </div>
            <div class="mb-3">
                <label for="fieldType" class="form-label">Loại sân</label>
                <select class="form-select" id="fieldType" name="fieldType" required>
                    <option value="" disabled ${stadium.fieldType == null ? 'selected' : ''}>-- Chọn loại sân --</option>
                    <c:forEach var="type" items="${fieldTypes}">
                        <option value="${type.name()}" ${stadium.fieldType == type ? 'selected' : ''}>${type}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="mb-3">
                <label for="description" class="form-label">Mô tả</label>
                <textarea class="form-control" id="description" name="description" rows="3">${stadium.description}</textarea>
            </div>
            <div class="mb-3">
                <label for="imageFile" class="form-label">Hình ảnh</label>
                <input type="file" class="form-control" id="imageFile" name="imageFile" accept="image/*">
                <c:if test="${isEdit && not empty stadium.imageUrl}">
                    <div class="mt-2">
                        <img src="${pageContext.request.contextPath}${stadium.imageUrl}" alt="Ảnh hiện tại" style="max-width: 200px; max-height: 150px;">
                        <input type="hidden" name="existingImageUrl" value="${stadium.imageUrl}"/>
                    </div>
                </c:if>
            </div>
            <div class="d-flex justify-content-between">
                <a href="<c:url value='/admin/stadiums'/>" class="btn btn-secondary">Hủy</a>
                <button type="submit" class="btn btn-success">${isEdit ? 'Cập nhật' : 'Thêm'} sân bóng</button>
            </div>
        </form>
    </div>
</div> 
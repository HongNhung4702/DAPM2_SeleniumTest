<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="container mt-4">
    <div class="row">
        <!-- Cột trái: filter danh mục -->
        <div class="col-md-3 mb-4">
            <div class="card shadow-sm">
                <div class="card-header bg-white">
                    <h5 class="mb-0">Danh mục</h5>
                </div>
                <ul class="list-group list-group-flush">
                    <li class="list-group-item d-flex align-items-center justify-content-between px-3 py-2 ${selectedCategoryId == null ? 'active bg-primary text-white' : ''}">
                        <a href="<c:url value='/home'/>" class="flex-grow-1">Tất cả</a>
                    </li>
                    <c:forEach var="cat" items="${categories}">
                        <li class="list-group-item d-flex align-items-center justify-content-between px-3 py-2 ${cat.id == selectedCategoryId ? 'active bg-primary text-white' : ''}">
                            <a href="<c:url value='/home?categoryId=${cat.id}'/>" class="flex-grow-1">${cat.name}</a>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </div>

        <!-- Cột phải: danh sách sản phẩm -->
        <div class="col-md-9">
            <h2 class="mb-3">Sản phẩm</h2>
            <!-- Form tìm kiếm sản phẩm -->
            <form class="mb-4" method="get" action="home">
                <div class="input-group">
                    <input type="text" class="form-control" name="search" placeholder="Tìm kiếm sản phẩm theo tên..." value="${search != null ? search : ''}" />
                    <button class="btn btn-primary" type="submit">Tìm kiếm</button>
                </div>
            </form>
            <div class="row">
                <c:forEach var="product" items="${products}">
                    <div class="col-lg-4 col-md-6 mb-4">
                        <div class="card h-100">
                            <img
                                    src="${pageContext.request.contextPath}${product.imageUrl}"
                                    class="card-img-top"
                                    alt="${product.name}"
                                    style="object-fit: cover; height: 180px;"
                            />
                            <div class="card-body d-flex flex-column">
                                <h5 class="card-title">${product.name}</h5>
                                <p class="card-text mb-2">
                                        ${fn:substring(product.description, 0, 50)}...
                                </p>
                                <p class="mt-auto fw-bold text-danger"><fmt:formatNumber value="${product.price}" type="currency" currencySymbol="" minFractionDigits="0"/> VNĐ</p>
                                <a
                                        href="<c:url value='/product/${product.id}'/>"
                                        class="btn btn-success btn-block"
                                >
                                    Chi tiết
                                </a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
                <c:if test="${empty products}">
                    <div class="col-12">
                        <p class="text-muted">Không có sản phẩm nào.</p>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>

<style>
    /* Link trong filter kế thừa màu, không gạch chân */
    .list-group-item a {
        color: inherit;
        text-decoration: none;
    }
    /* Hover nền sáng hơn */
    .list-group-item:hover {
        background-color: #f8f9fa;
    }
    /* Link khi active */
    .list-group-item.active a {
        color: #fff;
        text-decoration: none;
    }
</style>

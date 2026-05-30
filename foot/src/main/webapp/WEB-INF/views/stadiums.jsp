<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="container mt-4">
    <!-- Filter Bar -->
    <div class="row mb-4">
        <div class="col-12">
            <div class="filter-bar d-flex gap-3 flex-wrap">
                <select id="areaFilter" class="form-select w-auto" style="max-width: 200px;" onchange="updateStadiums()">
                    <option value="">Khu vực</option>
                    <c:forEach var="area" items="${uniqueAreas}">
                        <option value="${area}">${area}</option>
                    </c:forEach>
                </select>
                <select id="stadiumFilter" class="form-select w-auto" style="max-width: 250px;" disabled>
                    <option value="">Chọn tên sân</option>
                </select>
                <select id="fieldTypeFilter" class="form-select w-auto" style="max-width: 200px;">
                    <option value="">Chọn loại sân</option>
                    <c:forEach var="fieldType" items="${uniqueFieldType}">
                        <option value="${fieldType}">${fieldType}</option>
                    </c:forEach>
                </select>
                <button class="btn btn-warning text-white" onclick="filterStadiums()">Tìm Kiếm Nhanh</button>
            </div>
        </div>
    </div>
    <c:if test="${not empty success}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="fas fa-check-circle"></i> ${success}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle"></i> ${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <div class="row">
        <!-- Sidebar for Areas -->
        <div class="col-md-3">            <div class="card p-3 mb-4">
                <h5>Khu vực</h5>
                <ul class="list-group">
                    <c:forEach var="area" items="${uniqueAreas}">
                        <li class="list-group-item d-flex justify-content-between align-items-center area-item" 
                            onclick="filterByArea('${area}')" style="cursor: pointer;">
                            ${area}
                            <span class="badge bg-primary rounded-pill">
                                <c:set var="count" value="0"/>
                                <c:forEach var="stadium" items="${stadiums}">
                                    <c:if test="${stadium.area == area}">
                                        <c:set var="count" value="${count + 1}"/>
                                    </c:if>
                                </c:forEach>
                                ${count}
                            </span>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </div>

        <!-- Stadium List -->
        <div class="col-md-9">
            <h2>Danh sách sân bóng</h2>
            <c:if test="${empty stadiums}">
                <p>Chưa có sân bóng nào được thêm.</p>
            </c:if>
            <c:if test="${not empty stadiums}">
                <div id="stadiumList" class="row">
                    <c:forEach var="stadium" items="${stadiums}">
                        <div class="col-md-4 mb-4">
                            <div class="card h-100">
                                <c:choose>
                                    <c:when test="${stadium.imageUrl != null && stadium.imageUrl.startsWith('/')}" >
                                        <img src="${pageContext.request.contextPath}${stadium.imageUrl}" class="card-img-top" alt="${stadium.name}" style="height: 150px; object-fit: cover;">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${stadium.imageUrl}" class="card-img-top" alt="${stadium.name}" style="height: 150px; object-fit: cover;">
                                    </c:otherwise>
                                </c:choose>
                                <div class="card-body">
                                    <h5 class="card-title">${stadium.name}</h5>
                                    <p class="card-text">
                                        <i class="fas fa-money-bill-wave me-2"></i> <fmt:formatNumber value="${stadium.pricePerHour}" type="currency" currencySymbol="" minFractionDigits="0"/> VNĐ/giờ
                                    </p>
                                    <p class="card-text">
                                        <i class="fas fa-map-marker-alt me-2"></i> ${stadium.address}
                                    </p>
                                </div>
                                <div class="card-footer bg-transparent border-0">
                                    <a href="<c:url value='/stadiums/${stadium.id}/book'/>" class="btn btn-success">Chi tiết</a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:if>
        </div>
    </div>
</div>

<!-- Bootstrap Icons and JavaScript -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
    // Stadium data for JavaScript filtering
    var stadiumsData = [
        <c:forEach var="stadium" items="${stadiums}" varStatus="status">
        {
            id: '${stadium.id}',
            name: '${stadium.name}',
            area: '${stadium.area}',
            fieldType: '${stadium.fieldType.toString()}',
            pricePerHour: '${stadium.pricePerHour}',
            address: '${stadium.address}',
            imageUrl: '${stadium.imageUrl}'
        }<c:if test="${!status.last}">,</c:if>
        </c:forEach>
    ];
    function updateStadiums() {
        var selectedArea = $("#areaFilter").val();
        var $stadiumFilter = $("#stadiumFilter");
        $stadiumFilter.empty().append("<option value=''>Chọn tên sân</option>").prop("disabled", true);

        if (selectedArea) {
            $stadiumFilter.prop("disabled", false);
            stadiumsData.forEach(function(stadium) {
                if (stadium.area === selectedArea) {
                    $stadiumFilter.append("<option value='" + stadium.name + "'>" + stadium.name + "</option>");
                }
            });
        }
    }

    function filterByArea(area) {
        $("#areaFilter").val(area);
        updateStadiums();
        filterStadiums();
        
        // Highlight selected area
        $(".area-item").removeClass("active");
        $(".area-item").filter(function() {
            return $(this).text().trim().indexOf(area) === 0;
        }).addClass("active");
    }

    function filterStadiums() {
        var area = $("#areaFilter").val();
        var stadium = $("#stadiumFilter").val();
        var fieldType = $("#fieldTypeFilter").val();
        var $stadiumList = $("#stadiumList");

        $stadiumList.empty();
        
        stadiumsData.forEach(function(stadiumData) {
            var matchArea = !area || stadiumData.area.trim() === area.trim();
            var matchStadium = !stadium || stadiumData.name.trim() === stadium.trim();
            var matchFieldType = !fieldType || stadiumData.fieldType.trim() === fieldType.trim();

            if (matchArea && matchStadium && matchFieldType) {
                var imgSrc = stadiumData.imageUrl && stadiumData.imageUrl.startsWith('/') ? '${pageContext.request.contextPath}' + stadiumData.imageUrl : stadiumData.imageUrl;

                $stadiumList.append(
                    '<div class="col-md-4 mb-4">' +
                    '<div class="card h-100">' +
                    '<img src="' + imgSrc + '" class="card-img-top" alt="' + stadiumData.name + '" style="height: 150px; object-fit: cover;">' +
                    '<div class="card-body">' +
                    '<h5 class="card-title">' + stadiumData.name + '</h5>' +
                    '<p class="card-text">' +
                    '<i class="fas fa-money-bill-wave me-2"></i> ' + stadiumData.pricePerHour + ' VNĐ/giờ' +
                    '</p>' +
                    '<p class="card-text">' +
                    '<i class="fas fa-map-marker-alt me-2"></i> ' + stadiumData.address +
                    '</p>' +
                    '</div>' +
                    '<div class="card-footer bg-transparent border-0">' +
                    '<a href="${pageContext.request.contextPath}/stadiums/' + stadiumData.id + '/book" class="btn btn-success">Chi tiết</a>' +
                    '</div>' +
                    '</div>' +
                    '</div>'
                );
            }
        });
    }
</script>

<style>
    .filter-bar {
        background-color: #f8f9fa;
        padding: 15px;
        border-radius: 8px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }
    .card {
        transition: transform 0.2s;
    }
    .card:hover {
        transform: translateY(-5px);
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
    }    .list-group-item {
        border: none;
        padding: 10px 0;
        transition: background-color 0.3s;
    }
    .list-group-item:hover {
        background-color: #f8f9fa;
    }
    .list-group-item.active {
        background-color: #007bff;
        color: white;
    }
    .list-group-item.active .badge {
        background-color: white !important;
        color: #007bff;
    }
    .area-item:hover {
        background-color: #e9ecef;
    }
    .badge {
        background-color: #007bff !important;
    }
    .btn-primary {
        background-color: #0055A4;
        border: none;
    }
    .btn-warning {
        background-color: #ffd700;
        color: #000;
    }
    .btn-success {
        background-color: #28a745;
        border: none;
        color: white;
    }
    .card-body {
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        height: 100%;
    }
    .text-center {
        margin-top: auto;
        padding-top: 10px;
    }
</style>

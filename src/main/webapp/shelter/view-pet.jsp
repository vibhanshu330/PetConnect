<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${pet.name} - PetConnect</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/petconnect.css" rel="stylesheet">
</head>
<body class="bg-light pc-app">

<nav class="navbar navbar-dark bg-success">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/shelter/dashboard.jsp">🐾 PetConnect — Shelter</a>
        <form method="post" action="${pageContext.request.contextPath}/logout" class="d-inline"><%@ include file="/common/csrf.jspf" %><button type="submit" class="btn btn-outline-light btn-sm">Logout</button></form>
    </div>
</nav>

<div class="container py-4" style="max-width: 640px;">
    <a href="${pageContext.request.contextPath}/shelter/my-pets" class="btn btn-sm btn-outline-secondary mb-3">&larr; Back to My Pets</a>

    <div class="card shadow-sm">
        <c:if test="${not empty pet.imagePath}">
            <img src="${pageContext.request.contextPath}/${pet.imagePath}" class="card-img-top" style="max-height:300px; object-fit:cover;" onerror="this.hidden=true; this.nextElementSibling.hidden=false;">
            <div class="bg-secondary bg-opacity-10 text-center py-5 text-muted" hidden>Photo unavailable</div>
        </c:if>
        <div class="card-body">
            <h3>${pet.name}
                <c:choose>
                    <c:when test="${pet.status == 'PENDING'}"><span class="badge bg-warning text-dark">PENDING</span></c:when>
                    <c:when test="${pet.status == 'AVAILABLE'}"><span class="badge bg-success">AVAILABLE</span></c:when>
                    <c:when test="${pet.status == 'REJECTED'}"><span class="badge bg-danger">REJECTED</span></c:when>
                    <c:when test="${pet.status == 'ADOPTED'}"><span class="badge bg-secondary">ADOPTED</span></c:when>
                </c:choose>
            </h3>
            <p><strong>Type:</strong> ${pet.type} &nbsp; <strong>Breed:</strong> ${pet.breed}</p>
            <p><strong>Age:</strong> ${pet.age} years &nbsp; <strong>Gender:</strong> ${pet.gender}</p>
            <p><strong>Location:</strong> ${pet.location}</p>
            <p>${pet.description}</p>
            <a href="${pageContext.request.contextPath}/shelter/edit-pet?id=${pet.petId}" class="btn btn-outline-secondary">Edit</a>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jspf" %>
</body>
</html>

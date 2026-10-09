<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My Pets - PetConnect</title>
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

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3>My Pets</h3>
        <a href="${pageContext.request.contextPath}/shelter/add-pet" class="btn btn-success">+ Add New Pet</a>
    </div>

    <c:if test="${param.added == 'true'}"><div class="alert alert-success">Pet listing created — pending admin approval.</div></c:if>
    <c:if test="${param.updated == 'true'}"><div class="alert alert-success">Pet updated successfully.</div></c:if>
    <c:if test="${param.deleted == 'true'}"><div class="alert alert-success">Pet deleted.</div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>

    <c:choose>
        <c:when test="${empty pets}">
            <div class="alert alert-info">You haven't listed any pets yet.</div>
        </c:when>
        <c:otherwise>
            <table class="table table-bordered bg-white align-middle">
                <thead class="table-success">
                <tr>
                    <th>Photo</th>
                    <th>Name</th>
                    <th>Type</th>
                    <th>Breed</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="pet" items="${pets}">
                    <tr>
                        <td style="width:80px;">
                            <c:choose>
                                <c:when test="${not empty pet.imagePath}">
                                    <img src="${pageContext.request.contextPath}/${pet.imagePath}"
                                         alt="${pet.name}" width="60" height="60" style="object-fit:cover;"
                                         onerror="this.hidden=true; this.nextElementSibling.hidden=false;">
                                    <span class="text-muted small" hidden>Photo unavailable</span>
                                </c:when>
                                <c:otherwise><span class="text-muted">No photo</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td>${pet.name}</td>
                        <td>${pet.type}</td>
                        <td>${pet.breed}</td>
                        <td>
                            <c:choose>
                                <c:when test="${pet.status == 'PENDING'}"><span class="badge bg-warning text-dark">PENDING</span></c:when>
                                <c:when test="${pet.status == 'AVAILABLE'}"><span class="badge bg-success">AVAILABLE</span></c:when>
                                <c:when test="${pet.status == 'REJECTED'}"><span class="badge bg-danger">REJECTED</span></c:when>
                                <c:when test="${pet.status == 'ADOPTED'}"><span class="badge bg-secondary">ADOPTED</span></c:when>
                            </c:choose>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/shelter/view-pet?id=${pet.petId}" class="btn btn-sm btn-outline-primary">View</a>
                            <a href="${pageContext.request.contextPath}/shelter/edit-pet?id=${pet.petId}" class="btn btn-sm btn-outline-secondary">Edit</a>
                            <form action="${pageContext.request.contextPath}/shelter/delete-pet" method="post" style="display:inline;"
                                  onsubmit="return confirm('Delete ${pet.name}? This cannot be undone.');"><%@ include file="/common/csrf.jspf" %>
                                <input type="hidden" name="petId" value="${pet.petId}">
                                <button type="submit" class="btn btn-sm btn-outline-danger">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="/common/footer.jspf" %>
</body>
</html>

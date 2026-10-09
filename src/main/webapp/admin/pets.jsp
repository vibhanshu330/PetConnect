<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Manage Pets - PetConnect Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/petconnect.css" rel="stylesheet">
</head>
<body class="bg-light pc-app">

<nav class="navbar navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/admin/dashboard.jsp">🐾 PetConnect — Admin</a>
        <div>
            <a href="${pageContext.request.contextPath}/admin/dashboard.jsp" class="btn btn-outline-light btn-sm me-2">Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline-light btn-sm me-2">Manage Users</a>
            <a href="${pageContext.request.contextPath}/admin/pending-pets" class="btn btn-outline-light btn-sm me-2">Pending Approvals</a>
            <form method="post" action="${pageContext.request.contextPath}/logout" class="d-inline"><%@ include file="/common/csrf.jspf" %><button type="submit" class="btn btn-outline-light btn-sm">Logout</button></form>
        </div>
    </div>
</nav>

<div class="container py-4">
    <h3 class="mb-4">Manage Pets</h3>
    <p class="text-muted small">
        Approve/reject decisions for PENDING pets happen on the
        <a href="${pageContext.request.contextPath}/admin/pending-pets">Pending Approvals</a> page.
        This view is for platform-wide oversight of every listing, at any status.
    </p>

    <c:if test="${param.deleted == 'true'}"><div class="alert alert-success">Pet deleted.</div></c:if>
    <c:if test="${param.error == 'delete_failed'}"><div class="alert alert-danger">Could not delete that pet. Please try again.</div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>

    <c:choose>
        <c:when test="${empty pets}">
            <div class="alert alert-info">No pets found.</div>
        </c:when>
        <c:otherwise>
            <table class="table table-bordered bg-white align-middle">
                <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Type</th>
                    <th>Breed</th>
                    <th>Shelter ID</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="pet" items="${pets}">
                    <tr>
                        <td>${pet.petId}</td>
                        <td>${pet.name}</td>
                        <td>${pet.type}</td>
                        <td>${pet.breed}</td>
                        <td>${pet.shelterId}</td>
                        <td>
                            <c:choose>
                                <c:when test="${pet.status == 'PENDING'}"><span class="badge bg-warning text-dark">PENDING</span></c:when>
                                <c:when test="${pet.status == 'AVAILABLE'}"><span class="badge bg-success">AVAILABLE</span></c:when>
                                <c:when test="${pet.status == 'REJECTED'}"><span class="badge bg-danger">REJECTED</span></c:when>
                                <c:when test="${pet.status == 'ADOPTED'}"><span class="badge bg-secondary">ADOPTED</span></c:when>
                            </c:choose>
                        </td>
                        <td>
                            <form action="${pageContext.request.contextPath}/admin/delete-pet" method="post" style="display:inline;"
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

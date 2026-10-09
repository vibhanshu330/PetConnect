<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Manage Users - PetConnect Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/petconnect.css" rel="stylesheet">
</head>
<body class="bg-light pc-app">

<nav class="navbar navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/admin/dashboard.jsp">🐾 PetConnect — Admin</a>
        <div>
            <a href="${pageContext.request.contextPath}/admin/dashboard.jsp" class="btn btn-outline-light btn-sm me-2">Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/pets" class="btn btn-outline-light btn-sm me-2">Manage Pets</a>
            <a href="${pageContext.request.contextPath}/admin/pending-pets" class="btn btn-outline-light btn-sm me-2">Pending Approvals</a>
            <form method="post" action="${pageContext.request.contextPath}/logout" class="d-inline"><%@ include file="/common/csrf.jspf" %><button type="submit" class="btn btn-outline-light btn-sm">Logout</button></form>
        </div>
    </div>
</nav>

<div class="container py-4">
    <h3 class="mb-4">Manage Users</h3>

    <c:if test="${param.deleted == 'true'}"><div class="alert alert-success">User deleted.</div></c:if>
    <c:if test="${param.error == 'cannot_delete_self'}"><div class="alert alert-warning">You cannot delete your own account while logged in as it.</div></c:if>
    <c:if test="${param.error == 'delete_failed'}"><div class="alert alert-danger">Could not delete that user. Please try again.</div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>

    <c:choose>
        <c:when test="${empty users}">
            <div class="alert alert-info">No users found.</div>
        </c:when>
        <c:otherwise>
            <table class="table table-bordered bg-white align-middle">
                <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Phone</th>
                    <th>Role</th>
                    <th>Joined</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="user" items="${users}">
                    <tr>
                        <td>${user.userId}</td>
                        <td>${user.name}</td>
                        <td>${user.email}</td>
                        <td>${user.phone}</td>
                        <td>
                            <c:choose>
                                <c:when test="${user.role == 'ADMIN'}"><span class="badge bg-dark">ADMIN</span></c:when>
                                <c:when test="${user.role == 'SHELTER'}"><span class="badge bg-success">SHELTER</span></c:when>
                                <c:when test="${user.role == 'ADOPTER'}"><span class="badge bg-primary">ADOPTER</span></c:when>
                            </c:choose>
                        </td>
                        <td>${user.createdAt}</td>
                        <td>
                            <c:choose>
                                <c:when test="${user.userId == sessionScope.userId}">
                                    <span class="text-muted small">(you)</span>
                                </c:when>
                                <c:otherwise>
                                    <form action="${pageContext.request.contextPath}/admin/delete-user" method="post" style="display:inline;"
                                          onsubmit="return confirm('Delete ${user.name}? This also removes their pets/applications if applicable.');"><%@ include file="/common/csrf.jspf" %>
                                        <input type="hidden" name="userId" value="${user.userId}">
                                        <button type="submit" class="btn btn-sm btn-outline-danger">Delete</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
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

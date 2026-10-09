<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Pending Pets - PetConnect Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/petconnect.css" rel="stylesheet">
</head>
<body class="bg-light pc-app">

<nav class="navbar navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/admin/dashboard.jsp">🐾 PetConnect — Admin</a>
        <form method="post" action="${pageContext.request.contextPath}/logout" class="d-inline"><%@ include file="/common/csrf.jspf" %><button type="submit" class="btn btn-outline-light btn-sm">Logout</button></form>
    </div>
</nav>

<div class="container py-4">
    <h3 class="mb-4">Pending Pet Listings</h3>

    <c:if test="${param.approved == 'true'}"><div class="alert alert-success">Pet approved and is now AVAILABLE.</div></c:if>
    <c:if test="${param.rejected == 'true'}"><div class="alert alert-warning">Pet rejected.</div></c:if>
    <c:if test="${param.error == 'true'}"><div class="alert alert-warning">That pet is no longer pending review.</div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>

    <c:choose>
        <c:when test="${empty pets}">
            <div class="alert alert-info">No pets awaiting review.</div>
        </c:when>
        <c:otherwise>
            <table class="table table-bordered bg-white align-middle">
                <thead class="table-dark">
                <tr>
                    <th>Name</th><th>Type</th><th>Breed</th><th>Shelter ID</th><th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="pet" items="${pets}">
                    <tr>
                        <td>${pet.name}</td>
                        <td>${pet.type}</td>
                        <td>${pet.breed}</td>
                        <td>${pet.shelterId}</td>
                        <td>
                            <form action="${pageContext.request.contextPath}/admin/approve-pet" method="post" style="display:inline;"><%@ include file="/common/csrf.jspf" %>
                                <input type="hidden" name="petId" value="${pet.petId}">
                                <button type="submit" class="btn btn-sm btn-success">Approve</button>
                            </form>
                            <form action="${pageContext.request.contextPath}/admin/reject-pet" method="post" style="display:inline;"><%@ include file="/common/csrf.jspf" %>
                                <input type="hidden" name="petId" value="${pet.petId}">
                                <button type="submit" class="btn btn-sm btn-danger">Reject</button>
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

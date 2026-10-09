<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My Applications - PetConnect</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/petconnect.css" rel="stylesheet">
</head>
<body class="bg-light pc-app pc-app--dark">

<nav class="navbar navbar-dark bg-primary">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/adopter/dashboard.jsp">🐾 PetConnect — Adopter</a>
        <div>
            <a href="${pageContext.request.contextPath}/adopter/pets" class="btn btn-outline-light btn-sm me-2">Browse Pets</a>
            <form method="post" action="${pageContext.request.contextPath}/logout" class="d-inline"><%@ include file="/common/csrf.jspf" %><button type="submit" class="btn btn-outline-light btn-sm">Logout</button></form>
        </div>
    </div>
</nav>

<div class="container py-4">
    <h3 class="mb-4">My Applications</h3>

    <c:if test="${param.applied == 'true'}"><div class="alert alert-success">Application submitted successfully!</div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>

    <c:choose>
        <c:when test="${empty applications}">
            <div class="alert alert-info">
                You haven't applied for any pets yet.
                <a href="${pageContext.request.contextPath}/adopter/pets">Browse available pets</a>.
            </div>
        </c:when>
        <c:otherwise>
            <table class="table table-bordered bg-white align-middle">
                <thead class="table-primary">
                <tr>
                    <th>Pet</th>
                    <th>Type</th>
                    <th>Breed</th>
                    <th>Message</th>
                    <th>Applied</th>
                    <th>Status</th>
                    <th>Decided</th>
                    <th>Contact</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="app" items="${applications}">
                    <tr>
                        <td>${app.petName}</td>
                        <td>${app.petType}</td>
                        <td>${app.petBreed}</td>
                        <td>${app.message}</td>
                        <td>${app.appliedAt}</td>
                        <td>
                            <c:choose>
                                <c:when test="${app.status == 'PENDING'}"><span class="badge bg-warning text-dark">PENDING</span></c:when>
                                <c:when test="${app.status == 'APPROVED'}"><span class="badge bg-success">APPROVED</span></c:when>
                                <c:when test="${app.status == 'REJECTED'}"><span class="badge bg-danger">REJECTED</span></c:when>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty app.decidedAt}">${app.decidedAt}</c:when>
                                <c:otherwise><span class="text-muted">—</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td><a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/adopter/messages">Message Shelter</a></td>
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

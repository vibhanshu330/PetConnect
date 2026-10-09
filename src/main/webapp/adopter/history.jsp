<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Adoption History - PetConnect</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/petconnect.css" rel="stylesheet">
</head>
<body class="pc-app pc-app--dark">
<nav class="navbar navbar-dark bg-primary">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/adopter/dashboard.jsp">🐾 PetConnect — Adopter</a>
        <span>
            <a class="btn btn-outline-light btn-sm me-2" href="${pageContext.request.contextPath}/adopter/my-applications">My Applications</a>
            <form method="post" action="${pageContext.request.contextPath}/logout" class="d-inline"><%@ include file="/common/csrf.jspf" %><button type="submit" class="btn btn-outline-light btn-sm">Logout</button></form>
        </span>
    </div>
</nav>
<main class="container py-5">
    <h2>Adoption History</h2>
    <p class="text-muted">Your completed adoptions.</p>
    <c:if test="${not empty error}"><div class="alert alert-danger"><c:out value="${error}"/></div></c:if>
    <c:choose>
        <c:when test="${not empty history}">
            <div class="table-responsive">
                <table class="table table-striped align-middle">
                    <thead><tr><th>Pet</th><th>Type</th><th>Breed</th><th>Shelter</th><th>Adopted</th></tr></thead>
                    <tbody>
                    <c:forEach var="item" items="${history}">
                        <tr>
                            <td><c:out value="${item.petName}"/></td>
                            <td><c:out value="${item.petType}"/></td>
                            <td><c:out value="${item.petBreed}"/></td>
                            <td><c:out value="${item.shelterName}"/></td>
                            <td><c:out value="${item.adoptedAt}"/></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:when>
        <c:otherwise>
            <div class="alert alert-info">You do not have any completed adoptions yet.</div>
        </c:otherwise>
    </c:choose>
</main>
<%@ include file="/common/footer.jspf" %>
</body>
</html>

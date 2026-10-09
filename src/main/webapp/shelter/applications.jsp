<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Adoption Applications - PetConnect</title>
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
<main class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3>Adoption Applications</h3>
        <a class="btn btn-outline-success" href="${pageContext.request.contextPath}/shelter/my-pets">Manage My Pets</a>
    </div>
    <c:if test="${param.result == 'approved'}"><div class="alert alert-success">Application approved. The pet is now marked adopted.</div></c:if>
    <c:if test="${param.result == 'rejected'}"><div class="alert alert-success">Application rejected.</div></c:if>
    <c:if test="${param.result == 'unavailable'}"><div class="alert alert-warning">This application is no longer pending, or the pet is no longer available.</div></c:if>
    <c:if test="${param.result == 'error' or param.result == 'invalid'}"><div class="alert alert-danger">Could not complete that decision. Please try again.</div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>
    <c:choose>
        <c:when test="${empty applications}"><div class="alert alert-info">There are no applications for your pets yet.</div></c:when>
        <c:otherwise>
            <div class="table-responsive">
                <table class="table table-bordered table-hover bg-white align-middle">
                    <thead class="table-success"><tr><th>Pet</th><th>Adopter</th><th>Contact</th><th>Message</th><th>Applied</th><th>Status</th><th>Messages</th><th>Decision</th></tr></thead>
                    <tbody>
                    <c:forEach var="app" items="${applications}">
                        <tr>
                            <td>${app.petName}</td>
                            <td>${app.adopterName}</td>
                            <td>${app.adopterEmail}<br>${app.adopterPhone}</td>
                            <td>${app.message}</td>
                            <td>${app.appliedAt}</td>
                            <td>${app.status}</td>
                            <td><a class="btn btn-outline-success btn-sm" href="${pageContext.request.contextPath}/shelter/messages?petId=${app.petId}&amp;peerId=${app.adopterId}">Message Adopter</a></td>
                            <td>
                                <c:if test="${app.status == 'PENDING'}">
                                    <form method="post" action="${pageContext.request.contextPath}/shelter/approve-application" class="d-inline"><%@ include file="/common/csrf.jspf" %>
                                        <input type="hidden" name="applicationId" value="${app.applicationId}">
                                        <button class="btn btn-success btn-sm" type="submit">Approve</button>
                                    </form>
                                    <form method="post" action="${pageContext.request.contextPath}/shelter/reject-application" class="d-inline"><%@ include file="/common/csrf.jspf" %>
                                        <input type="hidden" name="applicationId" value="${app.applicationId}">
                                        <button class="btn btn-outline-danger btn-sm" type="submit">Reject</button>
                                    </form>
                                </c:if>
                                <c:if test="${app.status != 'PENDING'}"><span class="text-muted">Decided ${app.decidedAt}</span></c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</main>
<%@ include file="/common/footer.jspf" %>
</body>
</html>

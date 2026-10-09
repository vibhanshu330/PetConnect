<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Admin Dashboard - PetConnect</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/petconnect.css" rel="stylesheet">
</head>
<body class="pc-app">
<nav class="navbar navbar-dark bg-dark">
    <div class="container">
        <span class="navbar-brand">🐾 PetConnect — Admin</span>
        <span><a class="btn btn-outline-light btn-sm me-2" href="${pageContext.request.contextPath}/profile">Profile</a>
        <form method="post" action="${pageContext.request.contextPath}/logout" class="d-inline"><%@ include file="/common/csrf.jspf" %><button type="submit" class="btn btn-outline-light btn-sm">Logout</button></form></span>
    </div>
</nav>
<div class="container py-5">
    <h2>Welcome, ${sessionScope.userName}</h2>
    <p class="text-muted">Role: ${sessionScope.userRole}</p>
    <a href="${pageContext.request.contextPath}/admin/pending-pets" class="btn btn-dark">Review Pending Pets</a>
</div>
<%@ include file="/common/footer.jspf" %>
</body>
</html>

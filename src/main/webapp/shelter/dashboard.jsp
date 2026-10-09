<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Shelter Dashboard - PetConnect</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/petconnect.css" rel="stylesheet">
</head>
<body class="pc-app">
<nav class="navbar navbar-dark bg-success">
    <div class="container">
        <span class="navbar-brand">🐾 PetConnect — Shelter</span>
        <span><a class="btn btn-outline-light btn-sm me-2" href="${pageContext.request.contextPath}/profile">Profile</a>
        <form method="post" action="${pageContext.request.contextPath}/logout" class="d-inline"><%@ include file="/common/csrf.jspf" %><button type="submit" class="btn btn-outline-light btn-sm">Logout</button></form></span>
    </div>
</nav>
<main class="container py-5">
    <section class="pc-dashboard-hero pc-shelter-hero mb-4">
        <span class="pc-eyebrow">Shelter workspace</span>
        <h1>Welcome, ${sessionScope.userName}</h1>
        <p>Keep your listings up to date and help every pet find the right home.</p>
        <a href="${pageContext.request.contextPath}/shelter/add-pet" class="btn btn-light btn-lg">＋ Add a pet</a>
    </section>
    <h2 class="h4 mb-3">What would you like to do?</h2>
    <div class="row g-3">
        <div class="col-md-4"><a class="card pc-action-card h-100 text-decoration-none" href="${pageContext.request.contextPath}/shelter/my-pets"><div class="card-body"><span class="pc-action-icon">🐕</span><h3 class="h5 mt-3">Pet listings</h3><p class="text-muted mb-0">Review, update, or add pets looking for a home.</p></div></a></div>
        <div class="col-md-4"><a class="card pc-action-card h-100 text-decoration-none" href="${pageContext.request.contextPath}/shelter/applications"><div class="card-body"><span class="pc-action-icon">📋</span><h3 class="h5 mt-3">Applications</h3><p class="text-muted mb-0">Review adopter applications and make decisions.</p></div></a></div>
        <div class="col-md-4"><a class="card pc-action-card h-100 text-decoration-none" href="${pageContext.request.contextPath}/shelter/messages"><div class="card-body"><span class="pc-action-icon">💬</span><h3 class="h5 mt-3">Messages</h3><p class="text-muted mb-0">Connect directly with prospective adopters.</p></div></a></div>
    </div>
</main>
<%@ include file="/common/footer.jspf" %>
</body>
</html>

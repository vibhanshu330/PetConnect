<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Adopter Dashboard - PetConnect</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/petconnect.css" rel="stylesheet">
</head>
<body class="pc-app pc-app--dark">
<nav class="navbar navbar-dark bg-success">
    <div class="container">
        <span class="navbar-brand">🐾 PetConnect — Adopter</span>
        <span><a class="btn btn-outline-light btn-sm me-2" href="${pageContext.request.contextPath}/profile">Profile</a>
        <form method="post" action="${pageContext.request.contextPath}/logout" class="d-inline"><%@ include file="/common/csrf.jspf" %><button type="submit" class="btn btn-outline-light btn-sm">Logout</button></form></span>
    </div>
</nav>
<main class="container py-5">
    <section class="pc-dashboard-hero pc-adopter-hero mb-4">
        <span class="pc-eyebrow">Your adoption journey</span>
        <h1>Welcome, ${sessionScope.userName}</h1>
        <p>There’s a friend out there waiting to meet you. Start exploring pets from trusted shelters.</p>
        <a href="${pageContext.request.contextPath}/adopter/pets" class="btn btn-light btn-lg">Find your new best friend <span aria-hidden="true">→</span></a>
    </section>
    <h2 class="h4 mb-3">Your PetConnect</h2>
    <div class="row g-3">
        <div class="col-md-4"><a class="card pc-action-card h-100 text-decoration-none" href="${pageContext.request.contextPath}/adopter/my-applications"><div class="card-body"><span class="pc-action-icon">📋</span><h3 class="h5 mt-3">My applications</h3><p class="text-muted mb-0">Follow updates from the shelters you’ve contacted.</p></div></a></div>
        <div class="col-md-4"><a class="card pc-action-card h-100 text-decoration-none" href="${pageContext.request.contextPath}/adopter/messages"><div class="card-body"><span class="pc-action-icon">💬</span><h3 class="h5 mt-3">Messages</h3><p class="text-muted mb-0">Keep the conversation going with shelters.</p></div></a></div>
        <div class="col-md-4"><a class="card pc-action-card h-100 text-decoration-none" href="${pageContext.request.contextPath}/adopter/history"><div class="card-body"><span class="pc-action-icon">🏡</span><h3 class="h5 mt-3">Adoption history</h3><p class="text-muted mb-0">Revisit the pets who became part of your story.</p></div></a></div>
    </div>
</main>
<%@ include file="/common/footer.jspf" %>
</body>
</html>

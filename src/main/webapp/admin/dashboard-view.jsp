<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Admin Dashboard - PetConnect</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/petconnect.css" rel="stylesheet">
</head>
<body class="bg-light pc-app">

<nav class="navbar navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/admin/dashboard.jsp">🐾 PetConnect — Admin</a>
        <div>
            <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline-light btn-sm me-2">Manage Users</a>
            <a href="${pageContext.request.contextPath}/admin/pets" class="btn btn-outline-light btn-sm me-2">Manage Pets</a>
            <a href="${pageContext.request.contextPath}/admin/pending-pets" class="btn btn-outline-light btn-sm me-2">Pending Approvals</a>
            <a href="${pageContext.request.contextPath}/profile" class="btn btn-outline-light btn-sm me-2">Profile</a>
            <form method="post" action="${pageContext.request.contextPath}/logout" class="d-inline"><%@ include file="/common/csrf.jspf" %><button type="submit" class="btn btn-outline-light btn-sm">Logout</button></form>
        </div>
    </div>
</nav>

<div class="container py-4">
    <section class="pc-dashboard-hero pc-admin-hero mb-4">
        <span class="pc-eyebrow">Platform overview</span>
        <h1>Welcome, ${sessionScope.userName}</h1>
        <p>Review new pet listings, support the community, and keep the adoption platform moving.</p>
        <a href="${pageContext.request.contextPath}/admin/pending-pets" class="btn btn-light btn-lg">Review pending listings</a>
    </section>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <c:if test="${not empty stats}">
        <h4 class="mb-3">Platform at a glance</h4>
        <div class="row g-3 mb-4">

            <div class="col-md-3 col-sm-6">
                <div class="card text-center shadow-sm">
                    <div class="card-body">
                        <div class="text-muted small">Total Users</div>
                        <div class="fs-2 fw-bold">${stats.totalUsers}</div>
                    </div>
                </div>
            </div>

            <div class="col-md-3 col-sm-6">
                <div class="card text-center shadow-sm">
                    <div class="card-body">
                        <div class="text-muted small">Total Shelters</div>
                        <div class="fs-2 fw-bold">${stats.totalShelters}</div>
                    </div>
                </div>
            </div>

            <div class="col-md-3 col-sm-6">
                <div class="card text-center shadow-sm">
                    <div class="card-body">
                        <div class="text-muted small">Total Adopters</div>
                        <div class="fs-2 fw-bold">${stats.totalAdopters}</div>
                    </div>
                </div>
            </div>

            <div class="col-md-3 col-sm-6">
                <div class="card text-center shadow-sm">
                    <div class="card-body">
                        <div class="text-muted small">Total Pets</div>
                        <div class="fs-2 fw-bold">${stats.totalPets}</div>
                    </div>
                </div>
            </div>

            <div class="col-md-3 col-sm-6">
                <div class="card text-center shadow-sm border-warning">
                    <div class="card-body">
                        <div class="text-muted small">Pending Pets</div>
                        <div class="fs-2 fw-bold text-warning">${stats.pendingPets}</div>
                    </div>
                </div>
            </div>

            <div class="col-md-3 col-sm-6">
                <div class="card text-center shadow-sm border-success">
                    <div class="card-body">
                        <div class="text-muted small">Available Pets</div>
                        <div class="fs-2 fw-bold text-success">${stats.availablePets}</div>
                    </div>
                </div>
            </div>

            <div class="col-md-3 col-sm-6">
                <div class="card text-center shadow-sm border-secondary">
                    <div class="card-body">
                        <div class="text-muted small">Adopted Pets</div>
                        <div class="fs-2 fw-bold text-secondary">${stats.adoptedPets}</div>
                    </div>
                </div>
            </div>

            <div class="col-md-3 col-sm-6">
                <div class="card text-center shadow-sm border-primary">
                    <div class="card-body">
                        <div class="text-muted small">Total Applications</div>
                        <div class="fs-2 fw-bold text-primary">${stats.totalApplications}</div>
                    </div>
                </div>
            </div>

        </div>
    </c:if>

    <div class="d-flex gap-2">
        <a href="${pageContext.request.contextPath}/admin/pending-pets" class="btn btn-dark">Review Pending Pets</a>
        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline-dark">Manage Users</a>
        <a href="${pageContext.request.contextPath}/admin/pets" class="btn btn-outline-dark">Manage Pets</a>
    </div>
</div>
<%@ include file="/common/footer.jspf" %>
</body>
</html>

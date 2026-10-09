<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PetConnect - Find Your New Best Friend</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/petconnect.css" rel="stylesheet">
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark bg-success">
    <div class="container">
        <a class="navbar-brand fw-bold" href="index.jsp">🐾 PetConnect</a>
        <div>
            <a href="login.jsp" class="btn btn-outline-light btn-sm me-2">Login</a>
            <a href="register.jsp" class="btn btn-light btn-sm">Register</a>
        </div>
    </div>
</nav>

<main class="container py-5">
    <section class="pc-hero text-center">
        <div class="small text-uppercase fw-semibold mb-3" style="letter-spacing:.16em">A better way to find family</div>
        <h1 class="display-4 fw-bold">Meet the friend<br>you haven't met yet.</h1>
        <p class="lead mt-3">Discover adoptable pets from trusted shelters, start a conversation, and take the first step toward a lifelong bond.</p>
        <div class="d-flex justify-content-center gap-2 flex-wrap mt-4">
            <a href="register.jsp" class="btn btn-light btn-lg">Get started</a>
            <a href="login.jsp" class="btn btn-outline-light btn-lg">Sign in</a>
        </div>
    </section>
    <section class="row g-3 mt-4 text-center">
        <div class="col-md-4"><div class="card h-100"><div class="card-body"><div class="fs-2">🐾</div><h5 class="mt-2">Find your match</h5><p class="text-muted mb-0">Browse available pets by type, breed, and location.</p></div></div></div>
        <div class="col-md-4"><div class="card h-100"><div class="card-body"><div class="fs-2">🏡</div><h5 class="mt-2">Trusted shelters</h5><p class="text-muted mb-0">Meet pets listed by shelters in your community.</p></div></div></div>
        <div class="col-md-4"><div class="card h-100"><div class="card-body"><div class="fs-2">💚</div><h5 class="mt-2">Stay connected</h5><p class="text-muted mb-0">Apply and keep in touch throughout the journey.</p></div></div></div>
    </section>
</main>

<footer class="pc-footer">© 2026 PetConnect · Helping good homes find their new best friend.</footer>

</body>
</html>

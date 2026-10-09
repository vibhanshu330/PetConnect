<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - PetConnect</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/petconnect.css" rel="stylesheet">
</head>
<body class="pc-auth-page pc-app">

<nav class="navbar navbar-dark bg-success">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/index.jsp">🐾 PetConnect</a>
    </div>
</nav>

<main class="container pc-auth-wrap">
    <div class="pc-auth-layout">
        <section class="pc-auth-story">
            <span class="pc-eyebrow">A new chapter starts here</span>
            <h1>Good things happen when you find each other.</h1>
            <p>Meet adoptable pets, connect with trusted shelters, and take the next step toward a lifelong friendship.</p>
            <div class="pc-auth-note"><span aria-hidden="true">🐾</span><span>Every great adoption begins with a hello.</span></div>
        </section>
        <section class="card pc-auth-card">
        <div class="card-body p-4 p-lg-5">
            <span class="pc-eyebrow">Welcome back</span>
            <h2 class="mb-2">Sign in to PetConnect</h2>
            <p class="text-muted mb-4">Pick up where your next best friendship begins.</p>

            <% if (request.getParameter("registered") != null) { %>
                <div class="alert alert-success">Registration successful! Please log in.</div>
            <% } %>

            <% if (request.getParameter("error") != null) {
                   String errCode = request.getParameter("error");
                   String msg = "login_required".equals(errCode)
                           ? "Please log in to continue."
                           : "You do not have access to that page.";
            %>
                <div class="alert alert-warning"><%= msg %></div>
            <% } %>

            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="post"><%@ include file="/common/csrf.jspf" %>
                <div class="mb-3">
                    <label class="form-label">Email</label>
                    <input type="email" name="email" class="form-control"
                           value="${email}" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Password</label>
                    <input type="password" name="password" class="form-control" required>
                </div>
                <button type="submit" class="btn btn-success w-100">Login</button>
            </form>

            <p class="text-center mt-4 mb-0">
                Don't have an account?
                <a href="${pageContext.request.contextPath}/register.jsp">Register here</a>
            </p>
        </div>
        </section>
    </div>
</main>

<%@ include file="/common/footer.jspf" %>
</body>
</html>

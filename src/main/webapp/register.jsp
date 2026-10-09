<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - PetConnect</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/petconnect.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-dark bg-success">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/index.jsp">🐾 PetConnect</a>
    </div>
</nav>

<div class="container d-flex justify-content-center align-items-center py-5">
    <div class="card shadow-sm" style="width: 100%; max-width: 480px;">
        <div class="card-body p-4">
            <h3 class="text-center text-success mb-4">Create an Account</h3>

            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/register" method="post"><%@ include file="/common/csrf.jspf" %>
                <div class="mb-3">
                    <label class="form-label">Full Name</label>
                    <input type="text" name="name" class="form-control" value="${name}" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Email</label>
                    <input type="email" name="email" class="form-control" value="${email}" required>
                </div>
                <div class="mb-3">
                    <label class="form-label">Phone</label>
                    <input type="text" name="phone" class="form-control" value="${phone}" placeholder="10-digit number">
                </div>
                <div class="mb-3">
                    <label class="form-label">Password</label>
                    <input type="password" name="password" class="form-control" required minlength="6">
                </div>
                <div class="mb-3">
                    <label class="form-label">Confirm Password</label>
                    <input type="password" name="confirmPassword" class="form-control" required minlength="6">
                </div>
                <div class="mb-3">
                    <label class="form-label">I am registering as a...</label>
                    <select name="role" class="form-select" required>
                        <option value="">-- Select --</option>
                        <option value="SHELTER" ${role == 'SHELTER' ? 'selected' : ''}>Shelter</option>
                        <option value="ADOPTER" ${role == 'ADOPTER' ? 'selected' : ''}>Adopter</option>
                    </select>
                </div>
                <button type="submit" class="btn btn-success w-100">Register</button>
            </form>

            <p class="text-center mt-3 mb-0">
                Already have an account?
                <a href="${pageContext.request.contextPath}/login.jsp">Login here</a>
            </p>
        </div>
    </div>
</div>

<%@ include file="/common/footer.jspf" %>
</body>
</html>

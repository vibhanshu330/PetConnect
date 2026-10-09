<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My Profile - PetConnect</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/petconnect.css" rel="stylesheet">
</head>
<body class="bg-light">
<nav class="navbar navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/${sessionScope.userRole == 'ADMIN' ? 'admin/dashboard.jsp' : (sessionScope.userRole == 'SHELTER' ? 'shelter/dashboard.jsp' : 'adopter/dashboard.jsp')}">🐾 PetConnect</a>
        <form method="post" action="${pageContext.request.contextPath}/logout" class="d-inline"><%@ include file="/common/csrf.jspf" %><button type="submit" class="btn btn-outline-light btn-sm">Logout</button></form>
    </div>
</nav>
<main class="container py-5" style="max-width: 720px">
    <h1 class="h2 mb-4">My Profile</h1>
    <c:if test="${param.updated == 'true'}">
        <div class="alert alert-success" role="status">Your profile was updated.</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert"><c:out value="${error}" /></div>
    </c:if>
    <div class="card shadow-sm">
        <div class="card-body p-4">
            <form method="post" action="${pageContext.request.contextPath}/profile"><%@ include file="/common/csrf.jspf" %>
                <div class="mb-3">
                    <label for="name" class="form-label">Name</label>
                    <input id="name" name="name" class="form-control" type="text" maxlength="100" required
                           value="<c:out value='${requestScope.profileFormSubmitted ? requestScope.profileName : profile.name}' />" autocomplete="name">
                </div>
                <div class="mb-3">
                    <label for="email" class="form-label">Email</label>
                    <input id="email" class="form-control" type="email" value="<c:out value='${profile.email}' />" readonly>
                    <div class="form-text">Email is used to sign in and cannot be changed here.</div>
                </div>
                <div class="mb-3">
                    <label for="phone" class="form-label">Phone</label>
                    <input id="phone" name="phone" class="form-control" type="tel" maxlength="10"
                           pattern="[0-9]{10}" value="<c:out value='${requestScope.profileFormSubmitted ? requestScope.profilePhone : profile.phone}' />" autocomplete="tel">
                    <div class="form-text">Leave blank or enter a 10-digit phone number.</div>
                </div>
                <div class="mb-4">
                    <label class="form-label">Account role</label>
                    <input class="form-control" value="<c:out value='${profile.role}' />" readonly>
                </div>
                <button class="btn btn-primary" type="submit">Save changes</button>
            </form>
        </div>
    </div>
</main>
<%@ include file="/common/footer.jspf" %>
</body>
</html>

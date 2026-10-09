<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Add Pet - PetConnect</title>
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

<div class="container py-4" style="max-width: 640px;">
    <h3 class="mb-4">Add a New Pet</h3>

    <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>

    <form action="${pageContext.request.contextPath}/shelter/add-pet" method="post" enctype="multipart/form-data" class="bg-white p-4 rounded shadow-sm"><%@ include file="/common/csrf.jspf" %>

        <div class="mb-3">
            <label class="form-label">Pet Name</label>
            <input type="text" name="name" class="form-control" value="${name}" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Type</label>
            <input type="text" name="type" class="form-control" value="${type}" placeholder="Dog, Cat, Rabbit..." required>
        </div>

        <div class="mb-3">
            <label class="form-label">Breed</label>
            <input type="text" name="breed" class="form-control" value="${breed}">
        </div>

        <div class="row">
            <div class="col mb-3">
                <label class="form-label">Age (years)</label>
                <input type="number" name="age" min="0" max="40" class="form-control" value="${age}" required>
            </div>
            <div class="col mb-3">
                <label class="form-label">Gender</label>
                <select name="gender" class="form-select" required>
                    <option value="">-- Select --</option>
                    <option value="MALE" ${gender == 'MALE' ? 'selected' : ''}>Male</option>
                    <option value="FEMALE" ${gender == 'FEMALE' ? 'selected' : ''}>Female</option>
                </select>
            </div>
        </div>

        <div class="mb-3">
            <label class="form-label">Location</label>
            <input type="text" name="location" class="form-control" value="${location}" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Description</label>
            <textarea name="description" class="form-control" rows="3">${description}</textarea>
        </div>

        <div class="mb-3">
            <label class="form-label">Photo (optional)</label>
            <input type="file" name="image" class="form-control" accept="image/*">
        </div>

        <button type="submit" class="btn btn-success w-100">Submit for Approval</button>
    </form>
</div>
<%@ include file="/common/footer.jspf" %>
</body>
</html>

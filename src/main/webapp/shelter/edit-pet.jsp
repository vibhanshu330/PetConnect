<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Edit Pet - PetConnect</title>
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
    <h3 class="mb-4">Edit Pet</h3>

    <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>

    <form action="${pageContext.request.contextPath}/shelter/edit-pet" method="post" enctype="multipart/form-data" class="bg-white p-4 rounded shadow-sm"><%@ include file="/common/csrf.jspf" %>
        <input type="hidden" name="petId" value="${pet.petId}">

        <div class="mb-3">
            <label class="form-label">Pet Name</label>
            <input type="text" name="name" class="form-control" value="${pet.name}" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Type</label>
            <input type="text" name="type" class="form-control" value="${pet.type}" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Breed</label>
            <input type="text" name="breed" class="form-control" value="${pet.breed}">
        </div>

        <div class="row">
            <div class="col mb-3">
                <label class="form-label">Age (years)</label>
                <input type="number" name="age" min="0" max="40" class="form-control" value="${pet.age}" required>
            </div>
            <div class="col mb-3">
                <label class="form-label">Gender</label>
                <select name="gender" class="form-select" required>
                    <option value="MALE" ${pet.gender == 'MALE' ? 'selected' : ''}>Male</option>
                    <option value="FEMALE" ${pet.gender == 'FEMALE' ? 'selected' : ''}>Female</option>
                </select>
            </div>
        </div>

        <div class="mb-3">
            <label class="form-label">Location</label>
            <input type="text" name="location" class="form-control" value="${pet.location}" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Description</label>
            <textarea name="description" class="form-control" rows="3">${pet.description}</textarea>
        </div>

        <div class="mb-3">
            <label class="form-label">Current Photo</label><br>
            <c:if test="${not empty pet.imagePath}">
                <img src="${pageContext.request.contextPath}/${pet.imagePath}" width="100" class="mb-2" onerror="this.hidden=true; this.nextElementSibling.hidden=false;">
                <span class="text-muted small" hidden>Photo unavailable</span><br>
            </c:if>
            <label class="form-label">Replace photo (optional)</label>
            <input type="file" name="image" class="form-control" accept="image/*">
        </div>

        <p class="text-muted small">
            Note: this pet's approval status
            (<strong>${pet.status}</strong>) is not changed by editing details.
        </p>

        <button type="submit" class="btn btn-success w-100">Save Changes</button>
    </form>
</div>
<%@ include file="/common/footer.jspf" %>
</body>
</html>

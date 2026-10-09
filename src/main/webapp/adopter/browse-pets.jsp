<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Browse Pets - PetConnect</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/petconnect.css" rel="stylesheet">
</head>
<body class="bg-light pc-app pc-app--dark">

<nav class="navbar navbar-dark bg-primary">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/adopter/dashboard.jsp">🐾 PetConnect — Adopter</a>
        <div>
            <a href="${pageContext.request.contextPath}/adopter/my-applications" class="btn btn-outline-light btn-sm me-2">My Applications</a>
            <form method="post" action="${pageContext.request.contextPath}/logout" class="d-inline"><%@ include file="/common/csrf.jspf" %><button type="submit" class="btn btn-outline-light btn-sm">Logout</button></form>
        </div>
    </div>
</nav>

<div class="container py-4">
    <h3 class="mb-4">Browse Available Pets</h3>

    <c:if test="${param.error == 'not_available'}"><div class="alert alert-warning">That pet is no longer available.</div></c:if>
    <c:if test="${param.error == 'pet_unavailable'}"><div class="alert alert-warning">That pet is no longer available for adoption.</div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>
    <c:if test="${not empty ageError}"><div class="alert alert-warning">${ageError}</div></c:if>

    <form action="${pageContext.request.contextPath}/adopter/pets" method="get" class="bg-white p-3 rounded shadow-sm mb-4">
        <div class="row g-2">
            <div class="col-md-4">
                <label class="form-label small">Search</label>
                <input type="text" name="q" class="form-control" placeholder="Name, type, breed, or location..." value="${q}">
            </div>
            <div class="col-md-2">
                <label class="form-label small">Pet Type</label>
                <input type="text" name="type" class="form-control" placeholder="Dog, Cat..." value="${type}">
            </div>
            <div class="col-md-2">
                <label class="form-label small">Breed</label>
                <input type="text" name="breed" class="form-control" value="${breed}">
            </div>
            <div class="col-md-2">
                <label class="form-label small">Location</label>
                <input type="text" name="location" class="form-control" value="${location}">
            </div>
            <div class="col-md-2">
                <label class="form-label small">Maximum pet age</label>
                <select name="maxAge" class="form-select">
                    <option value="" ${empty maxAge ? 'selected' : ''}>Any age</option>
                    <c:forEach var="ageOption" begin="0" end="20">
                        <option value="${ageOption}" ${maxAge.toString() == ageOption.toString() ? 'selected' : ''}>${ageOption} years</option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-2 d-flex align-items-end">
                <button type="submit" class="btn btn-primary w-100">Search / Apply Filters</button>
            </div>
        </div>
        <div class="mt-2">
            <a href="${pageContext.request.contextPath}/adopter/pets" class="btn btn-sm btn-outline-secondary">Clear Filters</a>
        </div>
    </form>

    <div class="alert alert-primary">
        <strong>Pet recommendations</strong> are ranked by compatibility with the preferences you selected.
        <c:choose><c:when test="${hasPreferences}">The score explains how closely each available pet matches those preferences.</c:when><c:otherwise>Select one or more preferences above to get a meaningful compatibility score.</c:otherwise></c:choose>
        Type 40%, breed 30%, location 20%, and maximum age 10% are weighted; unselected preferences are left out of the calculation.
    </div>

    <c:choose>
        <c:when test="${empty pets}">
            <div class="alert alert-info">No available pets match your search.</div>
        </c:when>
        <c:otherwise>
            <div class="row g-4">
                <c:forEach var="recommendation" items="${recommendations}">
                    <c:set var="pet" value="${recommendation.pet}" />
                    <div class="col-md-4">
                        <div class="card h-100 shadow-sm">
                            <c:choose>
                                <c:when test="${not empty pet.imagePath}">
                                    <img src="${pageContext.request.contextPath}/${pet.imagePath}" class="card-img-top" style="height:200px; object-fit:cover;" onerror="this.hidden=true; this.nextElementSibling.hidden=false;">
                                    <div class="bg-secondary bg-opacity-10 text-center py-5 text-muted" hidden>Photo unavailable</div>
                                </c:when>
                                <c:otherwise>
                                    <div class="bg-secondary bg-opacity-10 text-center py-5 text-muted">No photo</div>
                                </c:otherwise>
                            </c:choose>
                            <div class="card-body d-flex flex-column">
                                <h5 class="card-title">${pet.name} <span class="badge bg-success">AVAILABLE</span></h5>
                                <p class="card-text mb-1"><strong>${pet.type}</strong> &middot; ${pet.breed}</p>
                                <p class="card-text mb-1">${pet.age} yrs &middot; ${pet.gender}</p>
                                <p class="card-text text-muted mb-3">${pet.location}</p>
                                <p class="mb-3"><strong>Compatibility: <c:choose><c:when test="${hasPreferences}">${recommendation.compatibility.percentage}%</c:when><c:otherwise>—</c:otherwise></c:choose></strong></p>
                                <a href="${pageContext.request.contextPath}/adopter/pet-details?id=${pet.petId}&amp;type=${type}&amp;breed=${breed}&amp;location=${location}&amp;maxAge=${maxAge}" class="btn btn-primary mt-auto">View Details</a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="/common/footer.jspf" %>
</body>
</html>

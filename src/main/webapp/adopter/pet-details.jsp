<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${pet.name} - PetConnect</title>
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

<div class="container py-4" style="max-width: 700px;">
    <a href="${pageContext.request.contextPath}/adopter/pets" class="btn btn-sm btn-outline-secondary mb-3">&larr; Back to Browse</a>

    <c:if test="${param.error == 'already_applied'}"><div class="alert alert-warning">You have already applied for this pet.</div></c:if>
    <c:if test="${param.error == 'message_required'}"><div class="alert alert-warning">Please write a short message before applying.</div></c:if>
    <c:if test="${param.error == 'submit_failed'}"><div class="alert alert-danger">Something went wrong submitting your application. Please try again.</div></c:if>

    <div class="card shadow-sm">
        <c:choose>
            <c:when test="${not empty pet.imagePath}">
                <img src="${pageContext.request.contextPath}/${pet.imagePath}" class="card-img-top" style="max-height:350px; object-fit:cover;" onerror="this.hidden=true; this.nextElementSibling.hidden=false;">
                <div class="bg-secondary bg-opacity-10 text-center py-5 text-muted" hidden>Photo unavailable</div>
            </c:when>
            <c:otherwise>
                <div class="bg-secondary bg-opacity-10 text-center py-5 text-muted">No photo</div>
            </c:otherwise>
        </c:choose>
        <div class="card-body">
            <h3>${pet.name} <span class="badge bg-success">AVAILABLE</span></h3>
            <p><strong>Type:</strong> ${pet.type} &nbsp; <strong>Breed:</strong> ${pet.breed}</p>
            <p><strong>Age:</strong> ${pet.age} years &nbsp; <strong>Gender:</strong> ${pet.gender}</p>
            <p><strong>Location:</strong> ${pet.location}</p>
            <p><strong>Listed by Shelter ID:</strong> ${pet.shelterId}</p>
            <p>${pet.description}</p>
            <div class="alert alert-info">
                <h5>Adoption compatibility
                    <c:choose><c:when test="${hasPreferences}">${compatibility.percentage}%</c:when><c:otherwise>Choose preferences to calculate a score</c:otherwise></c:choose>
                </h5>
                <p class="mb-2">A simple rule-based score compares this pet with the preferences selected on Browse Pets. Points below show the earned weight.</p>
                <ul class="mb-2">
                    <li>Pet type (40%): <c:choose><c:when test="${compatibility.typeConsidered}">${compatibility.typePoints}/40 points</c:when><c:otherwise>Not selected</c:otherwise></c:choose></li>
                    <li>Breed (30%): <c:choose><c:when test="${compatibility.breedConsidered}">${compatibility.breedPoints}/30 points</c:when><c:otherwise>Not selected</c:otherwise></c:choose></li>
                    <li>Location (20%): <c:choose><c:when test="${compatibility.locationConsidered}">${compatibility.locationPoints}/20 points</c:when><c:otherwise>Not selected</c:otherwise></c:choose></li>
                    <li>Maximum age preference (10%): <c:choose><c:when test="${compatibility.ageConsidered}">${compatibility.agePoints}/10 points</c:when><c:otherwise>Not selected</c:otherwise></c:choose></li>
                </ul>
                <small>The percentage is earned points divided by the total weight of selected preferences, rounded to the nearest whole number. Partial text matches count as a match. This is a guide, not an adoption decision.</small>
            </div>
            <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/adopter/messages?petId=${pet.petId}">Message this Shelter</a>

            <hr>

            <c:choose>
                <c:when test="${alreadyApplied}">
                    <div class="alert alert-info mb-0">
                        You've already submitted an application for this pet. Check
                        <a href="${pageContext.request.contextPath}/adopter/my-applications">My Applications</a> for its status.
                    </div>
                </c:when>
                <c:otherwise>
                    <h5>Apply for Adoption</h5>
                    <form action="${pageContext.request.contextPath}/adopter/apply" method="post"><%@ include file="/common/csrf.jspf" %>
                        <input type="hidden" name="petId" value="${pet.petId}">
                        <div class="mb-3">
                            <label class="form-label">Message to the shelter</label>
                            <textarea name="message" class="form-control" rows="4"
                                      placeholder="Tell the shelter why you'd be a great home for ${pet.name}..." required></textarea>
                        </div>
                        <button type="submit" class="btn btn-success">Apply for Adoption</button>
                    </form>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
<%@ include file="/common/footer.jspf" %>
</body>
</html>

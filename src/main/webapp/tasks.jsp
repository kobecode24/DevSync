<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Liste des Tâches</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Task/styles.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/taskStyles.css">
</head>
<body>
<div class="container">
  <h1>Liste des Tâches</h1>

  <c:if test="${not empty error}">
    <div class="error">${error}</div>
  </c:if>

  <a href="${pageContext.request.contextPath}/tasks/add" class="btn">Ajouter une Nouvelle Tâche</a>

  <table>
    <thead>
    <tr>
      <th>Titre</th>
      <th>Description</th>
      <th>Date Limite</th>
      <th>Statut</th>
      <th>Attribué à</th>
      <th>Tags</th>
      <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="task" items="${tasks}">
      <tr>
        <td>${task.title}</td>
        <td>${task.description}</td>
        <td>${task.dueDate}</td>
        <td>${task.status}</td>
        <td>${task.assignedUserName}</td>
        <td>
          <c:forEach var="tag" items="${task.tags}" varStatus="loop">
            ${tag}<c:if test="${!loop.last}">, </c:if>
          </c:forEach>
        </td>
        <td>
          <a href="${pageContext.request.contextPath}/tasks/edit/${task.id}" class="btn">Modifier</a>
          <form action="${pageContext.request.contextPath}/tasks/delete/${task.id}" method="post" style="display:inline;">
            <input type="submit" value="Supprimer" class="btn btn-danger" onclick="return confirm('Voulez-vous vraiment supprimer cette tâche ?');">
          </form>
          <form action="${pageContext.request.contextPath}/tasks/assignSelf" method="post" style="display:inline;">
            <input type="hidden" name="taskId" value="${task.id}">
            <input type="submit" value="S'Attribuer" class="btn btn-assign">
          </form>
        </td>
      </tr>
    </c:forEach>
    </tbody>
  </table>
</div>
<script src="${pageContext.request.contextPath}/js/taskActions.js"></script>
</body>
</html>

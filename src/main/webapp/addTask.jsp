<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Add Task</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Task/styles.css">
</head>
<body>
<h1>Add Task</h1>
<form action="${pageContext.request.contextPath}/tasks" method="post">
  <label for="title">Title:</label>
  <input type="text" id="title" name="title" required><br>

  <label for="description">Description:</label>
  <textarea id="description" name="description"></textarea><br>

  <label for="dueDate">Due Date:</label>
  <input type="datetime-local" id="dueDate" name="dueDate" required><br>

  <label for="status">Status:</label>
  <select id="status" name="status">
    <option value="TODO">To Do</option>
    <option value="IN_PROGRESS">In Progress</option>
    <option value="DONE">Done</option>
  </select><br>

  <label for="assignedUserId">Assign To:</label>
  <select id="assignedUserId" name="assignedUserId">
    <c:forEach var="user" items="${users}">
      <option value="${user.id}">${user.firstName} ${user.lastName}</option>
    </c:forEach>
  </select><br>

  <label for="tags">Tags (comma-separated):</label>
  <input type="text" id="tags" name="tags"><br>

  <input type="submit" value="Add Task" class="submit-btn">
</form>
</body>
</html>

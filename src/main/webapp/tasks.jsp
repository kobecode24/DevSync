<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Task List</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Task/styles.css">
</head>
<body>
<h1>Task List</h1>
<a href="${pageContext.request.contextPath}/tasks/add" class="add-task-btn">Add New Task</a>
<table>
  <thead>
  <tr>
    <th>ID</th>
    <th>Title</th>
    <th>Description</th>
    <th>Due Date</th>
    <th>Status</th>
    <th>Assigned To</th>
    <th>Tags</th>
    <th>Actions</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach var="task" items="${tasks}">
    <tr>
      <td>${task.id}</td>
      <td>${task.title}</td>
      <td>${task.description}</td>
      <td>${task.dueDate}</td>
      <td>${task.status}</td>
      <td>${task.assignedUserName}</td>
      <td>${task.tags}</td>
      <td>
        <a href="${pageContext.request.contextPath}/tasks/edit/${task.id}" class="edit-link">Edit</a>
        <form action="${pageContext.request.contextPath}/tasks/delete/${task.id}" method="post" class="delete-form">
          <input type="hidden" name="taskId" value="${task.id}">
          <button type="submit" class="delete-btn">Delete</button>
        </form>
      </td>
    </tr>
  </c:forEach>
  </tbody>
</table>
<a href="${pageContext.request.contextPath}/employee-tasks">Back to Manager View</a>
</body>
</html>

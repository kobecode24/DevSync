<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
  <title>Employee Tasks</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Task/styles.css">
  <style>
    table {
      border-collapse: collapse;
      width: 100%;
    }
    th, td {
      border: 1px solid black;
      padding: 8px;
      text-align: left;
    }
    th {
      background-color: #f2f2f2;
    }
  </style>
</head>
<body>
<h1>Employee Tasks</h1>
<table>
  <tr>
    <th>Title</th>
    <th>Description</th>
    <th>Due Date</th>
    <th>Status</th>
    <th>Assigned To</th>
    <th>Action</th>
  </tr>
  <c:forEach var="task" items="${tasks}">
    <tr>
      <td>${task.title}</td>
      <td>${task.description}</td>
      <td>${task.dueDate}</td>
      <td>${task.status}</td>
      <td>${task.assignedUserName}</td>
      <td>
        <form action="${pageContext.request.contextPath}/employee-tasks" method="post">
          <input type="hidden" name="taskId" value="${task.id}">
          <select name="status">
            <option value="TODO" ${task.status == 'TODO' ? 'selected' : ''}>To Do</option>
            <option value="IN_PROGRESS" ${task.status == 'IN_PROGRESS' ? 'selected' : ''}>In Progress</option>
            <option value="DONE" ${task.status == 'DONE' ? 'selected' : ''}>Done</option>
          </select>
          <input type="submit" value="Update Status">
        </form>
      </td>
    </tr>
  </c:forEach>
</table>
<br>
<a href="${pageContext.request.contextPath}/tasks">Back to Manager View</a>
</body>
</html>

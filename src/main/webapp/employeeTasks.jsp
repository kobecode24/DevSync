<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
  <title>Employee Tasks</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Task/styles.css">
  <style>
    table {
      border-collapse: collapse;
      width: 100%;
      margin-bottom: 20px;
    }
    th, td {
      border: 1px solid black;
      padding: 8px;
      text-align: left;
    }
    th {
      background-color: #f2f2f2;
    }
    .error-message {
      color: red;
      font-weight: bold;
      margin-bottom: 10px;
    }
    .token-info {
      margin-bottom: 20px;
    }
  </style>
</head>
<body>
<h1>Employee Tasks</h1>

<div class="token-info">
  <p>Replacement Tokens: ${replacementTokens}</p>
  <p>Deletion Tokens: ${deletionTokens}</p>
</div>

<h2>Your Tasks</h2>
<table>
  <tr>
    <th>Title</th>
    <th>Description</th>
    <th>Due Date</th>
    <th>Status</th>
    <th>Assigned To</th>
    <th>Tokens</th>
    <th>Tags</th>
    <th>Actions</th>
  </tr>
  <c:forEach var="task" items="${userTasks}">
    <tr>
      <td>${task.title}</td>
      <td>${task.description}</td>
      <td><fmt:formatDate value="${task.dueDate}" pattern="yyyy-MM-dd HH:mm" /></td>
      <td>
        <select name="status" class="status-dropdown" data-task-id="${task.id}">
          <option value="TODO" ${task.status == 'TODO' ? 'selected' : ''}>To Do</option>
          <option value="IN_PROGRESS" ${task.status == 'IN_PROGRESS' ? 'selected' : ''}>In Progress</option>
          <option value="DONE" ${task.status == 'DONE' ? 'selected' : ''}>Done</option>
        </select>
      </td>
      <td>${task.assignedUserName}</td>
      <td>Replacement: ${replacementTokens}, Deletion: ${deletionTokens}</td>
      <td>
        <c:forEach var="tag" items="${task.tags}" varStatus="loop">
          ${tag}<c:if test="${!loop.last}">, </c:if>
        </c:forEach>
      </td>
      <td>
        <form class="action-form" data-action="replaceTask">
          <input type="hidden" name="taskId" value="${task.id}">
          <input type="submit" value="Replace Task" onclick="return confirm('Are you sure you want to replace this task? This will use a replacement token.');">
        </form>
        <form class="action-form" data-action="deleteTask">
          <input type="hidden" name="taskId" value="${task.id}">
          <input type="submit" value="Delete Task" onclick="return confirm('Are you sure you want to delete this task? This may use a deletion token.');">
        </form>
        <a href="${pageContext.request.contextPath}/tasks/edit/${task.id}" class="btn">Edit</a>
      </td>
    </tr>
  </c:forEach>
</table>

<h2>Available Tasks</h2>
<table>
  <tr>
    <th>Title</th>
    <th>Description</th>
    <th>Due Date</th>
    <th>Status</th>
    <th>Tags</th>
    <th>Actions</th>
  </tr>
  <c:forEach var="task" items="${availableTasks}">
    <tr>
      <td>${task.title}</td>
      <td>${task.description}</td>
      <td><fmt:formatDate value="${task.dueDate}" pattern="yyyy-MM-dd HH:mm" /></td>
      <td>${task.status}</td>
      <td>
        <c:forEach var="tag" items="${task.tags}" varStatus="loop">
          ${tag}<c:if test="${!loop.last}">, </c:if>
        </c:forEach>
      </td>
      <td>
        <form class="action-form" data-action="assignToSelf">
          <input type="hidden" name="taskId" value="${task.id}">
          <input type="submit" value="Assign to Myself">
        </form>
        <a href="${pageContext.request.contextPath}/tasks/edit/${task.id}" class="btn">Edit</a>
        <form class="action-form" data-action="deleteTask">
          <input type="hidden" name="taskId" value="${task.id}">
          <input type="submit" value="Delete Task" onclick="return confirm('Are you sure you want to delete this task? This may use a deletion token.');">
        </form>
      </td>
    </tr>
  </c:forEach>
</table>

<a href="${pageContext.request.contextPath}/tasks">Back to Manager View</a>

<script>
  document.addEventListener('DOMContentLoaded', function() {
    const statusDropdowns = document.querySelectorAll('.status-dropdown');
    statusDropdowns.forEach(dropdown => {
      dropdown.addEventListener('change', function() {
        const taskId = this.dataset.taskId;
        const newStatus = this.value;
        const formData = new FormData();
        formData.append('taskId', taskId);
        formData.append('status', newStatus);
        formData.append('action', 'updateStatus');

        fetch('${pageContext.request.contextPath}/employee-tasks', {
          method: 'POST',
          body: formData
        })
                .then(response => response.json())
                .then(data => {
                  if (data.success) {
                    location.reload();
                  } else {
                    alert(data.error);
                  }
                })
                .catch(error => {
                  console.error('Error:', error);
                  alert('An error occurred. Please try again.');
                });
      });
    });
  });
</script>
</body>
</html>

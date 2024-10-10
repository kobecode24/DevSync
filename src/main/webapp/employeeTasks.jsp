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
  </style>
</head>
<body>
<h1>All Employee Tasks</h1>

<c:forEach var="entry" items="${userTasksMap}">
  <h2>Tasks for ${entry.key.firstName} ${entry.key.lastName} (${entry.key.email})</h2>
  <table>
    <tr>
      <th>Title</th>
      <th>Description</th>
      <th>Due Date</th>
      <th>Status</th>
      <th>Tags</th>
      <th>Actions</th>
    </tr>
    <c:forEach var="task" items="${entry.value}">
      <tr>
        <td>${task.title}</td>
        <td>${task.description}</td>
        <td><fmt:formatDate value="${task.dueDate}" pattern="yyyy-MM-dd HH:mm" /></td>
        <td>
          <select name="status" class="status-dropdown" data-task-id="${task.id}" data-user-id="${entry.key.id}">
            <option value="TODO" ${task.status == 'TODO' ? 'selected' : ''}>To Do</option>
            <option value="IN_PROGRESS" ${task.status == 'IN_PROGRESS' ? 'selected' : ''}>In Progress</option>
            <option value="DONE" ${task.status == 'DONE' ? 'selected' : ''}>Done</option>
          </select>
        </td>
        <td>
          <c:forEach var="tag" items="${task.tags}" varStatus="loop">
            ${tag}<c:if test="${!loop.last}">, </c:if>
          </c:forEach>
        </td>
        <td>
          <form class="action-form" data-action="requestEdit">
            <input type="hidden" name="taskId" value="${task.id}">
            <input type="hidden" name="userId" value="${entry.key.id}">
            <input type="submit" value="Request Edit">
          </form>
          <form class="action-form" data-action="requestDelete">
            <input type="hidden" name="taskId" value="${task.id}">
            <input type="hidden" name="userId" value="${entry.key.id}">
            <input type="submit" value="Request Delete">
          </form>
        </td>
      </tr>
    </c:forEach>
  </table>
</c:forEach>

<a href="${pageContext.request.contextPath}/tasks">Back to Manager View</a>

<script>
  document.addEventListener('DOMContentLoaded', function() {
    const statusDropdowns = document.querySelectorAll('.status-dropdown');
    statusDropdowns.forEach(dropdown => {
      dropdown.addEventListener('change', function() {
        const taskId = this.dataset.taskId;
        const userId = this.dataset.userId;
        const newStatus = this.value;
        const formData = new FormData();
        formData.append('taskId', taskId);
        formData.append('userId', userId);
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

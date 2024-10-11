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
<h1>All Employees Tasks</h1>
<p>Number of users: ${formattedTasksMap.size()}</p>

<c:forEach var="entry" items="${formattedTasksMap}">
  <h2>Tasks for ${entry.key.firstName} ${entry.key.lastName} (${entry.key.email})</h2>
  <p>Number of tasks: ${entry.value.size()}</p>
  <c:if test="${entry.value.size() > 0}">
    <table>
      <tr>
        <th>Title</th>
        <th>Description</th>
        <th>Due Date</th>
        <th>Status</th>
        <th>Tags</th>
        <th>Actions</th>
        <th>Pending Requests</th>
      </tr>
      <c:forEach var="task" items="${entry.value}">
        <tr>
          <td>${task.title}</td>
          <td>${task.description}</td>
          <td>${task.dueDate}</td>
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
            <button class="action-btn" data-action="requestEdit" data-task-id="${task.id}" data-user-id="${entry.key.id}">
              Request Edit
            </button>
            <button class="action-btn" data-action="requestDelete" data-task-id="${task.id}" data-user-id="${entry.key.id}">
              Request Delete
            </button>
          </td>
          <td>
            <c:forEach var="request" items="${task.pendingRequests}">
              ${request.type} request by ${request.requestedBy.firstName} ${request.requestedBy.lastName}<br>
            </c:forEach>
          </td>
        </tr>
      </c:forEach>
    </table>
  </c:if>
  <c:if test="${entry.value.size() == 0}">
    <p>No tasks for this user.</p>
  </c:if>
  <p>Replacement Tokens: ${entry.key.replacementTokens}</p>
  <p>Deletion Tokens: ${entry.key.deletionTokens}</p>
</c:forEach>

<a href="${pageContext.request.contextPath}/tasks">Back to Manager View</a>

<script>
  document.addEventListener('DOMContentLoaded', function() {
    const statusDropdowns = document.querySelectorAll('.status-dropdown');
    const actionButtons = document.querySelectorAll('.action-btn');

    statusDropdowns.forEach(dropdown => {
      dropdown.addEventListener('change', function() {
        sendRequest(this, 'updateStatus');
      });
    });

    actionButtons.forEach(button => {
      button.addEventListener('click', function(e) {
        e.preventDefault();
        sendRequest(this, this.dataset.action);
      });
    });

    function sendRequest(element, action) {
      const taskId = element.dataset.taskId;
      const userId = element.dataset.userId;

      console.log('Sending request:', { action, taskId, userId });

      if (!taskId || !userId) {
        alert('Missing taskId or userId');
        return;
      }

      const formData = new FormData();
      formData.append('taskId', taskId);
      formData.append('userId', userId);
      formData.append('action', action);

      if (action === 'updateStatus') {
        formData.append('status', element.value);
      }

      fetch('${pageContext.request.contextPath}/employee-tasks', {
        method: 'POST',
        body: formData
      })
              .then(response => response.json())
              .then(data => {
                if (data.success) {
                  alert('Action completed successfully!');
                  location.reload();
                } else {
                  alert(data.error || 'An error occurred');
                }
              })
              .catch(error => {
                console.error('Error:', error);
                alert('An error occurred. Please try again.');
              });
    }
  });
</script>
</body>
</html>

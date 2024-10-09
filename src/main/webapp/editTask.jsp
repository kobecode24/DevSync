<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Task</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Task/styles.css">
</head>
<body>
<c:if test="${not empty error}">
    <div class="error-message">${error}</div>
</c:if>

<h1>Edit Task</h1>
<form action="${pageContext.request.contextPath}/tasks/edit/${task.id}" method="post">
    <label for="title">Title:</label>
    <input type="text" id="title" name="title" value="${task.title}" required><br>

    <label for="description">Description:</label>
    <textarea id="description" name="description">${task.description}</textarea><br>

    <label for="dueDate">Due Date:</label>
    <input type="datetime-local" id="dueDate" name="dueDate" value="${task.dueDate}" required><br>

    <label for="status">Status:</label>
    <select id="status" name="status">
        <option value="TODO" ${task.status == 'TODO' ? 'selected' : ''}>To Do</option>
        <option value="IN_PROGRESS" ${task.status == 'IN_PROGRESS' ? 'selected' : ''}>In Progress</option>
        <option value="DONE" ${task.status == 'DONE' ? 'selected' : ''}>Done</option>
    </select><br>

    <label for="assignedUserId">Assign To:</label>
    <select id="assignedUserId" name="assignedUserId">
        <c:forEach var="user" items="${users}">
            <option value="${user.id}" ${task.assignedUser.id == user.id ? 'selected' : ''}>${user.firstName} ${user.lastName}</option>
        </c:forEach>
    </select><br>

    <label for="tags">Tags (comma-separated):</label>
    <input type="text" id="tags" name="tags" value="${tagsAsString}"><br>

    <input type="submit" value="Update Task" class="submit-btn">
</form>

<script>
    document.querySelector('form').addEventListener('submit', function(e) {
        var tags = document.getElementById('tags').value.split(',').filter(tag => tag.trim() !== '');
        if (tags.length < 2) {
            e.preventDefault();
            alert('Please enter at least 2 tags.');
        }
    });

    document.getElementById('dueDate').addEventListener('change', function() {
        var selectedDate = new Date(this.value);
        var now = new Date();
        var maxDate = new Date();
        maxDate.setDate(now.getDate() + 3);

        if (selectedDate < now) {
            alert('Due date cannot be in the past.');
            this.value = '';
        } else if (selectedDate > maxDate) {
            alert('Due date cannot be more than 3 days in the future.');
            this.value = '';
        }
    });
</script>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>User List</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<h2>User List</h2>

<table>
  <thead>
  <tr>
    <th>Id</th>
    <th>First Name</th>
    <th>Last Name</th>
    <th>Email</th>
    <th>Role</th>
    <th>Actions</th>
  </tr>
  </thead>
  <tbody>
  <c:forEach var="user" items="${users}">
    <tr>
      <td>${user.id}</td>
      <td>${user.firstName}</td>
      <td>${user.lastName}</td>
      <td>${user.email}</td>
      <td>${user.role}</td>
      <td>
        <form action="users" method="POST" style="display:inline;">
          <input type="hidden" name="action" value="delete">
          <input type="hidden" name="id" value="${user.id}">
          <input type="submit" value="Delete" class="delete-btn">
        </form>
        <a href="users?action=edit&id=${user.id}" class="edit-link">Edit</a>
      </td>
    </tr>
  </c:forEach>
  </tbody>
</table>

<a href="users/add" class="add-link">Add New User</a>

</body>
</html>

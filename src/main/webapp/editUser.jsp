<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit User</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/User/adduser.css">
</head>
<body>


<div class="container">

<h2>Edit User</h2>

<form action="users" method="POST">
    <input type="hidden" name="action" value="update">
    <input type="hidden" name="id" value="${user.id}">

    <div>
        <label for="firstName">First Name:</label>
        <input type="text" id="firstName" name="firstName" value="${user.firstName}" required>
    </div>

    <div>
        <label for="lastName">Last Name:</label>
        <input type="text" id="lastName" name="lastName" value="${user.lastName}" required>
    </div>

    <div>
        <label for="email">Email:</label>
        <input type="email" id="email" name="email" value="${user.email}" required>
    </div>

    <div>
        <label for="password">Password:</label>
        <input type="password" id="password" name="password" value="${user.password}" required>
    </div>

    <div>
        <label for="role">Role:</label>
        <select id="role" name="role" required>
            <option value="USER" ${user.role == 'USER' ? 'selected' : ''}>User</option>
            <option value="MANAGER" ${user.role == 'MANAGER' ? 'selected' : ''}>Manager</option>
        </select>
    </div>

    <div>
        <input type="submit" value="Update User">
    </div>
</form>

<a href="users">Back to User List</a>
</div>
</body>
</html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add New User</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>

<h2>Add New User</h2>

<form action="users" method="POST">
    <input type="hidden" name="action" value="add">

    <div>
        <label for="firstName">First Name:</label>
        <input type="text" id="firstName" name="firstName" required>
    </div>

    <div>
        <label for="lastName">Last Name:</label>
        <input type="text" id="lastName" name="lastName" required>
    </div>

    <div>
        <label for="email">Email:</label>
        <input type="email" id="email" name="email" required>
    </div>

    <div>
        <label for="password">Password:</label>
        <input type="password" id="password" name="password" required>
    </div>

    <div>
        <label for="role">Role:</label>
        <select id="role" name="role" required>
            <option value="USER">User</option>
            <option value="MANAGER">Manager</option>
        </select>
    </div>

    <div>
        <input type="submit" value="Add User">
    </div>
</form>

</body>
</html>
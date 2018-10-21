<%--
  Created by IntelliJ IDEA.
  User: 94545
  Date: 2018/10/15
  Time: 15:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Add User</title>
    <link rel="stylesheet" type="text/css" href="../../css/addUser.css">
</head>
<body>
<form action="${pageContext.request.contextPath}/addUser.action" method="post">
    <input type="hidden" name="method" value="register">
    <div id="register">
        <h1 style="text-align: center ">Add User</h1>
        <input type="text" name="username" placeholder="Username"/><br>
        <input type="password" name="password" placeholder="Password"/><br>
        <input class="button" style="text-align: center" type="submit" value="Add">
        <input class="button" style="text-align: center" type="reset" value="Reset">
    </div>
</form>
</body>
</html>

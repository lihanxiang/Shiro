<%--
  Created by IntelliJ IDEA.
  User: 94545
  Date: 2018/10/14
  Time: 22:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>User List</title>
    <link rel="stylesheet" type="text/css" href="../../css/addUser.css">
</head>
<body>
    <div>
        <table width="100%" border="1">
            <tr>
                <td>Username</td>
                <td>Role</td>
            </tr>
            <c:forEach items="${userList}" var="user">
                <tr>
                    <td>${user.username}</td>
                    <td>${user.role}</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/getUserInfo.action?id=${user.ID}">Edit</a>
                        <a href="${pageContext.request.contextPath}/deleteUser.action?id=${user.ID}">Delete</a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</body>
</html>

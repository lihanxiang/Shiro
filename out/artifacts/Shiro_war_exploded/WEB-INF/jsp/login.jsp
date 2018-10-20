<%--
  Created by IntelliJ IDEA.
  User: 94545
  Date: 2018/1/30
  Time: 14:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" type="text/css" href="../../../css/login.css">
    <script type="text/javascript">
        function RefreshCode(obj){
            obj.src = obj.src + "?code=" + Math.random();
        }
    </script>
</head>
<body>
<form action="${pageContext.request.contextPath}/login.action" method="post">
    <input type="hidden" name="method" value="login">
    <div id="login">
        <h1 style="text-align: center ">Login</h1>
        <p align="center" style="color:red;font-weight: 800">${message}</p>
        <p>
            <input type="text" name="username" placeholder="用户名" value="${user.username}"/>
        </p>
        <p>
            <input type="password" name="password" placeholder="密  码" value="${user.password}"/><br>
        </p>
        <p>
            <!--<input type="text" name="verifyCode" placeholder="验证码" size="10"/><br>
        <img id="verifyCode" src="getVerifyCode.action" title="点击更换" onclick="RefreshCode(this)"/><br>
        -->
        </p>
        <input class="button" type="submit" value="登录">
        <input class="button" type="reset" value="重置">
    </div>
</form>
</body>
</html>

<%--
  Created by IntelliJ IDEA.
  User: lfeng1
  Date: 2017/3/14 0014
  Time: 9:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>确认登录</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/socket/socket.io.js"></script>
    <script type="text/javascript">

    </script>
</head>
<body>
<h1>确认登录</h1>
<br/>

    <form action="<%=request.getContextPath()%>/dologin.html" method="post">
        <table>
            <tr>
                <td>
                    用户名：
                </td>
                <td>
                    <input title="用户名" name="userName" />
                </td>
            </tr>
            <tr>
                <td>
                    用户密码：
                </td>
                <td>
                    <input title="password" type="password" name="password">
                </td>
            </tr>
        </table>
        <input type="hidden" name="clientId" value="${clientId}">
        <button type="submit">提交</button>
    </form>

</body>

</html>

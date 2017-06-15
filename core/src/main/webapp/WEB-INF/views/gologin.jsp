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
    <title>扫码登录</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/socket/socket.io.js"></script>
    <script type="text/javascript">
        var socket = io.connect('http://119.23.243.79:8085');
        socket.emit('login', function () {
            $("#qrImg").html('<img id="img" alt="登录二维码" src="<%=request.getContextPath()%>/getQr.html"/>')
        });

        //连接
        socket.on('login', function (data) {
          //  timeDown(data);
            alert("欢迎："+data.userName);
        });

        
        function timeDown(data) {
            var c=5;//设置你要倒计时间
            timedCount(c,data);
            window.location.href=data;
        }
        

        function timedCount(c,data){
            $("#time").html("还有"+c+"秒跳转到.."+data);
            c=c-1;//重新赋值c
            if(c<=0){
               return ;
            }
            setTimeout("timedCount(c,data)",1000)//设置每秒运行一次函数
        }

    </script>
</head>
<body>
<h1>请扫码登录</h1>
<br/>
<div id="qrImg"></div>


<p id="time"></p>

</body>

</html>

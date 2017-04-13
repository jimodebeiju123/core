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
    <title>Socketio chat</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/socket/socket.io.js"></script>
    <style>
        body {
            padding: 20px;
        }
        #console {
            height: 400px;
            overflow: auto;
        }
        .username-msg {
            color: orange;
        }
        .connect-msg {
            color: green;
        }
        .disconnect-msg {
            color: red;
        }
        .send-msg {
            color: #888
        }
    </style>
</head>
<body>
<h1>简易聊天室</h1>
<br />
<div id="console" class="well"></div>
<form class="well form-inline" onsubmit="return false;">
    <input id="name" class="input-xlarge" type="text" placeholder="用户名称. . . " />
    <input id="msg" class="input-xlarge" type="text" placeholder="发送内容. . . " />
    <button type="button" onClick="sendMessage()" class="btn">Send</button>
    <button type="button" onClick="sendDisconnect()" class="btn">Disconnect</button>
    <button type="button" onClick="queryAll()" class="btn">查询所有在线用户</button>
    <button type="button" onClick="connectToUser()" class="btn">接入系统</button>
</form>
</body>
<script type="text/javascript">
    var socket = io.connect('http://localhost:8089');
    //连接
    socket.on('connect',function() {
        output('<span class="connect-msg">Client has connected to the server!</span>');
    });
    //点击开始
    socket.on('connectToUser',function(data) {
        output('<span class="connect-msg">欢迎'+data+' 接入系统</span>');
    });
    socket.on('chatevent', function(data) {
        output('<span class="username-msg">' + data.userName + ' : </span>' + data.message);
    });

    socket.on('queryall', function(data) {
        output('<span class="username-msg">当前在线用户 用户名: </span> '+data);
    });

    socket.on('disconnect',function() {
        output('<span class="disconnect-msg">The client has disconnected! </span>');
    });

    function sendDisconnect() {
        socket.disconnect();
    }
    function  queryAll() {
        socket.emit('queryall');
    }

    function  connectToUser() {
        var userName = $("#name").val();
        socket.emit('connectToUser',{
            user:userName
        });
    }

    function sendMessage() {
        var message = $('#msg').val();
        $('#msg').val('');
        var userName=$('#name').val();
        socket.emit('chatevent', {
            userName:userName,
            message : message
        });
    }

    function output(message) {
        var date=new Date();
        var currentTime = "<span class='time' >" +date+"</span>";
        var element = $("<div>" + currentTime + " " + message + "</div>");
        $('#console').prepend(element);
    }
</script>
</html>

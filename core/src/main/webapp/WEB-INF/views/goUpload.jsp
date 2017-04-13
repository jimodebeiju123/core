<%--
  Created by IntelliJ IDEA.
  User: lfeng1
  Date: 2017/2/16 0016
  Time: 10:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>上传测试</title>

    <script type="text/javascript" src="<%=request.getContextPath()%>/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/uploadify/my.jquery.uploadify.js"></script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/uploadify/uploadify.css" />
    <script type="text/javascript" >
        $(function() {
            initUpload("file_upload_1");
            initUpload("file_upload_2");
        });

        function  initUpload(id){
            $("#"+id).uploadify({
                buttonText:'上传文件',
                //  fileSizeLimit:'40MB',//文件限制
//                fileTypeDesc : 'Image Files',
//                fileTypeExts : '*.gif; *.jpg; *.png; *.ARW',
                removeCompleted:false,//是否移除完成的
                auto     : true,//是否自动上传
                height        : 30,
                swf           : '<%=request.getContextPath()%>/uploadify/uploadify.swf',
                uploader      : 'doUpload.html',//上传文件处理地址
                width         : 120,
                progressData : 'percentage',//进度条显示的数据
                onCancel : function(file) {
                    //  alert('The file ' + file.name + ' was cancelled.');
                },
                //上传成功保存id
                onUploadSuccess : function(file, data, response) {
                    //此处可以将返回的id保存起来
                    var json= eval('(' + data + ')');
                    alert(json.name);
                   // alert(file.id+"|||||"+data);
                },
                //队列中的数据上传完成之后执行
                onQueueComplete : function(queueData) {
                    if(queueData.uploadsErrored >1){
                        //存在失败的数据
                        return ;
                    }
                    //此处可以设置别的表单form是否可以提交。
                },
                fileObjName:'file'//表单文件参数名称
                // overrideEvents:['onCancel'],
            });
        }


    </script>
</head>
<body>
<div>
    <input id="file_upload_1" type="file" />
    <a href="javascript:$('#file_upload_1').uploadify('cancel')">取消第一个</a> |
    <a href="javascript:$('#file_upload_1').uploadify('cancel', '*')">取消所有</a> |
    <a href="javascript:$('#file_upload_1').uploadify('upload', '*')">上传所有</a>

    <input id="file_upload_2" type="file" />


    <p>img</p>
    <img src="${pageContext.request.contextPath}/getQr.html"/>
</div>



</body>
</html>



/**
 * 版权所有(C) 2015 深圳雁联计算系统有限公司
 * 创建：ZhangLinFeng  2017/2/17 0017
 */
package com.controller;

import com.socket.SocketServerHandler;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * @author ZhangLinFeng
 * @name UploadController
 * @data 2017/2/17 0017
 */
@Controller
public class UploadController {

    private Logger logger= LoggerFactory.getLogger(UploadController.class);

    @Resource(name="socketServerHandler")
    private SocketServerHandler socketServerHandler;

    @RequestMapping("/goUpload")
    public  String  goUpload(){
        return "goUpload";
    }

    @Resource(name="acceptCache")
    private Cache cache;

    @RequestMapping("/doUpload1")
    @ResponseBody
    public  String  doUpload1(){
        Element element=cache.get("ccm");
        String user= (String) element.getObjectKey();
        String msg= (String) element.getObjectValue();
        socketServerHandler.send(user,msg);
        return "doUpload";
    }

    @RequestMapping("/doUpload")
    @ResponseBody
    public  String  doUpload(@RequestParam("file")MultipartFile file,HttpServletRequest request){
        if(file.isEmpty()){
            return "error";
        }
        try{
            String filePath =   "/"+file.getOriginalFilename();
            // 转存文件
            file.transferTo(new File(filePath));
            //文件名称可以通过uuid 重新进行编码
            return "{'ddd':'ddddd','name':'"+file.getOriginalFilename()+"'}";
        }catch(Exception e){
            e.printStackTrace();
            return "error";
        }
    }


    @RequestMapping("/goNetty")
    public  String  goNetty(){
        logger.info("测试111111111111111111111111111111");
       return "netty";
    }




}

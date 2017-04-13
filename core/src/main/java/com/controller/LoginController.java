/**
 * 版权所有(C) 2015 深圳雁联计算系统有限公司
 * 创建：ZhangLinFeng  2017/3/28 0028
 */
package com.controller;

import com.ehcache.EhcacheService;
import com.redis.UserCacheService;
import com.redis.dao.UserDao;
import com.socket.SocketServerHandler;
import com.utils.QrUtil;
import dto.UserDto;
import net.sf.ehcache.Cache;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;

/**
 * @author ZhangLinFeng
 * @name LogionController
 * @data 2017/3/28 0028
 * 登录
 */
@Controller
public class LoginController {
    private Logger logger= LoggerFactory.getLogger(LoginController.class);

    @Resource(name="socketServerHandler")
    private SocketServerHandler socketServerHandler;
    @Resource(name="ehcacheService")
    private EhcacheService ehcacheService;
    @Resource(name="userCacheService")
    private UserCacheService userCacheService;
    @Resource(name="userDao")
    private UserDao userDao;

    @Resource(name="acceptCache")
    private Cache cache;

    @RequestMapping("/gologin")
    public  String  goLogin(){
        return "gologin";
    }

    @RequestMapping("/gologinbywx")
    public  String  goLoginByWx(Model model,String clientId){
        model.addAttribute("clientId",clientId);
        return "gologinbywx";
    }

    @RequestMapping("/dologin")
    @ResponseBody
    public  String    doLogin(String clientId, UserDto user){
        if(user==null|| StringUtils.isEmpty(clientId)){
            return "error";
        }
        //验证用户名和密码
        UserDto u= userDao.selectById(user.getUserName());
        if(u!=null&&u.getPassword().equals(user.getPassword())){
            logger.info("当前客户验证成功开始执行登录。");
            //执行登录  浏览器跳转
            socketServerHandler.login(clientId,user);
            return "success";
        }

        return "error";

    }


    @RequestMapping("/getQr")
    public  void  getQr(HttpServletRequest request, HttpServletResponse response){
        ServletOutputStream out = null;
        try {
            String uuid= (String) cache.get("login").getObjectValue();
            out = response.getOutputStream();
            QrUtil.createQr("http://192.168.123.170:8080/core/gologinbywx.html?clientId="+uuid,out);
            out.flush();
        } catch (FileNotFoundException e) {
            logger.error("文件读取失败,文件不存在",e);
        } catch (Exception e) {
            logger.error("文件流输出异常",e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
}

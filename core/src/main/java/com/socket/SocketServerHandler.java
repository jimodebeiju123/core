/**
 * 版权所有(C) 2015 深圳雁联计算系统有限公司
 * 创建：ZhangLinFeng  2017/3/13 0013
 */
package com.socket;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import dto.UserDto;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.ChatObject;
import pojo.UserPojo;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author ZhangLinFeng
 * @name SocketServerHandler
 * @data 2017/3/13 0013
 */
public class SocketServerHandler {
    private Logger logger= LoggerFactory.getLogger(SocketServerHandler.class);
    private SocketIOServer server;
    private String hostName="localhost";
    private int port=8080;
    private String eventName="liaotian";

    @Resource(name="acceptCache")
    private Cache cache;

    /**
     * 初始化
     */
    public void init(){
        Configuration config = new Configuration();
        config.setHostname(hostName);
        config.setPort(port);
        server=new SocketIOServer(config);

        /**
         * 连接时获取用户信息
         */
        server.addEventListener("connectToUser", UserPojo.class, (client, data, ackSender) -> {
            data.setSessionId(client.getSessionId());
            cache.put(new Element(data.getSessionId(),data));
            server.getBroadcastOperations().sendEvent("connectToUser",data.getUser());
        });

        //登录
        server.addEventListener("login", String.class, (client, data, ackSender) -> {
            cache.put(new Element("login",client.getSessionId().toString()));
        });

        /**
         * 获取所有用户
         */
        server.addEventListener("queryall", Object.class, new DataListener<Object>() {
            @Override
            public void onData(SocketIOClient client, Object data, AckRequest ackSender) throws Exception {
                List<String> userPojos=new ArrayList<>();
                Map<Object, Element> map= cache.getAll(cache.getKeys());
                for (Object o : map.keySet()) {
                    Element ele=map.get(o);
                    UserPojo pojo= (UserPojo) ele.getObjectValue();
                    userPojos.add(pojo.getUser());
                }
                client.sendEvent("queryall",userPojos);
            }
        });

        server.addEventListener(eventName, ChatObject.class, new DataListener<ChatObject>() {
            @Override
            public void onData(SocketIOClient client, ChatObject data, AckRequest ackSender) throws Exception {
//                ChatObject old= (ChatObject) cache.get(client.getSessionId()).getObjectValue();
//                old.setUserName(data.getUserName());
//                old.setMessage(data.getMessage());
//                old.setDate(new Date());
                if(data==null){
                   return ;
                }
                UserPojo user=new UserPojo();
                Map<Object, Element> map= cache.getAll(cache.getKeys());
                for (Object o : map.keySet()) {
                    Element ele=map.get(o);
                    UserPojo pojo= (UserPojo) ele.getObjectValue();
                    if(pojo.getUser().equals(data.getUserName())){
                        user=pojo;
                    }
                }
                SocketIOClient recClient=server.getClient(user.getSessionId());
                //查找出对手方客户端，如果为空则全部发送
                if(recClient==null){
                    server.getBroadcastOperations().sendEvent(eventName, data);
                }else{
                    //获取当前用户
                    UserPojo u= (UserPojo) cache.get(client.getSessionId()).getObjectValue();
                    data.setUserName(u.getUser());
                    recClient.sendEvent(eventName,data);
                    client.sendEvent(eventName,data);
                }

            }
        });
        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient client) {
                cache.remove(client.getSessionId());
            }
        });


        server.start();
    }


    public void send(String  userName,String msg){
//        ChatObject chatObject=null;//user.get(userName);
//        if(chatObject==null){
//            return ;
//        }
//        chatObject.setMessage(msg);
//        SocketIOClient client=server.getClient();
//        client.sendEvent(eventName,chatObject);

    }


    /**
     * 登录
     * @param clientId
     */
    public void login(String clientId, UserDto user){
//        Element element=cache.get(clientId);
//        if(element==null){
//            logger.info("没有该客户端!");
//            throw new RuntimeException("没有该客户端");
//        }
        SocketIOClient client=server.getClient(UUID.fromString(clientId));
        if(client!=null){
            client.sendEvent("login",user);
        }

    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }
}

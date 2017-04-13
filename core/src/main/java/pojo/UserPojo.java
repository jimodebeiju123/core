/**
 * 版权所有(C) 2015 深圳雁联计算系统有限公司
 * 创建：ZhangLinFeng  2017/3/16 0016
 */
package pojo;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author ZhangLinFeng
 * @name UserPojo
 * @data 2017/3/16 0016
 */
public class UserPojo implements Serializable {
    private static final long serialVersionUID = -2248384373768226607L;

    private UUID  sessionId=UUID.randomUUID();

    private String user;


    public UserPojo(){

    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}

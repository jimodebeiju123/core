/**
 * 版权所有(C) 2015 深圳雁联计算系统有限公司
 * 创建：ZhangLinFeng  2017/4/12 0012
 */
package dto;

import java.io.Serializable;

/**
 * @author ZhangLinFeng
 * @name UserDto
 * @data 2017/4/12 0012
 */
public class UserDto implements Serializable {
    private static final long serialVersionUID = -4648077081489178132L;

    private String id;

    private String userName;

    private String password;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

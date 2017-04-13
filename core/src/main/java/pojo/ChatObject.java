/**
 * 版权所有(C) 2015 深圳雁联计算系统有限公司
 * 创建：ZhangLinFeng  2017/3/13 0013
 */
package pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ZhangLinFeng
 * @name ChatObject
 * @data 2017/3/13 0013
 */
public class ChatObject implements Serializable{

    private static final long serialVersionUID = 9101340216372321964L;
    private String userName;
    private String message;

    private Date date;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

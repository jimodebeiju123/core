/**
 * 版权所有(C) 2015 深圳雁联计算系统有限公司
 * 创建：ZhangLinFeng  2017/2/16 0016
 */
package pojo;

import java.io.Serializable;

/**
 * @author ZhangLinFeng
 * @name Person
 * @data 2017/2/16 0016
 */
public class Person implements Serializable {

    private static final long serialVersionUID = -4168572831795701297L;

    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

   public void info(){
       System.out.println("name:"+name+";age:"+age);
   }
}

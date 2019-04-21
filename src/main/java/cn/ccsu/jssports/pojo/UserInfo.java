/*
 * Created by Long Duping
 * Date 2019-03-23 14:28
 */
package cn.ccsu.jssports.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserInfo implements Serializable {

    private Long id;
    private String openId;
    private String nickName;
    private String avatarUrl;
    private int gender;
    private String city;
    private String province;
    private String country;
    private Integer roleId;
    private String jwcAccount;
    private String realName;
    private Date createTime;
    private Date lastLoginTime;
}

/*
 * Created by Long Duping
 * Date 2019-04-04 16:20
 */
package cn.ccsu.jssports.pojo;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class Session {
    private String openId;
    private String sessionKey;
    private int expiredIn;
    private Date updateTime;
}

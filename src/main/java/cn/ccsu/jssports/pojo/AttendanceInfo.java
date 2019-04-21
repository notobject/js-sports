/*
 * Created by Long Duping
 * Date 2019-03-24 13:15
 */
package cn.ccsu.jssports.pojo;

import lombok.Data;
import lombok.ToString;

/**
 * @author Longduping
 */
@Data
@ToString
public class AttendanceInfo {
    private Long id;
    /**
     * openId
     */
    private String openId;
    /**
     * 打卡时间
     */
    private Long attendanceTime;
    /**
     * 步数
     */
    private Long step;
}

/*
 * Created by Long Duping
 * Date 2019-03-24 12:33
 */
package cn.ccsu.jssports.service.main;

import cn.ccsu.jssports.exception.AppException;
import cn.ccsu.jssports.pojo.AttendanceInfo;

import java.util.Map;

public interface MainService {

    boolean attendance(AttendanceInfo attendanceInfo) throws AppException;

    Map<String, Object> getRecordList(String openId) throws AppException;

    Map<String, Object> getTop(String openId, int count) throws AppException;

    Map<String, Object> getStep(String openId, String encryptedData, String iv) throws AppException;
}

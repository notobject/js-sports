/*
 * Created by Long Duping
 * Date 2019-03-24 13:21
 */
package cn.ccsu.jssports.dao;

import cn.ccsu.jssports.pojo.AttendanceInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MainMapper {
    boolean isAttendcanced(@Param("openId") String openId, @Param("leftTime") long leftTime, @Param("rightTime") long rightTime);

    int insert(AttendanceInfo attendanceInfo);

    List<AttendanceInfo> selectList(@Param("openId") String openId, @Param("leftTime") long leftTime, @Param("rightTime") long rightTime);

    List<AttendanceInfo> selectList(@Param("openId") String openId, @Param("leftTime") long leftTime);

    List<AttendanceInfo> selectList(@Param("openId") String openId);

    void deleteById(@Param("id") int id);
}

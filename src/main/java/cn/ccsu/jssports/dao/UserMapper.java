/*
 * Created by Long Duping
 * Date 2019-03-23 14:26
 */
package cn.ccsu.jssports.dao;

import cn.ccsu.jssports.pojo.Session;
import cn.ccsu.jssports.pojo.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {

    UserInfo selectByOpenId(@Param("openId") String openId);

    boolean isExist(@Param("openId") String openId);

    Boolean updateLastLoginTime(String openId);

    int insert(UserInfo ui);

    List<String> selectOpenIdList(@Param("pageIndex")int pageIndex, @Param("pageCount")int pageCount);

    boolean isSessionExist(@Param("openId") String openId);

    void updateSession(String openId, String sessionKey);

    void insertSession(String openId, String sessionKey);

    Session selectSession(String openId);

    void updateBindInfo(UserInfo user);
}

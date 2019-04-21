/*
 * Created by Long Duping
 * Date 2019-03-23 13:19
 */
package cn.ccsu.jssports.service.user;

import cn.ccsu.jssports.pojo.UserInfo;
import cn.ccsu.jssports.exception.AppException;
import cn.ccsu.jssports.pojo.WxLoginInfo;

import java.util.Map;

public interface UserService {
    Map<String, String> login(String code, String rawData) throws AppException;

    UserInfo getUser(String openId) throws AppException;

    UserInfo bind(String openId, String realName, String jwcAccount) throws AppException;
}

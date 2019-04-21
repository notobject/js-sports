/*
 * Created by Long Duping
 * Date 2019-03-23 13:20
 */
package cn.ccsu.jssports.service.user;

import cn.ccsu.jssports.cnt.Const;
import cn.ccsu.jssports.cnt.ErrorConst;
import cn.ccsu.jssports.controller.UserController;
import cn.ccsu.jssports.dao.UserMapper;
import cn.ccsu.jssports.exception.AppException;
import cn.ccsu.jssports.pojo.UserInfo;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service("userService")
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Value("${wx.app-id: wx5069986eaaebf1b3}")
    private String appId;
    @Value("${wx.app-secret: 215ad6e4c87995269e9d0b5514c500f5}")
    private String appSecret;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserMapper userMapper;


    @Override
    public Map<String, String> login(String code, String rawData) throws AppException {
        Map<String, String> resMap = new HashMap<>();
        if (Strings.isEmpty(code)) {
            throw new AppException(ErrorConst.PARAM_ERROR, "code can not be empty!");
        }
        if (Strings.isEmpty(rawData)) {
            throw new AppException(ErrorConst.PARAM_ERROR, "rawData can not be empty!");
        }
        Map<String, String> res = code2session(code, appId, appSecret);
        if (null == res || res.size() == 0) {
            throw new AppException(ErrorConst.SYSTEM_ERROR);
        }
        String openId = res.get("openId");
        resMap.put("openId", openId);
        // 用户是否存在
        if (userMapper.isExist(openId)) {
            // 记录最后登录时间
            logger.info("user exist, updateLastLoginTime");
            userMapper.updateLastLoginTime(openId);
        } else {
            logger.info("new user, insert into database");
            JSONObject json = JSONObject.parseObject(rawData);
            // 记录用户
            UserInfo ui = new UserInfo();
            ui.setOpenId(openId);
            ui.setNickName(json.getString("nickName"));
            ui.setAvatarUrl(json.getString("avatarUrl"));
            ui.setCity(json.getString("city"));
            ui.setCountry(json.getString("country"));
            ui.setGender(json.getInteger("gender"));
            ui.setProvince(json.getString("province"));
            ui.setRoleId(0);
            ui.setJwcAccount("");
            ui.setRealName("");
            ui.setCreateTime(new Date(System.currentTimeMillis()));
            ui.setLastLoginTime(new Date(System.currentTimeMillis()));
            userMapper.insert(ui);
        }
        // 保存Session
        String sessionKey = res.get("sessionKey");

        if (userMapper.isSessionExist(openId)) {
            userMapper.updateSession(openId, sessionKey);
            logger.info("update session_key:" + sessionKey);
        } else {
            userMapper.insertSession(openId, sessionKey);
            logger.info("insert session_key:" + sessionKey);
        }
        return resMap;
    }

    @Override
    public UserInfo getUser(String openId) throws AppException {
        UserInfo userInfo = userMapper.selectByOpenId(openId);
        if (null == userInfo) {
            throw new AppException(ErrorConst.USER_NOT_EXIST, openId);
        }
        return userInfo;
    }

    @Override
    public UserInfo bind(String openId, String realName, String jwcAccount) throws AppException {
        UserInfo user = this.getUser(openId);
        user.setRealName(realName);
        user.setJwcAccount(jwcAccount);
        userMapper.updateBindInfo(user);
        return user;
    }

    private Map<String, String> code2session(String code, String appId, String appSecret) throws AppException {
        Map<String, String> res = new HashMap<>();
        URI uri = UriComponentsBuilder.fromUriString(Const.WXAPI.CODE_TO_SESSION)
                .build()
                .expand(appId, appSecret, code)
                .encode()
                .toUri();
        ResponseEntity<String> entity = restTemplate.getForEntity(uri, String.class);
        if (HttpStatus.OK == entity.getStatusCode()) {
            JSONObject json = JSONObject.parseObject(entity.getBody());
            int errcode = json.getIntValue("errcode");
            if (Const.WXAPI.SUCCES_CODE == errcode) {
                res.put("openId", json.getString("openid"));
                res.put("sessionKey", json.getString("session_key"));
                return res;
            } else {
                throw new AppException(ErrorConst.CODE2SESSION_TRANS_ERROR, json.getString("errmsg"));
            }
        }
        throw new AppException(ErrorConst.HTTP_CONNECT_ERROR, uri.toString());
    }
}

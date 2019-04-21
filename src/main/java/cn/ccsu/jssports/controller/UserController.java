/*
 * Created by Long Duping
 * Date 2019-03-23 13:11
 */
package cn.ccsu.jssports.controller;

import cn.ccsu.jssports.cnt.ErrorConst;
import cn.ccsu.jssports.exception.AppException;
import cn.ccsu.jssports.pojo.BaseRes;
import cn.ccsu.jssports.pojo.UserInfo;
import cn.ccsu.jssports.service.user.UserService;
import cn.ccsu.jssports.util.Resp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
@Api(value = "用户相关", description = "登录，获取...")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Resource(name = "userService")
    private UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiOperation(value = "用户登录")
    public BaseRes login(@RequestParam String code, @RequestParam String rawData) throws AppException {
        logger.info("login req code={}, rawData={}", code, rawData);
        Map<String, String> resMap = userService.login(code, rawData);
        if (resMap != null && resMap.size() > 0) {
            log.info("login resp {}", resMap);
            return Resp.success(resMap);
        } else {
            throw new AppException(ErrorConst.LOGIN_FAILED);
        }
    }

    @RequestMapping(value = "/{openId}", method = RequestMethod.GET)
    @ApiOperation(value = "获取单个用户")
    public BaseRes getUser(@PathVariable String openId) throws AppException {
        log.info("getUser resp openId={}", openId);
        UserInfo user = userService.getUser(openId);
        log.info("getUser resp user={}", user);
        return Resp.success(user);
    }


    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "用户信息修改")
    public BaseRes updateUser(@RequestParam String openId, @RequestParam String realName, String jwcAccount) throws AppException {
        log.info("updateUser req openId={}, realName={}， jwcAccount={}", openId, realName, jwcAccount);
        UserInfo userInfo = userService.bind(openId, realName, jwcAccount);
        log.info("updateUser resp {}", userInfo);
        return Resp.success(userInfo);
    }
}

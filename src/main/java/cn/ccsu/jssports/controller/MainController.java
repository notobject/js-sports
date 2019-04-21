/*
 * Created by Long Duping
 * Date 2019-03-24 12:30
 */
package cn.ccsu.jssports.controller;

import cn.ccsu.jssports.cnt.ErrorConst;
import cn.ccsu.jssports.conf.AppConfig;
import cn.ccsu.jssports.exception.AppException;
import cn.ccsu.jssports.pojo.AttendanceInfo;
import cn.ccsu.jssports.pojo.BaseRes;
import cn.ccsu.jssports.service.main.MainService;
import cn.ccsu.jssports.util.Resp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@Api("主要功能")
@RequestMapping("/main")
public class MainController {
    @Autowired
    private MainService mainService;

    @RequestMapping(value = "/attendance", method = RequestMethod.POST)
    @ApiOperation("打卡")
    public BaseRes attendance(AttendanceInfo attendanceInfo) throws AppException {
        if (System.currentTimeMillis() < AppConfig.beginDate().getTimeInMillis()) {
            throw new AppException(ErrorConst.NOT_START, AppConfig.activityBeginDate());
        }
        log.info("attendance req ={}", attendanceInfo);
        if (mainService.attendance(attendanceInfo)) {
            log.info("attendance resp openId={}", attendanceInfo.getOpenId());
            return Resp.success(null);
        }
        throw new AppException(ErrorConst.SYSTEM_ERROR);
    }

    @RequestMapping(value = "/list/{openId}", method = RequestMethod.GET)
    @ApiOperation("我的打卡记录")
    public BaseRes getList(@PathVariable("openId") String openId) throws AppException {
        log.info("getList req  openId={}", openId);
        Map<String, Object> map = mainService.getRecordList(openId);
        if (map == null || map.size() == 0) {
            throw new AppException(ErrorConst.SYSTEM_ERROR);
        }
        log.info("getList resp  map={}", map);
        return Resp.success(map);
    }

    @RequestMapping(value = "/top/{count}", method = RequestMethod.GET)
    @ApiOperation("TOP排行榜")
    public BaseRes getTop(@PathVariable("count") int count, @RequestParam String openId) throws AppException {
        if (count <= 0 || count > 100) {
            throw new AppException(ErrorConst.PARAM_ERROR, "count范围为(0,100]");
        }
        if (Strings.isEmpty(openId)) {
            throw new AppException(ErrorConst.PARAM_ERROR, "openId can not be empty!");
        }
        log.info("getTop req  openId={},count={}", openId, count);
        Map<String, Object> top = mainService.getTop(openId, count);
        if (top == null || top.size() == 0) {
            throw new AppException(ErrorConst.NO_TOP_RANK);
        }
        log.info("getTop resp  size={}", top.size());
        return Resp.success(top);
    }


    @ApiOperation("获取今日步数")
    @RequestMapping(value = "/step")
    public BaseRes getStep(String openId, String encryptedData, String iv) throws AppException {
        log.info("getStep req openId={}", openId);
        Map<String, Object> map = mainService.getStep(openId, encryptedData, iv);
        if (map != null && map.size() > 0) {
            log.info("getStep resp map={}", map);
            return Resp.success(map);
        }
        return Resp.failed(ErrorConst.GET_STEP_FAILED, "map is empty");
    }


    @ApiOperation("配置")
    @RequestMapping(value = "/config")
    public BaseRes config(String beginDate, Integer stepCount, Integer dayCount) throws AppException {
        AppConfig.config(stepCount, beginDate, dayCount);
        String msg = "";
        msg += "beginDate=" + AppConfig.activityBeginDate() + ", ";
        msg += "activityDayCount=" + AppConfig.activityDayCount() + ", ";
        msg += "minStep=" + AppConfig.minStep() + ", ";
        log.info("活动配置 " + msg);
        log.info("config changed: {}", msg);
        return Resp.success("设置成功 " + msg);
    }
}

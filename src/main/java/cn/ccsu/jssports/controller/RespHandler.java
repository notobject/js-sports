/*
 * Created by Long Duping
 * Date 2019-03-23 13:29
 */
package cn.ccsu.jssports.controller;

import cn.ccsu.jssports.cnt.ErrorConst;
import cn.ccsu.jssports.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class RespHandler {
    @ExceptionHandler(AppException.class)
    @ResponseBody
    public Map<String, Object> handler(AppException e) {
        Map<String, Object> res = new HashMap<>();
        res.put("errcode", e.getErrcode());
        res.put("errmsg", e.getErrmsg());
        log.info("resp: errcod={},errmsg={}", e.getErrcode(), e.getErrmsg());
        return res;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Map<String, Object> handler(Exception e) {
        e.printStackTrace();
        Map<String, Object> res = new HashMap<>();
        res.put("errcode", ErrorConst.SYSTEM_ERROR.getErrcode());
        res.put("errmsg", ErrorConst.SYSTEM_ERROR.getErrmsg() + ": " + e.getMessage());
        log.info("resp: errcod={},errmsg={}", ErrorConst.SYSTEM_ERROR.getErrcode(), ErrorConst.SYSTEM_ERROR.getErrmsg() + ": " + e.getMessage());
        return res;
    }
}

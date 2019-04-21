/*
 * Created by Long Duping
 * Date 2019-03-23 13:34
 */
package cn.ccsu.jssports.cnt;

public enum ErrorConst {
    // 成功
    SUCCES(0, "succes"),
    SYSTEM_ERROR(10000, "未知的程序错误"),
    CODE2SESSION_TRANS_ERROR(10001, "CODE换取OPENID失败"),
    HTTP_CONNECT_ERROR(10002, "HTTP请求失败"),
    PARAM_ERROR(10003, "参数错误"),
    USER_NOT_EXIST(10004, "该用户不存在"),
    LOGIN_FAILED(10005, "登录失败"),
    ATTENDANCE_ERROR(10006, "打卡失败"),
    PARSE_ERROR(10007, "解析错误"),
    HAS_NO_RESULT(10008, "搜索不到结果"),
    GET_STEP_FAILED(10009, "获取步数失败"),
    SESSION_EXPIRED(10010, "Session Key 已过期，请重新登录"),
    NO_TOP_RANK(10011, "当前签到的人数还不足以产生排行榜"),
    STEP_DATA_PARSE_ERROR(10012, "步数数据解析失败"),
    ATTENDANCE_ERROR_STEP_LESS(10013, "打卡失败"),
    ATTENDANCE_ERROR_TIME_VALIED(10014, "打卡失败"),
    ATTENDANCE_ERROR_REPEATED(10015, "打卡失败"),
    ATTENDANCE_ERROR_INSERT_TO_DATABASE(10016, "打卡失败"),
    NOT_START(10017, "活动暂未开始");
    private Integer errcode;
    private String errmsg;

    ErrorConst(Integer errcode, String errmsg) {
        this.errcode = errcode;
        this.errmsg = errmsg;
    }

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

}

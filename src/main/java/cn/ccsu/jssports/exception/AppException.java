/*
 * Created by Long Duping
 * Date 2019-03-23 13:32
 */
package cn.ccsu.jssports.exception;

import cn.ccsu.jssports.cnt.ErrorConst;

public class AppException extends Exception {
    private Integer errcode;
    private String errmsg;

    public AppException() {
        super();
    }

    public AppException(int errcode, String errmsg) {
        this.errcode = errcode;
        this.errmsg = errmsg;
    }

    public AppException(ErrorConst ec) {
        this.errcode = ec.getErrcode();
        this.errmsg = ec.getErrmsg();
    }

    public AppException(ErrorConst ec, String etc) {
        this.errcode = ec.getErrcode();
        this.errmsg = ec.getErrmsg() + " : " + etc;
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

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}

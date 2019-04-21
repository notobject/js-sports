/*
 * Created by Long Duping
 * Date 2019-03-23 15:37
 */
package cn.ccsu.jssports.util;

import cn.ccsu.jssports.cnt.ErrorConst;
import cn.ccsu.jssports.pojo.BaseRes;

public class Resp {

    public static BaseRes success(Object obj) {
        if (null == obj) {
            obj = "";
        }
        return new BaseRes(0, "success", obj);
    }

    public static BaseRes failed(ErrorConst ec, Object obj) {
        if (null == obj)
            obj = "";
        return new BaseRes(ec.getErrcode(), ec.getErrmsg(), obj);
    }
}

/*
 * Created by Long Duping
 * Date 2019-03-23 15:35
 */
package cn.ccsu.jssports.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseRes {

    private Integer errcode;
    private String errmsg;
    private Object data;
}

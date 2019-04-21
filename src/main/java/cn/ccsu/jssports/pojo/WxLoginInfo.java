/*
 * Created by Long Duping
 * Date 2019-03-23 17:13
 */
package cn.ccsu.jssports.pojo;

import lombok.Data;

@Data
public class WxLoginInfo {
    private String code;            // 微信Code，调用 wx.login 得到
    private String rawData;         // 不包括敏感信息的原始数据字符串，用于计算签名
    private String signature;       //使用 sha1( rawData + sessionkey ) 得到字符串，用于校验用户信息
    private String encryptedData;   //包括敏感数据在内的完整用户信息的加密数据
    private String iv;              //加密算法的初始向量
}


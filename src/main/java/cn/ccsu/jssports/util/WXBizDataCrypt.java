package cn.ccsu.jssports.util;

import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.nio.charset.StandardCharsets;

/***
 * 用户敏感数据解密
 * @author Mr.Long
 *
 */
public class WXBizDataCrypt {
	
	/**
	 * 
	 * @param encryptedData 
	 * @param iv            
	 * @param sessionKey
	 * @return json
	 */
	public static JSONObject decodeCryptedData(String sessionKey, String encryptedData, String iv){
	    try {
	        byte[] resultByte = AESUtil.instance.decrypt(Base64.decode(encryptedData), Base64.decode(sessionKey), Base64.decode(iv));
	        if(null != resultByte && resultByte.length > 0){
	            String jsonStr = new String(resultByte, StandardCharsets.UTF_8);
	            JSONObject json = JSONObject.parseObject(jsonStr);
	            return json;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		return null;
	}
}

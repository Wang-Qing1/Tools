package org.tools.common;

import java.security.MessageDigest;

public class MD5Tools {
    /**
     * MD5 加密
     * @param strs 明文字符串
     * @return
     */
    public static String encrypt( String strs ) throws Exception {
        StringBuffer sb = new StringBuffer();
        try{
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bs = digest.digest(strs.getBytes());
            for (byte b : bs) {
                int x = b & 255;
                String s = Integer.toHexString(x);
                if( x > 0 && x < 16 ){
                    sb.append("0");
                    sb.append(s);
                }else{
                    sb.append(s);
                }
            }
        }catch( Exception e){
            throw new Exception("加密失败！");
        }
        return sb.toString();
    }
}

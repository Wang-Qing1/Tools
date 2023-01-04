package org.tools.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The type Data format tools.
 */
public class DataFormatTools {
    public static void main(String[] args) {
        System.out.println(dataFormat("yyyy-MM-dd HH:mm:ss", new Date()));
    }

    /**
     * 日期时间格式化
     * @param format    格式化字符串，例如：yyyy-MM-dd HH:mm:ss
     * @param date      日期对象
     * @return
     */
    public static String dataFormat(String format, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }
}

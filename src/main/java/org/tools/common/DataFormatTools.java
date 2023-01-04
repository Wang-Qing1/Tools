package org.tools.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The type Data format tools.
 */
public class DataFormatTools {
    public static void main(String[] args) throws ParseException {
        System.out.println(dataFormat("yyyy-MM-dd HH:mm:ss", new Date()));
        System.out.println(dataFormat("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()));
        System.out.println(dataFormat("yyyy-MM-dd HH:mm:ss", "2023-01-04 14:56:43"));
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

    /**
     * 日期时间格式化
     * @param format        格式化字符串，例如：yyyy-MM-dd HH:mm:ss
     * @param timeMills     时间戳
     * @return
     */
    public static String dataFormat(String format, Long timeMills) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(timeMills);
    }

    /**
     * 日期时间格式化
     * @param format    格式化字符串，例如：yyyy-MM-dd HH:mm:ss
     * @param dateStr   格式化日期字符串
     * @return
     * @throws ParseException
     */
    public static Date dataFormat(String format, String dateStr) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.parse(dateStr);
    }
}

package com.hsj.redis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hanhansongjiang on 17/6/19.
 */
public class DateUtil {

    public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";



    /**
     * 获得当前时间，格式自定义
     *
     * @param
     * @return
     */
    public static String getCurrentDate( ) {
        Calendar day = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
        return sdf.format(day.getTime());

    }


    /**
     * 按照时间字符串和格式转换成Date类
     *
     * @author lihe 2013-7-4 下午5:21:50
     * @param date
     * @return
     * @throws ParseException
     * @see
     * @since
     */
    public static Date getDate(String date)  {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }




}

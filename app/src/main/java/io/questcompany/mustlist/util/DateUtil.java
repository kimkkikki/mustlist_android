package io.questcompany.mustlist.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kimkkikki on 2016. 10. 20..
 * Date Util
 */

public class DateUtil {

    public static String getDateStringWithDay(int day) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.add(Calendar.DATE, day);

        date = calendar.getTime();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);

        return simpleDateFormat.format(date);
    }

    public static String getDateStringWithWeek(int week) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.add(Calendar.WEEK_OF_YEAR, week + 1);

        date = calendar.getTime();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);

        return simpleDateFormat.format(date);
    }
}

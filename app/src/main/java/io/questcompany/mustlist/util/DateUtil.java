package io.questcompany.mustlist.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kimkkikki on 2016. 10. 20..
 * Date Util
 */

public class DateUtil {

    public static Integer getDateIntegerWithDay(int day) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.add(Calendar.DATE, day);

        date = calendar.getTime();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);

        return Integer.parseInt(simpleDateFormat.format(date));
    }

    public static Integer getDateIntegerWithWeek(int startDay, int week) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.add(Calendar.DATE, ((startDay - 1) + (7 * (week + 1))));

        date = calendar.getTime();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);

        return Integer.parseInt(simpleDateFormat.format(date));
    }
}
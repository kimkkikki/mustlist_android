package io.questcompany.mustlist.util;

import android.content.Context;
import android.os.Build;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by kimkkikki on 2016. 10. 20..
 * Date Util
 */

public class DateUtil {

    private static Locale getCurrentLocale(Context context) {
        Locale currentLocale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentLocale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            currentLocale = context.getResources().getConfiguration().locale;
        }
        return currentLocale;
    }

    public static String getStartDateStringWithYearAndMonthAndDay(Context context, int year, int month, int day) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        date = calendar.getTime();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", getCurrentLocale(context));
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return simpleDateFormat.format(date);
    }

    public static String getEndDateStringWithYearAndMonthAndDay(Context context, int year, int month, int day) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        date = calendar.getTime();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", getCurrentLocale(context));
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return simpleDateFormat.format(date);
    }

    public static boolean compareStartDateAndToday(Context context, String startDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", getCurrentLocale(context));
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date start = simpleDateFormat.parse(startDate);
            Date today = new Date();

            return today.before(start);

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String convertDateToUTCDate(Context context, String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", getCurrentLocale(context));
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date convert = simpleDateFormat.parse(date);
            SimpleDateFormat convertDateFormat = new SimpleDateFormat("yyyy-MM-dd", getCurrentLocale(context));

            return convertDateFormat.format(convert);

        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }

    public static String convertDateToString(Context context, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", getCurrentLocale(context));
        return simpleDateFormat.format(date);
    }

    public static String getTodayString(Context context) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", getCurrentLocale(context));
        return simpleDateFormat.format(new Date());
    }
}
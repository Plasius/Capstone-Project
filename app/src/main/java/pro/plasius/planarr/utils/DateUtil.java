package pro.plasius.planarr.utils;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class DateUtil {
    public static String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }

    public static String formatTimestamp(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);

        return getMonthForInt(calendar.get(Calendar.MONTH)) +
                " " +
                calendar.get(Calendar.DAY_OF_MONTH);
    }
}

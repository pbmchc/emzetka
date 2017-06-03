package com.pbmchc.emzetka.utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Piotrek on 2017-02-23.
 */
public class DateUtils {

    private static final List<String> HOLIDAYS = Arrays.asList("17.04", "01.05", "03.05", "15.06", "15.08", "01.11",
            "11.11", "25.12", "26.12");

    public static int calculateTimeLeft(String time, String departureHour){

        Date currentDate = new Date();
        int minutes, hours;
        if (!time.equals("")) {
            String shortTime = time.substring(0, 2);
            minutes = parseTimeString(shortTime);
            hours = parseTimeString(departureHour);
            if (hours == currentDate.getHours())
                return minutes - currentDate.getMinutes();
            return minutes + 60 - currentDate.getMinutes();
        }
        return 0;
    }

    public static int calculateDayIndex(Date date){

        int dayNumber = 1;
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        String dayOfWeek = dateFormat.format(date);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd.MM");
        String dayOfMonth = dateFormat2.format(date);

        if(HOLIDAYS.contains(dayOfMonth) || dayOfWeek.equals("Sunday"))
            dayNumber = 3;
        else if (dayOfWeek.equals("Saturday")) {
            dayNumber = 2;
        }
        return dayNumber;
    }

    public static int parseTimeString(String input){
        if (input.startsWith("0"))
            return Integer.parseInt(input.substring(1));
        return Integer.parseInt(input);
    }

}

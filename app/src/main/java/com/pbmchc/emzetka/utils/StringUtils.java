package com.pbmchc.emzetka.utils;

/**
 * Created by Piotrek on 2017-02-23.
 */
public class StringUtils {

    public static String fixStopTimeForDisplaying(String stopTime){
        if (stopTime.equals("bd"))
            return "- ";
        return stopTime;
    }

    public static String fixLineNameForQuerying(String lineName){
        if (lineName.startsWith("_"))
            return "0" + lineName.substring(1);
        return lineName;
    }

    public static String fixLineNameForDisplaying(String lineName){
        if (lineName.startsWith("_"))
            return  lineName.substring(1);
        return lineName;
    }

    public static String fixLineNameForDisplayingFromDatabase(String lineName){
        if (lineName.startsWith("0"))
            return  lineName.substring(1);
        return lineName;
    }

    public static String extractLineNumber(String stopId) {
        return stopId.substring(stopId.length()-2);
    }


    public static String calculateHours(int hours){
        if (String.valueOf(hours).length() < 2)
            return "0" + String.valueOf(hours);
        return String.valueOf(hours);
    }

    public static boolean tryParse(String input){
        return input.matches("[-+]?\\d*\\.?\\d+");
    }
}

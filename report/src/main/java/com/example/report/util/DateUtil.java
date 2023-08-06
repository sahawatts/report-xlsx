package com.example.report.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DateUtil {

    public static final List<String> LONG_DATE_HOUR_SECOND_Y = Arrays.asList("yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd HH:mm");

    public static final List<String> LONG_DATE_HOUR_SECOND_D = Arrays.asList("dd/MM/yyyy HH:mm:ss",
            "dd/MM/yyyy HH:mm",
            "dd-MM-yyyy HH:mm:ss",
            "dd-MM-yyyy HH:mm");

    public static final String SHORT_DATE_FORMAT_REPORT = "dd/MM/yyyy";

    public static SimpleDateFormat getDateFormat(String s) {
        SimpleDateFormat sdf = null;

        List<String> format = new ArrayList<>();

        if (s.substring(2, 3).equals("/") || s.substring(2, 3).equals("-")) {
            format = LONG_DATE_HOUR_SECOND_D;
        } else if (s.substring(4, 5).equals("/") || s.substring(4, 5).equals("-")) {
            format = LONG_DATE_HOUR_SECOND_Y;
        }

        for (String formatString : format) {
            try {
                sdf = new SimpleDateFormat(formatString);
                sdf.parse(s);
                return sdf;
            } catch (ParseException e) {
            }
        }

        return sdf;
    }

    public static String getMonthNameInEnglish(int monthNumber) {
        switch (monthNumber) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return "";
        }
    }

    public static String getMonthNameInThai(int monthNumber) {
        switch (monthNumber) {
            case 1:
                return "มกราคม";
            case 2:
                return "กุมภาพันธ์";
            case 3:
                return "มีนาคม";
            case 4:
                return "เมษายน";
            case 5:
                return "พฤษภาคม";
            case 6:
                return "มิถุนายน";
            case 7:
                return "กรกฎาคม";
            case 8:
                return "สิงหาคม";
            case 9:
                return "กันยายน";
            case 10:
                return "ตุลาคม";
            case 11:
                return "พฤศจิกายน";
            case 12:
                return "ธันวาคม";
            default:
                return "";
        }
    }
}

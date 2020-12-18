package com.dabeeo.imsdk.sample.util;

public class TimeUtils {
    public static String timeToString(int totalSeconds) {
        int day = totalSeconds / (60 * 60 * 24);
        int hour = (totalSeconds - day * 60 * 60 * 24) / (60 * 60);
        int minute = (totalSeconds - day * 60 * 60 * 24 - hour * 3600) / 60;
        int second = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();

        if (day > 0) {
            sb.append(day).append("일");
        }

        if (hour > 0) {
            sb.append(hour).append("시간");
        }

        if (minute > 0) {
            sb.append(minute).append("분");
        }

        if (second > 0) {
            sb.append(second).append("초");
        }

        return sb.toString();
    }
}

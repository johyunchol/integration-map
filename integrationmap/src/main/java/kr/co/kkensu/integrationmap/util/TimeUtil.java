package kr.co.kkensu.integrationmap.util;

public class TimeUtil {
    public static String formatTime(double time) {
        int timeInt = (int) time;
        int minute = timeInt / 60;
        int hour = minute / 60;
        minute = minute % 60;
        if (timeInt % 60 > 30)
            minute += 1;

        if (hour == 0 && minute == 0)
            return "잠시 후 도착";
        StringBuilder builder = new StringBuilder();
        builder.append("약 ");
        if (hour > 0) {
            builder.append(hour);
            builder.append("시간 ");
        }
        if (minute > 0) {
            builder.append(minute);
            builder.append("분");
        }
        return builder.toString();
    }
}

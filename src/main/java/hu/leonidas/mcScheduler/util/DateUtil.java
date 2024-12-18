package hu.leonidas.mcScheduler.util;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class DateUtil {

    public List<String> getNow() {
        ConfigUtil.zonedDateTime = ZonedDateTime.now(ConfigUtil.timeZone);
        List<String> timeData = new ArrayList<>();
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ConfigUtil.timeZone);
        String actuallyDay = zonedDateTime.getDayOfWeek().toString();
        int actuallyHour = zonedDateTime.getHour();
        int actuallyMinute = zonedDateTime.getMinute();
        int actuallySeconds = zonedDateTime.getSecond();
        timeData.add(actuallyDay);
        timeData.add(Integer.toString(actuallyHour));
        timeData.add(Integer.toString(actuallyMinute));
        timeData.add(Integer.toString(actuallySeconds));
        return timeData;
    }

    public List<String> splitDate(String date) {
        List<String> splitDate = new ArrayList<>();
        String[] parts = date.split(":");

        if (parts.length < 4) {
            throw new IllegalArgumentException("Date format should be 'day:hour:minute:second'");
        }

        String day = parts[0];
        int hour = Integer.parseInt(parts[1]);
        int minute = Integer.parseInt(parts[2]);
        int second = Integer.parseInt(parts[3]);

        splitDate.add(day);
        splitDate.add(Integer.toString(hour));
        splitDate.add(Integer.toString(minute));
        splitDate.add(Integer.toString(second));

        return splitDate;
    }

    public String nextRun(String section) {
        ConfigUtil.zonedDateTime = ZonedDateTime.now(ConfigUtil.timeZone);
        String closestTime = null;
        DayOfWeek todayDayOfWeek = ConfigUtil.zonedDateTime.getDayOfWeek();

        Duration closestDuration = Duration.ofDays(7);
        EventConfig eventConfig = ConfigUtil.events.get(section);
        List<String> times = eventConfig.getTimes();

        for (String time : times) {
            List<String> list = splitDate(time);
            String dayString = list.get(0);
            int hour = Integer.parseInt(list.get(1));
            int minute = Integer.parseInt(list.get(2));
            int second = Integer.parseInt(list.get(3));

            DayOfWeek dayOfWeek = DayOfWeek.valueOf(dayString.toUpperCase());
            LocalTime eventTime = LocalTime.of(hour, minute, second);

            int dayDifference = dayOfWeek.getValue() - todayDayOfWeek.getValue();

            if (dayDifference == 0 && eventTime.isBefore(ConfigUtil.zonedDateTime.toLocalTime())) {
                dayDifference += 7;
            } else if (dayDifference < 0) {
                dayDifference += 7;
            }

            ZonedDateTime eventDateTime = ConfigUtil.zonedDateTime.with(eventTime).plusDays(dayDifference);
            Duration duration = Duration.between(ConfigUtil.zonedDateTime, eventDateTime);

            if (duration.compareTo(closestDuration) < 0) {
                closestDuration = duration;
                closestTime = time;
            }
        }

        if (closestTime != null) {
            long totalSeconds = closestDuration.getSeconds();
            long days = totalSeconds / (24 * 3600);
            long hours = (totalSeconds % (24 * 3600)) / 3600;
            long minutes = (totalSeconds % 3600) / 60;
            long seconds = totalSeconds % 60;

            if (days > 0) {
                return String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
            } else {
                return String.format("%02d:%02d:%02d", hours, minutes, seconds);
            }
        }

        return null;
    }



    public boolean isDayValid(String day) {
        return day.equals("sunday") || day.equals("monday") || day.equals("tuesday") || day.equals("wednesday") || day.equals("thursday") || day.equals("friday") || day.equals("saturday");
    }

    private boolean isTimeValid(Integer hour, Integer minute, Integer seconds) {
        return (hour >= 0 && hour < 24) && (minute >= 0 && minute < 60) && (seconds >= 0 && seconds < 60);
    }

    public boolean isDateValid(String date) {
        List<String> splitDate = splitDate(date);
        return isDayValid(splitDate.get(0)) && isTimeValid(Integer.valueOf(splitDate.get(1)), Integer.valueOf(splitDate.get(2)), Integer.valueOf(splitDate.get(3)));
    }
}

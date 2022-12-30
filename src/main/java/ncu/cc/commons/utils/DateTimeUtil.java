package ncu.cc.commons.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateTimeUtil {
    public static long elapsed_seconds(Date from, Date to) {
        return TimeUnit.MILLISECONDS.toSeconds(to.getTime() - from.getTime());
    }
    public static long elapsed_seconds(Date then) {
        return elapsed_seconds(then, Calendar.getInstance().getTime());
    }
}

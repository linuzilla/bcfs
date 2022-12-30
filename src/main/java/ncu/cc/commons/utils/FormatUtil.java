package ncu.cc.commons.utils;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jiann-Ching Liu (saber@g.ncu.edu.tw)
 * @version 1.0
 * @since 1.0
 */
public class FormatUtil {
    @FunctionalInterface
    interface MatchAction<T> {
        T convert(Matcher matcher, Object ...args);
    }

    static class PatternWithFunction<T> {
        MatchAction<T> action;
        Pattern pattern;

        public PatternWithFunction(String patternString, MatchAction<T> action) {
            this.action = action;
            pattern = Pattern.compile(patternString);
        }
        public T processing(String source, Object ...args) {
            Matcher m = pattern.matcher(source);

            return m.find() ? action.convert(m) : null;
        }
    }

    private static PatternWithFunction<Calendar> datePatterns[] = new PatternWithFunction[] {
            new PatternWithFunction(
                    "^(\\d{2,4})(\\d{2})(\\d{2})$",
                    (matcher, args) -> stringToCalendar(matcher.group(1), matcher.group(2), matcher.group(3))),
            new PatternWithFunction(
                    "^(\\d{2,4})/(\\d{1,2})/(\\d{1,2})$",
                    (matcher, args) -> stringToCalendar(matcher.group(1), matcher.group(2), matcher.group(3))),
            new PatternWithFunction(
                    "^(\\d{2,4})-(\\d{1,2})-(\\d{1,2})$",
                    (matcher, args) -> stringToCalendar(matcher.group(1), matcher.group(2), matcher.group(3)))
    };

    private static PatternWithFunction<String> phonePatterns[] = new PatternWithFunction[] {
            new PatternWithFunction(
                    "^(0\\d{1,2})[- ](\\d{6,8})$",
                    (matcher, args) -> twPhoneNumber(matcher.group(1), matcher.group(2), args)
            ),
            new PatternWithFunction(
                    "^(09\\d{2})[- ]?(\\d{6})$",
                    (matcher, args) -> twPhoneNumber(matcher.group(1), matcher.group(2), args)
            )
    };

    private static String twPhoneNumber(String lead, String tail, Object ...args) {
        if (args != null && args.length > 0 && (args[0] instanceof String)) {
            return String.format((String) args[0], lead, tail);
        } else {
            return lead + "-" + tail;
        }
    }

    public static String calendarToTwDate(String fmt, Calendar calendar) {
        return yymmddToTwDate(fmt,
                calendar.get(Calendar.YEAR) - 1911,
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DATE));
    }

    public static String yymmddToTwDate(String fmt, int year, int month, int day) {
        return String.format(fmt, year, month, day);
    }

    public static Calendar stringToCalendar(String yy, String mm, String dd) {
        int year = Integer.parseInt(yy);
        int month = Integer.parseInt(mm);
        int day = Integer.parseInt(dd);

        if (year < 1000) {
            year += 1911;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);

        final String format = "%04d/%02d/%02d";
        String r1 = yymmddToTwDate(format, year - 1911, month, day);
        String r2 = calendarToTwDate(format, calendar);

        return r1.equals(r2) ? calendar : null;
    }

    public static Calendar twDateFormatter(String input) {
        if (input != null && input.length() >= 6) {
            for (PatternWithFunction<Calendar> patternWithFunction : datePatterns) {
                Calendar result = patternWithFunction.processing(input);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public static String twPhoneFormatter(String phoneString, String format) {
        if (phoneString != null && phoneString.length() > 8) {
            for (PatternWithFunction<String> patternWithFunction: phonePatterns) {
                String result = patternWithFunction.processing(phoneString, format);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
}

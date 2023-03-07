package veinthrough.test.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.util.CalendarUtils;
import veinthrough.api.util.DateFormatUtils;

import java.util.Calendar;
import java.util.TimeZone;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * Calculate calendar(by CalendarUtils) and display(by DateFormatUtils) now/tomorrow/next month/next year.
 * {@link CalendarUtils}/{@link DateFormatUtils}
 */
@Slf4j
public class DateFormatTest {
    private static final TimeZone SHANG_HAI = TimeZone.getTimeZone("Asia/Shanghai");
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS z";
    
    @Test
    public void dateFormatTest() {
        long now = System.currentTimeMillis();
        log.info(methodLog(
                "now", DateFormatUtils.formatDate(
                        now, SHANG_HAI, DEFAULT_DATE_PATTERN),
                "tomorrow", DateFormatUtils.formatDate(
                        CalendarUtils.nextTime(Calendar.DAY_OF_MONTH)),
                "next month", DateFormatUtils.formatDate(
                        CalendarUtils.nextTime(Calendar.MONTH)),
                "next year", DateFormatUtils.formatDate(
                        CalendarUtils.nextTime(Calendar.YEAR))));
    }
}

package veinthrough.test.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Tests:
 * 1. simulate a calendar app as follows:
 *  Sun Mon Tue Wed Thu Fri Sat
 *                   1   2   3
 *  4   5   6   7   8   9  10
 *  11  12  13  14* 15  16  17
 *  18  19  20  21  22  23  24
 *  25  26  27  28  29  30  31
 * 2. Calculate calendar(by CalendarUtils) and display(by DateFormatUtils) now/tomorrow/next month/next year.
 * @see DateFormatTest#dateFormatTest()
 */
@Slf4j
public class CalendarTest {
    @Test
    public void calendarSimulateTest() {
        // construct d as current date
        GregorianCalendar d= new GregorianCalendar();

        int today = d.get(Calendar.DAY_OF_MONTH);
        int month = d.get(Calendar.MONTH);

        // set d to start date of the month
        d.set(Calendar.DAY_OF_MONTH, 1);

        int weekday = d.get(Calendar.DAY_OF_WEEK);
        int firstDayOfWeek = d.getFirstDayOfWeek();

        // determine the required indentation for the first line
        int indent = 0;
        while(weekday != firstDayOfWeek) {
            indent++;
            d.add(Calendar.DAY_OF_MONTH, -1);
            weekday = d.get(Calendar.DAY_OF_WEEK);
        }

        // print weekday names
        String[] weekdayNames = new DateFormatSymbols().getShortWeekdays();
        do {
            System.out.printf("%4s", weekdayNames[weekday]);
            d.add(Calendar.DAY_OF_MONTH, 1);
            weekday = d.get(Calendar.DAY_OF_WEEK);
        } while(weekday != firstDayOfWeek);
        System.out.println();

        for(int i=1; i<=indent; i++) {
            System.out.print("    ");
        }

        d.set(Calendar.DAY_OF_MONTH, 1);
        do {
            // print day
            int day = d.get(Calendar.DAY_OF_MONTH);
            System.out.printf("%3d", day);

            // mark current day with *
            if(day == today) System.out.print("*");
            else System.out.print(" ");

            // next day
            d.add(Calendar.DAY_OF_MONTH, 1);
            weekday = d.get(Calendar.DAY_OF_WEEK);

            // start a new line at the start of the week
            if(weekday == firstDayOfWeek) System.out.println();
        } while(d.get(Calendar.MONTH) == month);
        System.out.println();

    }

    /**
     * @see DateFormatTest#dateFormatTest()
     */
    @Test
    public void nextDateTest() {
        new DateFormatTest().dateFormatTest();
    }
}

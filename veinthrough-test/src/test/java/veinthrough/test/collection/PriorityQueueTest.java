package veinthrough.test.collection;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.PriorityQueue;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 * This program demonstrates the use of a priority queue.
 * 执行{@link PriorityQueue#remove()}才会按照顺序>
 */
@Slf4j
public class PriorityQueueTest {
    @Test
    public void priorityQueueTest() {
        PriorityQueue<GregorianCalendar> pq = new PriorityQueue<>();
        pq.add(new GregorianCalendar(1906, Calendar.DECEMBER, 9));
        pq.add(new GregorianCalendar(1815, Calendar.DECEMBER, 10));
        pq.add(new GregorianCalendar(1903, Calendar.DECEMBER, 3));
        pq.add(new GregorianCalendar(1910, Calendar.JUNE, 22));

        log.info(methodLog(1, "Iterating over elements..."));
        for (GregorianCalendar date : pq)
            log.info(methodLog("" + date.get(Calendar.YEAR)));
        log.info(methodLog(2, "Removing elements..."));
        while (!pq.isEmpty())
            log.info(methodLog("" + pq.remove().get(Calendar.YEAR)));
    }
}

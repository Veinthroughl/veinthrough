package veinthrough.test.collection;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.collection.CircularArrayQueue;

import java.util.Queue;

import static veinthrough.api.util.MethodLog.methodLog;


/**
 * {@link CircularArrayQueue}: 循环数组实现的队列
 */
@Slf4j
public class CircularArrayQueueTest {
    @Test
    public void circularArrayQueueTest() {
        Queue<String> q = new CircularArrayQueue<>(5);
        q.add("Amy");
        q.add("Bob");
        q.add("Carl");
        q.add("Deedee");
        q.add("Emile");
        q.remove();
        q.add("Fifi");
        q.remove();
        log.info(methodLog(q.toString()));
    }
}

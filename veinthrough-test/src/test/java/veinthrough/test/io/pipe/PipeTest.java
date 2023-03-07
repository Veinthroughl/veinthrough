package veinthrough.test.io.pipe;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.async.LoopRunnable;
import veinthrough.test.guava.ListenableFutureTest;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static veinthrough.api.util.Constants.MILLIS_PER_SECOND;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * Tests:
 * 1. 写了1026个byte数据, 只读了1024个数据, 默认缓冲区大小为1024
 * 2. 写了1026个byte数据, 分次读取, 默认缓冲区大小为1024
 * 3. 写了1026个char数据, 只读了1024个数据, 默认缓冲区大小为1024
 * [?] char once最终会有IOException: Read end dead, 但是byte once没有
 * 4. 写了1026个char数据, 分次读取, 默认缓冲区大小为1024
 *
 * APIs:
 * 1. PipedInputStream/PipedOutputStream/PipedReader/PipedWriter
 * @see Piper
 * 2. ExecutorService线程池
 * @see ListenableFutureTest
 */
@Slf4j
public class PipeTest{
    private static final int INTERVAL = MILLIS_PER_SECOND;
    private static final boolean ONCE = false;
    private static final boolean CONTINUED = true;
    private Piper receiver = new Piper();
    private Piper sender = new Piper();

    // 1. 写了1026个byte数据, 只读了1024个数据, 默认缓冲区大小为1024
    // [?] char once最终会有IOException: Read end dead, 但是byte once没有
    @Test
    public void rwByteOnceTest() throws IOException, InterruptedException {
        _test(Piper.DATA_TYPE.BYTE, ONCE);
    }

    // 2. 写了1026个byte数据, 分次读取, 默认缓冲区大小为1024
    @Test
    public void rwByteContinuedTest() throws IOException, InterruptedException {
        _test(Piper.DATA_TYPE.BYTE, CONTINUED);
    }

    // 3. 写了1026个char数据, 只读了1024个数据, 默认缓冲区大小为1024
    // [?] char once最终会有IOException: Read end dead, 但是byte once没有
    @Test
    public void rwCharOnceTest() throws IOException, InterruptedException {
        _test(Piper.DATA_TYPE.CHAR, ONCE);
    }

    // 4. 写了1026个char数据, 分次读取, 默认缓冲区大小为1024
    @Test
    public void rwCharContinuedTest() throws IOException, InterruptedException {
        _test(Piper.DATA_TYPE.CHAR, CONTINUED);
    }

    private void _test(Piper.DATA_TYPE dataType, boolean continued) throws InterruptedException, IOException {
        ExecutorService pool = Executors.newCachedThreadPool();
        // 1. initialize/reset both
        reset(dataType);
        // 2. connect
        sender.connect(receiver);
//        receiver.connect(sender); // the same effect
        // 3. start and wait for finishing
        pool.invokeAll(ImmutableList.of(
                Executors.callable(() -> receiver.start(continued), null),
                Executors.callable(() -> sender.start(continued), null),
                // wait for finish
                Executors.callable(LoopRunnable.sleepyAtInterval(
                        // loop task
                        () -> {
                            int finished = 0;
                            if (sender.finished()) finished += FINISH.SENDER_FINISHED.value;
                            if (receiver.finished()) finished += FINISH.RECEIVER_FINISHED.value;
                            log.debug(methodLog(FINISH.forValue(finished).toString()));
                        },
                        // condition
                        () -> sender.finished() && receiver.finished(),
                        INTERVAL,
                        // finish
                        // 监控sender/receiver是否完成
                        // 通过在LoopRunnable中增加finish参数来执行结束操作,
                        // 而不是使用Guava中的Futures.addCallback添加callback来执行结束操作
                        () -> log.info(methodLog(FINISH.BOTH_FINISHED.toString()))))));
        pool.shutdown();
    }

    private void initialize(Piper.DATA_TYPE dataType) {
        receiver.setDataType(dataType);
        sender.setDataType(dataType);
        receiver.setDuty(Piper.DUTY.READ);
        sender.setDuty(Piper.DUTY.WRITE);
    }

    private void reset(Piper.DATA_TYPE dataType) {
        if (receiver.initialized()) receiver.reset();
        if (sender.initialized()) sender.reset();
        initialize(dataType);
    }

    private enum FINISH {
        NEITHER_FINISHED(0),
        RECEIVER_FINISHED(1),
        SENDER_FINISHED(2),
        BOTH_FINISHED(3);

        @Getter
        private int value;
        private static final Map<Integer, FINISH> VALUE_MAP;

        static {
            final ImmutableMap.Builder<Integer, FINISH> map = ImmutableMap.builder();
            for (FINISH enumItem : FINISH.values()) {
                map.put(enumItem.getValue(), enumItem);
            }
            VALUE_MAP = map.build();
        }

        FINISH(Integer value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.name().toLowerCase().replace("_", " ");
        }

        public static FINISH forValue(Integer value) {
            return VALUE_MAP.get(value);
        }
    }
}

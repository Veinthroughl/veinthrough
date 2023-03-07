package veinthrough.api.async;

import lombok.extern.slf4j.Slf4j;
import veinthrough.api._interface.Condition;

import static veinthrough.api.util.Constants.MILLIS_PER_SECOND;
import static veinthrough.api.util.MethodLog.methodLog;

@SuppressWarnings({"unused", "WeakerAccess"})
@Slf4j
abstract class LoopTask {
    protected static final int DEFAULT_INTERVAL = MILLIS_PER_SECOND;
    protected static final Condition NEVER = never();
    protected static final Runnable NO_OP = () -> {};

    protected static long durationSince(long startTime) {
        return System.currentTimeMillis() - startTime;
    }

    protected static Condition never() {
        return () -> false;
    }

    protected static Condition timeout(long time) {
        return new Condition() {
            private final long startTime = System.currentTimeMillis();
            @Override
            public Boolean get() {
                long duration = durationSince(startTime);
                log.debug(methodLog(String.format("Duration: %d(ms)", duration)));
                return duration >= time;
            }
        };
    }

    protected static Condition timeoutOrCondition(Condition condition, long time) {
        return new Condition() {
            private final long startTime = System.currentTimeMillis();
            @Override
            public Boolean get() {
                long duration = durationSince(startTime);
                log.debug(methodLog(String.format("Duration: %d(ms)", duration)));
                return duration >= time &&
                        condition.met();
            }
        };
    }
}

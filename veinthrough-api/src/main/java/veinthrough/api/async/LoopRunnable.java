package veinthrough.api.async;

import lombok.extern.slf4j.Slf4j;
import veinthrough.api._interface.Condition;

import static veinthrough.api.util.MethodLog.*;

/**
 * @author veinthrough
 * 
 * 将一个Runnable包装成一个循环执行的Runnable, 直到condition/timeout/interrupted
 * hyper(8 functions): condition/timeout --X-- finish
 * sleepy(16 functions): condition/timeout --X-- interval --X-- finish
 *
 * 1. Terminate a Runnable/Callable:
 * (1) condition
 * (2) timeout
 * (3) interrupt
 * (4) endless loop: 不终止
 * 2. 循环方式:
 * (1) hyper(): No sleep() in while, 使用!Thread.interrupted(),
 * 这里不需要try/catch InterruptedException因为只有像sleep()才会抛出这个异常
 * (2) sleepy(): With sleep(), no need to use !Thread.interrupted(),
 * as sleep will clear interrupted sign
 * (3) With sleep() and re-interrupt() if InterruptedException,
 * try必须放在while内, 如果try放在while外, 会立刻跳出, !Thread.interrupted()就没有意义
 * 3. 是否有interval
 * 4. 循环完成是否执行的动作finish
 */

@Slf4j
@SuppressWarnings({"unused", "WeakerAccess"})
public class LoopRunnable extends LoopTask {
    // 2.(1) No sleep() in while, 使用!Thread.interrupted(),
    // 这里不需要try/catch InterruptedException因为只有像sleep()才会抛出这个异常
    // Terminate: condition/interrupted
    public static Runnable hyper(Runnable task, Condition condition, Runnable finish) {
        return () -> {
            log.debug(methodLog("Loop begin"));
            while (!condition.met() && !Thread.interrupted()) {
                log.debug(methodLog("Loop ing ..."));
                task.run();
            }
            // run finish
            finish.run();
            log.debug(methodLog("Loop end"));
        };
    }

    // 2.(1)
    // Terminate: condition/interrupted
    public static Runnable hyper(Runnable task, Condition condition) {
        return hyper(task, condition, NO_OP);
    }

    // 2.(1)
    // Terminate: timeout/interrupted
    public static Runnable hyper(Runnable task, long time, Runnable finish) {
        return hyper(task, timeout(time), finish);
    }

    // 2.(1)
    // Terminate: timeout/interrupted
    public static Runnable hyper(Runnable task, long time) {
        return hyper(task, timeout(time), NO_OP);
    }

    // 2.(1)
    // Terminate: condition/timeout/interrupted
    public static Runnable hyper(Runnable task, Condition condition,
                                 long time, Runnable finish) {
        return hyper(task, timeoutOrCondition(condition, time), finish);
    }

    // 2.(1)
    // Terminate: condition/timeout/interrupted
    public static Runnable hyper(Runnable task, Condition condition,
                                 long time) {
        return hyper(task, timeoutOrCondition(condition, time), NO_OP);
    }

    // 2.(1) No sleep() in while, 使用!Thread.interrupted()
    // Terminate: interrupted
    public static Runnable hyper(Runnable task, Runnable finish) {
        return hyper(task, NEVER, finish);
    }

    // 2.(1) No sleep() in while, 使用!Thread.interrupted()
    // Terminate: interrupted
    public static Runnable hyper(Runnable task) {
        return hyper(task, NEVER, NO_OP);
    }


    // 2.(2) With sleep(), no need to use !Thread.interrupted()
    // terminate: condition/interrupted
    public static Runnable sleepyAtInterval(Runnable task, Condition condition,
                                            long interval,
                                            Runnable finish) {
        return () -> {
            try {
                log.debug(methodLog("Loop begin"));
                // no need to use !Thread.interrupted(), as sleep will clear interrupted sign
//                while (!condition.met() && !Thread.interrupted()) {
                while (!condition.met()) {
                    log.debug(methodLog("Loop ing ..."));
                    task.run();
                    log.debug(methodLog("Sleeping ..."));
                    Thread.sleep(interval);
                }
                // run finish
                finish.run();
                log.debug(methodLog("Loop end"));
            } catch (InterruptedException e) {
                log.warn(exceptionLog(e));
            }
        };
    }

    // 2.(2) With sleep(), no need to use !Thread.interrupted()
    // terminate: condition/interrupted
    public static Runnable sleepyAtInterval(Runnable task, Condition condition,
                                            long interval) {
        return sleepyAtInterval(task, condition, interval, NO_OP);
    }

    // 2.(2)
    // terminate: condition/interrupted
    public static Runnable sleepy(Runnable task, Condition condition, Runnable finish) {
        return sleepyAtInterval(task, condition, DEFAULT_INTERVAL, finish);
    }

    // 2.(2)
    // terminate: condition/interrupted
    public static Runnable sleepy(Runnable task, Condition condition) {
        return sleepyAtInterval(task, condition, DEFAULT_INTERVAL, NO_OP);
    }

    // 2.(2)
    // terminate: timeout/interrupted
    public static Runnable sleepyAtInterval(Runnable task,
                                            long time, long interval,
                                            Runnable finish) {
        return sleepyAtInterval(task, timeout(time), interval, finish);
    }

    // 2.(2)
    // terminate: timeout/interrupted
    public static Runnable sleepyAtInterval(Runnable task,
                                            long time, long interval) {
        return sleepyAtInterval(task, timeout(time), interval, NO_OP);
    }

    // 2.(2)
    // terminate: timeout/interrupted
    public static Runnable sleepy(Runnable task, long time, Runnable finish) {
        return sleepyAtInterval(task, timeout(time), DEFAULT_INTERVAL, finish);
    }

    // 2.(2)
    // terminate: timeout/interrupted
    public static Runnable sleepy(Runnable task, long time) {
        return sleepyAtInterval(task, timeout(time), DEFAULT_INTERVAL, NO_OP);
    }

    // 2.(2)
    // terminate: terminate/timeout/interrupted
    public static Runnable sleepyAtInterval(Runnable task, Condition condition,
                                            long time, long interval,
                                            Runnable finish) {
        return sleepyAtInterval(task, timeoutOrCondition(condition, time), interval, finish);
    }

    // 2.(2)
    // terminate: terminate/timeout/interrupted
    public static Runnable sleepyAtInterval(Runnable task, Condition condition,
                                            long time, long interval) {
        return sleepyAtInterval(task, timeoutOrCondition(condition, time), interval, NO_OP);
    }

    // 2.(2)
    // terminate: terminate/timeout/interrupted
    public static Runnable sleepy(Runnable task, Condition condition,
                                  long time,
                                  Runnable finish) {
        return sleepyAtInterval(task, timeoutOrCondition(condition, time), DEFAULT_INTERVAL, finish);
    }

    // 2.(2)
    // terminate: terminate/timeout/interrupted
    public static Runnable sleepy(Runnable task, Condition condition,
                                  long time) {
        return sleepyAtInterval(task, timeoutOrCondition(condition, time), DEFAULT_INTERVAL, NO_OP);
    }

    // 2.(2)
    // terminate: timeout/interrupted
    public static Runnable sleepyAtInterval(Runnable task, long interval, Runnable finish) {
        return sleepyAtInterval(task, NEVER, interval, finish);
    }

    // 2.(2)
    // terminate: timeout/interrupted
    public static Runnable sleepyAtInterval(Runnable task, long interval) {
        return sleepyAtInterval(task, NEVER, interval, NO_OP);
    }

    // 2.(2)
    // terminate: timeout/interrupted
    public static Runnable sleepy(Runnable task, Runnable finish) {
        return sleepyAtInterval(task, NEVER, DEFAULT_INTERVAL, finish);
    }

    // 2.(2)
    // terminate: timeout/interrupted
    public static Runnable sleepy(Runnable task) {
        return sleepyAtInterval(task, NEVER, DEFAULT_INTERVAL, NO_OP);
    }

    // 2.(3) With sleep() and re-interrupt() if InterruptedException
    @Deprecated
    public static Runnable sleepyAndInterruptedAtInterval(
            Runnable task, Condition condition,
            long interval,
            Runnable finish) {
        return () -> {
            log.debug(methodLog("Loop begin"));
            while (!condition.met() && !Thread.interrupted()) {
                log.debug(methodLog("Loop ing ..."));
                task.run();
                // try必须放在while内, 如果try放在while外, 会立刻跳出, !Thread.interrupted()就没有意义
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    log.warn(exceptionLog(e));
                    Thread.currentThread().interrupt();
                }
            }
            // run finish
            finish.run();
            log.debug(methodLog("Loop end"));
        };
    }

    // 2.(3) With sleep() and re-interrupt() if InterruptedException
    @Deprecated
    public static Runnable sleepyAndInterruptedAtInterval(
            Runnable task,
            long time, long interval,
            Runnable finish) {
        return sleepyAndInterruptedAtInterval(task, timeout(time), interval, finish);
    }

    // 2.(3) With sleep() and re-interrupt() if InterruptedException
    @Deprecated
    public static Runnable sleepyAndInterruptedAtInterval(
            Runnable task,
            long time, long interval) {
        return sleepyAndInterruptedAtInterval(task, timeout(time), interval, NO_OP);
    }
}

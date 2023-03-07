package veinthrough.api.async;

import lombok.extern.slf4j.Slf4j;
import veinthrough.api._interface.Condition;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

import static veinthrough.api.util.MethodLog.*;

/**
 * @author veinthrough
 *
 * 将一个Callable包装成一个循环执行的Callable, 直到condition/timeout/interrupted
 * hyper(8 functions): condition/timeout --X-- consumer
 * sleepy(16 functions): condition/timeout --X-- interval --X-- consumer
 * 
 * 1. Terminate a Runnable/Callable:
 * (1) condition
 * (2) timeout
 * (3) interrupt
 * (4) endless loop: 不终止
 * 2. 循环方式:
 * (1) No sleep() in while, 使用!Thread.interrupted(),
 * 这里不需要try/catch InterruptedException因为只有像sleep()才会抛出这个异常
 * (2) With sleep(), no need to use !Thread.interrupted(),
 * as sleep will clear interrupted sign
 * (3) With sleep() and re-interrupt() if InterruptedException,
 * try必须放在while内, 如果try放在while外, 会立刻跳出, !Thread.interrupted()就没有意义
 * 3. 是否有interval
 * 4. 循环完成是否执行的动作consumer
 * (1) 没有consumer就返回Callable<T>, 因为要返回执行结果
 * (2) 如果有consumer, 直接在consumer消耗result, 不需要返回执行结果, 所以返回Runnable
 */

@Slf4j
@SuppressWarnings({"unused", "WeakerAccess", "Duplicates"})
public class LoopCallable extends LoopTask {
    // 2.(1) No sleep() in while, 使用!Thread.interrupted()
    // Terminate: condition/interrupted
    public static <T> Runnable hyper(Callable<T> task, Condition condition, Consumer<T> consumer) {
        return () -> {
            T result = null;
            log.debug(methodLog("Loop begin"));
            try {
                while (!condition.met() && !Thread.interrupted()) {
                    log.debug(methodLog("Loop ing ..."));
                    result = task.call();
                }
            } catch (Exception e) {
                log.error(exceptionLog(e));
            }
            // consume result
            consumer.accept(result);
            log.debug(methodLog("Loop end"));
        };
    }

    // 2.(1)
    // Terminate: condition/interrupted
    public static <T> Callable<T> hyper(Callable<T> task, Condition condition) {
        return () -> {
            T result = null;
            log.debug(methodLog("Loop begin"));
            while (!condition.met() && !Thread.interrupted()) {
                log.debug(methodLog("Loop ing ..."));
                result = task.call();
            }
            log.debug(methodLog("Loop end"));
            return result;
        };
    }

    // 2.(1)
    // Terminate: timeout/interrupted
    public static <T> Runnable hyper(Callable<T> task, long time, Consumer<T> consumer) {
        return hyper(task, timeout(time), consumer);
    }

    // 2.(1)
    // Terminate: timeout/interrupted
    public static <T> Callable<T> hyper(Callable<T> task, long time) {
        return hyper(task, timeout(time));
    }

    // 2.(1)
    // Terminate: condition/timeout/interrupted
    public static <T> Runnable hyper(Callable<T> task, Condition condition,
                                     long time,
                                     Consumer<T> consumer) {
        return hyper(task, timeoutOrCondition(condition, time), consumer);
    }

    // 2.(1)
    // Terminate: condition/timeout/interrupted
    public static <T> Callable<T> hyper(Callable<T> task, Condition condition,
                                        long time) {
        return hyper(task, timeoutOrCondition(condition, time));
    }

    // 2.(1)
    // Terminate: interrupted
    public static <T> Runnable hyper(Callable<T> task, Consumer<T> consumer) {
        return hyper(task, NEVER, consumer);
    }

    // 2.(1)
    // Terminate: interrupted
    public static <T> Callable<T> hyper(Callable<T> task) {
        return hyper(task, NEVER);
    }

    // 2.(2) With sleep(), no need to use !Thread.interrupted()
    // terminate: condition/interrupted
    public static <T> Runnable sleepyAtInterval(Callable<T> task, Condition condition,
                                                long interval,
                                                Consumer<T> consumer) {
        return () -> {
            T result = null;
            try {
                log.debug(methodLog("Loop begin"));
                // no need to use !Thread.interrupted(), as sleep will clear interrupted sign
//                while (!condition.met() && !Thread.interrupted()) {
                while (!condition.met()) {
                    log.debug(methodLog("Loop ing ..."));
                    result = task.call();
                    Thread.sleep(interval);
                }
                // consume result
                consumer.accept(result);
                log.debug(methodLog("Loop end"));
            } catch (InterruptedException e) {
                log.warn(exceptionLog(e));
            } catch (Exception e) {
                log.error(exceptionLog(e));
            }
        };
    }

    // 2.(2)
    // terminate: condition/interrupted
    public static <T> Callable<T> sleepyAtInterval(Callable<T> task, Condition condition,
                                                   long interval) {
        return () -> {
            T result = null;
            try {
                log.debug(methodLog("Loop begin"));
                // no need to use !Thread.interrupted(), as sleep will clear interrupted sign
//                while (!condition.met() && !Thread.interrupted()) {
                while (!condition.met()) {
                    log.debug(methodLog("Loop ing ..."));
                    result = task.call();
                    Thread.sleep(interval);
                }
                log.debug(methodLog("Loop end"));
            } catch (InterruptedException e) {
                log.warn(exceptionLog(e));
            }
            return result;
        };
    }

    // 2.(2)
    // terminate: condition/interrupted
    public static <T> Runnable sleepy(Callable<T> task, Condition condition, Consumer<T> consumer) {
        return sleepyAtInterval(task, condition, DEFAULT_INTERVAL, consumer);
    }

    // 2.(2)
    // terminate: condition/interrupted
    public static <T> Callable<T> sleepy(Callable<T> task, Condition condition) {
        return sleepyAtInterval(task, condition, DEFAULT_INTERVAL);
    }

    // 2.(2)
    // terminate: timeout/interrupted
    public static <T> Runnable sleepyAtInterval(Callable<T> task,
                                                long time, long interval,
                                                Consumer<T> consumer) {
        return sleepyAtInterval(task, timeout(time), interval, consumer);
    }

    // 2.(2)
    // terminate: timeout/interrupted
    public static <T> Callable<T> sleepyAtInterval(Callable<T> task,
                                                   long time, long interval) {
        return sleepyAtInterval(task, timeout(time), interval);
    }

    // 2.(2)
    // terminate: timeout/interrupted
    public static <T> Runnable sleepy(Callable<T> task, long time, Consumer<T> consumer) {
        return sleepyAtInterval(task, timeout(time), DEFAULT_INTERVAL, consumer);
    }

    // 2.(2)
    // terminate: timeout/interrupted
    public static <T> Callable<T> sleepy(Callable<T> task, long time) {
        return sleepyAtInterval(task, timeout(time), DEFAULT_INTERVAL);
    }

    // 2.(2)
    // terminate: terminate/timeout/interrupted
    public static <T> Runnable sleepyAtInterval(Callable<T> task, Condition condition,
                                                long time, long interval,
                                                Consumer<T> consumer) {
        return sleepyAtInterval(task, timeoutOrCondition(condition, time), interval, consumer);
    }

    // 2.(2)
    // terminate: terminate/timeout/interrupted
    public static <T> Callable<T> sleepyAtInterval(Callable<T> task, Condition condition,
                                                   long time, long interval) {
        return sleepyAtInterval(task, timeoutOrCondition(condition, time), interval);
    }

    // 2.(2)
    // terminate: terminate/timeout/interrupted
    public static <T> Runnable sleepy(Callable<T> task, Condition condition,
                                      long time,
                                      Consumer<T> consumer) {
        return sleepyAtInterval(task, timeoutOrCondition(condition, time), DEFAULT_INTERVAL, consumer);
    }

    // 2.(2)
    // terminate: terminate/timeout/interrupted
    public static <T> Callable<T> sleepy(Callable<T> task, Condition condition,
                                         long time) {
        return sleepyAtInterval(task, timeoutOrCondition(condition, time), DEFAULT_INTERVAL);
    }

    // 2.(2)
    // terminate: interrupted
    public static <T> Runnable sleepyAtInterval(Callable<T> task, long interval, Consumer<T> consumer) {
        return sleepyAtInterval(task, NEVER, interval, consumer);
    }

    // 2.(2)
    // terminate: interrupted
    public static <T> Callable<T> sleepyAtInterval(Callable<T> task, long interval) {
        return sleepyAtInterval(task, NEVER, interval);
    }

    // 2.(2)
    // terminate: interrupted
    public static <T> Runnable sleepy(Callable<T> task, Consumer<T> consumer) {
        return sleepyAtInterval(task, NEVER, DEFAULT_INTERVAL, consumer);
    }

    // 2.(2)
    // terminate: interrupted
    public static <T> Callable<T> sleepy(Callable<T> task) {
        return sleepyAtInterval(task, NEVER, DEFAULT_INTERVAL);
    }

    // 2.(3) With sleep() and re-interrupt() if InterruptedException
    @Deprecated
    public static <T> Runnable sleepyAndInterruptedAtInterval(
            Callable<T> task, Condition condition,
            long interval,
            Consumer<T> consumer) {
        return () -> {
            T result = null;
            log.debug(methodLog("Loop begin"));
            try {
                while (!condition.met() && !Thread.interrupted()) {
                    log.debug(methodLog("Loop ing ..."));
                    result = task.call();
                    // try必须放在while内, 如果try放在while外, 会立刻跳出, !Thread.interrupted()就没有意义
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        log.warn(exceptionLog(e));
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (Exception e) {
                log.error(exceptionLog(e));
            }
            // consume result
            consumer.accept(result);
            log.debug(methodLog("Loop end"));
        };
    }

    // 2.(3) With sleep() and re-interrupt() if InterruptedException
    @Deprecated
    public static <T> Callable<T> sleepyAndInterruptedAtInterval(
            Callable<T> task, Condition condition,
            long interval) {
        return () -> {
            T result = null;
            log.debug(methodLog("Loop begin"));
            while (!condition.met() && !Thread.interrupted()) {
                log.debug(methodLog("Loop ing ..."));
                result = task.call();
                // try必须放在while内, 如果try放在while外, 会立刻跳出, !Thread.interrupted()就没有意义
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    log.warn(exceptionLog(e));
                    Thread.currentThread().interrupt();
                }
            }
            log.debug(methodLog("Loop end"));
            return result;
        };
    }

    // 2.(3) With sleep() and re-interrupt() if InterruptedException
    @Deprecated
    public static <T> Runnable sleepyAndInterruptedAtInterval(
            Callable<T> task,
            long time, long interval,
            Consumer<T> consumer) {
        return sleepyAndInterruptedAtInterval(task, timeout(time), interval, consumer);
    }

    // 2.(3) With sleep() and re-interrupt() if InterruptedException
    @Deprecated
    public static <T> Callable<T> sleepyAndInterruptedAtInterval(
            Callable<T> task,
            long time, long interval) {
        return sleepyAndInterruptedAtInterval(task, timeout(time), interval);
    }
}

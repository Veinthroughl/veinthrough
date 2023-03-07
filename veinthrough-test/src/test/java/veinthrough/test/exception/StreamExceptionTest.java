package veinthrough.test.exception;

import lombok.extern.slf4j.Slf4j;
import veinthrough.test.async.WordCounter;

/**
 * @author veinthrough
 * 
 * Handle exception in Stream:
 * 1. CheckedException -> RuntimeException, 将终止程序，
 * {@link WordCounter#byBlockingQueue()}
 * 2. CheckedException -> Either.left, 将忽略Exception并计算最终结果,
 * {@link WordCounter.ThreadPoolCounter#call()}
 * 3. CheckedException/task -> Either.left, 将打印Exception并计算最终结果,
 * {@link WordCounter.FutureTaskCounter#call()}
 */
@Slf4j
public class StreamExceptionTest {
}

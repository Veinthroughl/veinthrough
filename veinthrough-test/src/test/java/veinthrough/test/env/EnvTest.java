package veinthrough.test.env;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.test.util.PropertiesTest;

import java.util.Map;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * Test of System's Environment.
 * This very basic OS-dependent method worked in 1.1 (and 1.2?), was
 * deprecated in 1.3, throws an exception in 1.4, and WORKS AGAIN in 1.5.
 * 1. case-sensitive:
 * case-insensitive in Windows.
 * case-sensitive in Linux.
 * 2. system properties
 * environment: 是系统级的环境变量，系统当中所有的进程都可以访问到
 * system property: 是java应用程序自身指定的变量，通常我们可以在启动应用的时候指定的
 * {@link PropertiesTest#systemPropertiesTest()}
 */
@Slf4j
public class EnvTest {
    @Test
    public void allEnvironmentsTest() {
        Map<String, String> envsMap = System.getenv();
        envsMap.keySet().forEach(
                key -> log.info(methodLog(key, System.getenv(key))));
    }

    @Test
    public void pathTest() {
        log.info(methodLog("Path", System.getenv("Path"),
                "PATH", System.getenv("PATH")));
    }
}

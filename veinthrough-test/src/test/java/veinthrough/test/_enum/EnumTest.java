package veinthrough.test._enum;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * 1. enum可以像byte/short/int/char/String一样用于switch
 * 2. APIs:
 * (1) 编译器自动生成的API:
 * 所有enum: {@link SIZE#values()}
 * enum名称: {@link SIZE#name()}
 * 根据名称得到enum: {@link SIZE#valueOf(String)}
 * (2) 手动实现API:
 * enum值: {@link SIZE#getValue()}
 * 根据值得到enum: {@link SIZE#forValue(Integer)}
 */
@Slf4j
public class EnumTest {
    @Test
    public void printAllElementsTest1() {
        for (SIZE size : SIZE.values()) {
            switch (size) {
                case TOO_SMALL:
                    log.info(methodLog("too small" + size.getScopeString()));
                    break;
                case EXTRA_LARGE:
                    log.info(methodLog("extra large" + size.getScopeString()));
                    break;
                case TOO_LARGE:
                    log.info(methodLog("too large" + size.getScopeString()));
                    break;
                default:
                    log.info(methodLog(size.toString()));
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void printAllElementsTest2() {
        for (SIZE2 size : SIZE2.values())
            log.info(methodLog(size.toString()));
    }

    @Test
    public void forValueTest1() {
        Stream.of(-1, 0, 25, 50, 100)
                .forEach(size -> {
                    try {
                        log.info(methodLog(
                                "" + size, SIZE.forValue(size).name()));
                    } catch (SIZE.InvalidSizeException e) {
                        log.error(exceptionLog(e));
                    }
                });
    }
}

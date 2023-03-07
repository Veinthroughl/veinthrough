package veinthrough.test.io.output;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Date;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * # pattern:
 * # __%____________________________________________________________________________conversion_____
 * #      |                  |   |        |  |         |  |    |                |              |
 * #     |___param index__$_|   |__flag__|  |__width__|  |    |__.__precision__|              |
 *                                                      |_____t_____time conversion__________|
 */
@Slf4j
public class PrintfTest {
    @Test
    public void hexTest() {
        String str = "ffffff00";
        // use parseUnsignedInt
        log.info(methodLog(
                String.format("%s: %#x", str, Integer.parseUnsignedInt(str, 16))));
    }

    @Test
    public void floatTest() {
        // flag: ,
        // precision: 2
        // conversion: f
        log.info(methodLog(
                "%,.2f", String.format("%,.2f", 10000.0 / 3.0),
                "%,6.2f", String.format("%,6.2f", 10000.0 / 3.0),
                "%,8.2f", String.format("%,8.2f", 10000.0 / 3.0),
                "%,10.2f", String.format("%,10.2f", 10000.0 / 3.0)));
    }

    @Test
    public void dateTest() {
        // conversion: s
        // param index: 1, 2
        // time conversion: B,e,Y
        System.out.printf("%1$s %2$tB %2$te, %2$tY\n", "Due date:", new Date());
    }
}

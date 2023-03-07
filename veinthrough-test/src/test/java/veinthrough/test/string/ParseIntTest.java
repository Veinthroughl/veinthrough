package veinthrough.test.string;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static veinthrough.api.util.MethodLog.methodLog;

@Slf4j
public class ParseIntTest {
    /**
     * "ffffff00":
     * (1) use {@link Integer#parseInt(String)}, will overflow: java.lang.NumberFormatException
     * (2) use {@link Integer#parseUnsignedInt(String)}
     */
    @Test
    public void parseIntTest() {
        String str = "ffffff00";
        // will overflow,java.lang.NumberFormatException
        log.info(methodLog(
//                "parseInt", String.format("%s: %#x", str, Integer.parseInt(str, 16)),
                "parseUnsignedInt", String.format("%s: %#x", str, Integer.parseUnsignedInt(str, 16))));
    }
}

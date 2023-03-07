package veinthrough.test.string;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.UUID;

import static veinthrough.api.string.UUIDUtils.macToUUID;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * Convert a mac address to uuid:
 * {@link veinthrough.api.string.Regex#isMacAddress(String)}
 * {@link veinthrough.api.string.Regex#delimOfMacAddress(String)}: delim of mac address can be ":"/"-"
 * {@link veinthrough.api.string.UUIDUtils#macToUUID(String)}
 */
@Slf4j
public class MacToUuidTest {
    @Test
    public void macToUuidTest() {
        // delim can be ":" or "-"
//        String mac= "00:00:00:00:00:01";
        String mac = "00-00-00-00-00-01";
        log.info(methodLog("mac", mac,
//                "uuid", UUID.randomUUID().toString()));
                "uuid", macToUUID(mac).orElse(UUID.randomUUID()).toString()));
    }
}

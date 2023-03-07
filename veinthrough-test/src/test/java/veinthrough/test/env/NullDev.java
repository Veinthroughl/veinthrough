package veinthrough.test.env;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;

import static veinthrough.api.util.Constants.*;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * Get null dev of different OS:
 * Linux: /dev/null
 * Windows: NUL:
 * Others: jnk
 */
@Slf4j
public class NullDev {
    @Test
    public void nullDevTest() {
        log.info(methodLog("Null dev", getNullDev()));
    }

    private static String getNullDev() {
        String osName = System.getProperty("os.name");
        System.out.println("OS name: " + osName);
        if (new File(UNIX_NULL_DEV).exists()) {
            return UNIX_NULL_DEV;
        } else if (osName.startsWith("Windows")) {
            return WINDOWS_NULL_DEV;
        }
        return FAKE_NULL_DEV;
    }
}
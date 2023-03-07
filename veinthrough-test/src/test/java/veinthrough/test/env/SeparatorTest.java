package veinthrough.test.env;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

import static veinthrough.api.util.Constants.EMPTY;
import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * Display separator and path separator in different os,
 * and create a file named separator_test.txt based on the directory of class.
 */
@Slf4j
public class SeparatorTest {
    private static final String fileName = "separator_test.txt";

    @Test
    public void separatorTest() {
        log.info(methodLog(
                "separator", File.separator,
                "separator char", "" + File.separatorChar,
                "path separator", File.pathSeparator,
                "path separator char", "" + File.pathSeparatorChar));
        // className.split(".") is not right
        String absoluteFileName = "src\\methodReferenceTest\\java\\" +
                // "\\.": 转义"."
                // windows下File.separator为\, 需要Matcher.quoteReplacement(File.separator)获取
                this.getClass().getName()
                        // replace last class name with empty
                        .replaceAll(this.getClass().getSimpleName() + "$", EMPTY)
                        .replaceAll("\\.", Matcher.quoteReplacement(File.separator))
                + fileName;
        try {
            // [WINDOWS]only when directory "\com\veinthrough\methodReferenceTest\env\SeparatorTest" exists, it will succeed.
            // [LINUX]only when directory "/com/veinthrough/methodReferenceTest/env/SeparatorTest" exists, it will succeed.
            if (new File(absoluteFileName).createNewFile()) {
                log.info(methodLog(absoluteFileName + " created!"));
            } else {
                log.info(methodLog(absoluteFileName + " already existed!"));
            }
        } catch (IOException e) {
            log.error(exceptionLog(e, "Error creating file " + absoluteFileName));
        }
    }
}
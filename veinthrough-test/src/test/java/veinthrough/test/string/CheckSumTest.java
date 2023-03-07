package veinthrough.test.string;

import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.google.common.base.Charsets.UTF_8;
import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * Print a checksum of a file with new line character considered.
 * 1. read a file by line via readLine(), which will not read new line characters
 * 2. read a file by char via read(), which will read new line characters
 * 3. newLine:
 * Windows: \r\n
 * Linux: \n
 * 4. Stream.reduce()的使用方法: {@link veinthrough.test.stream.StreamTest}
 */
@SuppressWarnings("UnstableApiUsage")
@Slf4j
public class CheckSumTest {
    private static final String fileName = "checksum_test.txt";
    private static long checkSumOfNewLine = checkSum(System.getProperty("line.separator"));

    @Test
    public void checkSumByLineTest() {
        try {
            long sumByLine = checkSumByLine();
            log.info(methodLog("Check sum by line: " + sumByLine));
        } catch (FileNotFoundException e) {
            log.error(exceptionLog(e, "File not found " + fileName));
        } catch (IOException e) {
            log.error(exceptionLog(e));
        }
    }

    @Test
    public void checkSumByCharTest() {
        try {
            long sumByChar = checkSumByChar();
            log.info(methodLog("Check sum by char: " + sumByChar));
        } catch (FileNotFoundException e) {
            log.error(exceptionLog(e, "File not found " + fileName));
        } catch (IOException e) {
            log.error(exceptionLog(e));
        }
    }

    public long checkSumByLine() throws IOException {
        // Use source of Guava can take advantage of Stream
        return Files.asCharSource(new File(fileName), UTF_8)
                // read a line, newLine is omitted
                .readLines()
                .stream()
                .reduce(0L,
                        (checksum, str) -> {
                            // + checksum of each line + checksum of newLine
                            checksum += checkSum(str) + checkSumOfNewLine;
                            return checksum;
                        },
                        (checksum1, checksum2) -> checksum1 + checksum2)
                // one less checksum of newLine
                - checkSumOfNewLine;
    }

    private long checkSumByChar() throws IOException {
        return checkSum(
                Files.asCharSource(new File(fileName), UTF_8)
                        .read());
    }

    public static long checkSum(String str) {
        // str.chars() returns a Stream
        return str.chars()
                .mapToLong(intValue -> (long) intValue)
                .reduce(0L,
                        (checksum, character) -> {
                            checksum += character;
                            return checksum;
                        });
    }
}

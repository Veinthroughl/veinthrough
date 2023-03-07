package veinthrough.test.io.file;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.test.io.StreamRWTest;

import java.io.*;

import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @see StreamRWTest
 * @see FileStreamTest
 * 1. FileWriter
 * (1) TODO [Problem] writer/OuputStream一般都是不存在就创建?
 * (2) constructors
 * {@link FileWriter#FileWriter(String fileName)}
 * {@link FileWriter#FileWriter(String fileName, boolean append)}
 * {@link FileWriter#FileWriter(File file)}
 * {@link FileWriter#FileWriter(File file, boolean append)}
 * {@link FileWriter#FileWriter(FileDescriptor fd)}
 * (3) FileWriter/OutputStreamWriter
 * {@link FileWriter}不使用charset作为参数，需要charset用{@link OutputStreamWriter}来构造,
 * {@link StreamRWTest}
 *
 * 2. FileReader
 * (1) constructors:
 * {@link FileReader#FileReader(String fileName)}
 * {@link FileReader#FileReader(File file)}
 * {@link FileReader#FileReader(FileDescriptor fd)}
 * (2) FileReader/InputStreamReader
 * {@link FileReader}不使用charset作为参数，需要charset用{@link InputStreamReader}来构造,
 * {@link StreamRWTest}
 *
 * 3. FileReader/FileWriter完全用InputStreamReader/OutputStreamWriter来实现
 * 4. FileReader/BufferedReader
 * {@link FileReader}没有readLine()函数,
 * {@link BufferedReader}才有: {@link BufferedReader#readLine()}
 *
 * {@link FileReader#read(char[])}
 * {@link FileReader#read(char[], int, int)}
 * 
 * Tests:
 * 1. read/write, 没有包含charset的构造函数, PrintWriter/InputStreamReader/OutputStreamWriter才有
 * {@link veinthrough.test.io.output.PrintWriterTest}
 * {@link StreamRWTest}
 * 2. read/write, override/append
 * @see FileStreamTest#overrideTest()
 * @see FileStreamTest#appendTest()
 */
@Slf4j
public class FileRWTest {
    private static final String FILE_NAME = "file_RW_test.txt";
    private static final int BUF_SIZE = 100;
    @Test
    public void writeTest() {
        try (FileWriter out = new FileWriter(FILE_NAME)) {
            out.write("姓名: veinthrough 冷\n");
        } catch (IOException e) {
            log.info(exceptionLog(e));
        }
    }

    @Test
    public void readTest() {
        try (FileReader in = new FileReader(FILE_NAME)) {
            if (in.ready()) {
                char[] buf = new char[BUF_SIZE];
                int len = in.read(buf, 0, BUF_SIZE);
                log.info(methodLog(String.format(
                        "%d chars: %s", len, new String(buf, 0, len))));
            }
        } catch (IOException e) {
            log.info(exceptionLog(e));
        }
    }
}

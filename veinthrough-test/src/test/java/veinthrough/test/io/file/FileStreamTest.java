package veinthrough.test.io.file;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.*;

import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * APIs:
 * {@link FileOutputStream#FileOutputStream(File)}
 * {@link FileOutputStream#FileOutputStream(File, boolean)}
 * {@link FileOutputStream#FileOutputStream(String path)}
 * {@link FileOutputStream#FileOutputStream(String path, boolean)}
 *
 * {@link FileOutputStream#FileOutputStream(FileDescriptor)}
 * {@link FileInputStream#FileInputStream(File)}
 * {@link FileInputStream#FileInputStream(String)}
 * {@link FileInputStream#FileInputStream(FileDescriptor)}
 *
 * {@link FileInputStream#read(byte[])}
 * {@link FileInputStream#read(byte[], int, int)}
 *
 * Tests:
 * 1. write in override mode and read
 * 2. write in append mode and read
 * 3. file descriptor methodReferenceTest: {@link FileDescriptorTest}
 */
@Slf4j
public class FileStreamTest {
    private static final int READ_LENGTH = 100;
    private static final String FILE_NAME = "file_stream_test.txt";
    private static final boolean OVERRIDE = false, APPEND = true;

    @Test
    public void overrideTest() {
        _writeTest(OVERRIDE);
    }

    @Test
    public void appendTest() {
        _writeTest(APPEND);
    }

    @Test
    public void overrideFdTest() throws IOException {
        new FileDescriptorTest().overrideFdTest();
    }

    @Test
    public void appendFdTest() throws IOException {
        new FileDescriptorTest().appendFdTest();
    }

    private void _writeTest(boolean mode) {
        try (FileOutputStream fos1 = new FileOutputStream(FILE_NAME)) {
            fos1.write('A');
        } catch (IOException e) {
            log.warn(exceptionLog(e));
        }

        try (FileOutputStream fos2 = new FileOutputStream(FILE_NAME, mode)) {
            fos2.write('a');
        } catch (IOException e) {
            log.warn(exceptionLog(e));
        }

        try (FileInputStream fis = new FileInputStream(FILE_NAME)) {
            byte[] buf = new byte[READ_LENGTH];
            int len = fis.read(buf, 0, READ_LENGTH);
            log.info(methodLog(
                    String.format("%4d bytes:%s", len, new String(buf).substring(0, len))));
        } catch (IOException e) {
            log.warn(exceptionLog(e));
        }
    }
}

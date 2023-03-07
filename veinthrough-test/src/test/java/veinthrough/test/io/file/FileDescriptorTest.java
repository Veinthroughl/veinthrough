package veinthrough.test.io.file;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.*;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * For full file/fd test: {@link FileTest}/{@link FileStreamTest}
 *
 * APIs:
 * {@link FileOutputStream#FileOutputStream(FileDescriptor)}
 * {@link FileInputStream#FileInputStream(FileDescriptor)}
 * {@link FileOutputStream#getFD()}
 *
 * Tests:
 * 1. stand fd test
 * 2. override fd test
 * 3. append fd test
 */
@Slf4j
public class FileDescriptorTest {
    private static final int READ_LENGTH = 100;
    private static final String FILE_NAME = "file_descriptor_test.txt";
    private static final boolean OVERRIDE = false, APPEND = true;

    /**
     * 03 -- err, standard error
     * 01 -- in, standard input
     * 02 -- out, standard output
     * standard output stream should not be closed, otherwise, it may effect the output next.
     */
    @Test
    public void standFDTest() {
        @SuppressWarnings("resource")
        PrintStream out = new PrintStream(
                new FileOutputStream(FileDescriptor.out));
        out.println("standard output descriptor");
    }

    @Test
    public void overrideFdTest() throws IOException {
        _descriptorTest(OVERRIDE);
    }

    @Test
    public void appendFdTest() throws IOException {
        _descriptorTest(APPEND);
    }


    private void _descriptorTest(boolean mode) throws IOException {
        // try resource中只能放resource
        try (FileOutputStream fos1 = new FileOutputStream(FILE_NAME, mode);
             // 不能通过fd修改模式(append)
             // fos2和fos1操作完全一样, 包括在stream中的位置
             // 对fos2操作完全等同于fos1操作
             FileOutputStream fos2 = new FileOutputStream(fos1.getFD())) {
            fos1.write('A');
            fos2.write('a');
            log.info(methodLog(1,
                    String.format("fd(%s) is %s", fos1.getFD(), fos1.getFD().valid())));
        }

        try (FileInputStream fis1 = new FileInputStream(FILE_NAME);
             FileInputStream fis2 = new FileInputStream(fis1.getFD())) {
            byte[] buf = new byte[READ_LENGTH];
            int len = fis1.read(buf, 0, READ_LENGTH);
            log.info(methodLog(2,
                    "Read by filename",
                    String.format("%4d bytes:%s", len, new String(buf).substring(0, len))));

            // fis2和fis1操作完全一样, 包括在stream中的位置
            // 对fis2操作完全等同于fis1操作
            // 因为fis1已经将内容读完, read返回-1
            // java.lang.StringIndexOutOfBoundsException: String index out of range: -1
            len = fis2.read(buf, 0, READ_LENGTH);
            if (len != -1)
                log.info(methodLog(3,
                        "Read by fd",
                        String.format("%4d bytes:%s", len, new String(buf).substring(0, len))));
        }
    }
}

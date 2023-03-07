package veinthrough.test.io;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;

import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * constructors:
 * BufferedInputStream(InputStream in):
 *  default buffer size:8192
 * BufferedInputStream(InputStream in, int bufferSize)
 * BufferedOutputStream(OutputStream out):
 *  default buffer size:8192
 * BufferedOutputStream(OutputStream out, int bufferSize)
 * 
 * APIs:
 * 1. BufferedInputStream.available():
 * only available bytes in buffer, not the available bytes of the whold bottom-layer stream.
 * [BufferedInputStream/BufferedReader/ByteArrayInputStream/CharArrayReader]
 * 2. NO BufferedOutputStream.size().
 * 
 * Tests:
 * 1. write and read
 * 2. write then flush and read
 * 3. mark/reset in read, [BufferedInputStream/BufferedReader/ByteArrayInputStream/CharArrayReader]
 * @see ByteArrayStreamTest#readTest()
 * @see CharArrayRWTest#readTest()
 */
@Slf4j
public class BufferedStreamTest {
    // correspond to "abcdefghijklmnopqrsttuvwxyz"
    private static final byte[] LETTER_BYTE_ARRAY = {
            0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6A, 0x6B, 0x6C, 0x6D, 0x6E, 0x6F,
            0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7A
    };
    private static final int SIZE_BLOCK = 5;
    private static final int SIZE_BUFFER = 11;
    private static final boolean FULL_FLUSH = false, MANUAL_FLUSH = true;
    private static final String fileName = "buffered_stream_test.txt";

    /**
     5 bytes Written: abcde
     0 bytes available
     5 bytes Written: fghij
     0 bytes available
     5 bytes Written: klmno
     10 bytes available
     10 bytes read: abcdefghij
     5 bytes available
     5 bytes read: klmno
     */
    @Test
    public void fullFlushTest() {
        _test(FULL_FLUSH);
    }

    /**
     5 bytes Written: abcde
     5 bytes available
     5 bytes read: abcde
     5 bytes Written: fghij
     5 bytes available
     5 bytes read: fghij
     5 bytes Written: klmno
     5 bytes available
     5 bytes read: klmno
     0 bytes available
     */
    @Test
    public void manualFlushTest() {
        _test(MANUAL_FLUSH);
    }

    @SuppressWarnings("Duplicates")
    private void _test(boolean flush) {
        try (BufferedOutputStream bos =
                     new BufferedOutputStream(new FileOutputStream(fileName), SIZE_BUFFER);
             BufferedInputStream bis =
                     new BufferedInputStream(new FileInputStream(fileName), SIZE_BUFFER)) {
            for (int i = 0; i < SIZE_BUFFER / SIZE_BLOCK + 1; i++) {
                byte[] toWrite = Arrays.copyOfRange(
                        LETTER_BYTE_ARRAY, i * SIZE_BLOCK, (i + 1) * SIZE_BLOCK);
                bos.write(toWrite);
                log.info(methodLog(1,
                        String.format("%d bytes Written: %s", SIZE_BLOCK, new String(toWrite))));
                // manually flush
                if (flush) {
                    bos.flush();
                    log.info(methodLog(2, "Flushed"));
                }

                int available = bis.available();
                log.info(methodLog(3,
                        available + " bytes available"));
                if (available > 0) {
                    byte[] read = new byte[available];
                    int len = bis.read(read, 0, available);
                    log.info(methodLog(3,
                            String.format("%d bytes read: %s", len, new String(read))));
                }
            }

            // flush at last
            // manual flush will not need it as it has flushed on every writing
            if (!flush) {
                // flush and read at last
                bos.flush();
                int available = bis.available();
                log.info(methodLog(4,
                        available + " bytes available"));
                if (available > 0) {
                    byte[] read = new byte[available];
                    int len = bis.read(read, 0, available);
                    log.info(methodLog(4,
                            String.format("%d bytes read: %s", len, new String(read))));
                }
            }
        } catch (IOException e) {
            log.warn(exceptionLog(e));
        }
    }
}

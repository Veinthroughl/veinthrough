package veinthrough.test.io;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.test.io.file.RandomAccessFileTest;

import java.io.*;
import java.util.Arrays;

import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 * @see BufferedStreamTest
 *
 * constructors:
 * BufferedReader(Reader in)
 *  default buffer size:8192
 * BufferedReader(Reader in, int size)
 * BufferedWriter(Writer out, int size)
 *  default buffer size:8192
 * BufferedWriter(Writer out, int size)
 * 
 * APIs:
 * 1. BufferedWriter.newLine()/BufferedReader.readLine()
 * 2. BufferedReader.ready()/BufferedInputStream.available()
 * available:only available chars in buffer, not the available chars of the whole bottom-layer stream.
 * 只有stream才有available(); reader没有available(), 只有ready()
 * [BufferedInputStream/BufferedReader/ByteArrayInputStream/CharArrayReader]
 * @see  BufferedStreamTest
 * 3. NO BufferedWriter.size().
 * 4. BufferedReader.lines()
 *  Java1.8才加入，返回Stream<String>支持流式操作
 * 5. BufferedReader.mark()/BufferedReader.reset()
 *
 * Tests:
 * 1. write and read[很奇怪和BufferedStreamTest的运行结果不一样]
 * 2. write then flush and read
 * 3. mark/reset in read, [BufferedInputStream/BufferedReader/ByteArrayInputStream/CharArrayReader]
 * @see ByteArrayStreamTest#readTest()
 * @see CharArrayRWTest#readTest()
 * 4. readLine methodReferenceTest
 * @see RandomAccessFileTest
 */
@Slf4j
public class BufferedRWTest {

    private static final char[] LETTER_CHAR_ARRAY = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private static final int SIZE_BLOCK = 5;
    private static final int SIZE_BUFFER = 11;
    private static final boolean FULL_FLUSH = false, MANUAL_FLUSH = true;
    private static final String fileName = "buffered_RW_test.txt";

    /**
     5 chars Written: abcde
     5 chars Written: fghij
     5 chars Written: klmno
     15 chars read: abcdefghij
     */
    // [?] 第4步flush之后才能读到数据, buffer满了也没有flush?
    @Test
    public void fullFlushTest() {
        _test(FULL_FLUSH);
    }

    /**
     5 chars Written: abcde
     5 chars read: abcde
     5 chars Written: fghij
     5 chars read: fghij
     5 chars Written: klmno
     5 chars read: klmno
     */
    @Test
    public void manualFlushTest() {
        _test(MANUAL_FLUSH);
    }

    private void _test(boolean flush) {
        try (BufferedWriter bw =
                     new BufferedWriter(new FileWriter(fileName), SIZE_BUFFER);
             BufferedReader br =
                     new BufferedReader(new FileReader(fileName), SIZE_BUFFER)) {

            int remaining = 0;
            for (int i = 0; i < SIZE_BUFFER / SIZE_BLOCK + 1; i++) {
                // write
                char[] toWrite = Arrays.copyOfRange(
                        LETTER_CHAR_ARRAY, i * SIZE_BLOCK, (i + 1) * SIZE_BLOCK);
                bw.write(toWrite);
                remaining += SIZE_BLOCK;
                log.info(methodLog(1,
                        String.format("%d chars Written: %s", SIZE_BLOCK, new String(toWrite))));
                // manually flush
                if (flush) {
                    bw.flush();
                    log.info(methodLog(2, "Flushed"));
                }

                // read
                if (br.ready()) {
                    char[] read = new char[remaining];
                    int len = br.read(read, 0, remaining);
                    remaining -= len;
                    log.info(methodLog(3,
                            String.format("%d chars read: %s", len, new String(read))));
                }
            }

            // flush and read at last
            if (remaining > 0) {
                bw.flush();
                if (br.ready()) {
                    char[] read = new char[remaining];
                    int len = br.read(read, 0, remaining);
                    // noinspection UnusedAssignment
                    remaining -= len;
                    log.info(methodLog(4,
                            String.format("%d chars read: %s", len, new String(read))));
                }
            }
        } catch (IOException e) {
            log.warn(exceptionLog(e));
        }
    }
}

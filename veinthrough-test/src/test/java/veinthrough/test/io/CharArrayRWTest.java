package veinthrough.test.io;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.test.nio.ByteBufTest;
import veinthrough.test.nio.ByteCharBufferTest;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;

import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 * @see ByteCharBufferTest , from nio
 * @see ByteArrayStreamTest , from io
 * @see ByteBufTest , from Netty
 * 
 * constructors:
 * CharArrayReader(char buf[])
 * CharArrayReader(char buf[], int offset, int length)
 * CharArrayWriter():
 *   The buffer capacity is initially 32, though its size increases if necessary
 * CharArrayWriter(int initialSize):
 *   The buffer capacity is initialized as the specified size
 *
 * [Attention]：no char array parameter in CharArrayWriter constructor.
 *
 * APIs:
 * 1. CharArrayReader/ByteArrayInputStream:
 *  1) CharArrayReader.ready()/ByteArrayInputStream.available();
 *  只有stream才有available(); reader没有available(), 只有ready()
 *  [BufferedInputStream/BufferedReader/ByteArrayInputStream/CharArrayReader]
 *  @see  ByteArrayStreamTest#readTest()
 *  2) CharArrayReader will throw exception but ByteArrayInputStream wll not.
 * 2. CharArrayWriter/ByteArrayOutputStream:
 *  1) write(String)/null
 *  2) toCharArray()/toByteArray()
 *  3) append(),append(CharSequence csq),append(CharSequence csq, int start, int end)/null
 * 3. CharArrayReader.ready()/CharArrayWriter.size()
 * 
 * Tests:
 * 1. write/append/read a single char
 * 2. write/append/read a specified size chars
 * 3. write/append/read a CharSeqence
 * 4. read: mark and reset, [BufferedInputStream/BufferedReader/ByteArrayInputStream/CharArrayReader]
 * @see ByteArrayStreamTest#readTest()
 * @see CharArrayRWTest#readTest()
 * 5. write: convert to a char array
 * 6. write: write to another output stream
 * </pre>
 */
@Slf4j
@SuppressWarnings({"UnusedAssignment", "Duplicates"})
public class CharArrayRWTest {

    private static final char[] LETTER_CHAR_ARRAY = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private static final int SIZE_BLOCK = 5;

    @Test
    public void readTest() throws IOException {
        try (CharArrayReader car = new CharArrayReader(LETTER_CHAR_ARRAY)) {
            int pos = 1;
            // CharArrayReader.ready()/ByteArrayInputStream.available()
            while (pos <= SIZE_BLOCK &&
                    car.ready()) {
                // read 1 char
                char read = (char) car.read();
                log.info(methodLog(1,
                        String.format("char letters[%d]: %c", pos++, read)));
            }

            //check whether mark is supported
            if (!car.markSupported()) {
                log.info(methodLog(2, "Mark not supported!"));
                return;
            }
            log.info(methodLog(2, "Mark supported!"));

            // Set the current marked position in the stream.
            // parameter: readAheadLimit, it has no meaning for this class
            car.mark(0);
            int mark = pos;

            log.info(methodLog(3, "Skip block of " + SIZE_BLOCK));
            // noinspection ResultOfMethodCallIgnored
            car.skip(SIZE_BLOCK);
            pos += SIZE_BLOCK;

            char[] buf = new char[SIZE_BLOCK];
            // read chars of a specified size
            int read = car.read(buf, 0, SIZE_BLOCK);
            log.info(methodLog(4,
                    String.format("char letters[%d-%d]: %s",
                            pos, pos + read - 1, new String(buf))));
            pos += read;

            // reset position to the mark
            log.info(methodLog(5, "Reset to mark " + mark));
            car.reset();
            pos = mark;

            read = car.read(buf, 0, SIZE_BLOCK);
            log.info(methodLog(6,
                    String.format("char letters[%d-%d]: %s",
                            pos, pos + read - 1, new String(buf))));
            pos += read;
        }
    }

    @Test
    public void writeTest() {
        // no byte array parameter in constructor
        try (CharArrayWriter caw = new CharArrayWriter();
             CharArrayWriter caw2 = new CharArrayWriter()) {

            // write a single byte
            caw.write('A');
            log.info(methodLog(1,
                    "Write " + caw.size() + " chars",
                    "caw", String.format("%3d chars:%s", caw.size(), caw)));

            // write string
            caw.write("BC");
            log.info(methodLog(2,
                    "Write " + "BC".length() + " chars",
                    "caw", String.format("%3d chars:%s", caw.size(), caw)));

            // write a specified size chars
            caw.write(LETTER_CHAR_ARRAY, 0, SIZE_BLOCK);
            log.info(methodLog(3,
                    "Write " + SIZE_BLOCK + " chars",
                    "caw", String.format("%3d chars:%s", caw.size(), caw)));

            // append
            caw.append('0')
                    .append("123456789")
                    .append(new String(LETTER_CHAR_ARRAY), 8, 12);
            log.info(methodLog(4,
                    "Write " + 14 + " chars",
                    "caw", String.format("%3d chars:%s", caw.size(), caw)));

            // convert to a byte array
            char[] buf = caw.toCharArray();
            log.info(methodLog(
                    5, "To a char array",
                    "buf", String.format("%3d chars:%s", buf.length, new String(buf))));

            // write to another output stream
            caw.writeTo(caw2);
            log.info(methodLog(
                    6, "Write to another writer",
                    "caw2", String.format("%3d bytes:%s", caw2.size(), caw2)));
        } catch (IOException e) {
            log.warn(exceptionLog(e));
        }
    }
}

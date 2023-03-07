package veinthrough.test.io;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.test.nio.ByteBufTest;
import veinthrough.test.nio.ByteCharBufferTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 * @see ByteCharBufferTest, from nio
 * @see CharArrayRWTest , from io
 * @see ByteBufTest, from Netty
 *
 * constructors:
 * ByteArrayInputStream(byte buf[])
 * ByteArrayInputStream(byte buf[], int offset, int length)
 * ByteArrayOutputStream():
 *   The buffer capacity is initially 32 bytes, though its size increases if necessary
 * ByteArrayOutputStream(int size):
 *   The buffer capacity is initialized as the specified size
 *
 * [Attention]：no byte array parameter in ByteArrayOutputStream constructor.
 * 
 * APIs:
 * 1. ByteArrayInputStream.available()/ByteArrayOutputStream.size()
 * 2. [input] markSupported()
 * 3. [input] mark(readAheadLimit)/reset(),
 *   readAheadLimit: it has no meaning for this class
 *   @see java.io.BufferedInputStream#mark(int)
 *   @see BufferedStreamTest
 * 4. read()/ read(buf, off, size),
 *    write()/write(buf, off, size)
 * 5. [input] skip()
 * 6. [output] toByteArray()
 * 7. [output] writeTo(OutputStream)
 * 8. [input] available()
 * 只有stream才有available(); reader没有available(), 只有ready()
 * [BufferedInputStream/BufferedReader/ByteArrayInputStream/CharArrayReader]
 * 
 * Tests:
 * 1. write/read a single byte
 * 2. write/read a specified size bytes
 * 3. read: mark and reset, [BufferedInputStream/BufferedReader/ByteArrayInputStream/CharArrayReader]
 * @see ByteArrayStreamTest#readTest()
 * 4. write: convert to a byte array
 * 5. write: write to another output stream
 */
@Slf4j
@SuppressWarnings({"UnusedAssignment", "Duplicates"})
public class ByteArrayStreamTest {

    // correspond to "abcdefghijklmnopqrsttuvwxyz"
    private static final byte[] lettersByteArray = new byte[]{
            0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6A, 0x6B, 0x6C, 0x6D, 0x6E, 0x6F,
            0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7A
    };
    private static final int SIZE_BLOCK = 5;

    @Test
    public void readTest() {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(lettersByteArray)) {
            int pos = 1;
            // CharArrayReader.ready()/ByteArrayInputStream.available()
            while (pos <= SIZE_BLOCK &&
                    bais.available() >= 0) {
                // read 1 byte
                byte read = (byte) bais.read();
                log.info(methodLog(1,
                        String.format("byte letters[%d]: %c(%#x)", pos++, read, read)));
            }

            // check whether mark is supported
            if (!bais.markSupported()) {
                log.info(methodLog(2, "Mark not supported!"));
                return;
            }
            log.info(methodLog(2, "Mark supported!"));

            // Set the current marked position in the stream.
            // parameter: readAheadLimit, it has no meaning for this class
            log.info(methodLog(2, "Mark " + pos));
            bais.mark(0);
            int mark = pos;

            log.info(methodLog(3, "Skip a block of " + SIZE_BLOCK));
            // noinspection ResultOfMethodCallIgnored
            bais.skip(SIZE_BLOCK);
            pos += SIZE_BLOCK;

            byte[] buf = new byte[SIZE_BLOCK];
            // read bytes of a specified size
            int read = bais.read(buf, 0, SIZE_BLOCK);
            log.info(methodLog(4,
                    String.format("byte letters[%d-%d]: %s",
                            pos, pos + read - 1, new String(buf))));
            pos += read;

            // reset position to the mark
            log.info(methodLog(5, "Reset to mark " + mark));
            bais.reset();
            pos = mark;

            // read bytes of a specified size
            read = bais.read(buf, 0, SIZE_BLOCK);
            log.info(methodLog(6,
                    String.format("byte letters[%d-%d]: %s",
                            pos, pos + read - 1, new String(buf))));
            pos += read;
        } catch (IOException e) {
            log.info(exceptionLog(e));
        }
    }

    @Test
    public void writeTest() {
        // no byte array parameter in constructor
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ByteArrayOutputStream baos2 = new ByteArrayOutputStream()) {
            // correspond to "ABCDE"
            // write a single byte
            baos.write(0x41);
            baos.write(0x42);
            baos.write(0x43);
            baos.write(0x44);
            baos.write(0x45);
            log.info(methodLog(0,
                    "Write " + baos.size() + " bytes",
                    "baos", String.format("%3d bytes:%s", baos.size(), baos)));

            // write a specified size bytes
            baos.write(lettersByteArray, 0, SIZE_BLOCK);
            log.info(methodLog(1,
                    "Write a block of " + SIZE_BLOCK,
                    "baos", String.format("%3d bytes:%s", baos.size(), baos)));

            // convert to a byte array
            byte[] buf = baos.toByteArray();
            log.info(methodLog(2,
                    "To a byte array",
                    "buf", String.format("%3d bytes:%s", buf.length, new String(buf))));

            // write to another output stream
            baos.writeTo(baos2);
            log.info(methodLog(3,
                    "Write to another stream",
                    "baos2", String.format("%3d bytes:%s", baos2.size(), baos2)));
        } catch (IOException e) {
            log.info(exceptionLog(e));
        }
    }
}

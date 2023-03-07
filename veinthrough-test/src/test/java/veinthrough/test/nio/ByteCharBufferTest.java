package veinthrough.test.nio;

import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.test.io.ByteArrayStreamTest;
import veinthrough.test.io.CharArrayRWTest;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 * 
 * ByteBuffer/CharBuffer are from nio
 * @see ByteArrayStreamTest , from io
 * @see CharArrayRWTest , from io
 * @see ByteBufTest, from Netty
 */
@Slf4j
@SuppressWarnings("SameParameterValue")
public class ByteCharBufferTest {
    private static final Charset UTF_8 = Charset.forName(Charsets.UTF_8.name());

    @Test
    public void charsetTest() {
        String name = "veinthrough 冷";
        log.info(methodLog("name", name,
                "Re-encoded and decoded name", new String(getChars(
                        getBytes(name.toCharArray(), UTF_8), UTF_8))));
    }

    private byte[] getBytes(char[] chars, Charset charset) {
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = charset.encode(cb);
        return bb.array();
    }

    private char[] getChars(byte[] bytes, Charset charset) {
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = charset.decode(bb);
        return cb.array();
    }
}

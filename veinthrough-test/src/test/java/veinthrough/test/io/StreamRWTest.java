package veinthrough.test.io;

import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * InputStreamReader/OutputStreamWriter是字节流通向字符流的桥梁,它使用指定的charset读写字节并将其解码为字符.
 * 写入的时候, 会将写入的内容默认转换utf-8编码并写入,
 * 读取的时候, 会将内容默认转换成utf-8的内容转换成字节并读出来
 *
 * constructors:
 * OutputStreamWriter(OutputStream out)
 * OutputStreamWriter(OutputStream out, String charsetName)
 * OutputStreamWriter(OutputStream out, Charset cs)
 * OutputStreamWriter(OutputStream out, CharsetEncoder enc)
 *
 * InputStreamReader(InputStream out)
 * InputStreamReader(InputStream out, String charsetName)
 * InputStreamReader(InputStream out, Charset cs)
 * InputStreamReader(InputStream out, CharsetEncoder enc)
 *
 * APIs:
 * 1. InputStreamReader.ready()/ NO OutputStreamWriter.size()
 * 2. {@link InputStreamReader#read(char[])}
 * {@link InputStreamReader#read(char[], int, int)}
 * {@link InputStreamReader#read(CharBuffer)}
 *
 * Tests:
 * 1. Used different charsets in OutputStreamWriter/InputStreamReader
 * US_ASCII can't encoding 中文
 */
@SuppressWarnings("TryWithIdenticalCatches")
@Slf4j
public class StreamRWTest {
    private static final String FILE_NAME = "stream_RW_test.txt";
    private static final int BUF_SIZE = 100;
    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();
    @SuppressWarnings("unused")
    private static final Charset US_ASCII = Charset.forName(Charsets.US_ASCII.name());
    private static final Charset GB2312 = Charset.forName("GB2312");
    private static final Charset GBK = Charset.forName("GBK");

    @Test
    public void defaultCharsetTest() {
        writeByCharset(DEFAULT_CHARSET);
        readByCharset(DEFAULT_CHARSET);
    }

    @Test
    public void GB2312Test() {
        writeByCharset(GB2312);
        readByCharset(GB2312);
    }

    @Test
    public void GBKTest() {
        writeByCharset(GBK);
        readByCharset(GBK);
    }

    private void writeByCharset(Charset charset) {
        try (OutputStreamWriter out = new OutputStreamWriter(
                new FileOutputStream(FILE_NAME), charset)) {
            out.write("姓名: veinthrough 冷\n");
        } catch (FileNotFoundException e) {
            log.warn(exceptionLog(e));
        } catch (IOException e) {
            log.warn(exceptionLog(e));
        }
    }

    private void readByCharset(Charset charset) {
        try (InputStreamReader in = new InputStreamReader(
                new FileInputStream(FILE_NAME), charset)) {
            if (in.ready()) {
                char[] buf = new char[BUF_SIZE];
                int len = in.read(buf, 0, BUF_SIZE);
                log.info(methodLog(String.format(
                        "%d chars:%s\n", len, new String(buf, 0, len))));
            }
        } catch (FileNotFoundException e) {
            log.warn(exceptionLog(e));
        } catch (IOException e) {
            log.warn(exceptionLog(e));
        }
    }
}

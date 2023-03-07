package veinthrough.test.io;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.IntStream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 * Tests:
 * 1. methodReferenceTest available Charsets
 * 2. Use different charsets encode/decode 中文
 * US_ASCII can't encoding 中文
 */
@Slf4j
public class CharsetTest {
    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();
    private static final Charset US_ASCII = Charset.forName(Charsets.US_ASCII.name());
    private static final Charset GB2312 = Charset.forName("GB2312");
    private static final Charset GBK = Charset.forName("GBK");

    @Test
    public void availableCharsetsTest() {
        final int numPerLine = 10;
        List<String> charsets =
                Lists.newArrayList(Charset.availableCharsets().keySet());
        IntStream.range(0, charsets.size())
                .forEach(index ->
                        charsets.set(index,
                                index % numPerLine == 0 ?
                                        charsets.get(index) + "\n" : charsets.get(index)));
        log.info(methodLog(
                String.format("%d charsets: %s",
                        charsets.size(),
                        charsets.toString())));
    }

    @Test
    public void basicTest() {
        String str = "姓名: veinthrough 冷";
        log.info(methodLog(
                "origin", str,
                DEFAULT_CHARSET.name(), new String(str.getBytes(DEFAULT_CHARSET), DEFAULT_CHARSET),
                GB2312.name(), new String(str.getBytes(GB2312), GB2312),
                // US_ASCII can't encoding 中文
                US_ASCII.name(), new String(str.getBytes(US_ASCII), US_ASCII),
                GBK.name(), new String(str.getBytes(GBK), GBK)));
    }
}

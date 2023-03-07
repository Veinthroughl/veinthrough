package veinthrough.test.string;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.lang.StringAlign;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 * @see StringAlign
 */
@Slf4j
public class StringAlignTest{
    @Test
    public void stringAlignTest() {
        String[] messages = {"JavaFun", "JavaFun!"};
        ImmutableList.of(
                new StringAlign(50, StringAlign.Justify.LEFT),
                new StringAlign(100, StringAlign.Justify.LEFT),
                new StringAlign(50, StringAlign.Justify.CENTER),
                new StringAlign(100, StringAlign.Justify.CENTER),
                new StringAlign(50, StringAlign.Justify.RIGHT),
                new StringAlign(100, StringAlign.Justify.RIGHT))
                .forEach(align ->
                        Stream.of(messages)
                                .forEach(message ->
                                        log.info(methodLog(
                                                String.format("[%s][%s,%d]", message, align.getJust(), align.getMaxChars()),
                                                align.format(message)))));
    }
}

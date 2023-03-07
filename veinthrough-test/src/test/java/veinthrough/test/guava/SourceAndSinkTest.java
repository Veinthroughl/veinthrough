package veinthrough.test.guava;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.test.string.CheckSumTest;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;

import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * 1. source/sink:
 * Abstract some common(general-purpose) operations from different kinds of char/byte stream.
 * (1) source operations: read()/hash()/readLines()/copyTo()/size()/isEmpty()/contentEquals()
 * (2) sink operations: write()/writeFrom()/writeLines()
 *
 * APIs:
 * 1. 得到source
 * (1) {@link CharSource}, 通过Files/Resources:
 * {@link Files#asCharSource(File, Charset)}
 * {@link Resources#asCharSource(URL, Charset)}
 * (2) {@link ByteSource},通过Files/Resources
 * {@link Files#asByteSource(File)}
 * {@link Resources#asByteSource(URL)}
 * 2. 得到sink:
 * {@link Files#asCharSink(File, Charset, FileWriteMode...)}
 * {@link Files#asByteSink(File, FileWriteMode...)}
 * 3. CharSource操作:
 * {@link CharSource#readLines()}/{@link CharSource#read()}
 * {@link CharSource#copyTo(CharSink)}
 *  Tests:{@link CheckSumTest#checkSumByLine()}/@link CheckSumTest#checkSum(String)}
 * 4. ByteSource操作:
 * {@link ByteSource#read()},
 * {@link ByteSource#copyTo(ByteSink)}
 * {@link ByteSource#hashCode()}/{@link ByteSource#hash(HashFunction)}
 */
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("UnstableApiUsage")
public class SourceAndSinkTest {
    private static final String fileName = "source_sink_test.txt";

    /**
     * {@link CharSource#readLines()}
     */
    @Test
    public void readLinesTest() {
        File file = new File(fileName);
        try {
            ImmutableList<String> lines = Files.asCharSource(file, Charsets.UTF_8)
                    .readLines();
            Iterator<String> iterator = lines.iterator();
            int line = 0;
            while (iterator.hasNext()) {
                log.info(methodLog(
                        String.format("Line %d: %s", ++line, iterator.next())));
            }
        } catch (IOException e) {
            log.info(exceptionLog(e));
        }
    }

    /**
     * {@link CharSource#read()}
     */
    @Test
    public void readTest() {
        File file = new File(fileName);
        try {
            Multiset<String> wordOccurrences = HashMultiset.create(
                    Splitter.on(CharMatcher.WHITESPACE)
                            .trimResults()
                            .omitEmptyStrings()
                            .split(Files.asCharSource(file, Charsets.UTF_8).read()));
            for (String element : wordOccurrences.elementSet()) {
                log.info(methodLog(
                        String.format("%s : %d", element, wordOccurrences.count(element))));
            }
        } catch (IOException e) {
            log.info(exceptionLog(e));
        }
    }

    /**
     * {@link ByteSource#hashCode()}
     * {@link ByteSource#hash(HashFunction)}
     */
    @Test
    public void hashTest() {
        File file = new File(fileName);
        try {
            HashCode hash = Files.asByteSource(file).hash(Hashing.sha1());
            log.info(methodLog(hash.toString()));
        } catch (IOException e) {
            log.info(exceptionLog(e));
        }
    }

    /**
     * {@link ByteSource#copyTo(ByteSink)}
     */
    @Test
    public void sinkTest() {
        File file = new File(fileName);
        try {
            Resources.asByteSource(
                    new URL("https://issues.apache.org/jira/si/jira.issueviews:issue-xml/IMPALA-2983/IMPALA-2983.xml"))
                    .copyTo(Files.asByteSink(file));
        } catch (IOException e) {
            log.info(exceptionLog(e));
        }
    }
}

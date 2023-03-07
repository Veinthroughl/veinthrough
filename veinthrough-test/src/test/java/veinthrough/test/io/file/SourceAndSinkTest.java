package veinthrough.test.io.file;

import org.junit.Test;

/**
 * How to use source and sink in guava to handle file.
 * @see veinthrough.test.guava.SourceAndSinkTest
 */
public class SourceAndSinkTest {
    /**
     * @see veinthrough.test.guava.SourceAndSinkTest
     */
    @Test
    public void sourceAndSinkTest() {
        veinthrough.test.guava.SourceAndSinkTest tester = new veinthrough.test.guava.SourceAndSinkTest();
        tester.readLinesTest();
        tester.readTest();
        tester.hashTest();
        tester.sinkTest();
    }
}

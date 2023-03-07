package veinthrough.leetcode;

public class ExampleForErrorException extends Exception {
    private static final String DEFAULT_MESSAGE = "This is an example for bug, You should not run it.";

    public ExampleForErrorException() {
        super(DEFAULT_MESSAGE);
    }

    public ExampleForErrorException(String message) {
        super(DEFAULT_MESSAGE + '\n' + message);
    }
}

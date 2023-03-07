package veinthrough.test.env;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.lang.GetOpt;
import veinthrough.api.lang.GetOptDesc;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static veinthrough.api.util.Constants.BLANK1;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * Demonstrate the modern way of using GetOpt. This allows a subset of
 * <pre>UNIX sort options: sort -n -o outfile infile1 infile2</pre>
 * which means: sort numerically (-n), writing to file "outfile" (-o
 * outfile), sort from infile1 and infile2.
 *
 * java environ.GetOptTest -M
 * java environ.GetOptTest -n a b c
 * java environ.GetOptTest -numeric a b c
 * java environ.GetOptTest -numeric -output-file /tmp/foo a b c
 */
@Slf4j
public class GetOptTest {
    private static final String command1 = "app -n -o a.txt b.txt c.txt";
    private static final String command2 = "app -n a.txt b.txt c.txt";
    private static final String command3 = "app -o a.txt b.txt c.txt";
    private static final String command4 = "app a.txt b.txt -n -o c.txt";

    // configuration of options
    private static final GetOptDesc[] options = {
            new GetOptDesc('n', "numeric", false),
            new GetOptDesc('o', "output-file", true)};

    @Test
    public void getOptTest() {
        Stream.of(command1, command2, command3, command4)
                .map(command -> {
                    String[] argv = command.split(BLANK1);
                    return Arrays.copyOfRange(argv, 1, argv.length);
                })
                .forEach(this::_getOptTest);
    }

    private void _getOptTest(String[] args) {
        boolean numeric_option = false;
        boolean errs = false;
        String outputFileName = null;
        GetOpt parser = new GetOpt(options);
        Map<String, String> optionsFound = parser.parseArguments(args);
        for (String key : optionsFound.keySet()) {
            char c = key.charAt(0);
            switch (c) {
                case 'n':
                    numeric_option = true;
                    break;
                case 'o':
                    outputFileName = optionsFound.get(key);
                    break;
                case '?':
                    errs = true;
                    break;
                default:
                    throw new IllegalStateException(
                            "Unexpected option character: " + c);
            }
        }
        if (errs) {
            log.error(methodLog("Usage: GetOptTest [-n][-o file][file...]"));
            return;
        }
        log.info(methodLog(
                "args", Arrays.toString(args),
                "numeric", "" + numeric_option,
                "output", outputFileName,
                "input files", parser.getFilenameList().toString()));
    }
}
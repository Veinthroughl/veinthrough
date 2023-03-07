package veinthrough.test.io.input;

import lombok.extern.slf4j.Slf4j;
import veinthrough.api.io.Console;

import java.io.IOException;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * How to use Console.
 * 1. System.console() will be null in eclipse/IntelliJ.
 * @see veinthrough.api.io.Console#readLine(String, Object...)
 * 2. Junit methodReferenceTest/ main()
 *   (1) 使用Junit test没有console, 必须使用main()函数才可以;
 *   如果使用Junit, Scanner(System.in).nextXXX()不会提示用户输入, 也一直获取不到输入
 *   public static void main() method is required for the correct console to appear,
 *   otherwise will only result in a console that doesn't receive inputs.
 *   (2) Junit test的file路径相对module的classpath, main()的file路径相对project的classpath, 如[test_file/scanner_test.txt]:
 *   如果使用Junit, 绝对路径为[...\IdeaProjects\methodReferenceTest(project)\test_file\scanner_test.txt]
 *   如果使用main(), 绝对路径为[...\IdeaProjects\methodReferenceTest(project)\veinthrough-methodReferenceTest(module)\test_file\scanner_test.txt]
 */
@Slf4j
public class ConsoleTest {
    /**
     * System.console() may return null, use wrapped Console instead System.console()
     *
     * @see veinthrough.api.io.Console#readLine(String, Object...)
     */
    private void consoleTest() throws IOException {
        Console console = new Console();
        log.info(methodLog("username", console.readLine("User name:"),
                "password", new String(console.readPassword("Password:"))));
    }

    public static void main(String[] args) throws IOException {
            new ConsoleTest().consoleTest();
    }
}

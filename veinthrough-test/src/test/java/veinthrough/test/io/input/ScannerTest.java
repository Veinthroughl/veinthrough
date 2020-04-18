package veinthrough.test.io.input;

import lombok.extern.slf4j.Slf4j;
import veinthrough.test.AbstractUnitTester;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * <p>---------------------------------------------------------
 * <pre>
 * Tests:
 * 1. standard input test.
 * 2. file input test with next()/ nextInt().
 * 3. file input test with nextLine()
 * </pre>
 * <p>---------------------------------------------------------
 * <pre>
 * 1. next() compared with nextLine():
 *   (1) next() will trim whitespace before the valid chars.
 *   (2) next() treat whitespace as spliter.
 *   (3) nextLine() treat line.separator as spliter.
 *   System.getProperty("line.separator")
 *   (4) next() will never get whitespace as result.
 * 2. Junit test/ main()
 *   (1) 使用Junit test没有console, 必须使用main()函数才可以;
 *   如果使用Junit, Scanner(System.in).nextXXX()不会提示用户输入, 也一直获取不到输入
 *   public static void main() method is required for the correct console to appear,
 *   otherwise will only result in a console that doesn't receive inputs.
 *   (2) Junit test的file路径相对module的classpath, main()的file路径相对project的classpath, 如[test_file/scanner_test.txt]:
 *   如果使用Junit, 绝对路径为[...\IdeaProjects\test(project)\test_file\scanner_test.txt]
 *   如果使用main(), 绝对路径为[...\IdeaProjects\test(project)\veinthrough-test(module)\test_file\scanner_test.txt]
 * </pre>
 */
@Slf4j
public class ScannerTest extends AbstractUnitTester {
    private static final String fileName = "test_file/scanner_test.txt";

    /* (non-Javadoc)
     * @see UnitTester#test()
     */
    @Override
    public void test() {
    }

    // standard input test
    private void stdInputTest() {
        // try with resource
        // automatically release resource
        try (Scanner in = new Scanner(System.in)) {
            System.out.print("Name:");
            String name = in.nextLine();

            System.out.print("Age:");
            Integer age = in.nextInt();

            log.info(methodLog("name", name,
                    "age", String.valueOf(age)));
        }
    }

    // file input test with next()/ nextInt()
    private void fileNextIntTest() {
        try (Scanner in = new Scanner(new File(fileName))) {
            while (in.hasNext()) {
                if (in.hasNextInt()) {
                    System.out.printf("int: %d\n", in.nextInt());
                } else {
                    System.out.println("str:" + in.next());
                }
            }
        } catch (FileNotFoundException e) {
            log.error(exceptionLog(e));
        }
    }

    // file input test with nextLine()
    private void fileNextLineTest() {
        try (Scanner in = new Scanner(new File(fileName))) {
            int line = 0;
            while (in.hasNextLine()) {
                System.out.printf("line %d: %s\n", ++line, in.nextLine());
            }
        } catch (FileNotFoundException e) {
            log.error(exceptionLog(e));
        }
    }

    public static void main(String[] args) {
        ScannerTest tester = new ScannerTest();

        tester.stdInputTest();
        tester.fileNextIntTest();
        tester.fileNextLineTest();
    }
}

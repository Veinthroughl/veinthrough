package veinthrough.test.io.output;

import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * PrintStream:
 * 1. extends FilterOutputStream
 * 2. System.out is a PrintStream
 * 3. PrintStream will never throw a IOException, otherwise, you can call checkError()
 *    to check whether a IOException is thrown.
 * 4. auto-flush
 * 5. function of set charset/encoding in constructor
 *
 * PrintStream/PrintWriter: {@link PrintWriterTest}
 *
 * constructors:
 * 1. manually-configured auto-flush with OutputStream
 *  PrintStream(OutputStream out)
 *  PrintStream(OutputStream out, boolean autoFlush)
 *  PrintStream(OutputStream out, boolean autoFlush, String encoding)
 * 2. no auto-flush parameter coordinated with file parameter, will automatically create FileOutputStream
 *  PrintStream(String fileName)
 *  PrintStream(String fileName, String charSetName)
 *  PrintStream(File file)
 *  PrintStream(File file, String charSetName)
 *
 * PrintStream/DataOutputStream:
 * 1. PrintStream是输出时采用的是用户指定的编码(创建PrintStream时指定的)，若没有指定，则采用系统默认的字符编码。
 * 而DataOutputStream则采用的是UTF-8。
 * 2. 目的不同: DataOutputStream的作用是装饰其它的输出流，它和DataInputStream配合使用：允许应用程序以与机器无关的方式从底层输入流中读写java数据类型。
 * 而PrintStream的作用虽然也是装饰其他输出流，但是它的目的不是以与机器无关的方式从底层读写java数据类型；而是为其它输出流提供打印各种数据值
 * 表示形式(字符串形式)，使其它输出流能方便的通过print(), println()或printf()等输出各种格式的数据。
 * 3. 异常处理机制不同: DataOutputStream在通过write()向“输出流”中写入数据时，若产生IOException，会抛出。
 * 而PrintStream在通过write()向“输出流”中写入数据时，若产生IOException，则会在write()中进行捕获处理;
 * 并设置trouble标记(用于表示产生了异常)为true。用户可以通过checkError()返回trouble值，从而检查输出 流中是否产生了异常。
 * 4. 构造函数不同: DataOutputStream的构造函数只有一个DataOutputStream(OutputStream out),
 * 即它只支持以输出流out作为"DataOutputStream的输出流";
 * 而PrintStream的构造函数有许多: 和DataOutputStream一样，支持以输出流out作为“PrintStream输出 流”的构造函数;
 * 还支持以“File对象”或者“String类型的文件名对象”的构造函数。
 * 而且，在PrintStream的构造函数中，能“指定字符集”和“是否支持自动flush() 操作”
 *
 * Tests:
 * 1. new a PrintStream by a file name and charset.
 * 2. write chinese methodLog and check error.
 */
@Slf4j
public class PrintStreamTest {
    private static final String FILE_NAME = "print_stream_test.txt";

    @Test
    public void writeChineseTest() {
        try (PrintStream ps = new PrintStream(FILE_NAME, Charsets.UTF_8.name())) {
            ps.append('a');
            ps.printf("My name is: %s\n", "Veinthrough 冷");
            log.info(methodLog(
                    String.format("Has error: %b", ps.checkError())));
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            log.warn(exceptionLog(e));
        }
    }
}

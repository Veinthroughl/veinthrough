package veinthrough.test.jvm;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 */
@SuppressWarnings({"StringEquality", "ConstantConditions", "NumberEquality", "RedundantStringOperation", "unused"})
@Slf4j
public class MemoryTest {
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Test
    public void boxingTest() {
        int a1 = 1;
        int b1 = 1;
        int c1 = 2;
        int d1 = a1 + b1;
        Integer a = 1;
        Integer b = 2;
        Integer c = 3;
        Integer d = 3;
        Integer e = 321;
        Integer f = 321;
        Long g = 3L;
        log.info(methodLog(
                "a1==b1", "" + (a1 == b1), // true, 基本类型, 比较值
                "c1==d1", "" + (c1 == d1), // true, 基本类型, 比较值
                "c==d", "" + (c == d), // true, 装箱类型, 比较地址， -127 - 128被缓存在常量区
                "e==f", "" + (e == f), // false, 装箱类型, 比较地址， 不会被缓存， 不会共享
                "c==(a+b)", "" + (c == (a + b)),  // true， 遇到算术运算的情况下自动拆箱
                "c.equals(a+b)", "" + (c.equals(a + b)),  // true, 装箱类型, 首先会执行if (obj instanceof Long), 然后比较值
                "g==(a+b)", "" + (g == (a + b)),  // true, 比较值， 遇到算术运算的情况下自动拆箱
                "g.equals(a+b)", "" + (g.equals(a + b)))); // false, 装箱类型, 首先会执行if (obj instanceof Long), 然后比较值
    }

    /**
     * 1. String.intern(), 堆引用常量区
     * 如果常量池中已经该字符串，则返回池中的字符串；否则将此字符串添加到常量池中，并返回字符串的引用
     * 2. static对象, 常量区引用堆
     */
    @Test
    public void stringTest() {
        String str1 = "abc"; // 常量区
        String str2 = new String("abc"); // 堆，但是堆会和常量区联系起来, 通过intern()
        String str3 = str2.intern(); // intern()返回在常量区的地址
        String str4 = new String("abc"); // 堆，但是堆会和常量区联系起来, 通过intern()
        String str5 = "ab" + "c";
        log.info(methodLog("str1==str2", "" + (str1 == str2), // false, 常量区/堆
                "str1==str3", "" + (str1 == str3), // true, 都为常量区
                "str2==str4", "" + (str2 == str4), // false, 堆中不同对象
                "str2.intern()==str4.intern()", "" + (str2.intern() == str4.intern()), // true, 堆中不同对象，但是关联的常量区(intern())相同
                "str1==str5", "" + (str1 == str5))); // true, 都为常量区;
    }

    /**
     * 由于变量b被final修饰，因此会被当做编译器常量，所以在使用到b的地方会直接将变量b替换为它的值。
     * 而对于变量d的访问却需要在运行时通过链接来进行。
     */
    @Test
    public void finalTest() {
        String a = "hello2";
        final String b = "hello";
        String c = "hello";
        String d = "hello" + 2;
        String e = b + 2; // 编译时确定, 因为有final, b是常量;
        String f = c + 2; // [?] 运行时确定，会在堆中新建对象，因为没有final, c是变量, 不能被优化么？
        log.info(methodLog(
                "b==c", "" + (b == c), // true, 都在常量区
                "a==d", "" + (a == d), // true, 编译时确定
                "a==e", "" + (a == e), // true, 编译时确定
                "a==f", "" + (a == f), // false, 运行时确定
                "a==f.intern()", "" + (a == f.intern()))); // true, 运行时确定, 但是f.intern()指向同一个常量区
    }
}

package veinthrough.test._class;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.reflect.ClassAnalyzer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * Test general inner class：内部类/静态内部类/匿名内部类/局部内部类
 * 1. 内部类才可以用private/static修饰符
 * 2. 在内部类不需要访问外围类对象的时候, 应该使用静态内部类
 * 3. 非static: 内部类和外部类对象是绑定的
 * 4. 访问外部类域
 * (1) 非static: 外部类会生成static access函数, 内部类中会把(组合)外部类的对象作为成员, 并且在构造函数中初始化
 * (2) static: 不会
 * 5. 访问外部局部变量/外部参数:匿名内部类/局部内部类
 * 在内部类中都会生成对应的final成员
 * 6. Variable 'age' is accessed from within inner class, needs to be [final] or [effectively final]
 * 局部内部类和匿名内部类访问的[局部变量]必须由final修饰，java8开始，可以不加final修饰符，由系统默认添加
 * (1) 这是由Java对lambda表达式的实现决定的，在Java中lambda表达式是匿名类语法上的进一步简化，其本质还是调用对象的方法。
 * (2) 在Java中方法调用是值传递的，所以在lambda表达式中对变量的操作都是基于原变量的副本，不会影响到原变量的值。
 * (3) 综上，假定没有要求lambda表达式外部变量为final修饰，那么开发者会误以为外部变量的值能够在lambda表达式中被改变，
 * 而这实际是不可能的，所以要求外部变量为final是在编译期以强制手段确保用户不会在lambda表达式中做修改原变量值的操作。
 * (4) 另外，对lambda表达式的支持是拥抱函数式编程，而函数式编程本身不应为函数引入状态，
 * 从这个角度看，外部变量为final也一定程度迎合了这一特点。
 * 内部类会自动拷贝外部变量的引用，为了避免
 * > 外部方法修改引用，而导致内部类得到的引用值不一致
 * > 内部类修改引用，而导致外部方法的参数值在修改前和修改后不一致。于是就用 final 来让该引用不可改变。
 * > 内部类函数执行时局部变量可能已经不存在了，所以对局部变量的操作都是基于原变量的副本
 * 所以内部匿名类的用外面的变量要加final，其实是一个闭包的解决方案的问题，程序员不希望在外面变量改变的时候，
 * 里面的值没有变，这样是反知觉的; 就说既然内外不能同步，那就不许大家改外围的局部变量。
 */

@Slf4j
public class InnerClassTest {
    private static final long DURATION_TEST = 10000L;

    @Test
    public void innerClassTest() throws InterruptedException {
        TalkingClock clock = new TalkingClock(1000, true);
        clock.start();
        Thread.sleep(DURATION_TEST);
    }

    @Test
    public void staticInnerClassTest() throws InterruptedException {
        TalkingClockStatic clock = new TalkingClockStatic(1000);
        clock.start();
        Thread.sleep(DURATION_TEST);
    }

    @Test
    public void anonymousInnerClassTest() throws InterruptedException {
        TalkingClockAnonymous clock = new TalkingClockAnonymous(1000, false);
        clock.start(true);

        Thread.sleep(DURATION_TEST);
    }

    @Test
    public void localInnerClassTest() throws InterruptedException {
        TalkingClockLocal clock = new TalkingClockLocal(1000, true);
        clock.start(false);
        Thread.sleep(DURATION_TEST);
    }

    @Test
    public void classAnalysisTest() {
        ClassAnalyzer analyzer = new ClassAnalyzer();
        // inner
        log.info(methodLog(analyzer.analyze(TalkingClock.class.getName())));
        log.info(methodLog(analyzer.analyze(TalkingClock.TimePrinter.class)));
        // static inner
        log.info(methodLog(analyzer.analyze(TalkingClockStatic.class)));
        log.info(methodLog(analyzer.analyze(TalkingClockStatic.TimePrinter.class)));
        // anonymous inner
        log.info(methodLog(new ClassAnalyzer().analyze(TalkingClockAnonymous.class)));
        // local inner
        log.info(methodLog(new ClassAnalyzer().analyze(TalkingClockLocal.class)));
    }
}

/**
 * 3. 非static: 内部类和外部类对象是绑定的
 * 4. 访问外部类域
 * (1) 非static: 外部类会生成static access函数, 内部类中会把(组合)外部类的对象作为成员, 并且在构造函数中初始化
 * (2) static: 不会
 *
 * # class TalkingClock
 * # {
 * #     public veinthrough.methodReferenceTest._class.TalkingClock(int, boolean);
 * #
 * #     static boolean access$100(TalkingClock);  // 访问了beep,而beep为private，相当于beep的static访问器
 *                                                 // [?] 这里为什么要做成static?
 * #     static Logger access$000();
 * #     public void start();
 * #
 * #     private static final Logger log;
 * #     private int interval;
 * #     private boolean beep;
 * # }
 * #
 * # TalkingClock$TimePrinter
 * # {
 * #     veinthrough.methodReferenceTest._class.TalkingClock$TimePrinter(TalkingClock);  // 在构造函数中初始化这些自动构造的域
 * #     public void actionPerformed(ActionEvent);
 * #     final TalkingClock this$0; // 非静态内部类将会生成引用外部类对象的域，该域为final
 * # }
 */
@AllArgsConstructor
@Slf4j
class TalkingClock {
    private int interval;
    private boolean beep;

    public void start() {
        ActionListener listener = new TimePrinter();
        Timer t = new Timer(interval, listener);
        t.start();
    }

    // 内部类才可以用private/static修饰符
    class TimePrinter implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            Date now = new Date();
            log.info(methodLog("now", now.toString()));
            // 访问外围类成员
            if (beep) Toolkit.getDefaultToolkit().beep();
        }
    }
}

/**
 * 4. 访问外部类域
 * (1) 非static: 外部类会生成static access函数, 内部类中会把(组合)外部类的对象作为成员, 并且在构造函数中初始化
 * (2) static: 不会
 *
 * # TalkingClockStatic
 * # {
 * #     public veinthrough.methodReferenceTest._class.TalkingClockStatic(int);
 * #
 * #     static Logger access$000();
 * #     public void start();
 * #     public static boolean isBeep();
 * #
 * #     private static final Logger log;
 * #     private int interval;
 * #     private static final boolean beep;
 * # }
 * #
 * #
 * # TalkingClockStatic$TimePrinter
 * # {
 * #     veinthrough.methodReferenceTest._class.TalkingClockStatic$TimePrinter();
 * #     public void actionPerformed(ActionEvent);
 * # }
 */
@Slf4j
@AllArgsConstructor
class TalkingClockStatic {
    private int interval;
    @Getter
    private static final boolean beep = false;

    public void start() {
        ActionListener listener = new TimePrinter();
        Timer t = new Timer(interval, listener);
        t.start();
    }

    // 内部类才可以用private/static修饰符
    // 内部类不访问外部类对象，应置static
    static class TimePrinter implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            Date now = new Date();
            log.info(methodLog("now", now.toString()));
            // 访问外围类静态成员
            if (TalkingClockStatic.isBeep()) Toolkit.getDefaultToolkit().beep();
        }
    }
}

/**
 * 5. 访问外部局部变量/外部参数: 匿名内部类/局部内部类
 * 在内部类中都会生成对应的final成员
 *
 * # TalkingClockAnonymous
 * # {
 * #     public veinthrough.methodReferenceTest._class.TalkingClockAnonymous(int, boolean);
 * #
 * #     static boolean access$100(TalkingClockAnonymous); // 访问了beep,而beep为private，相当于beep的static访问器
 * #     static Logger access$000();
 * #     public void start(boolean);
 * #     public void setBeep(boolean);
 * #
 * #     private static final Logger log;
 * #     private int interval;
 * #     private boolean beep;
 * # }
 * # TalkingClockAnonymous$1
 * # {
 * #     veinthrough.methodReferenceTest._class.TalkingClockAnonymous$1(TalkingClockAnonymous, Date, boolean);
 * #
 * #     public void actionPerformed(ActionEvent);
 * #
 * #     final Date val$now;                  // 外部局部变量
 * #     final boolean val$beepLocal;         // 外部参数
 * #     final TalkingClockAnonymous this$0;  // 用来访问外部类域
 * # }
 */
@AllArgsConstructor
@Slf4j
class TalkingClockAnonymous {
    private int interval;
    @Setter
    private boolean beep;

    // Local variable accessed by local inner class must be final or effectively final;
    // 即接下来没有被修改，所以最好把局部内部类访问的局部变量置final
    public void start(final boolean beepLocal) {
        final Date now = new Date();
        Timer t = new Timer(interval, new ActionListener() {
            // 局部内部类只能是abstract/final
            public void actionPerformed(ActionEvent event) {
                // 只能通过this.getClass()来获取class
                log.info(methodLog(new ClassAnalyzer().analyze(this.getClass())));
                log.info(methodLog("now", now.toString()));
                // 局部内部类可以访问外部类域, 也可以访问局部变量，但必须为final
                if (beep | beepLocal) Toolkit.getDefaultToolkit().beep();
            }
        });

        // 修改局部内部类访问的局部变量，那么就不是effectively final
//        now = new Date(new CalendarTest().next(Calendar.MONTH, now.getTime()));
        t.start();
    }
}

/**
 * 5. 访问外部局部变量/外部参数: 匿名内部类/局部内部类
 * 在内部类中都会生成对应的final成员
 * 
 * # TalkingClockLocal
 * # {
 * #     public veinthrough.methodReferenceTest._class.TalkingClockLocal(int, boolean);
 * #
 * #     static boolean access$100(TalkingClockLocal); // 访问了beep,而beep为private，相当于beep的static访问器
 * #     static Logger access$000();
 * #     public void start(boolean);
 * #     public void setBeep(boolean);
 * #
 * #     private static final Logger log;
 * #     private int interval;
 * #     private boolean beep;
 * # }
 * # TalkingClockLocal$1TimePrinter
 * # {
 * #     veinthrough.methodReferenceTest._class.TalkingClockLocal$1TimePrinter(TalkingClockLocal, Date, boolean);
 * #
 * #     public void actionPerformed(ActionEvent);
 * #
 * #     final Date val$now;                         // 外部参数
 * #     final boolean val$beep_;                    // 外部局部变量
 * #     final TalkingClockLocal this$0;             // 用来访问外部类域
 * # }
 */
@AllArgsConstructor
@Slf4j
class TalkingClockLocal {
    private int interval;
    @Setter
    private boolean beep;

    // Local variable accessed by local inner class must be final or effectively final;
    // 即接下来没有被修改，所以最好把局部内部类访问的局部变量置final
    public void start(final boolean beep_) {
        final Date now = new Date();
        // 局部内部类只能是abstract/final
        class TimePrinter implements ActionListener {
            public void actionPerformed(ActionEvent event) {
                log.info(methodLog("now", now.toString()));
                // 局部内部类可以访问外部类域
                // 局部内部类也可以访问局部变量，但必须为final
                if (beep | beep_) Toolkit.getDefaultToolkit().beep();
            }
        }

        // 修改局部内部类访问的局部变量，那么就不是effectively final
//        now = new Date(new CalendarTest().next(Calendar.MONTH, now.getTime()));

        log.info(methodLog(new ClassAnalyzer().analyze(TimePrinter.class)));
        ActionListener listener = new TimePrinter();
        Timer t = new Timer(interval, listener);
        t.start();
    }

}

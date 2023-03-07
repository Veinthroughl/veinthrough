package veinthrough.test._class;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * 通过代码说明Java中类、继承的一些规范、规则,
 * {@link DerivedTest}/{@link InterfaceTest}/{@link Employee}/{@link Manager}
 *
 * 1. 私有数据域+公有域访问器+公有域更改器，这里用lombok实现，{@link Employee#getName()}
 * 2. final 域，类似于指针常量，没有@Setter，{@link Employee}
 * 3. Java中的参数传递都为值传递。
 * 4. 构造函数：如果定义了有参构造函数，也应该定义无参构造函数，{@link Employee#Employee()},
 * 无参构造函数：
 * (1) new/未初始化对象都会默认调用无参构造函数
 * (2) 子类自动调用
 * (3) 有些框架需要: @entity in Spring Data Rest, Json网络传输, Serializable
 * 5. 初始化：
 * (1) static块: 在加载类，还没有创建对象的时候就会执行
 * (2) 初始化块: 在声明后，构造函数前执行
 */
@Slf4j
public class ClassTest {
    /**
     * (1) SuperClass.isAssignableFrom(InheritedClass)
     * (2) instance of: {@link Employee#equals(Object)}
     * (3) getClass() != otherObject.getClass(): {@link Employee#equals2(Object)}
     */
    @Test
    public void assignableClassTest() {
        log.info(methodLog(
                "Employee is assignable from manager", "" + Employee.class.isAssignableFrom(Manager.class),
                "Manager is assignable from employee", "" + Manager.class.isAssignableFrom(Employee.class)));
    }
}

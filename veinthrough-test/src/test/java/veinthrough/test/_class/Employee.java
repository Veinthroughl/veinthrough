package veinthrough.test._class;

import com.google.common.base.Preconditions;
import lombok.*;
import veinthrough.api._interface.Identifiable;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author veinthrough
 *
 * 通过代码说明Java中类、继承的一些规范、规则,
 * {@link ClassTest}/{@link DerivedTest}/{@link InterfaceTest}/{@link Manager}
 */
@RequiredArgsConstructor
@AllArgsConstructor()
public class Employee implements Comparable<Employee>, Cloneable, Identifiable<Long> {
    @Getter
    private static final AtomicLong NEXT_ID;

    // 2. final域，类似于指针常量，没有@Setter
    // id/hiredDay如果在初始化块中初始化，而没有在声明的时候初始化，lombok将会当作required args
    // 生成RequiredArgsConstructor时将会包括该参数, @AllArgsConstructor也会包含该参数
    @Getter
    private final Long id = NEXT_ID.incrementAndGet();
    @Getter
    private final Date hireDay = new Date(System.currentTimeMillis());

    // 1. 私有数据域+公有域访问器+公有域更改器，这里用lombok实现
    @NonNull
    @Getter
    @Setter
    private String name;
    @NonNull
    @Getter
    @Setter
    private Double salary;
    @Getter
    @Setter
    private String hobby;

    // 5. 初始化：
    // static块: 在加载类，还没有创建对象的时候就会执行
    static {
        NEXT_ID = new AtomicLong(0);
    }

    // 5. 初始化：
    // 初始化块: 在声明后，构造函数前执行
//    {
//        hireDay = new Date(System.currentTimeMillis());
//        id = NEXT_ID.incrementAndGet();
//    }

    // 4. 如果定义了有参构造函数，也应该定义无参构造函数
    // new/未初始化对象/子类/@Entity(spring data rest)都会默认调用无参构造函数
    // 6. 子类的构造函数没有显示调用父类的构造函数也会默认调用无参构造函数
    public Employee() {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(salary);
    }

    public void raiseSalary(double byPercent) {
        double raise = salary * byPercent / 100;
        salary += raise;
    }

    // 通过各个name/salary/hireDate的值来比较，那么在子类中可能有更多的域的值需要比较
    // 14. 子类拥有自己的相等概念，则对称性需求将强制采用getClass检测而不能用instanceof
    public boolean equals(Object otherObject) {
        // a quick methodReferenceTest to see if the objects are identical
        if (this == otherObject) return true;

        // must return false if the explicit parameter is null
        if (otherObject == null) return false;

        // if the classes don't match, they can't be equal
        // Using instanceof is a worse choice as
        // as it's not symmetrical when calling equals() in parent and child classes
        if (getClass() != otherObject.getClass()) return false;

        // now we know otherObject is a non-null Employee
        Employee other = (Employee) otherObject;

        // methodReferenceTest whether the fields have identical values
        // Objects.equals can handle null:
        // if only one of them is null, return false;
        // else return a.equals(b)
        return Objects.equals(name, other.name) &&
                Objects.equals(salary, other.salary) &&
                Objects.equals(hireDay, other.hireDay);

    }

    // 另外一种equals的实现，只通过id来比较
    // 11. 将方法和类声明为final的主要目的是：确保他们不会再子类中改变语义，
    // 因为如果只根据id来比较，就不需要在子类中改变语义
    // 15. 因为不需要在子类中改变语义，由父类决定相等概念，则可以用instanceof进行检测，
    // 且应该将方法声明为final
    @SuppressWarnings("unused")
    final boolean equals2(Object otherObject) {
        // a quick methodReferenceTest to see if the objects are identical
        if (this == otherObject) return true;

        // must return false if the explicit parameter is null
        if (otherObject == null) return false;

        // if the classes don't match, they can't be equal
        // Using instanceof is a worse choice as
        // as it's not symmetrical when calling equals() in parent and child classes
        if (!(otherObject instanceof Employee)) return false;
        // 和instanceof一样的效果
//        if(Employee.class.isAssignableFrom(otherObject.getClass())) return false;

        // now we know otherObject is a non-null Employee
        Employee other = (Employee) otherObject;

        // methodReferenceTest whether the fields have identical values
        // Objects.equals can handle null:
        // if only one of them is null, return false;
        // else return a.equals(b)
        return id.equals(other.getId());
    }

    // 16. 如果重定义了equals方法，就必须重定义hashCode方法并保持一致；
    // 即如果a.equals(b), 那么应该a.hashCode()==b.hashCode()
    public int hashCode() {
        return Objects.hash(name, salary, hireDay);
    }

    public String toString() {
        return getClass().getName() + "[name=" + name + ",salary=" + salary + ",hireDay=" + hireDay
                + "]";
    }

    // 14/18. 如果子类之间的比较含义不一样，那父类/子类就属于不同类对象的比较；
    // 那么父类和子类每个compareTo()都应该在开始进行getClass()==other.getClass()的比较
    // 11/15/18. 如果在父类和子类中存在通用的比较方法，则应该在父类中提供一个compareTo方法，
    // 并声明为final；将方法和类声明为final的主要目的是：确保他们不会再子类中改变语义。
    // 这里使用的是这个
    @Override
    final public int compareTo(Employee other) {
        return Double.compare(getSalary(), other.getSalary());
    }

    // 13/19. Cloneable:
    // (1) protected方法比较有实际意义，最好的示例是Object.clone()，因为Object.clone()方法
    // 只是浅拷贝，而子类可以使用其来实现自己的clone()
    // (2) Cloneable为标记接口，没有方法，Clone()实际上来自于Object类
    // (3) 即使clone的默认实现（浅拷贝）满足需求，也应该实现Cloneable接口，
    // 将clone重定义为public，并调用super.clone(), 以供其他类使用
    public Employee clone() throws CloneNotSupportedException {
        Employee cloned = (Employee) super.clone();
        cloned.hireDay.setTime(hireDay.getTime());
        return cloned;
    }

    @Override
    public Long getIdentifier() {
        return this.getId();
    }
}

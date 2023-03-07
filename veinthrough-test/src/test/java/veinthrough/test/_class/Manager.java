package veinthrough.test._class;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Objects;

/**
 * @author veinthrough
 *
 * 通过代码说明Java中类、继承的一些规范、规则,
 * {@link DerivedTest}/{@link InterfaceTest}/{@link Employee}/{@link Manager}
 */
// 子类中用lombok生成的构造函数不会包含父类中的域
//@RequiredArgsConstructor
public class Manager extends Employee
{
    @NonNull @Getter @Setter private Double bonus;

    // 6. 如果子类的构造函数没有显示调用父类的构造函数，会默认调用无参构造函数
    public Manager(String name, Double salary, Double bonus) {
        super(name,salary);
        this.bonus = bonus;
    }

    @SuppressWarnings("unused")
    @Builder
    public Manager(String name, Double salary, String hobby, Double bonus) {
        super(name,salary,hobby);
        this.bonus = bonus;
    }

    // 10. 在覆盖一个方法时，子类方法不能低于超类方法的可见性
    // (1) 多态原因，
    // 比如：Person person = new Man(); 这里man是person的子类，如果person中有个方法say，现在Man却变成了private的了，
    // 那如果用person.say()在运行的时候不是会报错呀，说没权访问！！
    // (2) 里氏替换原则，Liskov于1987年提出了一个关于继承的原则“Inheritance should ensure that any property proved about supertype objects
    // also holds for subtype objects.”——“继承必须确保超类所拥有的性质在子类中仍然成立。”
    // 也就是说，当一个子类的实例应该能够替换任何其超类的实例时，它们之间才具有is-A关系。
    // 该原则称为Liskov Substitution Principle——里氏替换原则。
    public Double getSalary()
    {
        // 7. 子类不能访问父类的private域，只能调用父类的域访问器
        double baseSalary = super.getSalary();
        return baseSalary + bonus;
    }

    // 14. 子类拥有自己的相等概念，则对称性需求将强制采用getClass检测而不能用instanceof
    @Override
    public boolean equals(Object otherObject)
    {
        if (!super.equals(otherObject)) return false;
        Manager other = (Manager) otherObject;
        // super.equals checked that this and other belong to the same class
        return Objects.equals(bonus, other.bonus);
    }

    @Override
    public int hashCode()
    {
        return super.hashCode() + 17 * bonus.hashCode();
    }

    @Override
    public String toString()
    {
        return super.toString() + "[bonus=" + bonus + "]";
    }
}


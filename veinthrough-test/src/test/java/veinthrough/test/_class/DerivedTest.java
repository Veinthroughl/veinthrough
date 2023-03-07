package veinthrough.test._class;

/**
 * @author veinthrough
 *
 * 通过代码说明Java中类、继承的一些规则,
 * {@link ClassTest}/{@link InterfaceTest}/{@link Employee}/{@link Manager}
 *
 * 6. 如果子类的构造函数没有显示调用父类的构造函数也会默认调用无参构造函数, {@link Manager}
 * 7. 子类不能访问父类的private域，只能调用父类的域访问器, {@link Manager#getSalary()}
 * 8. 子类(/数组的引用)可以转换成超类(/数组的引用)而不需要强制转换, {@link DerivedTest#polymorphismTest()}
 * 9. private/static/final方法将会执行静态绑定；而动态绑定依赖于隐式参数(this)的实际类型
 * 10. 在覆盖一个方法时，子类方法不能低于超类方法的可见性, {@link Manager#getSalary()}
 * 11. 将方法和类声明为final的主要目的是：确保他们不会再子类中改变语义，{@link Employee#equals2(Object)}，
 * 因为如果只根据id来比较，就不需要在子类中改变语义
 * 12. 将超类转换成子类前，先用instanceof检查, {@link DerivedTest#polymorphismTest()}
 * 13. 谨慎使用protected属性，因为其他人可以从这个类派生出子类来访问protected域，{@link Object#clone()}
 * protected方法比较有实际意义，最好的示例是Object.clone()，因为Object.clone()方法只是浅拷贝，
 * 也就是说protected是防止被其他类调用(因为它只是浅拷贝)，
 * 而子类可以使用其来实现自己的clone()。
 * 14. 子类拥有自己的相等概念，则对称性需求将强制采用getClass检测而不能用instanceof，
 * {@link Employee#equals(Object)}/{@link Manager#equals(Object)},{@link Employee#compareTo(Employee)}
 * 15. 因为不需要再子类中改变语义，由父类决定相等概念，则可以用instanceof进行检测，且应该将方法声明为final,
 * {@link Employee#equals2(Object)}, {@link Employee#compareTo(Employee)}
 * 16. 如果重定义了equals方法，就必须重定义hashCode方法并保持一致,{@link Employee#hashCode()},
 * 即如果a.equals(b), 那么应该a.hashCode()==b.hashCode()
 * 17.强烈建议为自定义的每一个类增加toString()，系统在需要字符串的地方都会自动调用toString()。
 */
class DerivedTest{
    @SuppressWarnings("all")
    private void polymorphismTest() {
        // 7. 子类(/数组的引用)可以转换成超类(/数组的引用)而不需要强制转换
        Manager[] managers = new Manager[2];
        managers[1] = new Manager("Bob", 60000D, 60000D);
        Employee[] staffs = managers;
        //然后staffs中一半是Employee，一半是Manager
        staffs[0] = new Employee("Alice", 70000D);

        // 11. 将超类转换成子类前，先用instanceof检查
        if(staffs[1] instanceof Manager) {
            Manager manager = (Manager) staffs[1];
        }

    }
}

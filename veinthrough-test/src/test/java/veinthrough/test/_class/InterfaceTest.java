package veinthrough.test._class;

/**
 * @author veinthrough
 *
 * 通过代码说明Java中类、继承的一些规范、规则,
 * {@link DerivedTest}/{@link InterfaceTest}/{@link Employee}/{@link Manager}
 *
 * 18. Comparable:
 * (1) 如果子类之间的比较含义不一样，那父类/子类就属于不同类对象的非法比较；
 * 那么父类和子类每个compareTo()都应该在开始进行getClass()==other.getClass()的比较
 * (2) 如果在父类和子类中存在通用的比较方法，则应该在父类中提供一个compareTo方法，
 * 并声明为final；将方法和类声明为final的主要目的是：确保他们不会再子类中改变语义。
 * 19. Cloneable:
 * (1) protected方法比较有实际意义，最好的示例是Object.clone()，因为Object.clone()方法
 * 只是浅拷贝，而子类可以使用其来实现自己的clone()
 * (2) Cloneable为标记接口，没有方法，Clone()实际上来自于Object类
 * (3) 即使clone的默认实现（浅拷贝）满足需求，也应该实现Cloneable接口，
 * 将clone重定义为public，并调用super.clone()
 */
@SuppressWarnings("unused")
class InterfaceTest {
}

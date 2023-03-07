package veinthrough.api.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author veinthrough
 * This class give a implementation of printing a class declaration by reflect.
 *
 * 1. Generic/non-generic, {@link GenericClassAnalyzer#analyze(Class)}
 * (1) getSuperclass/getGenericSuperclass
 * (2) getInterfaces/getGenericInterfaces
 * (3) getAnnotatedInterfaces: 注解
 *
 * 2. [static]获取Class: {@link Class#forName(String)}
 * 3. Class APIs
 * 父类: {@link Class#getSuperclass()}
 * 名称: {@link Class#getName()}/{@link Class#getSimpleName()}
 * [native]修饰符: {@link Class#getModifiers()}
 * 构造函数: Constructor<?>[] getDeclaredConstructors()/
 * 函数: Method[] getDeclaredMethods()
 * 成员变量: Field[] getDeclaredFields()
 * 4. Constructor APIs
 * 名称: {@link Constructor#getName()}
 * 修饰符: {@link Constructor#getModifiers()}
 * 参数类型: {@link Constructor#getParameterTypes()}
 * 5. Method APIs
 * 名称: {@link Method#getName()}
 * 返回类型: {@link Method#getReturnType()}
 * 参数类型: {@link Method#getParameterTypes()}
 * 修饰符: {@link Method#getModifiers()}
 * 6. Field APIs
 * 名称: {@link Field#getName()}
 * 类型: {@link Field#getType()}
 * 修饰符: {@link Field#getModifiers()}
 * 7. [static]Modifier APIs: {@link Modifier#toString(int)}
 */
public class ClassAnalyzer{
    private static final String SPACE = " ";
    private static final String FOUR_SPACE = "    ";

    public String analyze(String className) {
        String result = "";
        try {
            // print class name and superclass name (if != Object)
            Class<?> clazz = Class.forName(className);
            result = analyze(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String analyze(Class<?> clazz) {
        StringBuilder result = new StringBuilder();

        // modifiers
        result.append(modifiersString(clazz.getModifiers()));

        // name
        result.append("class ").append(clazz.getName());

        // super
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            result.append(" extends ").append(superClass.getSimpleName());
        }

        result.append("\n{\n");
        result.append(analyzeConstructors(clazz));
        result.append("\n");
        result.append(analyzeMethods(clazz));
        result.append("\n");
        result.append(analyzeFields(clazz));
        result.append("}\n");
        return result.toString();
    }

    private String analyzeConstructors(Class<?> clazz) {
        StringBuilder result = new StringBuilder();
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            // modifiers
            result.append(FOUR_SPACE)
                    .append(modifiersString(constructor.getModifiers()));

            // name
            result.append(constructor.getName()).append("(");

            // parameter types
            Class<?>[] paramTypes = constructor.getParameterTypes();
            int numParams = paramTypes.length;
            for (int j = 0; j < numParams; j++) {
                if (j > 0) result.append(", ");
                result.append(paramTypes[j].getSimpleName());
            }
            result.append(");\n");
        }
        return result.toString();
    }

    private String analyzeMethods(Class<?> clazz) {
        StringBuilder result = new StringBuilder();
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            Class<?> retType = method.getReturnType();

            // modifiers
            result.append(FOUR_SPACE)
                    .append(modifiersString(method.getModifiers()));

            // return type
            result.append(retType.getSimpleName()).append(SPACE);

            // name
            result.append(method.getName()).append("(");

            // parameter types
            Class<?>[] paramTypes = method.getParameterTypes();
            for (int j = 0; j < paramTypes.length; j++) {
                if (j > 0) result.append(", ");
                result.append(paramTypes[j].getSimpleName());
            }
            result.append(");\n");
        }
        return result.toString();
    }

    private String analyzeFields(Class<?> clazz) {
        StringBuilder result = new StringBuilder();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            Class<?> type = field.getType();

            // modifiers
            result.append(FOUR_SPACE)
                    .append(modifiersString(field.getModifiers()));

            // type
            result.append(type.getSimpleName()).append(SPACE);

            // name
            result.append(field.getName()).append(";\n");
        }
        return result.toString();
    }

    private String modifiersString(int modifier) {
        String modifiers = Modifier.toString(modifier);
        if (modifiers.length() > 0)
            return modifiers + SPACE;
        else
            return "";
    }
}

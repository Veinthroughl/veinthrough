package veinthrough.api.reflect;

import java.lang.reflect.*;
import java.util.Arrays;

/**
 * @author veinthrough
 *
 * This class give a implementation of printing a [generic] class declaration by reflect.
 *
 * 1. Non-generic/generic, {@link ClassAnalyzer#analyze(Class)}
 * (1) getSuperclass/getGenericSuperclass
 * (2) getInterfaces/getGenericInterfaces
 * (3) getAnnotatedInterfaces: 注解
 * 
 * 2. Type子类型:
 * (1) Class, 描述具体类型
 * (2) TypeVariable接口，描述类型变量(如T extends Comparable<? super T>)
 * (3) WildcardType接口，描述通配符(如? super T)
 * (4) ParameterizedType接口，描述泛型类或接口类型(如Comparable<? super T>)
 * (5) GenericArrayType接口，描述泛型数组(如T[])
 * 3. Class APIs:
 * 泛型参数: {@link Class#getTypeParameters()}
 * 泛型父类: {@link Class#getGenericSuperclass()}
 * 泛型接口: {@link Class#getGenericInterfaces()}
 * 函数: {@link Class#getDeclaredMethods()}
 * 3. Method's APIs:
 * 描述符: {@link Method#getModifiers()}
 * 泛型参数: {@link Method#getTypeParameters()}, 一般static method才有type parameters
 * 泛型返回类型: {@link Method#getGenericReturnType()}
 * 泛型参数类型: {@link Method#getGenericParameterTypes()}
 */
public class GenericClassAnalyzer {
    @SuppressWarnings("unused")
    public String analyze(String className) {
        try {
            // print class name and superclass name (if != Object)
            Class<?> clazz = Class.forName(className);
            return analyze(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String analyze(Class<?> clazz) {
        StringBuilder strBuilder = new StringBuilder();
        // print generic info for class and public methods
        printClass(clazz, strBuilder);
        for (Method m : clazz.getDeclaredMethods())
            printMethod(m, strBuilder);
        return strBuilder.toString();
    }

    private static void printClass(Class<?> cl, StringBuilder strBuilder) {
        strBuilder.append(cl);
        printTypes(cl.getTypeParameters(), "<", ", ", ">", true, strBuilder);
        Type sc = cl.getGenericSuperclass();
        if (sc != null) {
            strBuilder.append(" extends ");
            printType(sc, false, strBuilder);
        }
        printTypes(cl.getGenericInterfaces(), " implements ", ", ", "", false, strBuilder);
        strBuilder.append("\n");
    }

    private static void printMethod(Method m, StringBuilder strBuilder) {
        String name = m.getName();
        strBuilder.append(Modifier.toString(m.getModifiers()));
        strBuilder.append(" ");
        printTypes(m.getTypeParameters(), "<", ", ", "> ", true, strBuilder);

        printType(m.getGenericReturnType(), false, strBuilder);
        strBuilder.append(" ");
        strBuilder.append(name);
        strBuilder.append("(");
        printTypes(m.getGenericParameterTypes(), "", ", ", "", false, strBuilder);
        strBuilder.append(")").append("\n");
    }

    private static void printTypes(Type[] types, String pre, String sep, String suf,
                                   boolean isDefinition,
                                   StringBuilder strBuilder) {
        if (!pre.equals(" extends ") || !Arrays.equals(types, new Type[]{Object.class})) {
            if (types.length > 0) strBuilder.append(pre);
            for (int i = 0; i < types.length; i++) {
                if (i > 0) strBuilder.append(sep);
                printType(types[i], isDefinition, strBuilder);
            }
            if (types.length > 0) strBuilder.append(suf);
        }
    }

    private static void printType(Type type, boolean isDefinition,
                                  StringBuilder strBuilder) {
        // (1) Class, 描述具体类型
        if (type instanceof Class) {
            Class<?> t = (Class<?>) type;
            strBuilder.append(t.getName());
            // (2) TypeVariable接口，描述类型变量(如T extends Comparable<? super T>)
        } else if (type instanceof TypeVariable) {
            TypeVariable<?> t = (TypeVariable<?>) type;
            strBuilder.append(t.getName());
            if (isDefinition)
                // bounds, 如果T没有限制, 那么getBounds()将返回Object
                printTypes(t.getBounds(), " extends ", " & ", "", false, strBuilder);

            // (3) WildcardType接口，描述通配符(如? super T)
        } else if (type instanceof WildcardType) {
            WildcardType t = (WildcardType) type;
            strBuilder.append("?");
            printTypes(t.getUpperBounds(), " extends ", " & ", "", false, strBuilder);
            printTypes(t.getLowerBounds(), " super ", " & ", "", false, strBuilder);

            // (4) ParameterizedType接口，描述泛型类或接口类型(如Comparable<? super T>)
        } else if (type instanceof ParameterizedType) {
            ParameterizedType t = (ParameterizedType) type;
            Type owner = t.getOwnerType();
            if (owner != null) {
                printType(owner, false, strBuilder);
                strBuilder.append(".");
            }
            printType(t.getRawType(), false, strBuilder);
            printTypes(t.getActualTypeArguments(), "<", ", ", ">", false, strBuilder);

            // (5) GenericArrayType接口，描述泛型数组(如T[])
        } else if (type instanceof GenericArrayType) {
            GenericArrayType t = (GenericArrayType) type;
            printType(t.getGenericComponentType(), isDefinition, strBuilder);
            strBuilder.append("[]");
        }
    }
}

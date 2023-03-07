package veinthrough.api.reflect;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static veinthrough.api.util.MethodLog.exceptionLog;

/**
 * @author veinthrough
 *
 * This class give a implementation of printing all fields and coordinated values of a object.
 *
 * 1. Class APIs:
 * [native]是否是数组: {@link Class#isArray()}
 * [native]数组类型: {@link Class#getComponentType()}, only be called by a array type
 * [native]是否是基本类型: {@link Class#isPrimitive()}
 * 成员变量: {@link Class#getDeclaredFields()}
 * [native]父类: {@link Class#getSuperclass()}
 * 2. Field APIs: 
 * 某个对象的某个Field的值: {@link Field#get(Object)}
 * [static]设置Fields是否可访问: {@link AccessibleObject#setAccessible(AccessibleObject[], boolean)}
 */
@Slf4j
public class ObjectAnalyzer{

    private final List<Object> visited = new ArrayList<>();

    /*
     * analyze a object to a string.
     */
    public String analyze(Object obj) {
        // (1) null
        if(obj == null) return "null";

        // (2) have already visited to get rid of recursive referring
        if(visited.contains(obj)) return "...";
        visited.add(obj);

        Class<?> clazz = obj.getClass();

        // (3) String type
        if(clazz == String.class) return (String)obj;

        StringBuilder result = new StringBuilder();
        // (4) array type: isArray()
        if(clazz.isArray()) {
            // getComponentType()
            result.append(clazz.getComponentType()).append("[]{");
            int length = Array.getLength(obj);
            for(int i=0; i<length; i++) {
                if(i>0) result.append(",");
                Object value = Array.get(obj, i);
                // isPrimitive()
                if(clazz.getComponentType().isPrimitive()) result.append(value);
                else result.append(analyze(value));
            }
            result.append("}");
        }

        // (5) other type
        do {
            result.append(clazz.getName()).append("[");

            // getDeclaredFields()
            Field[] fields = clazz.getDeclaredFields();
            // fields can be non-accessible
            AccessibleObject.setAccessible(fields, true);
            for(Field field : fields) {
                if(!Modifier.isStatic(field.getModifiers())) {
                    // not the first field
                    if(!result.toString().endsWith("[")) result.append(",");
                    result.append(field.getName()).append("=");
                    try {
                        Class<?> field_clazz = field.getType();
                        Object value = field.get(obj);
                        if(field_clazz.isPrimitive()) result.append(value);
                        // recursion of the value
                        else result.append(analyze(value));
                    } catch(Exception e) {
                        log.error(exceptionLog(e));
                    }
                }
            }

            result.append("]");

            // iterate fields of super class
            clazz = clazz.getSuperclass();
        } while(clazz != null);

        return result.toString();
    }

}

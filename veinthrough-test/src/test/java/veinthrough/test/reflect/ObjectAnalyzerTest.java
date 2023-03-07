package veinthrough.test.reflect;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.reflect.ObjectAnalyzer;

import java.util.ArrayList;

import static veinthrough.api.util.MethodLog.methodLog;

@Slf4j
public class ObjectAnalyzerTest {
    /**
     * # java.util.ArrayList[
     * #   elementData=class java.lang.Object[]{
     * #     java.lang.Integer[value=1]java.lang.Number[]java.lang.Object[],
     * #     java.lang.Integer[value=4]java.lang.Number[]java.lang.Object[],
     * #     java.lang.Integer[value=9]java.lang.Number[]java.lang.Object[],
     * #     java.lang.Integer[value=16]java.lang.Number[]java.lang.Object[],
     * #     null,null,null,null,null,null}
     * #   [Ljava.lang.Object;[]java.lang.Object[],size=4]
     * # java.util.AbstractList[modCount=4]
     * # java.util.AbstractCollection[]java.lang.Object[]
     */
    @Test
    public void objectAnalyzerTest() {
        ArrayList<Integer> squares = new ArrayList<>();
        for (int i = 1; i < 5; i++) squares.add(i * i);
        log.info(methodLog((new ObjectAnalyzer().analyze(squares))));
    }
}

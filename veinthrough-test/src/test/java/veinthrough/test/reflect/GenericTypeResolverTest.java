package veinthrough.test.reflect;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.reflect.GenericTypeResolver;

import java.util.ArrayList;
import java.util.List;

import static veinthrough.api.util.MethodLog.methodLog;

@Slf4j
public class GenericTypeResolverTest {
    @Test
    public void genericTypeResolverTest() throws ClassNotFoundException {
        // [?] 必须带上{}, 类型参数才会实例化, 否则会得到T而不是java.lang.String
        List<Integer> list = new ArrayList<Integer>() {
        };
//        List<Integer> list = new ArrayList<>(10);
        log.info(methodLog(
                "list", GenericTypeResolver.GenericTypeOf(list).getName(),
                "list", GenericTypeResolver.GenericTypeNameOf(list)));
    }
}

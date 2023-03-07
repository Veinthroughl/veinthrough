package veinthrough.test.resource;

import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.test.util.PropertiesTest;

import java.io.*;
import java.net.URL;
import java.util.Properties;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * classpath: .../test(projects)/veinthrough-test(module/project)
 * the upper level path of src/main/java
 * 
 * Tests:
 * 1. 当需要读取当前路径(源代码目录)下的properties文件时，即在本地没有部署到具体服务器上的情况
 * FileInputStream(resource): resource必须以src/main/resources/作为前缀
 * 即读取的是.../src/main/resources/resource_properties_test.properties
 * 2. 当工程以war或者jar的形式部署到服务器后，在需要读取对应properties文件情况下，此时应该采取相对路径的读取方法
 * (1) classLoader.getResourceAsStream(resource): resource不需要以src/main/resources作为前缀
 * (2) 将resource转换成绝对路径, 但是没有必要转换成绝对路径, 不过我们可以得到它在jar中的路径,
 * 即读取的是.../target/classes/resource_properties_test.properties
 * 3. Properties(stream)
 * @see PropertiesTest#propertiesTest()
 * 4. Spring boot
 * 直接在属性上@Value("${local.server.port}")
 * 或者用类直接提取属性@PropertySource(value = {"classpath:application.properties"},encoding="utf-8"):
 * 当value出现中文时，会出现注入的值为乱码的现象, 原因是在SpringBoot自动加载application.properties时默认采用unicode编码方式
 */
@Slf4j
public class ResourcePropertiesTest {
    /**
     * 当需要读取当前路径(源代码目录)下的properties文件时，即在本地没有部署到具体服务器上的情况
     * FileInputStream(resource): resource必须是有src/main/resources/前缀
     */
    @Test
    public void srcPropertiesTest() throws IOException {
        // resource必须是有src/main/resources/前缀, 否则java.io.FileNotFoundException
        // src/main/resources为相对project的路径
        String fileName = "src/main/resources/resource_properties_test.properties";
        printProperties(codesResourceStream(fileName));
    }

    // 2.(1) 直接用classLoader.getResourceAsStream(resource)获取数据流
    @Test
    public void targetPropertiesTest() throws IOException {
        // resource不需要src/main/resources/前缀
        String fileName = "resource_properties_test.properties";
        printProperties(targetResourceStream(fileName));
    }

    // 2.(2) 将resource转换成绝对路径, 但是没有必要转换成绝对路径, 不过我们可以得到它在jar中的路径
    @Test
    public void targetPropertiesTest2() throws IOException {
        // resource不需要src/main/resources/前缀
        String fileName = "resource_properties_test.properties";
        String absoluteFilePath = absolutePathOfResource(fileName);
        // /D:/Cloud/Projects/IdeaProjects/methodReferenceTest/veinthrough-methodReferenceTest/target/classes/resource_properties_test.properties
        log.info(methodLog("absolute file path", absoluteFilePath));
        printProperties(new FileInputStream(new File(absoluteFilePath)));
    }

    /**
     * 注意将Stream作为参数不是一个很好的设计模式, 因为不知道何时close,
     * 但是java io中到处都是这样
     */
    private void printProperties(InputStream in) throws IOException {
        Properties properties = new Properties();
        properties.load(in);

        log.info(methodLog("name", properties.getProperty("name"),
                "password", properties.getProperty("password"),
                // 只能get字符串类型然后转换
                "age", "" + Integer.parseInt(properties.getProperty("age"))));
    }

    /**
     * 1. 当需要读取当前路径(源代码目录)下的properties文件时，即在本地没有部署到具体服务器上的情况
     * get file from classpath, resources folder
     * It's not a good idea to return a stream, otherwise, where should the stream be closed?
     * It violate the design patterns. 所以为private, 只在内部调用.
     */
    private InputStream codesResourceStream(String resourceName) throws FileNotFoundException {
        File file = new File(resourceName);
        return new FileInputStream(file);
    }

    /**
     * Extract absolute file path from resource file
     * 2.(1) 将resource转换成绝对路径, 但是没有必要转换成绝对路径, 不过我们可以得到它在jar中的路径
     */
    private String absolutePathOfResource(String resourceName) throws IOException {

        ClassLoader classLoader = ResourcePropertiesTest.class.getClassLoader();

        URL resource = classLoader.getResource(resourceName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            String path = resource.getFile();
            // if path contains chinese/ space, it will be encoded in UTF8,
            // then it will contains %
            if (path.contains("%")) {
                // .../target/classes/resource_properties_test.properties
                path = java.net.URLDecoder.decode(path, Charsets.UTF_8.name());
            }
            return path;
        }
    }

    /**
     * 2.(2) 直接用classLoader.getResourceAsStream(resource)获取数据流
     * It's not a good idea to return a stream, otherwise, where should the stream be closed?
     * It violate the design patterns. 所以为private, 只在内部调用.
     */
    private InputStream targetResourceStream(String resourceName) {
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResourceAsStream(resourceName);
    }
}

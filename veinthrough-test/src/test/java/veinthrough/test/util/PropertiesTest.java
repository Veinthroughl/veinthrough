package veinthrough.test.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.test.env.EnvTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * Test for Properties.
 *
 * Comments:
 * 1. {@link Preferences}: {@link PreferencesTest}
 * 2. environment: 是系统级的环境变量，系统当中所有的进程都可以访问到, {@link EnvTest}
 * 3. system property: 是java应用程序自身指定的变量，通常我们可以在启动应用的时候指定的
 * Disadvantages:
 * (1) 没有标准的为配置文件命名的规则，容易造成配置文件名冲突。
 * (2)（似乎）只能处理字符串类型
 * 
 * Constructions:
 * {@link Properties#Properties()}
 * {@link Properties#Properties(Properties)}
 * 
 * Tests:
 * 1. get/put
 * 2. load/store
 * 3. 使用二级属性映射来实现默认属性; 也可以get时给一个默认值
 */
@SuppressWarnings("Duplicates")
@Slf4j
public class PropertiesTest {
    private static final String DIR_NAME = "properties";
    private static final String FILE_NAME = "properties_test.xml";
    private static final int DEFAULT_LEFT = 0;
    private static final int DEFAULT_TOP = 0;
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 200;
    private static final String DEFAULT_TITLE = "";

    private int x;
    private int y;
    private int w;
    private int h;
    private String t;

    private Properties properties;

    @Test
    public void propertiesTest() throws IOException {
        // 使用二级属性映射来实现默认属性, 提前设置好了默认属性
        // Properties(Properties defaults)
        properties = new Properties(defaultProperties());

        String userDir = System.getProperty("user.home");
        String directory = userDir + "\\" + DIR_NAME;
        File propertiesFile = new File(directory, FILE_NAME);
        if (!propertiesFile.exists()) {
            // noinspection ResultOfMethodCallIgnored
            propertiesFile.createNewFile();
        }

        load(propertiesFile);
        modify();
        store(propertiesFile);
    }

    private Properties defaultProperties() {
        Properties defaultProperties = new Properties();
        // 只能put字符串类型
        defaultProperties.put("left", "" + DEFAULT_LEFT);
        defaultProperties.put("top", "" + DEFAULT_TOP);
        defaultProperties.put("width", "" + DEFAULT_WIDTH);
        defaultProperties.put("height", "" + DEFAULT_HEIGHT);
        defaultProperties.put("title", DEFAULT_TITLE);
        return defaultProperties;
    }

    private void load(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            // load properties
            properties.load(fis);

            // if property isn't loaded, it will get from defaults
            // 只能get字符串类型然后转换
            x = Integer.parseInt(properties.getProperty("left"));
            y = Integer.parseInt(properties.getProperty("top"));
            w = Integer.parseInt(properties.getProperty("width"));
            h = Integer.parseInt(properties.getProperty("height"));
            t = properties.getProperty("title");
            log.info(methodLog("left", "" + x,
                    "top", "" + y,
                    "width", "" + w,
                    "height", "" + h,
                    "title", "" + t));
        } catch (IOException e) {
            log.error(exceptionLog(e));
        }
    }

    private void modify() {
        x += 1;
        y += 1;
        w += 1;
        h += 1;
        t += "-";
    }

    private void store(File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            properties.put("left", "" + x);
            properties.put("top", "" + y);
            properties.put("width", "" + w);
            properties.put("height", "" + h);
            properties.put("title", t);
            // store properties
            properties.store(fos, "Frame configuration");
        } catch (IOException e) {
            log.error(exceptionLog(e));
        }
    }

    @Test
    public void systemPropertiesTest() {
        // simulate "java com.veinthrough.SysProp KEY=VALUE"
        // or "java -D KEY=VALUE com.veinthrough.SysProp"
        Stream.of("name=veinthrough", "java.version")
                .forEach(this::systemProperties);
    }

    private void systemProperties(String property) {
        Properties properties = System.getProperties();
        if (property.contains("=")) {
            int index = property.indexOf("=");
            String key = property.substring(0, index);
            String value = property.substring(index + 1);
            // set property
            properties.setProperty(key, value);
            // print out
            properties.list(System.out);
            // delete
            properties.remove(key);
        } else {
            // use System.getProperty()
            log.info(methodLog(property, System.getProperty(property)));
        }
    }
}

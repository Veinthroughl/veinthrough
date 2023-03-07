package veinthrough.test.io;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.test.io.Serializable.User;

import java.io.*;

import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 * @see User
 *
 * constructors:
 * ObjectInputStream(InputStream in)
 * ObjectOutputStream(OutputStream out)
 *
 * APIs:
 * 1. NO ObjectInputStream.available()
 * 2. NO ObjectOutputStream.size()
 * 
 * Tests:
 * 1. handle basic types
 * 2. handle system-defined serializable types
 * 3. handle user-defined serializable types
 * 
 */
@Slf4j
@SuppressWarnings({"TryWithIdenticalCatches"})
public class ObjectStreamTest {
    private static final String FILE_NAME = "object_stream_test.txt";

    @Test
    public void testWrite() {
        // located in classpath: .../veinthrough/methodReferenceTest
        // the upper level path of src/main/java
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(FILE_NAME))) {
            oos.writeBoolean(true);
            oos.writeByte((byte) 0x61);
            oos.writeChar('b');
            oos.writeFloat(3.14F);
            oos.writeDouble(1.414D);

            // write map, a system serializable object
            ImmutableMap<Integer, String> map = ImmutableMap.<Integer, String>builder()
                    .put(1, "red")
                    .put(2, "green")
                    .put(3, "blue")
                    .build();
            oos.writeObject(map);

            // write self-defined serializable object
            User user = new User("src/main/java/veinthrough", "123456", "Beijing");
            oos.writeObject(user);
        } catch (FileNotFoundException e) {
            log.warn(exceptionLog(e));
        } catch (IOException e) {
            log.warn(exceptionLog(e));
        }
    }

    @Test
    public void testRead() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(FILE_NAME))) {
            log.info(methodLog(
                    "Read boolean", "" + ois.readBoolean(),
                    "Read byte", String.format("%#x", ois.readByte()),
                    "Read char", String.format("%c", ois.readChar()),
                    "Read float", "" + ois.readFloat(),
                    "Read double", "" + ois.readDouble(),
                    "Read Map", "" + ois.readObject(),
                    "Read User", "" + ois.readObject()));

        } catch (FileNotFoundException | ClassNotFoundException e) {
            log.warn(exceptionLog(e));
        } catch (IOException e) {
            log.warn(exceptionLog(e));
        }
    }
}

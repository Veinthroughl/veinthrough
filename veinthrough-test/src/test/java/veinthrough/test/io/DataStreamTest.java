package veinthrough.test.io;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.*;

import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 1. please refer to document --Java IO Summary--
 * 2. DataInputStream extends FilterInputStream
 * 3. DataInputStream/DataOutputStream with ObjectInputStream/ObjectOutputStream:
 *  1) both can read/write basic types;
 *  2) both have readUTF()/writeUTF()
 *  3) ObjectInputStream has readObject();
 *     ObjectOutputStream has writeObject().
 * 4. writeUTF -- readUTF, will write/read length as UTF contains length.
 * 
 * constructors:
 * DataInputStream(InputStream in)
 * DataOutputStream(OutputStream out)
 *
 * APIs:
 * 1. NO DataInputStream.available().
 * 2. DataOutputStream.size():
 *  the number of bytes written to this data output stream so far.
 *
 * Tests:
 * 1. read/write basic types
 * 2. writeUTF()/readUTF()
 */
@Slf4j
@SuppressWarnings("TryWithIdenticalCatches")
public class DataStreamTest {
    private static final String FILE_NAME = "data_stream_test.txt";
    @Test
    public void writeTest() {
        // located in classpath: .../veinthrough/methodReferenceTest
        // the upper level path of src/main/java
        try (DataOutputStream dos = new DataOutputStream(
                new FileOutputStream(FILE_NAME))) {
            dos.writeBoolean(true);
            dos.writeByte((byte) 0x61);
            dos.writeChar('b');
            dos.writeShort((short) 0x4445);
            dos.writeFloat(3.14F);
            dos.writeDouble(1.414D);
            dos.writeUTF("Veinthrough 冷");
        } catch (FileNotFoundException e) {
            log.warn(exceptionLog(e));
        } catch (IOException e) {
            log.warn(exceptionLog(e));
        }
    }

    @Test
    public void readTest() {
        try (DataInputStream dis = new DataInputStream(
                new FileInputStream(FILE_NAME))) {
            log.info(methodLog("Read boolean", ""+dis.readBoolean(),
                    "Read byte", "" + dis.readByte(),
                    "Read char", "" + dis.readChar(),
                    "Read short", String.format("%#x", dis.readShort()),
                    "Read float", "" + dis.readFloat(),
                    "Read double", "" + dis.readDouble(),
                    "Read utf", dis.readUTF()));
        } catch (FileNotFoundException e) {
            log.warn(exceptionLog(e));
        } catch (IOException e) {
            log.warn(exceptionLog(e));
        }
    }
}

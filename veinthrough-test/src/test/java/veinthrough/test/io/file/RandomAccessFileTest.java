package veinthrough.test.io.file;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 有点像FileStream+DataStream+FileReader/FileWriter(readLine()其实是在BufferedReader里面)
 *
 * constructors:
 * 1. RandomAccessFile(String name, String mode)
 * 2. RandomAccessFile(File file, String mode)
 * FileOutputStream和FileWriter才有append参数构造函数
 * @see FileStreamTest#overrideTest()
 * @see FileStreamTest#appendTest()
 * @see FileRWTest
 *
 * dataType:
 *  "r"    以只读方式打开。调用结果对象的任何 write 方法都将导致抛出 IOException。
 *  "rw"   打开以便读取和写入。
 *  "rws"  打开以便读取和写入。相对于 "rw"，"rws" 还要求对“文件的内容”或“元数据”的每个更新都同步写入到基础存储设备。
 *  "rwd"  打开以便读取和写入，相对于 "rw"，"rwd" 还要求对“文件的内容”的每个更新都同步写入到基础存储设备。
 *
 * APIs:
 * 1. getFilePointer():Returns the current offset in this file.
 * 2. skipBytes()
 * 3. seek()
 * 4. setLength()：缩小或者扩展文件
 *  缩小：将会影响getFilePointer(), 如果缩小到比file pointer指向的更小的范围；
 *  扩展：扩展部分是undefined.
 * 5. readLine()
 * 
 * Tests:
 * 1. different dataType: r/rw
 * 2. writeChars()
 * 3. writeUTF()/readUTF()
 * 4. getPointer()/length()/seek()/skipBytes()
 * 5. readLine()
 * 6. number of bytes of different basic types
 */
@Slf4j
public class RandomAccessFileTest {
    private static final String fileName = "random_access_file_test.txt";
    
    @Test
    public void createWriteTest() {
        cleanFile();

        // read-write mode
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "rw")) {
            String str = "abcdefghijklmnopqrstuvwxyz\n";
            raf.writeChars(str);
            log.info(methodLog(
                    "After writing " + str,
                    "File length", String.valueOf(raf.length())));

            str = "9876543210\n";
            raf.writeChars(str);
            log.info(methodLog(
                    "After writing " + str,
                    "File length", String.valueOf(raf.length())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void appendWriteTest() {
        // read-write mode
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "rw")) {
            long fileLen = raf.length();
            log.info(methodLog(1, "File length", String.valueOf(raf.length())));
            raf.seek(fileLen);

            raf.writeBoolean(true);
            log.info(methodLog(2,
                    "After writing boolean",
                    "File length", String.valueOf(raf.length())));
            raf.writeByte(0x41);
            log.info(methodLog(2,
                    "After writing byte",
                    "File length", String.valueOf(raf.length())));
            raf.writeChar('a');
            log.info(methodLog(2,
                    "After writing char",
                    "File length", String.valueOf(raf.length())));
            raf.writeShort(0x3c3c);
            log.info(methodLog(2,
                    "After writing short",
                    "File length", String.valueOf(raf.length())));
            raf.writeInt(0x75);
            log.info(methodLog(2,
                    "After writing int",
                    "File length", String.valueOf(raf.length())));
            raf.writeLong(0x1234567890123456L);
            log.info(methodLog(2,
                    "After writing long",
                    "File length", String.valueOf(raf.length())));
            raf.writeFloat(4.7f);
            log.info(methodLog(2,
                    "After writing float",
                    "File length", String.valueOf(raf.length())));
            raf.writeDouble(8.256);
            log.info(methodLog(2,
                    "After writing double",
                    "File length", String.valueOf(raf.length())));

            raf.writeUTF("veinthrough 冷\n");
            log.info(methodLog(3,
                    "After writing UTF",
                    "File length", String.valueOf(raf.length())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readTest() {
        // read-only mode
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "r")) {
            log.info(methodLog(1,
                    // read 'a'
                    "Read char", String.valueOf(raf.readChar()),
                    // read "bcdefghijklmnopqrstuvwxyz\n"
                    "Read line", raf.readLine(),
                    "File pointer", String.valueOf(raf.getFilePointer())));

            // read "9876543210\n"
            raf.seek(raf.getFilePointer() + 22);
            // the same effect
//            raf.skipBytes(22);
            log.info(methodLog(2,
                    "After increase 22",
                    "file pointer", String.valueOf(raf.getFilePointer())));

            // read base types: boolean, byte, char, short, int, long, float, double
            log.info(methodLog(3,
                    "Read boolean", String.valueOf(raf.readBoolean()),
                    "Read byte", String.valueOf(raf.readByte()),
                    "Read char", String.valueOf(raf.readChar()),
                    "Read short", String.valueOf(raf.readShort()),
                    "Read int", String.valueOf(raf.readInt()),
                    "Read long", String.valueOf(raf.readLong()),
                    "Read float", String.valueOf(raf.readFloat()),
                    "file double", String.valueOf(raf.readDouble()),
                    "file pointer", String.valueOf(raf.getFilePointer())));

            // read "veinthrough 冷\n"
            log.info(methodLog(4,
                    "Read UTF", raf.readUTF(),
                    "file pointer", String.valueOf(raf.getFilePointer())));
        } catch (IOException e) {
            log.warn(exceptionLog(e));
        }
    }

    private void cleanFile() {
        File file = new File(fileName);
        if (file.exists()) {
            // noinspection ResultOfMethodCallIgnored
            file.delete();
        }
    }
}

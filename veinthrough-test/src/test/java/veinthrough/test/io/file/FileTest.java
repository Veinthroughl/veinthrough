package veinthrough.test.io.file;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.util.DateFormatUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * @author veinthrough
 *
 * APIs:
 * 1. mkdir()/mkdirs():
 * {@link File#mkdir()}: parent must be existed
 * {@link File#mkdirs()} : create dirs including non-existed parent dir
 * 2. {@link File#createNewFile()}: 最好是和exists()一起用
 * return true if the named file does not exist and was successfully created;
 * return false if the named file already exists;
 * return exception if the parent directory isn't existed
 * 3. mkdir()/createNewFile(): if parent doesn't exist,
 * {@link File#mkdir()} return false
 * {@link File#createNewFile()} throw IOException().
 * 4. File(String)/File(URI),
 * {@link File#File(String)}: can handle both "\" and "/";
 * {@link File#File(URI)}: URI can't handle "\", but only "/".
 * 5. 相对路径: path --> [project_path]/path
 * test_file/dir2 --> D:\Cloud\Projects\IdeaProjects\veinthrough\veinthrough-methodReferenceTest\test_file\dir2
 *
 * Tests:
 * Print separator.
 * Create directory.
 * Create sub directory.
 * Create file.
 * List files in a directory.
 * Print file info.
 * File descriptor test: {@link FileDescriptorTest}
 */
@SuppressWarnings({"unused", "ResultOfMethodCallIgnored"})
@Slf4j
public class FileTest {
    private static final TimeZone SHANG_HAI = TimeZone.getTimeZone("Asia/Shanghai");
    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS z";

    @Test
    public void separatorTest() {
        log.info(methodLog(
                // ";"
                "File.pathSeparator", File.pathSeparator,
                "File.pathSeparatorChar", String.valueOf(File.pathSeparatorChar),
                // Windows:"\", Linux:"/"
                "File.separator", File.separator,
                "File.separatorChar", String.valueOf(File.separatorChar)));
    }

    /**
     * {@link File#mkdir()}: parent must be existed
     * {@link File#mkdirs()} : create dirs including non-existed parent dir
     */
    @Test
    public void createDirTest() {
        // mkdir, parent must be existed
        File dir1 = new File("test_file/dir1");
        dir1.mkdir();
        // mkdirs: create dirs including non-existed parent dir
        File dir2 = new File("test_file/dir2");
        dir2.mkdirs();
        // mkdir, parent must be existed
        try {
            // should be "file:/E:" instead of "file:E:"
            File dir3 = new File(new URI("file:/E:/methodReferenceTest/dir3"));
            dir3.mkdir();
        } catch (URISyntaxException e) {
            log.warn(exceptionLog(e));
        }
    }

    /**
     * {@link File#mkdir()}: parent must be existed
     * {@link File#mkdirs()} : create dirs including non-existed parent dir
     */
    @Test
    public void createSubDirTest() {
        File sub1 = new File("src/main/java/veinthrough", "dir1");
        sub1.mkdir();

        File sub2 = new File("test_file/dir2");
        sub2.mkdir();

        // mkdirs include non-existed parent dir
        File dir3 = new File("test_file/dir3");
        dir3.mkdirs();

        try {
            // should be "file:/E:" instead of "file:E:"
            File dir4 = new File(new URI("file:/E:/methodReferenceTest/dir4"));
            dir4.mkdirs();
        } catch (URISyntaxException e) {
            log.warn(exceptionLog(e));
        }
    }

    /**
     * 1. {@link File#createNewFile()}:
     * (1) true if the named file does not exist and was successfully created;
     * (2) false if the named file already exists
     * (3) exception if the parent directory isn't existed, parent directory must be existed
     * 2. {@link File#mkdir()}/{@link File#createNewFile()}:
     * (1) 最好和{@link File#exists()}一起使用
     * (2) if parent doesn't exist,
     * {@link File#mkdir()} return false
     * {@link File#createNewFile()} throw IOException().
     */

    @Test
    public void createFileTest() throws IOException, URISyntaxException {
        File file1 = new File("test_file/file1.txt");
        file1.createNewFile();

        File file2 = new File("test_file", "file2.txt");
        file2.createNewFile();

        File dir = new File("test_file");
        // filePath = ...\veinthrough\methodReferenceTest\methodReferenceTest\file3.txt
        // File(String) can handle both "\" and "/"
        String filePath = dir.getAbsolutePath() + File.separator + "file3.txt";
        File file3 = new File(filePath);
        // createNewFile()最好的方式是exists()和一起使用
        if (!file3.exists()) {
            file3.createNewFile();
        }

        // filePath = file:\...\veinthrough\methodReferenceTest\methodReferenceTest\file4.txt
        filePath = "file:\\" + dir.getAbsolutePath() + File.separator + "file4.txt";
        // URI can't handle "\", but only "/"
        filePath = filePath.replace("\\", "/");
        File file4 = new File(new URI(filePath));
        // URI uri = new URL(filePath).toURI();
        file4.createNewFile();

        // filePath = file:\...\veinthrough\methodReferenceTest\methodReferenceTest\file4.txt
        filePath = "file:\\" + dir.getAbsolutePath() + File.separator + "file5.txt";
        // the same effect
        // filePath = "file:/" + dir.getAbsolutePath() + File.separator + "file5.txt";
        // URI can't handle "\", but only "/"
        File file5 = new File(new URL(filePath).toURI());
        file5.createNewFile();
    }

    /**
     * {@link File#exists()}
     * {@link File#isDirectory()}
     * {@link File#listFiles()}
     */
    @Test
    public void listFilesTest() throws IOException {
        File dir = new File("test_file");
        if (dir.exists() && dir.isDirectory()) {
            List<File> file_list = Lists.newArrayList(Objects.requireNonNull(dir.listFiles()));
            for (int i = 0; i < file_list.size(); i++) {
                File file = file_list.get(i);
                String dirOrFile = "File";
                if (file.isDirectory()) {
                    dirOrFile = "Directory";
                    file_list.addAll(Lists.newArrayList(Objects.requireNonNull(file.listFiles())));
                }
                log.info(methodLog(
                        String.format("[%s:%s]:\n" + "%s",
                                dirOrFile, file.getName(), fileInfo(file))));
            }
        }
    }

    /**
     * File info:
     * {@link File#getParent()}
     * {@link File#getPath()}
     * {@link File#getAbsolutePath()}
     * {@link File#getCanonicalPath()}
     * {@link File#lastModified()}
     */
    private String fileInfo(File file) throws IOException {
        return String.format("    parent:%s\n"
                        + "    path:%s\n"
                        + "    absolute path:%s\n"
                        + "    canonical path:%s\n"
                        + "    lastModified:%s\n",
                file.getParent(),
                file.getPath(),
                file.getAbsolutePath(),
                file.getCanonicalPath(),
                DateFormatUtils.formatDate(
                        file.lastModified(), SHANG_HAI, DEFAULT_DATE_PATTERN));
    }
}

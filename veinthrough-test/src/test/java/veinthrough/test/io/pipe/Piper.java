package veinthrough.test.io.pipe;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import veinthrough.api._interface.Initialized;
import veinthrough.api._interface.Resettable;
import veinthrough.api.async.LoopRunnable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static veinthrough.api.util.Constants.MILLIS_PER_SECOND;
import static veinthrough.api.util.MethodLog.*;

/**
 * @author veinthrough
 *
 * 实现了一个Piper可以根据以下进行读写:
 * 职责(Duty): READ/WRITE/READ_WRITE
 * 模式(Mode): continued/once
 * 数据类型(data type): BYTE/CHAR
 *
 * constructors: PipedReader same as PipedInputStream, PipedWriter same as PipedOutputStream
 * PipedInputStream(): default pipe buffer size is 1024
 * PipedInputStream(int pipeSize)
 * PipedInputStream(PipedOutputStream src): default pipe buffer size is 1024
 * PipedInputStream(PipedOutputStream src, int pipeSize)
 * 
 * PipedOutputStream()
 * PipedOutputStream(PipedInputStream snk): PipedOutputStream没有buf
 *
 * APIs:
 * 1. read()/receive():receive()为非public, read()为public; 并且从构造函数看PipedOutputStream没有buf，
 * 实际上基本所有的任务都是PipedInputStream来完成的, PipedOutputStream.write()实际上调用了连接的PipedInputStream.receive().
 * 2. PipedInputStream.available()
 * 3. NO PipedOutputStream.size()
 */
@Slf4j
@SuppressWarnings("unused")
@NoArgsConstructor
public class Piper
        implements Initialized, Resettable, Closeable {
    // the default max pipe buffer size is 1024
    private static final int DEFAULT_PIPE_BUFFER_SIZE = 1024;
    private static final int MAX_PIPE_BUFFER_SIZE = 1024;
    @Getter
    private PipedInputStream inStream;
    @Getter
    private PipedOutputStream outStream;
    @Getter
    private PipedReader reader;
    @Getter
    private PipedWriter writer;
    @Getter
    @Setter
    private DATA_TYPE dataType;
    @Getter
    @Setter
    private DUTY duty;
    @Getter
    private boolean initialized;

    // tasks
    private static final int DEFAULT_INTERVAL = MILLIS_PER_SECOND;
    private static final int DEFAULT_DURATION = 5 * DEFAULT_INTERVAL;
    private final List<Callable<Object>> tasks = new ArrayList<>();
    private final ExecutorService pool = Executors.newCachedThreadPool();

    Piper(DUTY duty, DATA_TYPE dataType) {
        this.duty = duty;
        this.dataType = dataType;
        initialize();
    }

    public void initialize() {
        Preconditions.checkNotNull(duty, "Mode should not be null");
        Preconditions.checkNotNull(dataType, "Data type should not be null");

        switch (dataType) {
            case BYTE:
                if (asWriter()) outStream = new PipedOutputStream();
                if (asReader()) inStream = new PipedInputStream();
                break;
            case CHAR:
                if (asWriter()) writer = new PipedWriter();
                if (asReader()) reader = new PipedReader();
                break;
            default:
                break;
        }
        this.initialized = true;
    }

    /**
     * Connect to the peer Piper.
     *
     * @param peer              the peer Piper
     * @param effectivePeerDUTY the effective Mode of peer to connect,
     *                          it should be READ/WRITE, but can't be READ_WRITE
     */
    public void connect(Piper peer, DUTY effectivePeerDUTY) throws IOException {
        checkInitialization();
        Preconditions.checkNotNull(peer, "Null peer Piper");
        peer.checkInitialization();
        Preconditions.checkNotNull(effectivePeerDUTY, "Null effectivePeerDUTY");
        Preconditions.checkArgument(Objects.equals(dataType, peer.getDataType()),
                "Different data types");
        Preconditions.checkArgument(checkEffective(peer, effectivePeerDUTY),
                "Check peer DUTY failed");
        _connect(peer, effectivePeerDUTY);
    }

    /**
     * Connect to the peer Piper, automatically detect the effective peer duty.
     *
     * @param peer the peer Piper
     */
    void connect(Piper peer) throws IOException {
        checkInitialization();
        Preconditions.checkNotNull(peer, "Null peer Piper");
        peer.checkInitialization();
        Preconditions.checkArgument(Objects.equals(dataType, peer.getDataType()),
                "Different data types");
        Preconditions.checkArgument(checkEffective(peer),
                "Check peer DUTY failed");
        _connect(peer, effectivePeerMode(peer));
    }

    private void _connect(Piper peer, DUTY effectivePeerDUTY) throws IOException {
        switch (dataType) {
            case BYTE:
                if (effectivePeerDUTY.equals(DUTY.READ))
                    peer.getInStream().connect(this.getOutStream());
                else this.getInStream().connect(peer.getOutStream());
                break;
            case CHAR:
                if (effectivePeerDUTY.equals(DUTY.READ))
                    peer.getReader().connect(this.getWriter());
                else this.getReader().connect(peer.getWriter());
                break;
            default:
                break;
        }
    }

    public void start(boolean continued) {
        checkInitialization();

        switch (dataType) {
            case BYTE:
                if (asWriter()) addWriteTask(this::writeByteMessage, continued);
                if (asReader()) addReadTask(this::readByteMessage, continued);
                break;
            case CHAR:
                if (asWriter()) addWriteTask(this::writeCharMessage, continued);
                if (asReader()) addWriteTask(this::readCharMessage, continued);
                break;
            default:
                break;
        }
        log.debug(methodLog("Invoke all tasks",
                "tasks", tasks.toString()));
        try {
            pool.invokeAll(tasks);
        } catch (InterruptedException e) {
            log.error(exceptionLog(e));
        } finally {
            pool.shutdown();
        }
    }

    boolean finished() {
        return pool.isTerminated();
    }

    private boolean checkEffective(Piper peer, DUTY duty) {
        if (!duty.effective()) {
            log.error(methodLog("Ineffective peer DUTY"));
            return false;
        } else return peer.getDuty().effectiveOf(duty) &&
                getDuty().effectiveOf(duty.effectivePeerMode());
    }

    private boolean checkEffective(Piper peer) {
        if (peer.getDuty() == null) {
            log.error(methodLog("Null peer DUTY"));
            return false;
        } else if (!peer.getDuty().effective() && !duty.effective()) {
            log.error(methodLog("Both ineffective DUTY"));
            return false;
        } else {
            return duty.effective() ? peer.getDuty().effectiveOf(duty.effectivePeerMode()) :
                    duty.effectiveOf(peer.getDuty().effectivePeerMode());
        }
    }

    private DUTY effectivePeerMode(Piper peer) {
        return duty.effective() ? peer.getDuty() : duty.effectivePeerMode();
    }


    // write每隔一段时间写入一段数据, 需要sleepy, 所以是sleepy;
    // read本身就是block, 而这个block依赖于write, 所以是hyper
    private void addWriteTask(Runnable task, boolean continued) {
        if (!continued) tasks.add(Executors.callable(task));
        else tasks.add(Executors.callable(
                LoopRunnable.sleepyAtInterval(
                        task, DEFAULT_DURATION, DEFAULT_INTERVAL)));
    }

    private void addReadTask(Runnable task, boolean continued) {

        if (!continued) tasks.add(Executors.callable(task));
        else tasks.add(Executors.callable(
//                LoopRunnable.sleepyAtInterval(task, DEFAULT_DURATION, DEFAULT_INTERVAL)
                LoopRunnable.hyper(task, DEFAULT_DURATION)));  // hyper, no interval
    }

    /**
     * 每次读取1024个bytes:
     * writer退出, reader会尽量读取pipe里面的数据,
     * IOException: Read end dead/Pipe broken
     */
    private void readByteMessage() {
        byte[] buf = new byte[DEFAULT_PIPE_BUFFER_SIZE];
        try {
//            int len = inStream.read(buf);
            int len = inStream.read(buf, 0, DEFAULT_PIPE_BUFFER_SIZE);
            log.info(methodLog(
                    String.format("Read %4d bytes:%s", len, new String(buf, 0, len))));
        } catch (IOException e) {
            log.warn(exceptionLog(e));
        }
    }

    /**
     * 每次读取1024个chars
     * writer退出, reader会尽量读取pipe里面的数据,
     * IOException: Read end dead/Pipe broken
     */
    private void readCharMessage() {
        char[] buf = new char[DEFAULT_PIPE_BUFFER_SIZE];
        try {
//            int len = reader.read(buf);
            int len = reader.read(buf, 0, DEFAULT_PIPE_BUFFER_SIZE);
            log.info(methodLog(
                    String.format("Read %4d chars:%s", len, new String(buf, 0, len))));
        } catch (IOException e) {
            log.warn(exceptionLog(e));
        }
    }

    /**
     * 写入1026个bytes
     */
    private void writeByteMessage() {
        String str = constructLongMessage(MAX_PIPE_BUFFER_SIZE + 2);
        try {
            log.info(methodLog(
                    String.format("Writing %4d bytes:%s", str.length(), str)));
//            outStream.write(str.getBytes());
            outStream.write(str.getBytes(), 0, str.length());
        } catch (IOException e) {
            log.warn(exceptionLog(e));
        }

    }

    /**
     * 写入1026个chars
     */
    private void writeCharMessage() {
        String str = constructLongMessage(MAX_PIPE_BUFFER_SIZE + 2);
        try {
            log.info(methodLog(
                    String.format("Writing %4d chars:%s", str.length(), str)));
//            writer.write(str);
            writer.write(str.toCharArray(), 0, str.length());
        } catch (IOException e) {
            log.warn(exceptionLog(e));
        }
    }

    @SuppressWarnings("SameParameterValue")
    private String constructLongMessage(int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size / 10; i++) {
            sb.append("0123456789");
        }
        for (int i = 0; i < size % 10; i++) {
            sb.append(i);
        }
        return sb.toString();
    }

    @Override
    public boolean initialized() {
        return this.isInitialized();
    }

    @Override
    public void close() {
        try {
            tasks.clear();
            pool.shutdownNow();
            if (null != inStream) inStream.close();
            if (null != outStream) outStream.close();
            if (null != reader) reader.close();
            if (null != writer) writer.close();
        } catch (IOException e) {
            log.warn(exceptionLog(e));
        }
    }

    @Override
    public void reset() {
        close();
        inStream = null;
        outStream = null;
        reader = null;
        writer = null;
        duty = null;
        dataType = null;
        initialized = false;
    }

    private boolean asReader() {
        return duty == DUTY.READ || duty == DUTY.READ_WRITE;
    }

    private boolean asWriter() {
        return duty == DUTY.WRITE || duty == DUTY.READ_WRITE;
    }

    public enum DATA_TYPE {
        BYTE, CHAR
    }

    public enum DUTY {
        READ,
        WRITE,
        READ_WRITE;

        public Boolean effective() {
            return !equals(READ_WRITE);
        }

        public Boolean effectiveOf(DUTY duty) {
            Preconditions.checkArgument(duty.effective(), "Ineffective DUTY");
            return !equals(duty.effectivePeerMode());
        }

        public DUTY effectivePeerMode() {
            Preconditions.checkArgument(effective(), "Ineffective DUTY");
            return equals(READ) ? WRITE : READ;
        }
    }
}

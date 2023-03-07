package veinthrough.test.async;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.async.LoopRunnable;
import veinthrough.api.generic.Either;
import veinthrough.api.generic.Tuple;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static veinthrough.api._interface.CheckedFunction.liftWithInput;
import static veinthrough.api._interface.CheckedFunction.wrapInt;
import static veinthrough.api.util.Constants.MILLIS_PER_SECOND;
import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * Find all keyword matches in files of a directory:
 * (1) by recursively invoke FutureTasks, {@link #byFutureTask()}
 * 提交任务: [递归]每个子目录重新通过new Thread提交任务,
 * 收集数据: [递归]累加Future.get()获取结果
 * 线程数量: 和directory数量相关
 * (2) by recursively submitting tasks, {@link #byThreadPool()}
 * 提交任务: [递归]每个子目录重新通过thread pool提交任务,
 * 收集数据: [递归]累加Future.get()获取结果
 * 线程数量: 和directory数量相关
 * (3) by fork-join framework, {@link #byForkJoin()}
 * 提交任务: [递归]每个子目录重新通过ForkJoinTask.invokeAll提交任务,
 * 收集数据: [递归]累加task.join获取结果
 * 线程数量: 和directory数量相关
 * (4) [非递归] by Multi-threads co-work through BlockingQueue, {@link #byBlockingQueue()}
 * 提交任务: [非递归]1个file enumeration task, 固定数量的search task
 * 收集数据: [非递归]累加search task's Future.get()获取结果
 * 线程数量: 固定数量, 更好的控制
 */
@Slf4j
@SuppressWarnings({"unchecked", "OptionalGetWithoutIsPresent"})
public class WordCounter {
    private static final String directory =
            "D:\\Cloud\\Projects\\IdeaProjects\\veinthrough\\veinthrough-methodReferenceTest\\src\\methodReferenceTest\\java\\veinthrough\\methodReferenceTest";
    private static final String keyword = "AbstractUnitTester";
    // thread pool
    private static ExecutorService pool;
    // blocking queue
    private static File DUMMY = new File("");
    private static final int FILE_QUEUE_SIZE = 10;
    private static final int SEARCH_THREADS = 10;
    private static final BlockingQueue<File> queue = new ArrayBlockingQueue<>(FILE_QUEUE_SIZE);

    @Test
    public void byFutureTask() throws ExecutionException, InterruptedException {
        // FutureTask是Future/Runnable的中转站, 使用FutureTask来包装Callable
        FutureTask<Integer> task = new FutureTask<>(
                new FutureTaskCounter(0, new File(directory)));
        new Thread(task).start();
        log.info(methodLog(
                // task.get() may block
                "Matches", String.valueOf(task.get())));
    }

    @Test
    public void byThreadPool() throws ExecutionException, InterruptedException {
        pool = Executors.newCachedThreadPool();
        Future<Integer> task = pool.submit(new ThreadPoolCounter(0, new File(directory)));
        log.info(methodLog("Matches", "" + task.get()));
        log.info(methodLog("Largest pool size",
                "" + ((ThreadPoolExecutor) pool).getLargestPoolSize()));
        // 因为后面还要递归将任务放入pool, 所以只能在最后shutdown
        pool.shutdown();
        pool = null;
    }

    @Test
    public void byForkJoin() {
        ForkJoinCounter counter = new ForkJoinCounter(0, new File(directory));
        new ForkJoinPool().invoke(counter);
        log.info(methodLog("Matches", "" + counter.join()));
    }

    @Test
    public void byBlockingQueue() throws InterruptedException {
        pool = Executors.newFixedThreadPool(SEARCH_THREADS);

        // 1. invoke all tasks: file enumeration task and search tasks.
        List<Callable<Integer>> searchTasks = IntStream.range(0, SEARCH_THREADS)
                .mapToObj(SearchTask::new)
                .collect(Collectors.toList());
        pool.invokeAll(
                ImmutableList.<Callable<Integer>>builder()
                        // file enumeration task: Runnable --> Callable<Integer>
                        .add(Executors.callable(
                                new FileEnumerationTask(queue, new File(directory)), 0))
                        // search tasks: Callable<Integer>
                        .addAll(searchTasks)
                        .build());

        // 这里pool可以提前关闭, 因为不会再有新任务
        pool.shutdown();

        // 2. waiting for finish
        Thread waiting = new Thread(LoopRunnable.sleepyAtInterval(
                () -> {
                },
                pool::isTerminated,
                MILLIS_PER_SECOND,
                () -> {
                    // 收集数据
                    // Exception方式1. CheckedException -> RuntimeException, 将终止程序
                    int counter = searchTasks.stream()
                            // CheckedException -> RuntimeException
                            .mapToInt(wrapInt(Callable::call))
                            .sum();
                    log.info(methodLog(
                            "Count", "" + counter,
                            "Largest pool size", "" + ((ThreadPoolExecutor) pool).getLargestPoolSize()));
                }
        ));
        waiting.start();

        // 3. join waiting task
        waiting.join();
        pool = null;
    }

    @AllArgsConstructor
    public static class FutureTaskCounter implements Callable<Integer> {
        private static final boolean ALL_FILES = true;
        private static final AtomicLong threadNum = new AtomicLong(1);
        @Getter
        private long id;
        private List<File> files;
        private boolean allFiles;

        static long generateId() {
            return threadNum.getAndIncrement();
        }

        FutureTaskCounter(long id, List<File> files) {
            this(id, files, false);
        }

        FutureTaskCounter(long id, File directory) {
            // noinspection ConstantConditions
            this(id, ImmutableList.copyOf(directory.listFiles()));
        }

        @Override
        public Integer call() {
            int counter = 0;
            if (files != null && files.size() != 0) {
                // 1. files里面没有目录文件
                if (allFiles) {
                    counter = files.stream()
                            .mapToInt(WordCounter::searchFile)
                            .sum();
                }
                // 2. files里面有目录文件
                else {
                    // 这里使用FutureTask来包装Callable
                    List<FutureTask<Integer>> tasks = new ArrayList<>();
                    List<File> filesOfDir = new ArrayList<>();
                    for (File file : files) {
                        if (!file.isDirectory()) {
                            filesOfDir.add(file);
                        } else {
                            // 2.(1) 添加任务, 每个子目录作为一个任务
                            tasks.add(new FutureTask<>(
                                    new FutureTaskCounter(generateId(), file)));
                        }
                    }
                    // 2.(2) 添加任务, 所有子文件作为一个任务
                    tasks.add(new FutureTask<>(
                            new FutureTaskCounter(generateId(), filesOfDir, ALL_FILES)));

                    // 2.(3) 处理任务
                    // Exception方式1. CheckedException -> RuntimeException, 将终止程序
//                    counter = tasks.stream()
//                            .peek(task -> new Thread(task).start())
//                            // CheckedException -> RuntimeException
//                            .mapToInt(wrapInt(FutureTask::get))
//                            .sum();

                    // 2.(3) 处理任务
                    // Exception方式2. CheckedException -> Either.left, 将忽略Exception并计算最终结果
//                    counter = tasks.stream()
//                            .peek(task -> new Thread(task).start())
//                            // CheckedException -> Either.left
//                            .map(liftWithInput(FutureTask::get))
//                            .filter(Either::isRight)
//                            .mapToInt(either -> (int) either.getRight().get())
//                            .sum();

                    // 2.(3) 处理任务
                    // Exception方式3. CheckedException/task -> Either.left, 将打印Exception并计算最终结果
                    Map<Boolean, List<Either>> results = tasks.stream()
                            // 每个任务作为一个thread运行
                            .peek(task -> new Thread(task).start())
                            // 忽略exception
                            // lambda:
                            // (1) liftWithInput(CheckedFunction), 这里函数名不是限制, T -> R即可
                            // (2) 这里[类上]的[实例方法]引用, 把this当参数(当T), futureTask.get()返回的值当R
                            .map(liftWithInput(FutureTask::get))
                            .collect(Collectors.partitioningBy(Either::isLeft)); // 按照结果分类
                    // 2.(4) 打印出错的任务/exception
                    results.get(true)
                            .forEach(either -> {
                                Tuple<Throwable, FutureTask<Integer>> taskAndException =
                                        (Tuple<Throwable, FutureTask<Integer>>) either.getLeft().get();
                                log.warn(exceptionLog(
                                        taskAndException.getFirst(),
                                        "task", taskAndException.getSecond().toString()));
                            });
                    // 2.(5) 收集数据
                    counter = results.get(false).stream()
                            .mapToInt(either -> (int) either.getRight().get())
                            .sum();
                }
            }
            log.info(methodLog(
                    String.format("Thread %d: %d", getId(), counter)));
            return counter;
        }
    }

    @AllArgsConstructor
    public static class ThreadPoolCounter implements Callable<Integer> {
        private static final boolean ALL_FILES = true;
        private static final AtomicLong threadNum = new AtomicLong(1);
        @Getter
        private long id;
        private List<File> files;
        private boolean allFiles;

        static long generateId() {
            return threadNum.getAndIncrement();
        }

        ThreadPoolCounter(long id, List<File> files) {
            this(id, files, false);
        }

        ThreadPoolCounter(long id, File directory) {
            // noinspection ConstantConditions
            this(id, ImmutableList.copyOf(directory.listFiles()));
        }

        @Override
        public Integer call() throws InterruptedException {
            int counter = 0;
            if (files != null && files.size() != 0) {
                // 1. files里面没有目录文件
                if (allFiles) {
                    counter = files.stream()
                            .mapToInt(WordCounter::searchFile)
                            .sum();
                }
                // 2. files里面有目录文件
                else {
                    List<Callable<Integer>> tasks = new ArrayList<>();
                    List<File> filesOfDir = new ArrayList<>();
                    for (File file : files) {
                        if (!file.isDirectory()) {
                            filesOfDir.add(file);
                        } else {
                            // 2.(1) 添加任务, 每个子目录作为一个任务
                            tasks.add(new ThreadPoolCounter(generateId(), file));
                        }
                    }
                    // 2.(2) 添加任务, 所有子文件作为一个任务
                    tasks.add(new ThreadPoolCounter(
                            generateId(), filesOfDir, ALL_FILES));

                    // 2.(3) 处理任务和收集数据
                    // Exception方式2. CheckedException -> Either.left, 将忽略Exception并计算最终结果
                    counter = pool.invokeAll(tasks).stream()
                            // CheckedException -> Either.left
                            // lambda:
                            // (1) liftWithInput(CheckedFunction), 这里函数名不是限制, T -> R即可
                            // (2) 这里[类上]的[实例方法]引用, 把this当参数(当T), future.get()返回的值当R
                            .map(liftWithInput(Future::get))
                            .filter(Either::isRight)
                            .mapToInt(either -> (int) either.getRight().get())
                            .sum();
                }
            }
            log.info(methodLog(
                    String.format("Thread %d: %d", getId(), counter)));
            return counter;
        }
    }

    @AllArgsConstructor
    public static class ForkJoinCounter extends RecursiveTask<Integer> {
        private static final boolean ALL_FILES = true;
        private static final AtomicLong threadNum = new AtomicLong(1);
        @Getter
        private long id;
        private List<File> files;
        private boolean allFiles;

        static long generateId() {
            return threadNum.getAndIncrement();
        }

        ForkJoinCounter(long id, List<File> files) {
            this(id, files, false);
        }

        ForkJoinCounter(long id, File directory) {
            // noinspection ConstantConditions
            this(id, ImmutableList.copyOf(directory.listFiles()));
        }

        @Override
        public Integer compute() {
            int counter = 0;
            if (files != null && files.size() != 0) {
                // 1. files里面没有目录文件
                if (allFiles) {
                    counter = files.stream()
                            .mapToInt(WordCounter::searchFile)
                            .sum();
                }
                // 2. files里面有目录文件
                else {
                    List<RecursiveTask<Integer>> tasks = new ArrayList<>();
                    List<File> filesOfDir = new ArrayList<>();
                    for (File file : files) {
                        if (!file.isDirectory()) {
                            filesOfDir.add(file);
                        } else {
                            // 2.(1) 添加任务, 每个子目录作为一个任务
                            tasks.add(new ForkJoinCounter(generateId(), file));
                        }
                    }
                    // 2.(2) 添加任务, 所有子文件作为一个任务
                    tasks.add(new ForkJoinCounter(
                            generateId(), filesOfDir, ALL_FILES));

                    // 2.(3) 处理任务
                    invokeAll(tasks);

                    // 2.(4) 收集数据
                    counter = tasks.stream()
                            .mapToInt(ForkJoinTask::join)
                            .sum();
                }
            }
            log.info(methodLog(
                    String.format("Thread %d: %d", getId(), counter)));
            return counter;
        }
    }

    @AllArgsConstructor
    static class FileEnumerationTask implements Runnable {
        // The field DUMMY cannot be declared static in a non-static inner type,
        // unless initialized with a constant expression
        private BlockingQueue<File> queue;
        private File directory;

        void enumerate(File directory) throws InterruptedException {
            File[] files = directory.listFiles();
            if (files != null && files.length != 0) {
                for (File file : files) {
                    if (file.isDirectory()) enumerate(file);
                        // may throw InterruptedException
                    else queue.put(file);
                }
            }
        }

        @Override
        public void run() {
            try {
                if (directory.isDirectory()) enumerate(directory);
                else queue.put(directory);
                queue.put(DUMMY);
            } catch (InterruptedException e) {
                log.error(exceptionLog(e));
            }
        }
    }

    @AllArgsConstructor
    class SearchTask implements Callable<Integer> {
        @Getter
        private long id;

        @Override
        public Integer call() {
            int counter = 0;
            try {
                File file = queue.take();       // take task from BlockingQueue
                while (file != DUMMY) {
                    counter += searchFile(file);
                    file = queue.take();
                }
                queue.put(DUMMY);
            } catch (InterruptedException e) {
                log.error(exceptionLog(e));
            }
            log.info(methodLog(
                    String.format("Thread %d: %d", getId(), counter)));
            return counter;
        }
    }

    private static int searchFile(File file) {
        int counter = 0;
        try (Scanner in = new Scanner(file)) {
            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.contains(keyword)) counter++;
            }
        } catch (FileNotFoundException e) {
            log.error(exceptionLog(e));
        }
        log.debug(methodLog(
                String.format("File %s: %d", file.getPath(), counter)));
        return counter;
    }
}

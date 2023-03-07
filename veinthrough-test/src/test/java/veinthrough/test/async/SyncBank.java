package veinthrough.test.async;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.async.LoopRunnable;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import static veinthrough.api.util.Constants.MILLIS_PER_SECOND;
import static veinthrough.api.util.MethodLog.exceptionLog;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * Simulate a bank: randomly transfer from one account to another.
 *
 * {@link ReentrantLock}/{@link synchronized}:
 * {@link Condition#wait()}/{@link Object#wait()},
 * {@link Condition#signalAll()}/{@link Object#notifyAll()}
 * 1. 注意lock是在synchronized之后出现的。
 * 2.使用
 * > Synchronized
 * (1) 在需要同步的对象中加入此控制，synchronized可以加在方法上，也可以加在特定代码块中，括号中表示需要锁的对象。
 * (2) synchronized同步块执行完成或者遇到异常时锁会自动释放，而lock必须调用unlock()方法释放锁，因此在finally块中释放锁
 * (3) 多个方法(实例方法之间/静态方法之间)是联动的
 * > Lock
 * (1) 需要显示指定起始位置和终止位置。一般使用ReentrantLock类做为锁，多个线程中必须要使用一个ReentrantLock类做为对象才能保证锁的生效。
 * 且在加锁和解锁处需要通过lock()和unlock()显示指出。
 * (2) 所以一般会在finally块中写unlock()以防死锁。
 * (3) 随着JVM的升级，几乎不需要修改代码
 * 3. 性能
 * > Synchronized
 * (1) 是托管给JVM执行的，而lock是java写的控制锁的代码。
 * (2) 在Java1.5中，synchronized是性能低效的。因为这是一个重量级操作，需要调用操作接口，导致有可能加锁消耗的系统时间比加锁以外的操作还多;
 * 采用的是CPU悲观锁机制，即线程获得的是独占锁。独占锁意味着其他线程只能依靠阻塞来等待线程释放锁。
 * 而在CPU转换线程阻塞时会引起线程上下文切换(mutex系统调用)，当有很多线程竞争锁的时候，会引起CPU频繁的上下文切换导致效率很低。
 * (3) 但是到了Java1.6，发生了变化。synchronized在语义上很清晰，可以进行很多优化，有适应自旋，锁消除，锁粗化，轻量级锁，偏向锁等等。
 * 导致在Java1.6上synchronized的性能并不比Lock差。
 * (4) 官方也表示，他们也更支持synchronized，在未来的版本中还有优化余地。
 * > Lock(参考锁的实现)
 * Lock用的是乐观锁方式。所谓乐观锁就是，每次不加锁而是假设没有冲突而去完成某项操作，如果因为冲突失败就重试，直到成功为止。
 * 乐观锁实现的机制就是CAS操作（Compare and Swap）。我们可以进一步研究ReentrantLock的源代码，
 * 会发现其中比较重要的获得锁的一个方法是compareAndSetState。这里其实就是调用的CPU提供的特殊指令。
 * 现代的CPU提供了指令，可以自动更新共享数据，而且能够检测到其他线程的干扰，而 compareAndSet() 就用这些代替了锁定。
 * 4. 用途
 * synchronized原语和ReentrantLock在一般情况下没有什么区别，但是在非常复杂的同步应用中，请考虑使用ReentrantLock，特别是遇到下面这些需求的时候:
 * (1) 某个线程在等待一个锁的控制权的这段时间需要中断, synchronized不能响应中断
 * (2) 需要分开处理一些wait-notify，ReentrantLock可以有多个Condition，能够分开控制/notify
 * (3) 具有公平锁功能，每个到来的线程都将排队等候
 * (4) 超时获取锁
 * (5) lock可以使用tryLock来自旋, 然后再自旋中还可以提前做一些操作(比如ConcurrentHashMap(JDK1.7)的scanAndLockForPut())
 */
@Slf4j
public class SyncBank {
    private static final int NACCOUNTS = 3;
    private static final double INITIAL_BALANCE = 1000;
//    private static final long TIME_OUT = 10 * MILLIS_PER_SECOND;

    @Test
    public void lockedBankTest() throws InterruptedException {
        _syncBankTest(new LockedBank(NACCOUNTS, INITIAL_BALANCE));
    }

    @Test
    public void synchronizedBankTest() throws InterruptedException {
        _syncBankTest(new SynchronizedBank(NACCOUNTS, INITIAL_BALANCE));
    }

    /**
     * timeout:
     * 1. 如果没有timeout, 不会出现死锁;
     * 2. 有timeout, 当账户很少时, 容易出现死锁, 比如1/2都余额不足, 3余额足但是已经退出
     */
    private void _syncBankTest(Bank bank) throws InterruptedException {
        ExecutorService pool = Executors.newCachedThreadPool();
        // TODO [Problem] 在IntStream中使用pool.submit()线程不明原因的停止
        // 而使用pool.invokeAll()就不会, IntStream中submit太快?
        pool.invokeAll(IntStream.range(0, NACCOUNTS)
                .mapToObj(from -> Executors.callable(LoopRunnable.sleepyAtInterval(
                        () -> {
                            try {
                                bank.transfer(from,
                                        (int) (bank.getSize() * Math.random()), // random to
                                        INITIAL_BALANCE * Math.random()); // random amount
                            } catch (InterruptedException e) {
                                log.error(exceptionLog(e));
                            }
                        },
//                        TIME_OUT, // 加上timeout容易出现死锁(如果账户比较少), 比如1/2都余额不足, 3余额足但是已经退出
                        (long) (MILLIS_PER_SECOND * Math.random()))))  // random interval
                .collect(Collectors.toList()));
        pool.shutdown();
    }

    class LockedBank extends AbstractBank {
        private Lock bankLock;
        private Condition sufficientFunds;

        LockedBank(int n, double initialBalance) {
            super(n, initialBalance);

            // reentrant lock
            bankLock = new ReentrantLock();
            // one condition of lock
            sufficientFunds = bankLock.newCondition();
        }

        @Override
        public void transfer(int from, int to, double amount) throws InterruptedException {
            if (from == to) return;

            int step = 0;
            log.debug(methodLog(++step,
                    "from", "" + from,
                    "to", "" + to,
                    "amount", "" + amount));

            // 1. get lock and lock
            log.debug(methodLog(step++, "Manually lock"));
            bankLock.lock();

            // 虽然没有exception, 使用try ... finally来保证unlock
            try {
                // 2. insufficient funds
                // await may throw InterruptedException
                while (accounts[from] < amount) {
                    log.debug(methodLog(++step,
                            "Inefficient funds",
                            "accounts[from]", "" + accounts[from],
                            "amount", "" + amount));
                    sufficientFunds.await();
                }

                // 3. sufficient funds
                log.debug(methodLog(++step,
                        "Efficient funds",
                        "accounts[from]", "" + accounts[from],
                        "amount", "" + amount));

                // 4. transfer
                log.info(methodLog(++step,
                        Thread.currentThread().getName(),
                        String.format("Transfer %10.2f from %d to %d", amount, from, to)));
                accounts[from] -= amount;
                accounts[to] += amount;

                // 5. signal all as it can make others sufficient.
                log.info(methodLog(++step,
                        "Accounts", Arrays.toString(accounts),
                        "Total Balance", String.format("%10.2f%n", getTotalBalance())));
                sufficientFunds.signalAll();
            } finally {
                // 6. unlock
                log.debug(methodLog(++step,
                        "Manually unlock"));
                bankLock.unlock();
            }
        }
    }

    class SynchronizedBank extends AbstractBank {
        SynchronizedBank(int n, double initialBalance) {
            super(n, initialBalance);
        }

        @Override
        public synchronized void transfer(int from, int to, double amount) throws InterruptedException {
            if (from == to) return;

            int step = 0;
            // 1. get lock
            log.debug(methodLog(++step,
                    "Automatically lock",
                    "from", "" + from,
                    "to", "" + to,
                    "amount", "" + amount));

            // 2. insufficient funds
            // await may throw InterruptedException
            if (accounts[from] < amount) {
                log.debug(methodLog(++step,
                        "Inefficient funds",
                        "accounts[from]", "" + accounts[from],
                        "amount", "" + amount));
                wait();
            }

            // 3. sufficient funds
            log.debug(methodLog(++step,
                    "Efficient funds",
                    "accounts[from]", "" + accounts[from],
                    "amount", "" + amount));

            // 4. transfer
            log.info(methodLog(++step,
                    Thread.currentThread().getName(),
                    String.format("Transfer %10.2f from %d to %d", amount, from, to)));
            accounts[from] -= amount;
            accounts[to] += amount;

            // 5. signal all as it can make others to be with sufficient funds
            log.info(methodLog(++step,
                    "Accounts", Arrays.toString(accounts),
                    "Total Balance", String.format("%10.2f%n", getTotalBalance())));
            notifyAll();

            // 6. unlock
            log.debug(methodLog(++step, "Automatically unlock"));
        }
    }

    interface Bank {
        /**
         * Number of accounts.
         */
        int getSize();

        /**
         * Transfers money from one account to another.
         */
        void transfer(int from, int to, double amount) throws InterruptedException;
    }


    abstract class AbstractBank implements Bank {
        final double[] accounts;
        @Getter
        protected int size;

        /**
         * Constructs the bank.
         *
         * @param n              the number of accounts
         * @param initialBalance the initial balance for each account
         */
        AbstractBank(int n, double initialBalance) {
            this.size = n;
            accounts = new double[n];
            Arrays.fill(accounts, initialBalance);
        }

        /**
         * Gets the sum of all account balances.
         */
        synchronized double getTotalBalance() {
            return DoubleStream.of(accounts).sum();
        }
    }
}

package veinthrough.leetcode.math;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.Math.sqrt;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 求出N以内的质数
 */
@SuppressWarnings("Duplicates")
@Slf4j
public class Prime {
    @Test
    public void test() {
        Stream.of(100, 200, 500)
                .forEach(N ->
                        Stream.<Function<Integer, int[]>>of(this::prime, this::prime2, this::prime3)
                                .forEach(function ->
                                        log.info(methodLog(
                                                function.toString() + "(" + N + ")", Arrays.toString(function.apply(N))))));
    }

    /**
     * 如果一个数是[N]的因数，那么[N]除以这个数得到的自然数必然也是[N]的因数， [N]写成两个数相乘，
     * 这两个乘数要不相等，要不一个大一个小，可以肯定的是小的一定小于其平方主根，大的一定大于其平方主根。
     * 所以如果[1,√N] 有[N]的因数那么[√N,N] 里必然也有[N]的因数，
     * 如果如果[1,√N] 没有[N]的因数那么[√N, N]里必然也没有[N]的因数，所以判定到[√N]就够了。
     */
    private int[] prime(int N) {
        List<Integer> primes = new ArrayList<>(N / 2);
        primes.add(1);
        for (int i = 2, j; i < N; i++) {
            boolean flag = true;
            for (j = 2; j <= sqrt(i); j++) {
                if (i % j == 0) {
                    flag = false;
                    break;
                }
            }
            if (flag) primes.add(i);
        }
        return primes.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * 埃氏（Eratosthenes）筛法
     * 埃拉托斯特尼筛法，简称埃氏筛或爱氏筛，是一种由希腊数学家埃拉托斯特尼所提出的一种简单检定素数的算法。
     * 要得到自然数n以内的全部素数，必须把不大于根号n的所有素数的倍数剔除，剩下的就是素数。
     * 这种算法的时间复杂度是O(nloglogn)。
     */
    private int[] prime2(int N) {
        List<Integer> primes = new ArrayList<>(N / 2);
        boolean[] isPrime = new boolean[N + 1];
        Arrays.fill(isPrime, true);
        primes.add(1);
        for (int i = 2, j; i <= N; i++) {
            if (isPrime[i]) {
                primes.add(i);
                // 以(当前的质数i)*(...)来筛选
                for (j = i + i; j <= N; j += i)
                    isPrime[j] = false;
            }
        }
        return primes.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * 欧拉（Euler）筛选法
     * 欧拉筛法就是所谓中的高级筛法，时间复杂度削减到了O(N)。
     * (1) 埃氏（Eratosthenes）筛法: 质数才筛选
     * (2) 它的思想是在埃氏筛法的基础上，让每个合数只被它的最小质因子筛选一次，以达到不重复的目的。
     */
    private int[] prime3(int N) {
        List<Integer> primes = new ArrayList<>(N / 2);
        boolean[] isPrime = new boolean[N + 1];
        Arrays.fill(isPrime, true);
        primes.add(1);
        // count为质数个数
        for (int i = 2, j, count = 1, prime; i <= N; i++) {
            if (isPrime[i]) {
                primes.add(i);
                count++;
            }
            // 不是只有质数才筛
            // 以(当前所有的质数(除去1，从2开始))*(i(不一定是质数))筛选，但是会跳过很多
            j = 1;
            // N=30
            // 质数               筛选
            // [1,2]              2筛选4
            // [1,2,3]            3筛选6,9
            // [1,2,3]            4筛8, 1次后跳过
            // [1,2,3,5]          5筛选10,15,25
            // [1,2,3,5]          6筛选12，1次后跳过
            // [1,2,3,5,7]        7筛选14,21
            // [1,2,3,5,7]        8筛选16, 1次后然后跳过
            // [1,2,3,5,7]        9筛选18,27，2次后跳过
            // [1,2,3,5,7]        10筛选20, 1此后跳过
            while (j < count && (prime = primes.get(j++)) * i <= N) {
                isPrime[i * prime] = false;
                if (i % prime == 0) break;
            }
        }
        return primes.stream().mapToInt(Integer::intValue).toArray();
    }
}

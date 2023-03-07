package veinthrough.leetcode.math;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第1025题: 除数博弈
 * 爱丽丝和鲍勃一起玩游戏，他们轮流行动。爱丽丝先手开局。
 * 最初，黑板上有一个数字 N 。在每个玩家的回合，玩家需要执行以下操作：
 * . 选出任一 x，满足 0 < x < N 且 N % x == 0 。
 * . 用 N - x 替换黑板上的数字N。
 * 如果玩家无法执行这些操作，就会输掉游戏。
 * 只有在爱丽丝在游戏中取得胜利时才返回 True，否则返回 false。假设两个玩家都以最佳状态参与游戏。
 *
 * 规律: N为奇数的时候 Alice（先手）必败，N为偶数的时候 Alice 必胜。
 */
@Slf4j
public class DivisorGame {
    @Test
    public void test1025() {
        Stream.of(3, 5, 8)
                .forEach(n -> log.info(methodLog(
                        "n", "" + n,
                        "方法1(找规律)", "" + divisorGame(n),
                        "方法2(动态规划)", "" + divisorGame2(n))));
    }

    /**
     * 方法1: 找到规律
     * 1. 规律
     * n=1 的时候，区间 (0, 1)中没有整数是 n的因数，所以此时Alice 败。
     * n=2 的时候， Alice 只能拿 1，n 变成 1， Bob 无法继续操作，故Alice 胜。
     * n=3 的时候， Alice 只能拿 1，n 变成 2，根据n=2 的结论，我们知道此时Bob 会获胜， Alice 败。
     * n=4 的时候， Alice 能拿 1 或 2，如果Alice 拿 1，根据n=3 的结论， Bob 会失败， Alice 会获胜。
     * n=5 的时候， Alice 只能拿 1，根据 n=4 的结论， Alice 会失败。
     * 2. 证明
     * (1) n=1 和 n=2 时结论成立。
     * (2)n>2 时，假设n≤k 时该结论成立，则n=k+1 时
     *  >如果k 为偶数，则k+1 为奇数，x 是k+1 的因数，只可能是奇数，而奇数减去奇数等于偶数，且k+1−x≤k，
     *  故轮到 Bob 的时候都是偶数。而根据我们的猜想假设n≤k 的时候偶数的时候先手必胜，
     *  故此时无论Alice 拿走什么， Bob 都会处于必胜态，所以Alice 处于必败态。
     *  > 如果 k 为奇数，则k+1 为偶数，x 可以是奇数也可以是偶数，若Alice 减去一个奇数，那么k+1−x 是一个小于等于 k 的奇数，
     *  此时Bob 占有它，处于必败态，则Alice 处于必胜态。
     */
    private boolean divisorGame2(int n) {
        return n % 2 == 0;
    }

    /**
     * 方法2: 动态规划
     * f[i] 表示当前数字i的时候先手是处于必胜态还是必败态，true表示先手必胜，false表示先手必败，从前往后递推，
     * 根据我们上文的分析，枚举i在 (0, i)中i的因数j，看是否存在f[i-j]为必败态即可。
     */
    public boolean divisorGame(int N) {
        if (N == 1) return false;
        if (N == 2) return true;

        boolean[] win = new boolean[N + 1];
        win[2] = true;
        for (int i = 3; i <= N; i++) {
            // 枚举i在 (1, i)中i的因数j，看是否存在f[i-j]为必败态
            // 那么f[i]即为必胜态
            for (int j = 1; j < i; j++) {
                if (i % j == 0 && !win[i - j]) {
                    win[i] = true;
                    break;
                }
            }
        }
        return win[N];
    }
}
package veinthrough.leetcode.math;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第89题：格雷码，
 * n 位格雷码序列 是一个由2^n个整数组成的序列，其中：
 * (1) 每个整数都在范围 [0, 2^n - 1] 内（含0 和 2^n - 1）
 * (2) 第一个整数是0
 * (3) 一个整数在序列中出现不超过一次
 * (4) 每对相邻整数的二进制表示恰好一位不同，且第一个和最后一个整数的二进制表示恰好一位不同
 * 给你一个整数n，返回任一有效的n位格雷码序列。
 * > 示例 1：
 * 输入：n = 2
 * 输出：[0,1,3,2]
 * 解释：
 * [0,1,3,2] 的二进制表示是[00,01,11,10]。
 * - 00 和 01 有一位不同
 * - 01 和 11 有一位不同
 * - 11 和 10 有一位不同
 * - 10 和 00 有一位不同
 * [0,2,3,1]也是一个有效的格雷码序列，其二进制表示是[00,10,11,01]。
 * - 00 和 10 有一位不同
 * - 10 和 11 有一位不同
 * - 11 和 01 有一位不同
 * - 01 和 00 有一位不同
 * > 示例 2：
 * 输入：n = 1
 * 输出：[0,1]
 */
@Slf4j
public class GrayCode {
    @Test
    public void test89() {
        Stream.of(2, 3, 4, 5)
                .forEach(n ->
                        log.info(methodLog(
                                "Method 1", "" + grayCode(n),
                                "Method 2", "" + grayCode2(n))));
    }

    /**
     * n=2
     * 00      ---     0
     * 01      ---     1
     * 11      ---     3
     * 10      ---     2
     *
     * n=3
     * 000     ---     0                               --(0)
     * 001     ---     1                       --(1)
     * 011     ---     3               --(3)
     * 010     ---     2       --(2)
     * 110     ---     6       --(2+4)
     * 111     ---     7               --(3+4)
     * 101     ---     5                       --(1+4)
     * 100     ---     4                               --(0+4)
     *
     * 由n=2去构造n=3
     * (1) 添0,   符合要求: [00, 01, 11, 10]  -->  [000, 001, 011, 010]
     * (2) 添1, 不符合要求: [00, 01, 11, 10]  -->                     [100, 101, 111, 110]
     * 不符合要求(一头一尾):
     * > 010/100差2位, 10/00原来只差1位, 在10上添0/在00上添1导致差2位
     * > 000/110差2位, 00/10原来只差1位, 在00上添0/在10上添1导致差2位
     * 那就把[100, 101, 111, 110]逆序
     */
    private List<Integer> grayCode(int n) {
        List<Integer> res = new LinkedList<>();
        res.add(0);
        res.add(1);

        //
        int lastSize;
        int highest;
        for (int i = 2; i <= n; i++) {
            lastSize = res.size();
            highest = 1 << i - 1;
            for (int j = lastSize - 1; j >= 0; j--) // 逆序
                res.add(res.get(j) | highest);
        }
        return res;
    }

    /**
     * 方法2：
     * 1. 对n位二进制的码字，从右到左，以0到n-1编号
     * 2. 如果二进制码字的第i位和i+1位相同，则对应的格雷码的第i位为0，否则为1（当i+1=n时，
     * 二进制码字的第n位被认为是0，即第n-1位不变）
     * 公式表示： Gi=Bi⊕Bi+1（G：格雷码，B：二进制码）
     * 
     */
    private List<Integer> grayCode2(int n) {
        List<Integer> res = new LinkedList<>();
        int max = 1 << n;
        for (int i = 0; i < max; i++)
            res.add(i ^ (i >> 1));
        return res;
    }
}

package veinthrough.leetcode.math;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第29题，不使用乘法、除法和mod实现除法：
 * 给定两个整数，被除数 dividend 和除数 divisor。将两数相除，要求不使用乘法、除法和 mod 运算符。
 * 返回被除数 dividend 除以除数 divisor 得到的商。
 * 整数除法的结果应当截去（truncate）其小数部分，例如：truncate(8.345) = 8 以及 truncate(-2.7335) = -2
 *
 * 提示：
 * 被除数和除数均为 32 位有符号整数。
 * 除数不为 0。
 * 假设环境只能存储32位有符号整数，其数值范围是 [−2^31,  2^31−1]。本题中，如果除法结果溢出，则返回 2^31 − 1。
 *
 * 思路就是取得一个"二进制串":
 * dividend逐步减去一个divisor*2^n, result逐步加上2^n
 */
@Slf4j
@SuppressWarnings("Duplicates")
public class Divide {
    @Test
    public void test29() {
        // 1, 1073741820, -1073741824, 1073741824, -1
        Stream.of(
                divide(-2147483648, -1109186033),
                divide(2147483641, 2),
                divide(-2147483648, 2),
                divide(-2147483648, -2),
                divide(-2147483648, 1262480350))
                .forEach(result -> log.info(methodLog("" + result)));
    }

    @Test
    public void test29_2() {
        // 1, 1073741820, -1073741824, 1073741824, -1
        Stream.of(
                divide2(-2147483648, -1109186033),
                divide2(2147483641, 2),
                divide2(-2147483648, 2),
                divide2(-2147483648, -2),
                divide2(-2147483648, 1262480350))
                .forEach(result -> log.info(methodLog("" + result)));
    }

    /**
     * 方法1：
     * 如说result=Divide(3084,5)
     * 必须先构造一个不同位的对应：
     * 0	1   2   3   4   5   6   7   8   9
     * 1   2	4	8	16	32	64	128	256	512
     * 5  10   20  40	80	160	320	640	1280 2560
     * 然后不断用3048-2560(-1280)-640...
     * 同时result不断+512(+256)+128...
     *
     * 而不能使用求二进制的过程(不断/2)，因为这个过程是从低位高位, 而这里必须从高位开始减
     */
    private int divide(int _dividend, int _divisor) {
        // 1. 0
        if (_dividend == 0) return 0;
        // 2. divisor==Integer.MIN_VALUE: 0/1
        if (_divisor == MIN_VALUE)
            return _dividend == MIN_VALUE ? 1 : 0;
        // 3. overflow
        if (_dividend == MIN_VALUE && _divisor == -1) return Integer.MAX_VALUE;
        // 4. divisor==1
        if (_divisor == 1) return _dividend;
        if (_divisor == -1) return -_dividend;
        // 5. divisor==2
        if (_divisor == 2) return _dividend >> 1;
        if (_divisor == -2) return -(_dividend >> 1);

        // 6.
        long result;
        boolean negative = _dividend < 0 && _divisor > 0 || _dividend > 0 && _divisor < 0;
        // (1) 正负转换
        // 这里divisor/dividend换成long效率应该更高
        long divisor = _divisor < 0 ? -(long) _divisor : _divisor;
        long dividend = _dividend < 0 ? -(long) _dividend : _dividend;

        // (2) 0/1
        if (dividend < divisor) result = 0;
        else if (dividend == divisor) result = 1;
        else {

            List<Long> multiples = new ArrayList<>();
            List<Long> products = new ArrayList<>();
            // multiple: n, product: divisor*2^n
            long multiple = 1, product = divisor;

            // (3) 确定最高位n
            // dividend为2147483647时移位可能溢出
            for (; product < dividend && product > 0; ) {
                multiples.add(multiple);
                products.add(product);
                multiple <<= 1;
                product <<= 1;
            }

            // difference: 从最高位开始逐步减去一个divisor*2^n;
            // result: 逐步加上2^n
            int size = products.size();
            int i = size - 2;
            long difference = dividend - products.get(size - 1);
            result = multiples.get(size - 1);
            for (; difference >= divisor; i--) {
                if (difference >= products.get(i)) {
                    result += multiples.get(i);
                    difference -= products.get(i);
                }
            }
        }

        return negative ? -(int) result : (int) result;
    }

    /**
     * 方法2：
     * 使用二分查找，【注意里面几个防止溢出的操作】
     * 由于题目规定了「只能存储32位整数」，本题解的正文部分和代码中都不会使用任何64位整数。
     * 诚然，使用64 位整数可以极大地方便我们的编码，但这是违反题目规则的。
     * 1. 特殊情况的处理
     * 如果除法结果溢出，那么我们需要返回2^31-1作为答案。因此在编码之前，我们可以首先对于溢出或者容易出错的边界情况进行讨论：
     * (1) 当被除数为32位有符号整数的最小值 -2^31时：
     * 如果除数为 1，那么我们可以直接返回答案 -2^31；
     * 如果除数为 −1，那么答案为 2^31，产生了溢出。此时我们需要返回 2^31-1
     * (2) 当除数为32位有符号整数的最小值 -2^31时：
     * 如果被除数同样为-2^31，那么我们可以直接返回答案1；
     * 对于其余的情况，我们返回答案0。
     * (3) 当除数为1时
     * (4) 当被除数为0时，我们可以直接返回答案0。
     * (5) 对于一般的情况，根据除数和被除数的符号，我们需要考虑 4 种不同的可能性。
     * 因此，为了方便编码，我们可以将被除数或者除数取相反数，使得它们符号相同。
     * 2. 正负的处理
     * 如果我们将被除数和除数都变为正数，那么可能会导致溢出。例如当被除数为 -2^31时，它的相反数 2^31产生了溢出。
     * 因此，我们可以考虑将被除数和除数都变为负数，这样就不会有溢出的问题，在编码时只需要考虑1种情况了。
     * 如果我们将被除数和除数的其中（恰好）一个变为了正数，那么在返回答案之前，我们需要对答案也取相反数。
     * 3. 二分查找
     * 我们记被除数为X，除数为 Y，并且X和Y都是负数。我们需要找出 X/Y的结果Z。Z一定是正数或0。
     *
     * 根据除法以及余数的定义，我们可以将其改成乘法的等价形式，即：
     * Z×Y≥X>(Z+1)×Y
     * (如果XYZ都是正数，那么应该是Z×Y≤X<(Z+1)×Y)
     * 因此，我们可以使用二分查找的方法得到Z，即找出最大的Z使得Z×Y≥X 成立。
     *
     * 由于我们不能使用乘法运算符，因此我们需要使用「快速乘」算法得到Z×Y 的值。「快速乘」算法与「快速幂」类似，
     * 前者通过加法实现乘法，后者通过乘法实现幂运算。「快速幂」算法可以参考{@link Pow#_pow(double, int)}，
     * 「快速乘」算法只需要在「快速幂」算法的基础上，将乘法运算改成加法运算即可。
     * 细节:
     * 由于我们只能使用32 位整数，因此二分查找中会有很多细节。
     * (1) 首先，二分查找的下界为1，上界为 2^31-1(拿X/Y都为正数来说，我觉得上界是被除数X, 因为X/Y都为整数，Y最小为1)。
     * 唯一可能出现的答案为 2^31的情况已经被我们在「前言」部分进行了特殊处理，因此答案的最大值为 2^31。如果二分查找失败，那么答案一定为0。
     * (2) 在实现「快速乘」时，我们需要使用加法运算，然而较大的Z也会导致加法运算溢出。
     * 例如我们要判断A+B 是否小于C 时（其中A,B,C 均为负数），A+B 可能会产生溢出，
     * 因此我们必须将判断改为A<C−B 是否成立。由于任意两个负数的差一定在 [-2^{31} + 1, 2^{31} - 1]范围内，这样就不会产生溢出。
     */
    public int divide2(int _dividend, int _divisor) {
        // 1. 特殊情况
        if (_divisor == -1) return _dividend == MIN_VALUE ? MAX_VALUE : -_dividend; // overflow
        if (_divisor == 1) return _dividend;
        if (_divisor == MIN_VALUE) return _dividend == MIN_VALUE ? 1 : 0;
        if (_divisor == MAX_VALUE) {
            if (_dividend == MIN_VALUE || _dividend == -MAX_VALUE) return -1;
            if (_dividend == MAX_VALUE) return 1;
            return 0;
        }
        if (_dividend == 0) return 0;
        if (_divisor == 2) return _dividend >> 1;
        if (_divisor == -2) return -(_dividend >> 1);
        // 2. 【防止溢出操作1】正负转化, 都转化为负数防止溢出
        boolean negative = (_dividend < 0 && _divisor > 0) || (_dividend > 0 && _divisor < 0);
        int dividend = _dividend > 0 ? -_dividend : _dividend;
        int divisor = _divisor > 0 ? -_divisor : _divisor;
        // 3.
        int i = 1;
        // (1) 右边界: 这里将负数转成正数可能溢出
        // MIN_VALUE为-2147483648, 结果不能为2147483648, 2147483648已经溢出
        // 结果为2147483648只可能-2147483648除以-1, 这种情况已经在特殊情况中处理
        // (2) 题解中将右边界设定为MAX_VALUE, 这里将右边界设定为-dividend(除了溢出情况)而不是MAX_VALUE
        // 因为被除数/除数都为整数，所以商的绝对值不可能超过被除数
        int j = dividend==MIN_VALUE? MAX_VALUE : -dividend;
        int mid, quotient = 0;
        // dividend/divisor都为正数: k*divisor<=dividend<(k+1)*divisor
        // dividend/divisor都为负数: k*divisor>=dividend>(k+1)*divisor
        while (i <= j) {
            // 【防止溢出操作2】这样写(mid = (i + j) >> 1)会溢出
            mid = i + ((j - i) >> 1);
            // 验证是否k*divisor>=dividend
            if (_kDivisorGeDividend(dividend, divisor, mid)) {
                quotient = mid;
                // 【防止溢出操作3】提前跳出: 因为这里-dividend有可能是MAX_VALUE
                if(mid==-dividend) break;
                i = mid + 1;
            } else j = mid - 1;
        }
        return negative ? -quotient : quotient;
    }

    /**
     * 对于dividend/divisor都为负数，验证是否k*divisor>=dividend；
     * 所以如果dividend/divisor都为正数, 实际上相当于k*divisor<=dividend,
     * 求k*divisor的过程中使用了快速累加，类似于求幂过程中的快速累乘{@link Pow#_pow(double, int)}
     */
    private boolean _kDivisorGeDividend(int dividend, int divisor, int k) {
        int weight = divisor;
        int sum = 0;
        while (k != 0) {
            // 使用&代替模运算
            if ((k & 1) !=0) {
                // 【防止溢出操作4】: 【提前】判定sum+weight>=dividend
                // 将加法改成减法防止溢出
                if(sum<dividend-weight) return false;
                sum += weight;
            }
            if (k != 1) {
                // 【防止溢出操作5】: 【提前】保证weight+weight>=dividend
                // 否则weight+weight也有可能溢出, 同样将加法改成减法
                if(weight<dividend-weight) return false;
                weight += weight;
            }
            k >>= 1;
        }
        return true;
    }
}
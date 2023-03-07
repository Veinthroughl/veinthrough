package veinthrough.leetcode;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.IntStream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第38题：外观数列
 * 给定一个正整数 n ，输出外观数列的第 n 项。
 * 「外观数列」是一个整数序列，从数字 1 开始，序列中的每一项都是对前一项的描述。
 * 你可以将其视作是由递归公式定义的数字字符串序列：
 * countAndSay(1) = "1"
 * countAndSay(n) 是对 countAndSay(n-1) 的描述，然后转换成另一个数字字符串。
 * 前五项如下：
 * 1.     1
 * 2.     11
 * 3.     21
 * 4.     1211
 * 5.     111221
 * 第一项是数字 1
 * 描述前一项，这个数是 1 即 “ 一 个 1 ”，记作 "11"
 * 描述前一项，这个数是 11 即 “ 二 个 1 ” ，记作 "21"
 * 描述前一项，这个数是 21 即 “ 一 个 2 + 一 个 1 ” ，记作 "1211"
 * 描述前一项，这个数是 1211 即 “ 一 个 1 + 一 个 2 + 二 个 1 ” ，记作 "111221"
 * 要描述一个数字字符串，首先要将字符串分割为最小数量的组，每个组都由连续的最多相同字符组成。
 * 然后对于每个组，先描述字符的数量，然后描述字符，形成一个描述组。
 * 要将描述转换为数字字符串，先将每组中的字符数量用数字替换，再将所有描述组连接起来。
 *
 * 1 <= n <= 30
 */
@Slf4j
public class CountAndSay {
    private static String[] say = new String[30];
    static {
        say[0] = "1";
        say[1] = "11";
        say[2] = "21";
        say[3] = "1211";
        say[4] = "111221";
        char[] chs;
        StringBuilder str;
        for(int i=5,j,count;i<30;i++) {
            chs = say[i-1].toCharArray();
            str = new StringBuilder();
            for(j=1,count=1; j<chs.length; j++) {
                if(chs[j]==chs[j-1])
                    count++;
                else {
                    str.append(count);
                    str.append(chs[j-1]);
                    count = 1;
                }
            }
            str.append(count);
            str.append(chs[j-1]);
            say[i] = str.toString();
        }
    }

    @Test
    public void test() {
        IntStream.rangeClosed(1, 30)
                .forEach(n -> log.info(methodLog(""+n, countAndSay(n))));
    }

    private static String countAndSay(int n) {
        return say[n-1];
    }
}

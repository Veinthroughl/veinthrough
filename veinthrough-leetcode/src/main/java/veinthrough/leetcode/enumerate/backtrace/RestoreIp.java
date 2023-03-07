package veinthrough.leetcode.enumerate.backtrace;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 地93题：复原IP地址，
 * 有效 IP 地址 正好由四个整数（每个整数位于 0 到 255 之间组成，且不能含有前导 0），整数之间用 '.' 分隔。
 * 例如："0.1.2.201" 和 "192.168.1.1" 是 有效 IP 地址，但是 "0.011.255.245"、"192.168.1.312" 和 "192.168@1.1" 是 无效 IP 地址。
 * 给定一个只包含数字的字符串s，用以表示一个 IP 地址，返回所有可能的有效 IP 地址，这些地址可以通过在s中插入 '.' 来形成。
 * 你不能重新排序或删除 s 中的任何数字。你可以按任何顺序返回答案。
 *
 * 示例 1：
 * 输入：s = "25525511135"
 * 输出：["255.255.11.135","255.255.111.35"]
 *
 * 示例 2：
 * 输入：s = "0000"
 * 输出：["0.0.0.0"]
 *
 * 示例 3：
 * 输入：s = "101023"
 * 输出：["1.0.10.23","1.0.102.3","10.1.0.23","10.10.2.3","101.0.2.3"]
 */
@Slf4j
public class RestoreIp {
    private int n;
    private int[][] value;
    private List<String> res;

    @Test
    public void test93() {
        Stream.of("25525511135", "0000", "101023")
                .forEach(str -> log.info(methodLog(
                        str, "" + restoreIpAddresses(str))));
    }

    private List<String> restoreIpAddresses(String s) {
        // boundary
        res = new LinkedList<>();
        int[] nums = s.chars().map(num -> num - '0').toArray();
        if ((n = nums.length) < 4) return res;
        if (n == 4) {
            res.add(buildIp(nums));
            return res;
        }

        // 1. 提前计算好1/2/3个数字是否是一个有效的IP数字
        value = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                value[i][j] = -1;
        for (int i = 0, j, k; i < n; i++) {
            value[i][i] = nums[i];  // 1 digit
            if ((j = i + 1) < n && nums[i] != 0 && (value[i][j] = 10 * nums[i] + nums[j]) <= 25 && // 2 digits
                    (k = j + 1) < n && (value[i][k] = 10 * value[i][j] + nums[k]) > 255)  // 3 digits
                value[i][k] = -1;
        }
        // 2. dfs
        dfs(new int[4], 0, 0);
        return res;
    }

    public void dfs(int[] building, int idx, int i) {
        // (1) 匹配
        if (idx == 4 && i == n) {
            res.add(buildIp(building));
            return;
        }

        // (2) 需要计算下一个idx
        if (idx < 4 && i < n) {
            int j = i + 1, k = j + 1, nextIdx = idx + 1;
            // 1 digit
            building[idx] = value[i][i];
            dfs(building, nextIdx, j);
            // 2 digits
            if (j < n && value[i][j] != -1) {
                building[idx] = value[i][j];
                dfs(building, nextIdx, k);
            }
            // 3 digits
            if (k < n && value[i][k] != -1) {
                building[idx] = value[i][k];
                dfs(building, nextIdx, k + 1);
            }
        }
    }

    private String buildIp(int[] ipNums) {
        return ipNums[0] + "." +
                ipNums[1] + "." +
                ipNums[2] + "." +
                ipNums[3];
    }
}

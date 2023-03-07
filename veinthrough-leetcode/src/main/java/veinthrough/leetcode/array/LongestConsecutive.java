package veinthrough.leetcode.array;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class LongestConsecutive {
    public int longestConsecutive(int[] nums) {
        // 建立一个存储所有数的哈希表，同时起到去重功能
        Set<Integer> set = new HashSet<>();
        for (int num : nums) {
            set.add(num);
        }

        int ans = 0;
        // 遍历去重后的所有数字
        for (int num : set) {
            int cur = num;
            // 只有当num-1不存在时，才开始向后遍历num+1，num+2，num+3......
            // 因为如果存在num-1, 那么num肯定已经在num-1的那一轮计算过了
            if (!set.contains(cur - 1)) {
                while (set.contains(cur + 1)) {
                    cur++;
                }
            }
            // [num, cur]之间是连续的，数字有cur - num + 1个
            ans = Math.max(ans, cur - num + 1);
        }
        return ans;
    }

    /**
     * [204,4,200,201,1,203,3,2,202]
     * #                                    204  4   202 201  1   203 3   2   10  200
     * # i=0, right=204                     204  4   202 201  1   203 3   2   10  200
     * # i=1, right=5                       204  4   202 201  1   203 3   2   10  200
     * # i=2, right=203/204, ans=3          204  4  204* 201  1   203 3   2   10  200
     * # i=3, right=202/204, ans=4          204  4   204 204* 1   203 3   2   10  200
     * # i=4, right=2/3/4, ans=4            204  4   204 204  4*  203 3   2   10  200
     * # i=5, right=204, ans=4              204  4   204 204  4  204* 3   2   10  200
     * # i=6, right=4, ans=4                204  4   204 204  4   204 4*  2   10  200
     * # i=7, right=3/4, ans=4              204  4   204 204  4   204 4   4*  10  200
     * # i=8, right=11, ans=4               204  4   204 204  4   204 4   4   10  200
     * # i=9, right=201/204, ans=5          204  4   204 204  4   204 4   4   10  204*
     */
    public int longestConsecutive2(int[] nums) {
        // key表示num，value表示num最远到达的连续右边界
        Map<Integer, Integer> map = new HashMap<>();
        // 初始化每个num的右边界为自己
        for (int num : nums) {
            map.put(num, num);
        }

        int ans = 0;
        for (int num : nums) {
            if (!map.containsKey(num - 1)) {
                int right = map.get(num);
                // 遍历得到最远的右边界
                while (map.containsKey(right + 1)) {
                    right = map.get(right + 1);
                }
                // 更新右边界
                map.put(num, right);
                // 更新答案
                ans = Math.max(ans, right - num + 1);
            }
        }
        return ans;
    }

    /**
     * [204,4,200,201,1,203,3,2,202]
     * #                                    0    1   2   3    4   5   6   7   8   9
     * #                                    204  4   202 201  1   203 3   2   10  200
     * # i=0, left=0, right=0, curLen=1     {(204,1)}
     * # i=1, left=0, right=0, curLen=1     {(204,1), (4,1)}
     * # i=2, left=0, right=0, curLen=1     {(204,1), (4,1), (202,1)}
     * # i=3, left=0, right=1, curLen=2     {(204,1), (4,1), (202,2), (201,2)}
     * # i=4, left=0, right=0, curLen=1     {(204,1), (4,1), (202,2), (201,2), (1,1)}
     * # i=5, left=2, right=1, curLen=4     {(204,4), (4,1), (202,2), (201,4), (1,1), (203,-1)}
     * # i=6, left=0, right=1, curLen=2     {(204,4), (4,2), (202,2), (201,4), (1,1), (203,-1), (3,2)}
     * # i=7, left=1, right=2, curLen=4     {(204,4), (4,4), (202,2), (201,4), (1,4), (203,-1), (3,2), (2,-1)}
     * # i=8, left=0, right=0, curLen=1     {(204,4), (4,4), (202,2), (201,4), (1,4), (203,-1), (3,2), (2,-1), (10,1)}
     * # i=9, left=0, right=4, curLen=5     {(204,5), (4,4), (202,2), (201,4), (1,4), (203,-1), (3,2), (2,-1), (10,1), (200,5)}
     */
    public int longestConsecutive3(int[] nums) {
        // key表示num，value表示num所在连续区间的长度
        Map<Integer, Integer> map = new HashMap<>();
        int ans = 0;
        for (int num : nums) {
            // 当map中不包含num，也就是num第一次出现
            if (!map.containsKey(num)) {
                // left为num-1所在连续区间的长度，更进一步理解为：左连续区间的长度
                int left = map.getOrDefault(num - 1, 0);
                // right为num+1所在连续区间的长度，更进一步理解为：右连续区间的长度
                int right = map.getOrDefault(num + 1, 0);
                // 当前连续区间的总长度
                int curLen = left + right + 1;
                ans = Math.max(ans, curLen);
                // 将num加入map中，表示已经遍历过该值。其对应的value可以为任意值。
                map.put(num, -1);
                // 更新当前连续区间左边界和右边界对应的区间长度
                map.put(num - left, curLen);
                map.put(num + right, curLen);
            }
        }
        return ans;
    }
}

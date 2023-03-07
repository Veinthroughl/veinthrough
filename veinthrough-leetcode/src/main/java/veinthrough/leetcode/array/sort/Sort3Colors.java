package veinthrough.leetcode.array.sort;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第75题，颜色分类(排序)，
 * 给定一个包含红色、白色和蓝色共 n 个元素的数组 nums，原地对它们进行排序，使得相同颜色的元素相邻，并按照红色、白色、蓝色顺序排列。
 * 我们使用整数 0、 1 和 2 分别表示红色、白色和蓝色。
 * 必须在不使用库的sort函数的情况下解决这个问题。
 */
@SuppressWarnings({"Duplicates", "unused"})
@Slf4j
public class Sort3Colors {
    @Test
    public void test75() {
        Stream.of(
                new int[]{0, 0, 1},
                new int[]{0, 1, 2},
                new int[]{2, 0, 2, 0, 1, 0, 2, 1},
                new int[]{2, 0, 2, 1, 1, 0},
                new int[]{2, 0, 2, 2, 2, 0, 1, 0, 2, 1, 2, 0, 1, 0, 1, 0},
                new int[]{2, 0, 2, 2, 2, 0, 1, 2, 0, 2, 1, 2, 0, 1, 0, 1, 0},
                new int[]{2, 0, 2, 2, 2, 0, 1, 0, 2, 0, 2, 1, 2, 0, 1, 0, 1, 0})
                .forEach(nums -> {
                    log.info(methodLog("Pre:", Arrays.toString(nums)));
                    sortColors(nums);
                    log.info(methodLog("Post", Arrays.toString(nums)));
                });
    }

    private void sortColors(int[] nums) {
        // boundary
        int n;
        if ((n = nums.length) == 1) return;
        if (n == 2) {
            if (nums[0] > nums[1]) swap(nums, 0, 1);
            return;
        }

        // 方法1
//        _sortColors(nums, n);
        // 方法2
        _sortColors2(nums, n);
    }


    /**
     * 方法1(题解做法)：
     * p0表示0的结尾的下一个，p1表示1的结尾的下一个；
     * 遇到0/1就需要插入到结尾(p0/p1)，不断交换和更新p0/p1
     * #  0      1      2      3      4      5
     * #  -------------------------------------------------------------------
     * #  2      0      2      1      1      0
     * #  p0,p1  i                                  交换(i,p0), p0++, p1++
     * #  -------------------------------------------------------------------
     * #  0      2      2      1      1      0
     * #       p0,p1    i
     * #  -------------------------------------------------------------------
     * #  0      2      2      1      1      0
     * #       p0,p1           i                    交换(i,p1), p1++
     * #  -------------------------------------------------------------------
     * #  0      1      2      2      1      0
     * #        p0     p1             i             交换(i,p1), p1++
     * #  -------------------------------------------------------------------
     * #  0      1      1      2      2      0
     * #        p0            p1             i      交换(i,p0), p0++
     * #  0      0      1      2      2      1
     * #                p0     p1            i      交换(i,p1), p1++
     * #  0      0      1      1      2      2
     * #                p0            p1
     */
    private void _sortColors(int[] nums, int n) {
        int i = 0;
        int p0 = 0, p1 = 0;
        while (i < n && nums[i] == 0) {
            i++;
            p0++;
            p1++;
        }
        while (i < n && nums[i] == 1) {
            i++;
            p1++;
        }
        for (int swap; i < n; i++) {
            swap = nums[i];
            // 1. 遇到0
            if (nums[i] == 0) {
                swap(nums, p0, i);
                p0++;
            }
            // 2. 遇到1:
            // (1) 从上面的(遇到0)中交换过来的nums[p0]==1, 需要继续交换
            // (2) 原本的nums[i]==1
            // 不能在上面修改p1，因为这里还需要用
            if (nums[i] == 1) swap(nums, p1, i);

            // 3. 需要修改p1
            if (swap <= 1) p1++;
        }
    }

    /**
     * 方法1(自己做法)：
     * 1. 构造成：被[1]分成很多段(每个段内都是2)
     * 2. 将段为1的段插入到前面没有改变的那段后面
     * ---------------------------------------------------------------------------
     * #	0	1	2	3	4	5	6	7	8	9	10	11	12	13	14	15
     * #	2*	0	2	2	2	0	1	0	2	1	2	0	1	0	1	0*
     * #	0	0	2*	2	2	0	1	0	2	1	2	0	1	0	1*	2
     * #	0	0	1*	2	2	0	1	0	2	1	2	0	1	0*	2	2
     * #	0	0	0	2*	2	0	1	0	2	1	2	0	1	1*	2	2
     * #	0	0	0	1*	2	0	1	0	2	1	2	0*	[1]	2	2	2	#
     * #	0	0	0	0	2*	0	1	0	2	1	2	1*	[1]	2	2	2
     * #	0	0	0	0	1*	0	1	0*	2	[1]	2	2	[1]	2	2	2	#
     * #	0	0	0	0	0	0	[1]	[1]	2	[1]	2	2	[1]	2	2	2	#
     * #						l/r
     * ---------------------------------------------------------------------------
     * #	0	1	2	3	4	5	6	7	8	9	10	11	12	13	14	15	16
     * #	2*	0	2	2	2	0	1	2	0	2	1	2	0	1	0	1	0*
     * #	0	0	2*	2	2	0	1	2	0	2	1	2	0	1	0	1*	2
     * #	0	0	1*	2	2	0	1	2	0	2	1	2	0	1	0*	2	2
     * #	0	0	0	2*	2	0	1	2	0	2	1	2	0	1	1*	2	2
     * #	0	0	0	1*	2	0	1	2	0	2	1	2	0*	[1]	2	2	2	#
     * #	0	0	0	0	2*	0	1	2	0	2	1	2	1*	[1]	2	2	2
     * #	0	0	0	0	1*	0	1	2	0*	2	[1]	2	2	[1]	2	2	2	#
     * #	0	0	0	0	0	0	1*	2*	[1]	2	[1]	2	2	[1]	2	2	2	#
     * #							l/r
     * ---------------------------------------------------------------------------
     * #	0	1	2	3	4	5	6	7	8	9	10	11	12	13	14	15	16	17
     * #	2*	0	2	2	2	0	1	0	2	0	2	1	2	0	1	0	1	0*
     * #	0	0	2*	2	2	0	1	0	2	0	2	1	2	0	1	0	1*	2
     * #	0	0	1*	2	2	0	1	0	2	0	2	1	2	0	1	0*	2	2
     * #	0	0	0	2*	2	0	1	0	2	0	2	1	2	0	1	1*	2	2
     * #	0	0	0	1*	2	0	1	0	2	0	2	1	2	0*	[1]	2	2	2	#
     * #	0	0	0	0	2*	0	1	0	2	0	2	1	2	1*	[1]	2	2	2
     * #	0	0	0	0	1*	0	1	0	2	0*	2	[1]	2	2	[1]	2	2	2	#
     * #	0	0	0	0	0	0	1*	0*	2	[1]	2	[1]	2	2	[1]	2	2	2	#
     * #	0	0	0	0	0	0	0	1*	2	[1]	2	[1]	2	2	[1]	2	2	2
     * #								l/r	p1	s1		s1			s1
     */
    private void _sortColors2(int[] nums, int n) {
        // 1. 构造成：被[1]分成很多段(每个段内都是2)
        // 也就是说:
        // (1) left: 0, 跳过
        // (2) right: 2, 跳过
        // (3) left/right: 1/1, 跳过right
        // (4) left/right: 1/0，交换
        // (5) left/right: 2/0，交换
        // (6) left/right: 2/1，交换
        int l = 0, r = n - 1;
        int left, right;
        while (l < r) {
            // left: 1/2
            // (1) left==0, l++
            if ((left = nums[l]) == 0) {
                l++;
                continue;
            }
            // right: 0/1
            // (1) right==2, r--
            // (2) left==right==1
            if ((right = nums[r]) == 2 ||
                    left == right) {
                r--;
                continue;
            }

            // (1) left/right: 1/0
            // (2) left/right: 2/0
            // (3) left/right: 2/1
            swap(nums, l, r);
        }

        // 2. 前面一段(直到遇到2)不需要改变
        int i = l;
        while (i < n && nums[i] < 2) i++;
        // 3. 将段为[1]的段插入到前面没有改变的那段后面
        int p1 = i, s1; // p1表示元素为1的末尾
        while (i < n) {
            while (i < n && nums[i] == 2) i++;
            s1 = i;
            while (i < n && nums[i] == 1) i++;
            // section 1: [s1, i)
            if (s1 < n) swap(nums, p1, s1, i);
            // 更新1的末尾
            p1 += i - s1;
        }

    }

    /**
     * 块块交换
     * 0,1,2,3,4,5,6
     * 1,1,0,0,0,0   -(只需要交换2次)->
     * 0,0,0,0,1,1
     * 1,1,1,1,0,0   -(只需要交换2次)->
     * 0,0,1,1,1,1
     */
    private void swap(int[] nums, int l1, int l2, int next) {
        int gap = Math.max(l2 - l1, next - l2);
        int end = next - gap;
        for (int i = l1; i < end; i++)
            swap(nums, i, i + gap);
    }

    /**
     * 元素交换
     */
    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
}

package veinthrough.leetcode.array.sort;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.function.Function;

import static veinthrough.api.array.Array.swap;
import static veinthrough.api.util.MethodLog.methodLog;

@SuppressWarnings("unused")
@Slf4j
public class Sort {
    //    private int[] nums = new int[]{4, 3, 2, 6, 5, 4, 6, 2, 7, 2, 7, 4};
    private int[] nums = new int[]{1, 5, 19, 13, 6, 7, 11, 16, 3, 9, 8, 17, 4, 14, 2, 10, 20, 12, 15, 18};

    @Test
    public void test() {
        ImmutableMap.<String, Function<int[], int[]>>of(
                "Quick Sort", Sort::quickSort,
                "Merge Sort", Sort::mergeSort,
                "Heap Sort", Sort::heapSort,
                "Shell Sort", Sort::shellSort,
//                "Bubble Sort", Sort::bubbleSort,
                "Insert Sort", Sort::insertSort)
                .forEach((sortName, sort) ->
                        log.info(methodLog(
                                sortName, Arrays.toString(sort.apply(Arrays.copyOf(nums, nums.length))))));
    }

    /**
     * 冒泡排序
     */
    private static int[] bubbleSort(int[] nums) {
        int n = nums.length;
        int temp;
        int k;
        // 外循环, bubble正确的数到i位置: 最大的数-->n-1, 第2大的数-->n-2...
        for (int i = n - 1; i > 0; i--) {
            // 内循环: 从0开始bubble
            for (int j = 0; j < i; j++) {
                k = j + 1;
                if (nums[k] < nums[j]) {
                    temp = nums[j];
                    nums[j] = nums[k];
                    nums[k] = temp;
                }
            }
        }
        return nums;
    }

    /**
     * 插入排序
     */
    private static int[] insertSort(int[] nums) {
        int n = nums.length;
        int temp;
        // 外循环, 插入的数
        for (int i = 1, j; i < n; i++) {
            // 内循环: 向前插入的范围
            temp = nums[i];
            for (j = i; j > 0 && temp < nums[j - 1]; j--)
                nums[j] = nums[j - 1];
            nums[j] = temp;
        }
        return nums;
    }

    /**
     * 希尔排序
     */
    private static int[] shellSort(int[] nums) {
        int n = nums.length;
        int temp;
        // 在插入排序上加了一层gap
        for (int gap = n >> 1; gap > 0; gap >>= 1) {
            for (int i = gap, j; i < n; i += gap) {
                temp = nums[i];
                for (j = i; j >= gap && temp < nums[j - gap]; j -= gap)
                    nums[j] = nums[j - gap];
                nums[j] = temp;
            }
        }
        return nums;
    }

    /**
     * 堆排序
     *
     * 1. 构建最大堆
     * # 0  1   2   3  4  5  6   7   8  9  10  11  12  13  14  15  16  17  18  19
     * # 1  5  19  13  6  7  11  16  3  9  8  17   4   14   2  10  20  12  15  18
     * -------------------初始
     * #
     * #                                           1
     * #                           /                           \
     * #                        5                               19
     * #                /             \                 /             \
     * #              13              6                7               11
     * #          /      \        /      \         /     \         /      \
     * #         16      3       9       8      17        4      14        2
     * #       /  \    /  \    /
     * #      10  20  12  15  18
     * -------------------perDown(9)
     * #
     * #                                           1
     * #                           /                           \
     * #                        5                               19
     * #                /             \                 /             \
     * #              13              6                7               11
     * #          /      \        /      \         /     \         /      \
     * #         16      3      18*      8      17        4      14        2
     * #       /  \    /  \    /
     * #      10  20  12  15  9*
     * -------------------perDown(8)
     * #
     * #                                           1
     * #                           /                           \
     * #                        5                               19
     * #                /             \                 /             \
     * #              13              6                7               11
     * #          /      \        /      \         /     \         /      \
     * #         16     15*     18       8      17        4      14        2
     * #       /  \    /  \    /
     * #      10  20  12  3*  9
     * -------------------perDown(7)
     * #
     * #                                           1
     * #                           /                           \
     * #                        5                               19
     * #                /             \                 /             \
     * #              13              6                7               11
     * #          /      \        /      \         /     \         /      \
     * #        20*      15     18       8      17        4      14        2
     * #       /  \    /  \    /
     * #      10  16* 12  3   9
     * -------------------perDown(6)
     * #
     * #                                           1
     * #                           /                           \
     * #                        5                               19
     * #                /             \                 /             \
     * #              13              6                7              14*
     * #          /      \        /      \         /     \         /      \
     * #        20       15     18       8      17        4     11*        2
     * #       /  \    /  \    /
     * #      10  16  12  3   9
     * -------------------perDown(5)
     * #
     * #                                           1
     * #                           /                           \
     * #                        5                               19
     * #                /             \                 /             \
     * #              13              6               17*              14
     * #          /      \        /      \         /     \         /      \
     * #        20       15     18       8      7*        4      11        2
     * #       /  \    /  \    /
     * #      10  16  12  3   9
     * -------------------perDown(4): 4 --> 9  -->  19
     * #
     * #                                           1
     * #                           /                           \
     * #                        5                               19
     * #                /             \                 /             \
     * #              13             18*               17              14
     * #          /      \        /      \         /     \         /      \
     * #        20       15     9*       8       7        4      11        2
     * #       /  \    /  \    /
     * #      10  16  12  3   6*
     * -------------------perDown(3): 3  -->  7  -->  16
     * #
     * #                                           1
     * #                           /                           \
     * #                        5                               19
     * #                /             \                 /             \
     * #             20*              18              17              14
     * #          /      \        /      \         /     \         /      \
     * #        16*       15     9       8       7        4      11        2
     * #       /  \    /  \    /
     * #      10 13*  12  3   6
     * -------------------perDown(2), 无交换
     * #
     * #                                           1
     * #                           /                           \
     * #                        5                               19
     * #                /             \                 /             \
     * #             20               18              17              14
     * #          /      \        /      \         /     \         /      \
     * #        16        15     9       8       7        4      11        2
     * #       /  \    /  \    /
     * #      10 13  12   3   6
     * -------------------perDown(1): 1  -->  3  -->  7  -->  16
     * #
     * #                                           1
     * #                           /                           \
     * #                       20*                               19
     * #                /             \                 /             \
     * #             16*               18              17              14
     * #          /      \        /      \         /     \         /      \
     * #        13*      15     9       8       7        4      11        2
     * #       /  \    /  \    /
     * #      10 5*  12   3   6
     * -------------------perDown(0): 0  -->  1  -->  4  -->  9  -->  19
     * #
     * #                                         20*
     * #                           /                           \
     * #                        18*                            19
     * #                /             \                 /             \
     * #              16              9*              17              14
     * #          /      \        /      \         /     \         /      \
     * #         13      15     6*       8       7        4      11        2
     * #       /  \    /  \    /
     * #      10  5  12   3   1*
     */
    private static int[] heapSort(int[] nums) {
        int len = nums.length;
        // 1. 最大堆
        for (int i = (len >> 1) - 1; i >= 0; i--)
            percDown(nums, i, len);
        // 2. 每次将最大堆的最大值放在i
        // (1) [0,i)范围内为最大堆
        // (2) [i,n)范围为已经排过序的
        for (int i = len - 1; i > 0; i--) {
            swap(nums, 0, i);
            percDown(nums, 0, i);
        }
        return nums;
    }

    private static int leftChild(int i) {
        // 注意这里<<的优先级特别低, 必须加括号
        return (i << 1) + 1;
    }

    /**
     * @param n 这里限定了最大的索引为n
     */
    private static void percDown(int[] nums, int i, int n) {
        int child;
        int temp;
        // 不断迭代子节点, 把大的节点交换上来
        for (temp = nums[i]; (child = leftChild(i)) < n; i = child) {
            // (1) 找到更大的child, 从left/right
            // > 有右孩子: child !=n-1
            if (child != n - 1 && nums[child] < nums[child + 1])
                child++;
            // (2) 更大的child比父节点更大, 需要交换
            if (temp < nums[child])
                nums[i] = nums[child];
                // (3) 不需要交换, 如果本节点不需要交换, 不需要再迭代子节点;
                // 因为只有再本节点交换的情况下, 才会导致子节点变化更小, 需要再迭代子节点
            else
                break;
        }
        nums[i] = temp;
    }

    /**
     * 1. 左闭右开: [l, r)
     * 2. 只需要1个临时数组
     * (1) 每次使用temp的区间, 对应nums中相同区间
     * (2) merge是mergeSort中的最后一行
     */
    private static void mergeSort(int[] nums, int[] temp, int l, int r) {
        // >=2个元素
        if (l + 1 < r) {
            int mid = (l + r) / 2;
            mergeSort(nums, temp, l, mid);
            mergeSort(nums, temp, mid, r);
            merge(nums, temp, l, mid, r);
        }
    }

    private static int[] mergeSort(int[] nums) {
        mergeSort(nums, new int[nums.length], 0, nums.length);
        return nums;
    }

    /**
     * 1. 左闭右开: [l,r), [r,end)
     * 2. 每次使用temp的区间, 对应nums中相同区间
     */
    private static void merge(int[] nums, int[] temp, int l, int r, int end) {
        int i = l, j = r, k = l;
        while (i < r && j < end) {
            if (nums[i] < nums[j]) temp[k++] = nums[i++];
            else temp[k++] = nums[j++];
        }
        while (i < r) temp[k++] = nums[i++];
        while (j < end) temp[k++] = nums[j++];
        for (i = l; i < end; i++) nums[i] = temp[i];
    }

    /**
     * 1. 将枢纽元与最后的元素交换是的枢纽元离开要被分割的数据段
     * 2. 枢纽元的选定
     * 3. 当i遇到等于枢纽元的元素是是否应该停止, 当j遇到等于枢纽元的元素是是否应该停止
     */
    private static int[] quickSort(int[] nums) {
        quickSort(nums, 0, nums.length);
        return nums;
    }

    /**
     * 1. 左闭右开: [l,r)
     * l    l+1..... m......r-1
     * min  l+1.....max.....pivot
     * 2. 执行完median3之后, 应该需要做排序的区间变成[l+1,r-1)
     */
    private static int median3(int[] nums, int l, int r) {
        int m = (l + r) >> 1;
        // 1. 插入排序: m
        if (nums[m] < nums[l]) swap(nums, l, m);
        // 2. 插入排序: r-1
        if (nums[r - 1] < nums[l]) swap(nums, l, r - 1);
        if (nums[r - 1] < nums[m]) swap(nums, m, r - 1);
        // 3. 将pivot放入最后一个位置
        swap(nums, m, r - 1);

        return nums[r - 1];
    }

    /**
     * 左闭右开: [l,r)
     * #                            0   1   2   3   4   5   6   7   8   9   10  11
     * #                            4   3   2   6   5   4   6   2   7   2   7   4
     * # 1. [0，12), median3=6      4*  3   2   6   5   4  6*   2   7   2   7   4*
     * #              i=1, j=10     4   3   2   2*  5   4   6   2   7   6*  7   4
     * #              i=3, j=9      4   3   2  2   2*   4   6  5*   7   6   7   4
     * #              i=4, j=7      4   3   2  2   2    4   6*  5   7   6   7   4
     * #   处理pivot, i=j=6         4   3   2  2   2    4   4*  5   7   6   7   6     2.1.(1), i=j=6, 【需交换pivot】
     * # 2. [0,6), median3=3       2*   3   2  4*  2    4*  4   5   7   6   7   6
     * #              i=1, j=4     2    3   2  4   2*   4   4   5   7   6   7   6
     * #   处理pivot, i=j=4        2    3   2  4   2    4   4   5   7   6   7   6     2.1.(2), i=j=4, 【需要交换pivot】
     * # 3. [0,4), median3=2       2*   3  4*  2*  2    4   4   5   7   6   7   6
     * #              i=1, j=2     2   3*  4   2   2    4   4   5   7   6   7   6
     * #   交换pivot, i=j=1        2   2*  4  3*   2    4   4   5   7   6   7   6
     */
    private static void quickSort(int[] nums, int l, int r) {
        // 1. 至少3个元素
        if (l + 2 < r) {
            int pivot = median3(nums, l, r);
            // 执行完median3之后, 应该需要做排序的区间变成[l+1, r-1)
            int i = l + 1, j = r - 2;
            // 2 根据pivot分割
            // 2.1 寻找分割点: i右移j左移并交换, 直到相遇(最终i==j):
            // (1) nums[i]>pivot: i因为而停留, 【需交换pivot】
            //      ....(i/j)......(r-1)    --(交换pivot)-->  ...(i/j)......(r-1)
            //      ...(>pivot)...(pivot)  --(交换pivot)-->  ...(pivot)...(>pivot)
            //   拆分(i不需再排序): [l,i)/[i+1,r)
            // (2) nums[i]<=pivot: i因为等于j而停留, 【无需交换pivot】
            //      ..........(i/j),     (r-1)
            //      ........(<=pivot),  (pivot)
            //   拆分(i不需再排序): [l,i+1)/(i+1,r), 实际上右边为空(i+1==r)
            while (i < j) {
                // 里面还需要判断i<j
                while (nums[i] <= pivot && i < j) i++;
                while (nums[j] >= pivot && i < j) j--;
                swap(nums, i, j);
            }
            // 2.2 分割
            // 2.2.(1)
            if (nums[i] > pivot) {
                swap(nums, i, r - 1);
                quickSort(nums, l, i);
            }
            // 2.2.(2)
            else quickSort(nums, l, i + 1);
            quickSort(nums, i + 1, r);
        }
        // 2. 只有2个元素
        else if (l + 2 == r && nums[l] > nums[l + 1])
            swap(nums, l, l + 1);
    }
}

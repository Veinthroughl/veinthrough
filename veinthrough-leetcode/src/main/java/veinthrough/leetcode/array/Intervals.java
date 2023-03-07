package veinthrough.leetcode.array;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.api.array.Array;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第56题，合并区间，
 * 以数组 intervals 表示若干个区间的集合，其中单个区间为 intervals[i] = [starti, endi] 。
 * 请你合并所有重叠的区间，并返回一个不重叠的区间数组，该数组需恰好覆盖输入中的所有区间。
 *
 *
 * 第57题，插入区间，
 * 给你一个 无重叠的 ，按照区间起始端点排序的区间列表。
 * 在列表中插入一个新的区间，你需要确保列表中的区间仍然有序且不重叠（如果有必要的话，可以合并区间）。
 */
@SuppressWarnings("Duplicates")
@Slf4j
public class Intervals {
    @Test
    public void test56() {
        Stream.of(
                new int[][]{{1, 3}, {2, 6}, {8, 10}, {15, 18}}, // [[1, 6], [8, 10],[15, 18]]
                new int[][]{{1, 3}, {2, 6}, {5, 16}, {15, 18}}, // [1,18]
                new int[][]{{1, 4}, {4, 5}},                    // [[1, 5]]
                new int[][]{{1, 4}, {2, 3}})                    // [[1, 4]]
                .forEach(intervals -> log.info(methodLog(
                        "Merge before", Array.stringOf2DArray(intervals),
                        "Merged", Array.stringOf2DArray(mergeIntervals(intervals))
                )));
    }

    @Test
    public void test57() {
        int[][] intervals = new int[][]{{8, 10}, {12, 16}};
        log.info(methodLog("Merge " + Array.stringOf2DArray(intervals)));
        Stream.of(
                new int[]{7, 7},      // [[7,7],[8,10],[12,16]]
                new int[]{7, 11},     // [[7,11],[12,16]]
                new int[]{8, 11},     // [[8,11],[12,16]]
                new int[]{9, 11},     // [[8,11],[12,16]]
                new int[]{11, 11},    // [[8,10],[11,11],[12,16]]
                new int[]{11, 13},    // [[8,10],[11,16]]
                new int[]{11, 17},    // [[8,10],[11,17]]
                new int[]{13, 17},    // [[8,10],[12,17]]
                new int[]{7, 17},     // [[7,17]]
                new int[]{16, 18},    // [[8,10],[12,18]]
                new int[]{17, 18})    // [[8,10],[12,16],[17,18]]
                .forEach(newInterval -> log.info(methodLog(
                        "With " + Arrays.toString(newInterval),
                        Array.stringOf2DArray(insertInterval2(intervals, newInterval)))));
    }

    /**
     * 合并区间
     */
    private int[][] mergeIntervals(int[][] intervals) {
        int n;
        // boundary
        if ((n = intervals.length) == 1) return intervals;

        // 1. 按starti排序
        Arrays.sort(intervals, Comparator.comparing(x -> x[0]));

        // 2. merge
        int start, end;
        List<int[]> res = new LinkedList<>();
        start = intervals[0][0];
        end = intervals[0][1];
        for (int i = 1; i < n; i++) {
            // (1) can merge
            if (intervals[i][0] <= end) end = Math.max(end, intervals[i][1]);
                // (2) can't merge
            else {
                res.add(new int[]{start, end});
                start = intervals[i][0];
                end = intervals[i][1];
            }
        }
        res.add(new int[]{start, end});
        return res.toArray(new int[0][]);

    }


    private int[][] insertInterval2(int[][] intervals, int[] newInterval) {
        int n;
        // boundary
        if ((n = intervals.length) == 0) return new int[][]{{newInterval[0], newInterval[1]}};
        if (newInterval[1] < intervals[0][0]) {
            int[][] result = new int[n + 1][2];
            result[0] = newInterval;
            System.arraycopy(intervals, 0, result, 1, n);
            return result;
        }
        if (newInterval[0] > intervals[n - 1][1]) {
            int[][] result = new int[n + 1][2];
            System.arraycopy(intervals, 0, result, 0, n);
            result[n] = newInterval;
            return result;
        }

        // 1. 找到开始starti和结束索引endi
        boolean startInGap = false, endInGap = false;
        int starti = -2, endi = n; // 使用-2是为了不和-1冲突
        for (int i = 0; i < n; i++) {
            if (starti == -2) {
                if (newInterval[0] < intervals[i][0]) {
                    starti = i - 1;
                    startInGap = true;
                } else if (newInterval[0] <= intervals[i][1]) starti = i;
            }
            // 只有计算好istart之后才能计算iend
            if (starti != -2) {
                if (newInterval[1] < intervals[i][0]) {
                    endi = i - 1;
                    endInGap = true;
                    break;
                }
                if (newInterval[1] <= intervals[i][1]) {
                    endi = i;
                    break;
                }
            }
        }

        // 2. 根据开始/结束索引[starti,endi]来确定：
        // (1) 需要删除的索引区间
        // (2) 合并后的区间
        int delStart, delEnd; // 需要删除的索引区间
        int start, end; // 合并后的区间
        delStart = startInGap ? starti + 1 : starti;
        start = startInGap ? newInterval[0] : intervals[starti][0];
        delEnd = endi;
        end = endInGap || endi==n ? newInterval[1] : intervals[endi][1];

        // 3. 执行删除和合并区间
        List<int[]> res = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (i == delStart) res.add(new int[]{start, end}); // 合并区间加入
            if (i >= delStart && i <= delEnd) continue; // 删除, 不加入
            res.add(intervals[i]); // 原封不动地加入
        }

        return res.toArray(new int[0][]);
    }

    /**
     * 插入区间:
     * intervals               (starti,endi)                       (delstart,delEnd)/(start,end)
     * {[8,10],[12,16]}/[7,7]       (-1,-1)         2.1 -->         (0,-1)/(7,7)
     * {[8,10],[12,16]}/[7,11]      (-1,0)          2.2.1/2.3.2 --> (0,0)/(7,11)
     * {[8,10],[12,16]}/[8,11]      (0,0)           2.2.2/2.3.2 --> (0,0)/(8,11)
     * {[8,10],[12,16]}/[9,11]      (0,0)           2.2.2/2.3.2 --> (0,0)/(8,11)
     * {[8,10],[12,16]}/[11,11]     (0,0)           2.1         --> (1,-1)/(11,11)
     * {[8,10],[12,16]}/[11,13]     (0,1)           2.2.3/2.3.1 --> (1,1)/(11,16)
     * {[8,10],[12,16]}/[11,17]     (0,1)           2.2.3/2.3.2 --> (1,1)/(11,17)
     * {[8,10],[12,16]}/[13,17]     (1,1)           2.2.2/2.3.2 --> (1,1)/(12,17)
     * {[8,10],[12,16]}/[7, 17]     (-1,1)          2.2.1/2.3.2 --> (0,1)/(7,17)
     * {[8,10],[12,16]}/[16, 18]    (1,1)           2.2.2/2.3.2 --> (1,1)/(12,18)
     * {[8,10],[12,16]}/[17, 18]    (1,1)           2.2.3/2.3.2 --> (2,1)/(17,18)
     */
    private int[][] insertInterval(int[][] intervals, int[] newInterval) {
        int n;
        // boundary
        if ((n = intervals.length) == 0) return new int[][]{{newInterval[0], newInterval[1]}};
        if (newInterval[1] < intervals[0][0]) {
            int[][] result = new int[n + 1][2];
            result[0] = newInterval;
            System.arraycopy(intervals, 0, result, 1, n);
            return result;
        }
        if (newInterval[0] > intervals[n - 1][1]) {
            int[][] result = new int[n + 1][2];
            System.arraycopy(intervals, 0, result, 0, n);
            result[n] = newInterval;
            return result;
        }

        // 1. 找到开始starti和结束索引endi
        int starti = -2, endi = n - 1; // 使用-2是为了不和-1冲突
        for (int i = 0; i < n; i++) {
            if (starti == -2) {
                if (newInterval[0] < intervals[i][0]) starti = i - 1;
                else if (newInterval[0] <= intervals[i][1]) starti = i;
            }
            // 只有计算好istart之后才能计算iend
            if (starti != -2) {
                if (newInterval[1] < intervals[i][0]) {
                    endi = i - 1;
                    break;
                }
                if (newInterval[1] <= intervals[i][1]) {
                    endi = i;
                    break;
                }
            }
        }

        // 2. 根据开始/结束索引[starti,endi]来确定：
        // (1) 需要删除的索引区间
        // (2) 合并后的区间
        int delStart, delEnd; // 需要删除的索引区间
        int start, end; // 合并后的区间
        // 2.1 不需要删除区间
        // 其实istart==endi==-1的情况已经在boundary中考虑了
        if (starti == endi && (starti == -1 || newInterval[0] > intervals[starti][1])) {
            delStart = starti + 1;
            delEnd = -1; // 代表不需要删除
            start = newInterval[0];
            end = newInterval[1];
        } else {
            // 2.2 定位delStart/start
            // 2.2.1
            if (starti == -1) {
                delStart = 0;
                start = newInterval[0];
            }
            // 2.2.2
            else if (newInterval[0] <= intervals[starti][1]) {
                delStart = starti;
                start = intervals[starti][0];
            }
            // 2.2.3
            else {
                delStart = starti + 1;
                start = newInterval[0];
            }
            // 2.3 定位delEnd/end
            delEnd = endi;
            if (newInterval[1] <= intervals[endi][1]) end = intervals[endi][1]; // 2.3.1
            else end = newInterval[1]; // 2.3.2
        }

        // 3. 执行删除和合并区间
        List<int[]> res = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (i == delStart) res.add(new int[]{start, end}); // 合并区间加入
            if (i >= delStart && i <= delEnd) continue; // 删除, 不加入
            res.add(intervals[i]); // 原封不动地加入
        }

        return res.toArray(new int[0][]);
    }
}

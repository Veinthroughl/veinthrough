package veinthrough.leetcode.array;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static veinthrough.api.util.MethodLog.methodLog;

@Slf4j
public class CompleteCircle {
    @Test
    public void test134() {
        ImmutableMap.of(
                new int[]{2,4}, new int[]{3,4},
                new int[]{2, 3, 4}, new int[]{3, 4, 3},
                new int[]{1, 2, 3, 4, 5}, new int[]{3, 4, 5, 1, 2},
                new int[]{5, 8, 2, 8}, new int[]{6, 5, 6, 6})
                .forEach((gas, cost) -> log.info(
                        methodLog("" + canCompleteCircuit(gas, cost))));
    }

    private int canCompleteCircuit(int[] gas, int[] cost) {
        int n = gas.length;

        // i --> j(...)
        int sum;
        for (int i = 0, j = i, count = 1; i < n; i += count, count = 1, j = i) { // i=j+1
            if ((sum = gas[j] - cost[j]) >= 0) {
                //noinspection StatementWithEmptyBody
                for (j = (i + 1) % n;
                     count < n && (sum = sum + gas[j] - cost[j]) >= 0;
                     j = (j + 1) % n, count++)
                    ;
                // 回到起点还未计算
                if (count == n) return i;
            }
        }
        return -1;
    }
}

package veinthrough.leetcode.fsm;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 第65题：有效数字，
 * 有效数字（按顺序）可以分成以下几个部分：
 * 1. 一个 小数 或者 整数
 * 2. （可选）一个 'e' 或 'E' ，后面跟着一个 整数
 * > 小数（按顺序）可以分成以下几个部分：
 * （可选）一个符号字符（'+' 或 '-'）
 * 下述格式之一：
 * 至少一位数字，后面跟着一个点 '.'
 * 至少一位数字，后面跟着一个点 '.' ，后面再跟着至少一位数字
 * 一个点 '.' ，后面跟着至少一位数字
 * 给你一个字符串 s ，如果 s 是一个 有效数字 ，请返回 true 。
 * > 整数（按顺序）可以分成以下几个部分：
 * （可选）一个符号字符（'+' 或 '-'）
 * 至少一位数字
 * > 部分有效数字列举如下：
 * ["2", "0089", "-0.1", "+3.14", "4.", "-.9", "2e10", "-90E3", "3e+7", "+6e-1", "53.5e93", "-123.456e789"]
 * > 部分无效数字列举如下：
 * ["abc", "1a", "1e", "e3", "99e2.5", "--6", "-+3", "95a54e53"]
 */
@Slf4j
public class EffectiveNum {
    @Test
    public void test65() {
        Stream.of("-0.9e+5", "0", "e", ".")
                .forEach(str -> log.info(methodLog(
                        str, "" + effectiveNum(str))));
    }

    private static final int STATE_START = 0, STATE_SIGN = 1, STATE_INTEGRAL = 2, STATE_DOT = 3, STATE_DOT_WITHOUT_INT = 4;
    private static final int STATE_FRACTION = 5, STATE_E = 6, STATE_SIGN_EXP = 7, STATE_EXP = 8, STATE_ERROR = 9;
    private static final int SIGN = 0, DIGIT = 1, DOT = 2, E = 3, OTHER = 4;
    private int[][] trans = new int[][]{
            {STATE_SIGN, STATE_INTEGRAL, STATE_DOT_WITHOUT_INT, STATE_ERROR},
            {STATE_ERROR, STATE_INTEGRAL, STATE_DOT_WITHOUT_INT, STATE_ERROR},
            {STATE_ERROR, STATE_INTEGRAL, STATE_DOT, STATE_E},
            {STATE_ERROR, STATE_FRACTION, STATE_ERROR, STATE_E},
            {STATE_ERROR, STATE_FRACTION, STATE_ERROR, STATE_ERROR},
            {STATE_ERROR, STATE_FRACTION, STATE_ERROR, STATE_E},
            {STATE_SIGN_EXP, STATE_EXP, STATE_ERROR, STATE_ERROR},
            {STATE_ERROR, STATE_EXP, STATE_ERROR, STATE_ERROR},
            {STATE_ERROR, STATE_EXP, STATE_ERROR, STATE_ERROR}
    };

    /**
     * 方法：优先自动状态机，
     * 状态图见文档说明
     */
    private boolean effectiveNum(String s) {
        int state = STATE_START;
        int mark;
        for (char ch : s.toCharArray()) {
            mark = charResolve(ch);
            state = mark == OTHER ? STATE_ERROR : trans[state][mark];
            if (state == STATE_ERROR) return false;
        }
        return state == STATE_INTEGRAL || state == STATE_DOT || state == STATE_FRACTION || state == STATE_EXP;
    }

    private int charResolve(char ch) {
        if (ch == '+' || ch == '-') return SIGN;
        if (ch >= '0' && ch <= '9') return DIGIT;
        if (ch == '.') return DOT;
        if (ch == 'e' || ch == 'E') return E;
        return OTHER;
    }
}

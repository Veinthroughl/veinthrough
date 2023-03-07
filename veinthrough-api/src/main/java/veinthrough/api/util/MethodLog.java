package veinthrough.api.util;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;

/**
 * @author veinthrough
 * Make log string for method.
 */
@SuppressWarnings("SameParameterValue")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MethodLog {
    private static final String VARIABLES_PRE = "Details: {";
    private static final String SPACE_PRE = "    ";
    private static final String DELIM_MESSAGE = ", ";
    private static final String DELIM_NEXT_LINE = ",\n" + SPACE_PRE;
    private static final String DELIM_NEXT_VARIABLE = DELIM_NEXT_LINE +
            String.format("%-" + VARIABLES_PRE.length() + "s", " ");
    //    private static final String DELIM_END_LINE = ".";
    private static final String END = "\n";
    private static final int STACK_DEPTH_CALLER = 1;
    // getMethodString:[3] -> private _methodLog:[2] -> caller:[1]
    // getMethodString:[4] -> private _methodLog:[3] -> public exceptionLog/methodLog:[2] -> caller:[1]
    private static final int STACK_DEPTH_METHOD = STACK_DEPTH_CALLER + 2;

    private static String _methodLog(int depthAugment, String... vars) {
        if (vars.length == 0) {
            return getMethodString(STACK_DEPTH_METHOD + depthAugment) + END;
        } else if (vars.length == 1) {
            return getMethodString(STACK_DEPTH_METHOD + depthAugment)
                    + DELIM_MESSAGE + vars[0]
                    + END;
        } else if (vars.length % 2 != 0) {
            return getMethodString(STACK_DEPTH_METHOD + depthAugment)
                    + DELIM_MESSAGE + vars[0] + DELIM_NEXT_LINE
                    + getVarsString(Arrays.copyOfRange(vars, 1, vars.length))
                    + END;
        } else {
            return getMethodString(STACK_DEPTH_METHOD + depthAugment) + DELIM_NEXT_LINE
                    + getVarsString(vars)
                    + END;
        }
    }

    public static String methodLog(String... vars) {
        return _methodLog(1, vars);
    }

    private static String _methodLog(int depthAugment, int step, String... vars) {
        if (vars.length == 0) {
            return getMethodString(STACK_DEPTH_METHOD + depthAugment) + getStepString(step) + END;
        } else if (vars.length == 1) {
            return getMethodString(STACK_DEPTH_METHOD + depthAugment) + getStepString(step)
                    + DELIM_MESSAGE + vars[0]
                    + END;
        } else if (vars.length % 2 != 0) {
            return getMethodString(STACK_DEPTH_METHOD + depthAugment)
                    + getStepString(step) + DELIM_MESSAGE + vars[0] + DELIM_NEXT_LINE
                    + getVarsString(Arrays.copyOfRange(vars, 1, vars.length))
                    + END;
        } else {
            return getMethodString(STACK_DEPTH_METHOD + depthAugment)
                    + getStepString(step) + DELIM_NEXT_LINE
                    + getVarsString(vars)
                    + END;
        }
    }

    public static String methodLog(int step, String... vars) {
        return _methodLog(1, step, vars);
    }

    public static String exceptionLog(Throwable e, String... vars) {
        if (vars.length == 0) {
            return _methodLog(1, exceptionString(e));
        } else if (vars.length == 1) {
            return _methodLog(1, exceptionString(e, vars[0]));
        } else {
            return _methodLog(1,
                    ImmutableList.<String>builder()
                            .add(exceptionString(e, vars[0]))
                            .add(Arrays.copyOfRange(vars, 1, vars.length))
                            .build()
                            .toArray(new String[0]));
        }
    }

    public static String exceptionLog(int step, Throwable e, String... vars) {
        if (vars.length == 0) {
            return _methodLog(1, step, exceptionString(e));
        } else if (vars.length == 1) {
            return _methodLog(1, step, exceptionString(e, vars[0]));
        } else {
            return _methodLog(1, step,
                    ImmutableList.<String>builder()
                            .add(exceptionString(e, vars[0]))
                            .add(Arrays.copyOfRange(vars, 1, vars.length))
                            .build()
                            .toArray(new String[0]));
        }
    }

    private static String exceptionString(Throwable e) {
        return String.format("%s(%s)", e.getClass().getSimpleName(), e.getMessage());
    }

    private static String exceptionString(Throwable e, String extraMessage) {
        return exceptionString(e) + DELIM_MESSAGE + extraMessage;
    }

    // public for comment-reference
    public static String getMethodString(int depth) {
        return String.format("[IN]%s()",
                Thread.currentThread().getStackTrace()[depth].getMethodName());
    }

    private static String getStepString(int step) {
        return String.format("[STEP %03d]", step);
    }

    private static String getVarsString(String... vars) {
        int length = vars.length;
        if (length != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(VARIABLES_PRE);
            for (int i = 0; i < length; i += 2) {
                sb.append(vars[i]).append(": ");
                if (i + 1 < length) sb.append(vars[i + 1]);
                if (i + 2 < length) sb.append(DELIM_NEXT_VARIABLE);
            }
            sb.append("}");
            return sb.toString();
        } else {
            return "";
        }
    }
}

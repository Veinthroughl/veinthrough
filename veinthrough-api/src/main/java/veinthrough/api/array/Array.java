package veinthrough.api.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Array {
    public static <T> String stringOf2DArray(T[][] array) {
        StringBuilder str = new StringBuilder("[");
        for (T[] sub : array)
            str.append("\n").append(Arrays.toString(sub));
        return str.append("]").toString();
    }

    public static String stringOf2DArray(char[][] array) {
        StringBuilder str = new StringBuilder("[");
        for (char[] sub : array)
            str.append("\n").append(Arrays.toString(sub));
        return str.append("]").toString();
    }

    public static String stringOf2DArray(int[][] array) {
        StringBuilder str = new StringBuilder("[");
        for (int[] sub : array)
            str.append("\n").append(Arrays.toString(sub));
        return str.append("]").toString();
    }

    /**
     * copy a 2-dimension array
     */
    public static int[][] copy(int[][] array) {
        // boundary
        if (array == null) return null;
        //
        int[][] copy = new int[array.length][];
        for (int i = 0; i < array.length; i++)
            copy[i] = Arrays.copyOf(array[i], array[i].length);
        return copy;
    }

    /**
     * copy a 2-dimension array
     */
    public static <T> T[][] copy(T[][] array) {
        // boundary
        if (array == null) return null;
        List<T[]> copy = new ArrayList<>(array.length);
        for (T[] tArray : array) copy.add(Arrays.copyOf(tArray, tArray.length));
        //noinspection unchecked
        return (T[][]) copy.toArray();
    }

    /**
     * copy a 2-dimension array
     */
    public static char[][] copy(char[][] array) {
        // boundary
        if (array == null) return null;
        //
        char[][] copy = new char[array.length][];
        for (int i = 0; i < array.length; i++)
            copy[i] = Arrays.copyOf(array[i], array[i].length);
        return copy;
    }

    public static void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    public static <T> void swap(T[] arr, int i, int j) {
        T temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static boolean equals(char[][] arr1, int start1, char[][] arr2, int start2, int len) {
        int n1 = arr1.length, n2 = arr2.length;
        int i, j, k;
        // boundary
        // 1. null/null
        if (start1 >= n1 && start2 >= n2) return true;
        if (start1 >= n1 || start2 >= n2) return false;

        // 2.
        if (n1 - start1 >= len && n2 - start2 >= len) {
            for (i = start1, j = start2, k = 0; k < len; i++, j++, k++) {
                if (arr1[i] != arr2[j]) return false;
            }
            return true;
        }
        return equals(arr1, start1, arr2, start2);
    }

    public static boolean equals(char[][] arr1, int start1, char[][] arr2, int start2) {
        int n1 = arr1.length, n2 = arr2.length;
        int i, j;
        // boundary
        // 1. null/null
        if (start1 >= n1 && start2 >= n2) return true;
        if (start1 >= n1 || start2 >= n2) return false;

        // 2.
        for (i = start1, j = start2; i < n1 && j < n2; i++, j++) {
            if (arr1[i] != arr2[j]) return false;
        }
        // 3. 
        return i == n1 && j == n2;
    }

    /**
     * This method attempts to grow an source by allocating source new source and copying all elements.
     *
     * @param source    the array to grow
     * @param newLength the new length
     * @return larger array that contains all elements of source. However, the returned source has
     *         type Object[], not the same type as source
     */
    // 1. 不能接受原始(primitive)类型, 如int[]
    // 2. 返回的类型Object[] can't be converted to other array, like String[]
    // 从定义开始的Object[]不能转化成其他数组
    // 除非是object[]作为中间状态, 从其他数组转换成object[]再转换成其他数组
    // 如String[] -> Object[] -> String[]
    // 因为Java数组会记住每个元素的类型
    @Deprecated
    public static Object[] badCopyOf(Object[] source, int newLength) {
        Object[] newArray = new Object[newLength];
        System.arraycopy(source, 0, newArray, 0, Math.min(source.length, newLength));
        return newArray;
    }

    /**
     * This method grows an array by allocating source new array of the same type and
     * copying all elements.
     *
     * @param source the array to grow. This can be an object array or source primitive
     *               type array
     * @return larger array that contains all elements of source.
     */
    // 1. 参数和返回类型都为Object而非Object[]
    // 2. use reflect
    // [?] @SuppressWarnings
    @SuppressWarnings("SuspiciousSystemArraycopy")
    public static Object copyOf(Object source, int newLength) {
        Class cl = source.getClass();
        if (!cl.isArray()) return null;
        Class componentType = cl.getComponentType();
        int length = java.lang.reflect.Array.getLength(source);
        // new an array by reflect
        Object newArray = java.lang.reflect.Array.newInstance(componentType, newLength);
        System.arraycopy(source, 0, newArray, 0, Math.min(length, newLength));
        return newArray;
    }
}

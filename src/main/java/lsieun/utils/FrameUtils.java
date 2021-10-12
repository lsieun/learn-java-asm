package lsieun.utils;

import java.util.List;

public class FrameUtils {
    private static final String START = "{";
    private static final String STOP = "}";
    private static final String EMPTY = "{}";
    private static final String SEPARATOR = "|";

    public static <T> String toLine(List<T> localList, List<T> stackList) {
        String locals_str = toLine(localList);
        String stack_str = toLine(stackList);
        return String.format("%s %s %s", locals_str, SEPARATOR, stack_str);
    }

    private static <T> String toLine(List<T> list) {
        if (list == null || list.size() == 0) return EMPTY;
        int size = list.size();

        StringBuilder sb = new StringBuilder();
        sb.append(START);
        for (int i = 0; i < size - 1; i++) {
            T item = list.get(i);
            sb.append(item).append(", ");
        }
        sb.append(list.get(size - 1));
        sb.append(STOP);
        return sb.toString();
    }
}

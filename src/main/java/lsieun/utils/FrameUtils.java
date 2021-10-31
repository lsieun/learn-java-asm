package lsieun.utils;

import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FrameUtils {
    private static final String START = "{";
    private static final String STOP = "}";
    private static final String EMPTY = "{}";
    private static final String SEPARATOR = "|";

    public static <V extends Value, T> String getFrameLine(Frame<V> f, Function<V, T> func) {
        if (f == null) {
            return toLine(null, (List)null);
        }

        List<Object> localList = new ArrayList<>();
        for (int i = 0; i < f.getLocals(); ++i) {
            V localValue = f.getLocal(i);
            if (func == null) {
                localList.add(localValue);
            }
            else {
                T item = func.apply(localValue);
                localList.add(item);
            }

        }

        List<Object> stackList = new ArrayList<>();
        for (int j = 0; j < f.getStackSize(); ++j) {
            V stackValue = f.getStack(j);
            if (func == null) {
                stackList.add(stackValue);
            }
            else {
                T item = func.apply(stackValue);
                stackList.add(item);
            }
        }

        return toLine(localList, stackList);
    }

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

package lsieun.utils;

import lsieun.asm.analysis.InsnText;
import lsieun.cst.Const;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FrameUtils {
    private static final String START = "{";
    private static final String STOP = "}";
    private static final String EMPTY = "{}";
    private static final String SEPARATOR = "|";

    public static  <V extends Value, T> void printFrames(String owner, MethodNode mn, Analyzer<V> analyzer, Function<V, T> func) throws AnalyzerException {
        System.out.println(mn.name + ":" + mn.desc);

        //（1）获取Instruction信息
        InsnList instructions = mn.instructions;
        int size = instructions.size();
        InsnText insnText = new InsnText();

        //（2）获取Frame信息
        Frame<V>[] frames = analyzer.analyze(owner, mn);

        //（3）结合Instruction信息和Frame信息
        // NOTE: 右对齐，使用“%36s”；左对齐，使用“%-36s”。
        String format = "%03d:    %-36s    %s";
        for (int index = 0; index < size; index++) {
            AbstractInsnNode node = instructions.get(index);
            List<String> nodeLines = insnText.toLines(node);

            Frame<V> f = frames[index];
            String frameLine = FrameUtils.getFrameLine(f, func);

            String firstLine = String.format(format, index, nodeLines.get(0), frameLine);
            System.out.println(firstLine);
            for (int i = 1; i < nodeLines.size(); i++) {
                String item = nodeLines.get(i);
                String line = String.format("%4s    %-36s", "", item);
                System.out.println(line);
            }
        }

        System.out.println(Const.DIVISION_LINE);
        System.out.println();
    }

    public static <V extends Value, T> String getFrameLine(Frame<V> f, Function<V, T> func) {
        if (f == null) {
            return toLine(null, null);
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

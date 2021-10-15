package lsieun.utils;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.SourceValue;

import java.util.Arrays;

public class ValueUtils {
    public static String fromBasicValue2String(BasicValue basicValue) {
        String descriptor = basicValue.toString();
        return DescriptorUtils.simplify(descriptor);
    }

    public static String fromSourceValue2String(MethodNode mn, SourceValue sourceValue) {
        int size = sourceValue.insns.size();
        int[] array = new int[size];
        int i = 0;
        for (AbstractInsnNode node : sourceValue.insns) {
            array[i] = mn.instructions.indexOf(node);
            i++;
        }
        Arrays.sort(array);
        return Arrays.toString(array);
    }

    public static String fromSourceValue2Index(MethodNode mn, SourceValue sourceValue) {
        int size = sourceValue.insns.size();
        int[] array = new int[size];
        int i = 0;
        for (AbstractInsnNode node : sourceValue.insns) {
            array[i] = mn.instructions.indexOf(node);
            i++;
        }
        Arrays.sort(array);
        return Arrays.toString(array);
    }
}

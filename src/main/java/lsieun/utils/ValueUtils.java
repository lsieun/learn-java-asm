package lsieun.utils;

import lsieun.asm.analysis.nullability.Nullability;
import lsieun.asm.analysis.nullability.NullabilityInterpreter;
import lsieun.asm.analysis.nullability.NullabilityValue;
import org.objectweb.asm.Type;
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

    public static String fromNullabilityValue2String(NullabilityValue value) {
        String firstPart;

        Type type = value.getType();
        if (value == NullabilityInterpreter.UNINITIALIZED_VALUE) {
            return ".";
        }
        else if (value == NullabilityInterpreter.RETURN_ADDRESS_VALUE) {
            return "address";
        }
        else if (type.getSort() == Type.INT) {
            firstPart = "int";
        }
        else if (type.getSort() == Type.FLOAT) {
            firstPart = "float";
        }
        else if (type.getSort() == Type.LONG) {
            firstPart = "long";
        }
        else if (type.getSort() == Type.DOUBLE) {
            firstPart = "double";
        }
        else if (value.isReference()) {
            firstPart = type.getClassName();
            int index = firstPart.lastIndexOf(".");
            if (index != -1) {
                firstPart = firstPart.substring(index + 1);
            }
        }
        else {
            firstPart = String.format("illegal value: %s", type);
        }

        String secondPart;
        Nullability state = value.getState();
        switch (state) {
            case UNKNOWN:
                secondPart = "";
                break;
            case NOT_NULL:
                secondPart = ":NOT-NULL";
                break;
            case NULL:
                secondPart = ":NULL";
                break;
            case NULLABLE:
                secondPart = ":NULLABLE";
                break;
            default:
                secondPart = ":IMPOSSIBLE";
        }
        return firstPart + secondPart;
    }
}

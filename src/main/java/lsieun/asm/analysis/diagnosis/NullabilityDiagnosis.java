package lsieun.asm.analysis.diagnosis;

import lsieun.asm.analysis.nullability.Nullability;
import lsieun.asm.analysis.nullability.NullabilityAnalyzer;
import lsieun.asm.analysis.nullability.NullabilityInterpreter;
import lsieun.asm.analysis.nullability.NullabilityValue;
import lsieun.trove.TIntArrayList;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;

import java.util.Arrays;

import static org.objectweb.asm.Opcodes.*;

public class NullabilityDiagnosis {
    public static int[] diagnose(String className, MethodNode mn) throws AnalyzerException {
        // 第一步，获取Frame信息
        Analyzer<NullabilityValue> analyzer = new NullabilityAnalyzer(new NullabilityInterpreter(Opcodes.ASM9));
        Frame<NullabilityValue>[] frames = analyzer.analyze(className, mn);

        // 第二步，判断是否为null或maybe-null，收集数据
        TIntArrayList intArrayList = new TIntArrayList();
        InsnList instructions = mn.instructions;
        int size = instructions.size();
        for (int i = 0; i < size; i++) {
            AbstractInsnNode insn = instructions.get(i);
            if (frames[i] != null) {
                NullabilityValue value = getTarget(insn, frames[i]);
                if (value == null) continue;
                if (value.getState() == Nullability.NULL || value.getState() == Nullability.NULLABLE) {
                    intArrayList.add(i);
                }
            }
        }

        // 第三步，将结果转换成int[]形式
        int[] array = intArrayList.toNativeArray();
        Arrays.sort(array);
        return array;
    }

    private static NullabilityValue getTarget(AbstractInsnNode insn, Frame<NullabilityValue> frame) {
        int opcode = insn.getOpcode();
        switch (opcode) {
            case GETFIELD:
            case ARRAYLENGTH:
            case MONITORENTER:
            case MONITOREXIT:
                return getStackValue(frame, 0);
            case PUTFIELD:
                return getStackValue(frame, 1);
            case INVOKEVIRTUAL:
            case INVOKESPECIAL:
            case INVOKEINTERFACE:
                String desc = ((MethodInsnNode) insn).desc;
                return getStackValue(frame, Type.getArgumentTypes(desc).length);

        }
        return null;
    }

    private static NullabilityValue getStackValue(Frame<NullabilityValue> frame, int index) {
        int top = frame.getStackSize() - 1;
        return index <= top ? frame.getStack(top - index) : null;
    }
}

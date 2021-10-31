package lsieun.asm.analysis.diagnosis;

import lsieun.asm.analysis.nullability.NullDeferenceInterpreter;
import lsieun.trove.TIntArrayList;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.*;

import java.util.Arrays;

import static org.objectweb.asm.Opcodes.*;

public class NullDereferenceDiagnosis {
    public static int[] diagnose(String owner, MethodNode mn) throws AnalyzerException {
        // 第一步，获取Frame信息
        Analyzer<BasicValue> analyzer = new Analyzer<>(new NullDeferenceInterpreter(ASM9));
        Frame<BasicValue>[] frames = analyzer.analyze(owner, mn);

        // 第二步，判断是否为null或maybe-null，收集数据
        TIntArrayList intArrayList = new TIntArrayList();
        InsnList instructions = mn.instructions;
        int size = instructions.size();
        for (int i = 0; i < size; i++) {
            AbstractInsnNode insn = instructions.get(i);
            if (frames[i] != null) {
                Value value = getTarget(insn, frames[i]);
                if (value == NullDeferenceInterpreter.NULL_VALUE || value == NullDeferenceInterpreter.MAYBE_NULL_VALUE) {
                    intArrayList.add(i);
                }
            }
        }

        // 第三步，将结果转换成int[]形式
        int[] array = intArrayList.toNativeArray();
        Arrays.sort(array);
        return array;
    }

    private static BasicValue getTarget(AbstractInsnNode insn, Frame<BasicValue> frame) {
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

    private static BasicValue getStackValue(Frame<BasicValue> frame, int index) {
        int top = frame.getStackSize() - 1;
        return index <= top ? frame.getStack(top - index) : null;
    }
}

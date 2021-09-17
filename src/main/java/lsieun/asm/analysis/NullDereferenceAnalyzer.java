package lsieun.asm.analysis;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.*;

import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class NullDereferenceAnalyzer {
    public List<AbstractInsnNode> findNullDereferences(String owner, MethodNode mn) throws AnalyzerException {
        List<AbstractInsnNode> result = new ArrayList<>();
        Analyzer<BasicValue> analyzer = new Analyzer<>(new IsNullInterpreter(ASM9));
        Frame<BasicValue>[] frames = analyzer.analyze(owner, mn);
        AbstractInsnNode[] insns = mn.instructions.toArray();
        for (int i = 0; i < insns.length; i++) {
            AbstractInsnNode insn = insns[i];
            if (frames[i] != null) {
                Value v = getTarget(insn, frames[i]);
                if (v == IsNullInterpreter.NULL_VALUE || v == IsNullInterpreter.MAYBE_NULL_VALUE) {
                    result.add(insn);
                }
            }
        }
        return result;
    }

    private static BasicValue getTarget(AbstractInsnNode insn, Frame<BasicValue> f) {
        int opcode = insn.getOpcode();
        switch (opcode) {
            case GETFIELD:
            case ARRAYLENGTH:
            case MONITORENTER:
            case MONITOREXIT:
                return getStackValue(f, 0);
            case PUTFIELD:
                return getStackValue(f, 1);
            case INVOKEVIRTUAL:
            case INVOKESPECIAL:
            case INVOKEINTERFACE:
                String desc = ((MethodInsnNode) insn).desc;
                return getStackValue(f, Type.getArgumentTypes(desc).length);

        }
        return null;
    }

    private static BasicValue getStackValue(Frame<BasicValue> f, int index) {
        int top = f.getStackSize() - 1;
        return index <= top ? f.getStack(top - index) : null;
    }
}

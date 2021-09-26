package lsieun.asm.analysis.nullability;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.*;

import java.util.ArrayList;
import java.util.List;

public class NullabilityAnalyzer extends Analyzer<NullabilityValue> {
    public NullabilityAnalyzer(Interpreter<NullabilityValue> interpreter) {
        super(interpreter);
    }

    @Override
    protected Frame<NullabilityValue> newFrame(Frame<? extends NullabilityValue> frame) {
        return new NullabilityFrame((NullabilityFrame) frame);
    }

    @Override
    protected Frame<NullabilityValue> newFrame(int numLocals, int numStack) {
        return new NullabilityFrame(numLocals, numStack);
    }

    public List<AbstractInsnNode> findNullDereferences(MethodNode mn) throws AnalyzerException {
        List<AbstractInsnNode> result = new ArrayList<>();
//        Analyzer<NullabilityValue> analyzer = new Analyzer<>(new IsNullInterpreter(ASM9));
//        Frame<NullabilityValue>[] frames = analyzer.analyze(owner, mn);
        Frame<NullabilityValue>[] frames = getFrames();
        AbstractInsnNode[] insnNodes = mn.instructions.toArray();
        for (int i = 0; i < insnNodes.length; i++) {
            AbstractInsnNode insn = insnNodes[i];
            if (frames[i] != null) {
                Value v = getTarget(insn, frames[i]);
                if (v == NullabilityValue.NULL_VALUE || v == NullabilityValue.NULLABLE_VALUE) {
                    result.add(insn);
                }
            }
        }
        return result;
    }

    private static NullabilityValue getTarget(AbstractInsnNode insn, Frame<NullabilityValue> f) {
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

    private static NullabilityValue getStackValue(Frame<NullabilityValue> f, int index) {
        int top = f.getStackSize() - 1;
        return index <= top ? f.getStack(top - index) : null;
    }
}

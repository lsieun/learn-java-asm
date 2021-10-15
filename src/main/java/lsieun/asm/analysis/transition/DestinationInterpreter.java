package lsieun.asm.analysis.transition;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.SourceValue;

import java.util.HashSet;
import java.util.List;

public class DestinationInterpreter extends Interpreter<SourceValue> implements Opcodes {
    public DestinationInterpreter() {
        super(ASM9);
        if (getClass() != DestinationInterpreter.class) {
            throw new IllegalStateException();
        }
    }

    protected DestinationInterpreter(final int api) {
        super(api);
    }

    @Override
    public SourceValue newValue(Type type) {
        if (type == Type.VOID_TYPE) {
            return null;
        }
        return new SourceValue(type == null ? 1 : type.getSize(), new HashSet<>());
    }

    @Override
    public SourceValue newOperation(AbstractInsnNode insn) {
        int size;
        switch (insn.getOpcode()) {
            case LCONST_0:
            case LCONST_1:
            case DCONST_0:
            case DCONST_1:
                size = 2;
                break;
            case LDC:
                Object value = ((LdcInsnNode) insn).cst;
                size = value instanceof Long || value instanceof Double ? 2 : 1;
                break;
            case GETSTATIC:
                size = Type.getType(((FieldInsnNode) insn).desc).getSize();
                break;
            default:
                size = 1;
                break;
        }
        return new SourceValue(size, new HashSet<>());
    }

    @Override
    public SourceValue copyOperation(AbstractInsnNode insn, SourceValue value) throws AnalyzerException {
        int opcode = insn.getOpcode();
        if (opcode >= ISTORE && opcode <= ASTORE) {
            value.insns.add(insn);
        }

        return new SourceValue(value.getSize(), new HashSet<>());
    }

    @Override
    public SourceValue unaryOperation(AbstractInsnNode insn, SourceValue value) throws AnalyzerException {
        value.insns.add(insn);

        int size;
        switch (insn.getOpcode()) {
            case LNEG:
            case DNEG:
            case I2L:
            case I2D:
            case L2D:
            case F2L:
            case F2D:
            case D2L:
                size = 2;
                break;
            case GETFIELD:
                size = Type.getType(((FieldInsnNode) insn).desc).getSize();
                break;
            default:
                size = 1;
                break;
        }
        return new SourceValue(size, new HashSet<>());
    }

    @Override
    public SourceValue binaryOperation(AbstractInsnNode insn, SourceValue value1, SourceValue value2) throws AnalyzerException {
        value1.insns.add(insn);
        value2.insns.add(insn);

        int size;
        switch (insn.getOpcode()) {
            case LALOAD:
            case DALOAD:
            case LADD:
            case DADD:
            case LSUB:
            case DSUB:
            case LMUL:
            case DMUL:
            case LDIV:
            case DDIV:
            case LREM:
            case DREM:
            case LSHL:
            case LSHR:
            case LUSHR:
            case LAND:
            case LOR:
            case LXOR:
                size = 2;
                break;
            default:
                size = 1;
                break;
        }
        return new SourceValue(size, new HashSet<>());
    }

    @Override
    public SourceValue ternaryOperation(AbstractInsnNode insn, SourceValue value1, SourceValue value2, SourceValue value3) throws AnalyzerException {
        value1.insns.add(insn);
        value2.insns.add(insn);
        value3.insns.add(insn);

        return new SourceValue(1, new HashSet<>());
    }

    @Override
    public SourceValue naryOperation(AbstractInsnNode insn, List<? extends SourceValue> values) throws AnalyzerException {
        if (values != null) {
            for (SourceValue v : values) {
                v.insns.add(insn);
            }
        }

        int size;
        int opcode = insn.getOpcode();
        if (opcode == MULTIANEWARRAY) {
            size = 1;
        }
        else if (opcode == INVOKEDYNAMIC) {
            size = Type.getReturnType(((InvokeDynamicInsnNode) insn).desc).getSize();
        }
        else {
            size = Type.getReturnType(((MethodInsnNode) insn).desc).getSize();
        }
        return new SourceValue(size, new HashSet<>());
    }

    @Override
    public void returnOperation(AbstractInsnNode insn, SourceValue value, SourceValue expected) throws AnalyzerException {
        // Nothing to do.
    }

    @Override
    public SourceValue merge(final SourceValue value1, final SourceValue value2) {
        return new SourceValue(Math.min(value1.size, value2.size), new HashSet<>());
    }
}

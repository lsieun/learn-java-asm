package lsieun.asm.analysis.state;

import lsieun.cst.Const;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.Value;

import java.util.Arrays;

// FIXME: 我觉得，可能还是有处理的不对的地方
// TODO: 它有什么样的应用呢？
// NOTE: 如果只是出栈操作，不需要做任何处理。
public class LocalStackStateFrame<V extends Value> extends Frame<V> {
    public static final int NOT_EXISTED = -1;

    private final int[] localStackStates;

    public LocalStackStateFrame(int numLocals, int numStack) {
        super(numLocals, numStack);
        int length = numLocals + numStack;
        this.localStackStates = new int[length];
        initLocalStackStates();
    }

    public LocalStackStateFrame(LocalStackStateFrame<? extends V> frame) {
        super(frame);
        int length = getMaxLength();
        this.localStackStates = new int[length];
        initLocalStackStates();
        System.arraycopy(frame.localStackStates, 0, this.localStackStates, 0, length);
    }

    // region getter and setter
    public int[] getLocalStackStates() {
        return localStackStates;
    }

    public int getMaxLength() {
        return getLocals() + getMaxStackSize();
    }

    public int getLength() {
        return getLocals() + getStackSize();
    }

    public int getStackTopIndex() {
        return getLength() - 1;
    }

    public int getLocalStackState(int index) {
        checkIndex(index);
        return localStackStates[index];
    }

    public void setLocalStackState(int index, int state) {
        checkIndex(index);
        checkState(state);

        localStackStates[index] = state;
    }
    // endregion

    // region 操作localStackStates字段的相关方法
    private void initLocalStackStates() {
        int length = getMaxLength();
        for (int i = 0; i < length; i++) {
            setLocalStackState(i, NOT_EXISTED);
        }
    }

    private void pushStackTopState(int fromIndex) {
        int toIndex = getStackTopIndex() + 1;
        updateLocalStackState(fromIndex, toIndex);
    }

    private void updateLocalStackState(int fromIndex, int toIndex) {
        sortLocalStackStates();

        int fromState;
        if (fromIndex == NOT_EXISTED) {
            fromState = NOT_EXISTED;
        }
        else {
            fromState = getLocalStackState(fromIndex);
        }

        if (fromState == NOT_EXISTED) {
            fromState = fromIndex;
        }
        setLocalStackState(toIndex, fromState);

        sortLocalStackStates();
    }

    private void swapLocalStackState(int fromIndex, int toIndex) {
        sortLocalStackStates();

        int fromState = getLocalStackState(fromIndex);
        int toState = getLocalStackState(toIndex);

        setLocalStackState(fromIndex, toState);
        setLocalStackState(toIndex, fromState);

        sortLocalStackStates();
    }

    private void invalidLocalStackValue(int index) {
        sortLocalStackStates();

        int state = getLocalStackState(index);
        if (state == NOT_EXISTED) {
            int firstIndex = NOT_EXISTED;
            int length = getMaxLength();
            for (int i = index + 1; i < length; i++) {
                int currentState = getLocalStackState(i);
                if (currentState == index) {
                    firstIndex = i;
                    break;
                }
            }
            if (firstIndex != NOT_EXISTED) {
                setLocalStackState(firstIndex, NOT_EXISTED);
                for (int i = firstIndex + 1; i < length; i++) {
                    int currentState = getLocalStackState(i);
                    if (currentState == index) {
                        setLocalStackState(i, firstIndex);
                    }
                }
            }
        }


        setLocalStackState(index, NOT_EXISTED);
        sortLocalStackStates();
    }

    private void sortLocalStackStates() {
        int length = getMaxLength();
        for (int i = 0; i < length; i++) {
            int state = getLocalStackState(i);
            if (state == NOT_EXISTED) continue;
            if (state >= i) {
                setLocalStackState(i, NOT_EXISTED);
            }

            for (int j = i + 1; j < length; j++) {
                int currentState = getLocalStackState(j);
                if (state == currentState) {
                    setLocalStackState(j, i);
                }
            }
        }

        assert !Const.DEBUG || isSorted();
    }

    private boolean isSorted() {
        int length = getMaxLength();
        for (int i = 0; i < length; i++) {
            int state = getLocalStackState(i);
            if (state >= i) {
                return false;
            }
        }
        return true;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= getMaxLength()) {
            String message = String.format("index = %d, %s", index, getFrameSize());
            throw new IllegalArgumentException(message);
        }
    }

    private void checkState(int state) {
        if (state < NOT_EXISTED || state >= getMaxLength()) {
            String message = String.format("invalid state: %d", state);
            throw new IllegalArgumentException(message);
        }
    }

    private String getFrameSize() {
        return String.format("maxLocals: %d, maxStack: %d, current stack: %s", getLocals(), getMaxStackSize(), getStackSize());
    }

    // TODO: 这个方法是否真的需要
    public void printLocalStackStates() {
        String str = Arrays.toString(localStackStates);
        System.out.println(str);
    }
    // endregion

    @SuppressWarnings("Duplicate")
    @Override
    public void execute(AbstractInsnNode insn, Interpreter<V> interpreter) throws AnalyzerException {
        // 首先，处理自己的代码逻辑
        int opcode = insn.getOpcode();
        switch (opcode) {
            case Opcodes.NOP:
                break;
            case Opcodes.ACONST_NULL:
            case Opcodes.ICONST_M1:
            case Opcodes.ICONST_0:
            case Opcodes.ICONST_1:
            case Opcodes.ICONST_2:
            case Opcodes.ICONST_3:
            case Opcodes.ICONST_4:
            case Opcodes.ICONST_5:
            case Opcodes.LCONST_0:
            case Opcodes.LCONST_1:
            case Opcodes.FCONST_0:
            case Opcodes.FCONST_1:
            case Opcodes.FCONST_2:
            case Opcodes.DCONST_0:
            case Opcodes.DCONST_1:
            case Opcodes.BIPUSH:
            case Opcodes.SIPUSH: {
                // operand stack入栈：1
                pushStackTopState(NOT_EXISTED);
                break;
            }
            case Opcodes.LDC: {
                // NOTE: 如果加载的long和double类型的数据，在operand stack上占用两个位置
                // operand stack入栈：1或2
                int topIndex = getStackTopIndex();
                updateLocalStackState(NOT_EXISTED, topIndex + 1);
                LdcInsnNode ldcInsnNode = (LdcInsnNode) insn;
                Object cst = ldcInsnNode.cst;
                if ((cst instanceof Long) || (cst instanceof Double)) {
                    updateLocalStackState(NOT_EXISTED, topIndex + 2);
                }
                break;
            }
            case Opcodes.ILOAD:
            case Opcodes.FLOAD:
            case Opcodes.ALOAD: {
                // operand stack入栈：1
                int fromIndex = ((VarInsnNode) insn).var;
                pushStackTopState(fromIndex);
                break;
            }
            case Opcodes.LLOAD:
            case Opcodes.DLOAD: {
                // operand stack入栈：1
                int fromIndex = ((VarInsnNode) insn).var;
                int topIndex = getStackTopIndex();
                updateLocalStackState(fromIndex, topIndex + 1);
                updateLocalStackState(NOT_EXISTED, topIndex + 2);
                break;
            }
            case Opcodes.ISTORE:
            case Opcodes.FSTORE:
            case Opcodes.ASTORE: {
                // operand stack出栈：1
                // local variable更新：1
                int fromIndex = getStackTopIndex();
                int toIndex = ((VarInsnNode) insn).var;
                invalidLocalStackValue(toIndex);
                updateLocalStackState(fromIndex, toIndex);
                break;
            }
            case Opcodes.LSTORE:
            case Opcodes.DSTORE: {
                // operand stack出栈：2
                // local variable更新：2
                int fromIndex = getStackTopIndex() - 1;
                int toIndex = ((VarInsnNode) insn).var;
                invalidLocalStackValue(toIndex);
                invalidLocalStackValue(toIndex + 1);
                updateLocalStackState(fromIndex, toIndex);
                updateLocalStackState(NOT_EXISTED, toIndex + 1);
                break;
            }
            case Opcodes.IASTORE:
            case Opcodes.LASTORE:
            case Opcodes.FASTORE:
            case Opcodes.DASTORE:
            case Opcodes.AASTORE:
            case Opcodes.BASTORE:
            case Opcodes.CASTORE:
            case Opcodes.SASTORE: {
                // operand stack出栈：3
                break;
            }
            case Opcodes.POP: {
                // operand stack出栈：1
                break;
            }
            case Opcodes.POP2: {
                // operand stack出栈：2
                break;
            }
            case Opcodes.DUP: {
                // operand stack入栈：1
                int topIndex = getStackTopIndex();
                updateLocalStackState(topIndex, topIndex + 1);
                break;
            }
            case Opcodes.DUP_X1: {
                int topIndex = getStackTopIndex();
                updateLocalStackState(topIndex, topIndex + 1);
                updateLocalStackState(topIndex - 1, topIndex);
                updateLocalStackState(topIndex + 1, topIndex - 1);
                break;
            }
            case Opcodes.DUP_X2: {
                int topIndex = getStackTopIndex();
                updateLocalStackState(topIndex, topIndex + 1);
                updateLocalStackState(topIndex - 1, topIndex);
                updateLocalStackState(topIndex - 2, topIndex - 1);
                updateLocalStackState(topIndex + 1, topIndex - 2);
                break;
            }
            case Opcodes.DUP2: {
                // operand stack入栈：2
                int topIndex = getStackTopIndex();
                updateLocalStackState(topIndex, topIndex + 2);
                updateLocalStackState(topIndex - 1, topIndex + 1);
                break;
            }
            case Opcodes.DUP2_X1: {
                int topIndex = getStackTopIndex();
                updateLocalStackState(topIndex, topIndex + 2);
                updateLocalStackState(topIndex - 1, topIndex + 1);
                updateLocalStackState(topIndex - 2, topIndex);
                updateLocalStackState(topIndex + 2, topIndex - 1);
                updateLocalStackState(topIndex + 1, topIndex - 2);
                break;
            }
            case Opcodes.DUP2_X2: {
                int topIndex = getStackTopIndex();
                updateLocalStackState(topIndex, topIndex + 2);
                updateLocalStackState(topIndex - 1, topIndex + 1);
                updateLocalStackState(topIndex - 2, topIndex);
                updateLocalStackState(topIndex - 3, topIndex - 1);
                updateLocalStackState(topIndex + 2, topIndex - 2);
                updateLocalStackState(topIndex + 1, topIndex - 3);
                break;
            }
            case Opcodes.SWAP: {
                int topIndex = getStackTopIndex();
                swapLocalStackState(topIndex - 1, topIndex);
                break;
            }
            case Opcodes.IALOAD:
            case Opcodes.FALOAD:
            case Opcodes.AALOAD:
            case Opcodes.BALOAD:
            case Opcodes.CALOAD:
            case Opcodes.SALOAD: {
                // operand stack出栈：2，入栈：1
                int topIndex = getStackTopIndex();
                updateLocalStackState(NOT_EXISTED, topIndex - 1);
                break;
            }
            case Opcodes.LALOAD:
            case Opcodes.DALOAD: {
                // operand stack出栈：2，入栈：2
                int topIndex = getStackTopIndex();
                updateLocalStackState(NOT_EXISTED, topIndex - 1);
                updateLocalStackState(NOT_EXISTED, topIndex);
                break;
            }
            case Opcodes.IADD:
            case Opcodes.FADD:
            case Opcodes.ISUB:
            case Opcodes.FSUB:
            case Opcodes.IMUL:
            case Opcodes.FMUL:
            case Opcodes.IDIV:
            case Opcodes.FDIV:
            case Opcodes.IREM:
            case Opcodes.FREM:
            case Opcodes.ISHL:
            case Opcodes.ISHR:
            case Opcodes.IUSHR:
            case Opcodes.IAND:
            case Opcodes.IOR:
            case Opcodes.IXOR:
            case Opcodes.FCMPL:
            case Opcodes.FCMPG: {
                // operand stack出栈：2，入栈：1
                int topIndex = getStackTopIndex();
                updateLocalStackState(NOT_EXISTED, topIndex);
                break;
            }
            case Opcodes.LADD:
            case Opcodes.DADD:
            case Opcodes.LSUB:
            case Opcodes.DSUB:
            case Opcodes.LMUL:
            case Opcodes.DMUL:
            case Opcodes.LDIV:
            case Opcodes.DDIV:
            case Opcodes.LREM:
            case Opcodes.DREM:
            case Opcodes.LSHL:
            case Opcodes.LSHR:
            case Opcodes.LUSHR:
            case Opcodes.LAND:
            case Opcodes.LOR:
            case Opcodes.LXOR:
            case Opcodes.LCMP:
            case Opcodes.DCMPL:
            case Opcodes.DCMPG: {
                // operand stack出栈：4，入栈：2
                int topIndex = getStackTopIndex();
                updateLocalStackState(NOT_EXISTED, topIndex - 2);
                updateLocalStackState(NOT_EXISTED, topIndex - 3);
                break;
            }
            case Opcodes.INEG:
            case Opcodes.FNEG: {
                // operand stack出栈：1，入栈：1
                int topIndex = getStackTopIndex();
                updateLocalStackState(NOT_EXISTED, topIndex);
                break;
            }
            case Opcodes.LNEG:
            case Opcodes.DNEG: {
                // operand stack出栈：2，入栈：2
                int topIndex = getStackTopIndex();
                updateLocalStackState(NOT_EXISTED, topIndex);
                updateLocalStackState(NOT_EXISTED, topIndex - 1);
                break;
            }
            case Opcodes.IINC: {
                break;
            }
            case Opcodes.I2F:
            case Opcodes.F2I:
            case Opcodes.I2B:
            case Opcodes.I2C:
            case Opcodes.I2S: {
                // operand stack出栈：1，入栈：1
                int topIndex = getStackTopIndex();
                updateLocalStackState(NOT_EXISTED, topIndex);
                break;
            }
            case Opcodes.I2L:
            case Opcodes.I2D:
            case Opcodes.F2L:
            case Opcodes.F2D: {
                // operand stack出栈：1，入栈：2
                int topIndex = getStackTopIndex();
                updateLocalStackState(NOT_EXISTED, topIndex);
                updateLocalStackState(NOT_EXISTED, topIndex + 1);
                break;
            }
            case Opcodes.L2I:
            case Opcodes.L2F:
            case Opcodes.D2I:
            case Opcodes.D2F: {
                // operand stack出栈：2，入栈：1
                int topIndex = getStackTopIndex();
                updateLocalStackState(NOT_EXISTED, topIndex - 1);
                break;
            }
            case Opcodes.L2D:
            case Opcodes.D2L: {
                // operand stack出栈：2，入栈：2
                int topIndex = getStackTopIndex();
                updateLocalStackState(NOT_EXISTED, topIndex - 1);
                updateLocalStackState(NOT_EXISTED, topIndex);
                break;
            }
            case Opcodes.IFEQ:
            case Opcodes.IFNE:
            case Opcodes.IFLT:
            case Opcodes.IFGE:
            case Opcodes.IFGT:
            case Opcodes.IFLE: {
                // operand stack出栈：1
                break;
            }
            case Opcodes.IF_ICMPEQ:
            case Opcodes.IF_ICMPNE:
            case Opcodes.IF_ICMPLT:
            case Opcodes.IF_ICMPGE:
            case Opcodes.IF_ICMPGT:
            case Opcodes.IF_ICMPLE:
            case Opcodes.IF_ACMPEQ:
            case Opcodes.IF_ACMPNE: {
                // operand stack出栈：2
                break;
            }
            case Opcodes.GOTO:
                break;
            case Opcodes.JSR:
                break;
            case Opcodes.RET:
                break;
            case Opcodes.TABLESWITCH:
            case Opcodes.LOOKUPSWITCH: {
                // operand stack出栈：1
                break;
            }
            case Opcodes.IRETURN:
            case Opcodes.FRETURN:
            case Opcodes.ARETURN: {
                // operand stack出栈：1
                break;
            }
            case Opcodes.LRETURN:
            case Opcodes.DRETURN: {
                // operand stack出栈：2
                break;
            }
            case Opcodes.RETURN:
                break;
            case Opcodes.GETSTATIC: {
                // operand stack入栈：1或2
                FieldInsnNode fieldNodeInsn = (FieldInsnNode) insn;
                String desc = fieldNodeInsn.desc;
                Type t = Type.getType(desc);
                int size = t.getSize();
                int topIndex = getStackTopIndex();
                updateLocalStackState(NOT_EXISTED, topIndex + 1);
                if (size == 2) {
                    updateLocalStackState(NOT_EXISTED, topIndex + 2);
                }

                break;
            }
            case Opcodes.PUTSTATIC: {
                // operand stack出栈：1或2
                break;
            }
            case Opcodes.PUTFIELD: {
                // operand stack出栈：2或3
                break;
            }
            case Opcodes.GETFIELD: {
                // operand stack出栈：1，入栈：1或2
                FieldInsnNode fieldNodeInsn = (FieldInsnNode) insn;
                String desc = fieldNodeInsn.desc;
                Type t = Type.getType(desc);
                int size = t.getSize();

                int topIndex = getStackTopIndex();
                updateLocalStackState(NOT_EXISTED, topIndex);
                if (size == 2) {
                    updateLocalStackState(NOT_EXISTED, topIndex + 1);
                }
                break;
            }
            case Opcodes.INVOKEVIRTUAL:
            case Opcodes.INVOKESPECIAL:
            case Opcodes.INVOKEINTERFACE:
            case Opcodes.INVOKESTATIC: {
                // operand stack出栈：argumentsSize，入栈：returnSize
                int topIndex = getStackTopIndex();
                MethodInsnNode methodInsnNode = (MethodInsnNode) insn;
                String desc = methodInsnNode.desc;
                Type methodType = Type.getMethodType(desc);
                int argumentsAndReturnSizes = methodType.getArgumentsAndReturnSizes();
                int argumentsSize = argumentsAndReturnSizes >> 2;
                if (opcode == Opcodes.INVOKESTATIC) {
                    argumentsSize--;
                }
                int returnSize = argumentsSize & 3;
                for (int i = 0; i < returnSize; i++) {
                    updateLocalStackState(NOT_EXISTED, topIndex + 1 - argumentsSize + i);
                }
                break;
            }
            case Opcodes.INVOKEDYNAMIC: {
                // operand stack出栈：argumentsSize，入栈：returnSize
                InvokeDynamicInsnNode invokeDynamicInsnNode = (InvokeDynamicInsnNode) insn;
                String desc = invokeDynamicInsnNode.desc;
                Type methodType = Type.getMethodType(desc);
                int argumentsAndReturnSizes = methodType.getArgumentsAndReturnSizes();
                int argumentsSize = argumentsAndReturnSizes >> 2;
                argumentsSize--;
                int returnSize = argumentsSize & 3;
                int topIndex = getStackTopIndex();
                for (int i = 0; i < returnSize; i++) {
                    updateLocalStackState(NOT_EXISTED, topIndex + 1 - argumentsSize + i);
                }
                break;
            }
            case Opcodes.NEW: {
                // operand stack入栈：1
                pushStackTopState(NOT_EXISTED);
                break;
            }
            case Opcodes.NEWARRAY:
            case Opcodes.ANEWARRAY:
            case Opcodes.ARRAYLENGTH: {
                // operand stack出栈：1，入栈：1
                int topIndex = getStackTopIndex();
                updateLocalStackState(NOT_EXISTED, topIndex);
                break;
            }
            case Opcodes.ATHROW: {
                // operand stack出栈：1
                break;
            }
            case Opcodes.CHECKCAST: {
                // operand stack出栈：1，入栈：1
                break;
            }
            case Opcodes.INSTANCEOF: {
                // operand stack出栈：1，入栈：1
                int topIndex = getStackTopIndex();
                updateLocalStackState(NOT_EXISTED, topIndex);
                break;
            }
            case Opcodes.MONITORENTER:
            case Opcodes.MONITOREXIT: {
                // operand stack出栈：1
                break;
            }
            case Opcodes.MULTIANEWARRAY: {
                // operand stack出栈：n，入栈：1
                MultiANewArrayInsnNode multiANewArrayInsnNode = (MultiANewArrayInsnNode) insn;
                int count = multiANewArrayInsnNode.dims;
                int topIndex = getStackTopIndex();
                updateLocalStackState(NOT_EXISTED, topIndex - count + 1);
                break;
            }
            case Opcodes.IFNULL:
            case Opcodes.IFNONNULL: {
                // operand stack出栈：1
                break;
            }
            default:
                throw new AnalyzerException(insn, "Illegal opcode " + opcode);
        }

        // 其次，调用父类的方法实现
        super.execute(insn, interpreter);
    }
}

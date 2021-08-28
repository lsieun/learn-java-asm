package lsieun.asm.template;

import lsieun.utils.OpcodeConst;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class MethodPrintInstructionAdapter extends MethodVisitor {
    private static final String OPCODE_INSTRUCTION_FORMAT = "%-15s %-6s";
    public static final Type OBJECT_TYPE = Type.getType("Ljava/lang/Object;");

    private final String methodName;
    private final String methodDesc;

    public MethodPrintInstructionAdapter(int api, MethodVisitor methodVisitor, String methodName, String methodDesc) {
        super(api, methodVisitor);
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    @Override
    public void visitCode() {
        String line = String.format("Method: %s:%s", methodName, methodDesc);
        printMessage(line);

        super.visitCode();
    }

    // ILOAD, LLOAD, FLOAD, DLOAD, ALOAD,
    // ISTORE, LSTORE, FSTORE, DSTORE, ASTORE
    // or RET.
    @Override
    public void visitVarInsn(int opcode, int index) {
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, OpcodeConst.getOpcodeName(opcode), index);
        printMessage(instruction);

        if (opcode == ILOAD) {
            super.visitVarInsn(opcode, index);
            dupAndPrintValueOnStack(Type.INT_TYPE);
        }
        else if (opcode == LLOAD) {
            super.visitVarInsn(opcode, index);
            dupAndPrintValueOnStack(Type.LONG_TYPE);
        }
        else if (opcode == FLOAD) {
            super.visitVarInsn(opcode, index);
            dupAndPrintValueOnStack(Type.FLOAT_TYPE);
        }
        else if (opcode == DLOAD) {
            super.visitVarInsn(opcode, index);
            dupAndPrintValueOnStack(Type.DOUBLE_TYPE);
        }
        else if (opcode == ALOAD) {
            super.visitVarInsn(opcode, index);
            dupAndPrintValueOnStack(OBJECT_TYPE);
        }
        else if (opcode == ISTORE) {
            dupAndPrintValueOnStack(Type.INT_TYPE);
            super.visitVarInsn(opcode, index);
        }
        else if (opcode == LSTORE) {
            dupAndPrintValueOnStack(Type.LONG_TYPE);
            super.visitVarInsn(opcode, index);
        }
        else if (opcode == FSTORE) {
            dupAndPrintValueOnStack(Type.FLOAT_TYPE);
            super.visitVarInsn(opcode, index);
        }
        else if (opcode == DSTORE) {
            dupAndPrintValueOnStack(Type.DOUBLE_TYPE);
            super.visitVarInsn(opcode, index);
        }
        else if (opcode == ASTORE) {
            dupAndPrintValueOnStack(OBJECT_TYPE);
            super.visitVarInsn(opcode, index);
        }
        else {
            super.visitVarInsn(opcode, index);
            super.visitLdcInsn("not supported");
            printValueOnStack(OBJECT_TYPE);
        }
    }

    @Override
    public void visitInsn(int opcode) {
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, OpcodeConst.getOpcodeName(opcode), "");
        printMessage(instruction);

        super.visitInsn(opcode);
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        String opcode_arg = String.format("%s %s", var, increment);
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, "iinc", opcode_arg);
        printMessage(instruction);

        super.visitIincInsn(var, increment);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        String opcode_arg = String.valueOf(operand);
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, OpcodeConst.getOpcodeName(opcode), opcode_arg);
        printMessage(instruction);

        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, OpcodeConst.getOpcodeName(opcode), "");
        printMessage(instruction);

        switch (opcode) {
            case IFEQ:
            case IFNE:
            case IFLT:
            case IFGE:
            case IFGT:
            case IFLE:
                dupAndPrintValueOnStack(Type.INT_TYPE);
                break;
            case IF_ICMPEQ:
            case IF_ICMPNE:
            case IF_ICMPLT:
            case IF_ICMPGE:
            case IF_ICMPGT:
            case IF_ICMPLE:
                super.visitInsn(DUP2);
                super.visitInsn(POP);
                printValueOnStack(Type.INT_TYPE);
                dupAndPrintValueOnStack(Type.INT_TYPE);
                break;
            case IF_ACMPEQ:
            case IF_ACMPNE:
                super.visitInsn(DUP2);
                super.visitInsn(POP);
                printValueOnStack(OBJECT_TYPE);
                dupAndPrintValueOnStack(OBJECT_TYPE);
                break;
            case IFNULL:
            case IFNONNULL:
                dupAndPrintValueOnStack(OBJECT_TYPE);
                break;
            default:
                break;
        }

        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLdcInsn(Object value) {
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, "LDC", value);
        printMessage(instruction);

        super.visitLdcInsn(value);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, "tableswitch", "");
        printMessage(instruction);

        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, "lookupswitch", "");
        printMessage(instruction);

        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        String opcode_arg = descriptor + ":" + numDimensions;
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, "multianewarray", opcode_arg);
        printMessage(instruction);

        super.visitMultiANewArrayInsn(descriptor, numDimensions);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        String opcode_arg = String.format("%s.%s:%s", owner, name, descriptor);
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, OpcodeConst.getOpcodeName(opcode), opcode_arg);
        printMessage(instruction);

        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        String opcode_arg = String.format("%s.%s:%s", owner, name, descriptor);
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, OpcodeConst.getOpcodeName(opcode), opcode_arg);
        printMessage(instruction);

        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
        String opcode_arg = String.format("%s:%s", name, descriptor);
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, "invokedynamic", opcode_arg);
        printMessage(instruction);

        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        String instruction = String.format(OPCODE_INSTRUCTION_FORMAT, OpcodeConst.getOpcodeName(opcode), type);
        printMessage(instruction);

        super.visitTypeInsn(opcode, type);
    }

    private void printMessage(String message) {
        super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        super.visitLdcInsn(message);
        super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    private void dup(Type t) {
        int size = t.getSize();
        if (size == 1) {
            super.visitInsn(DUP);
        }
        else {
            super.visitInsn(DUP2);
        }
    }

    private void printValueOnStack(Type t) {
        int size = t.getSize();
        String descriptor = String.format("(%s)V", t.getDescriptor());

        super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        if (size == 1) {
            super.visitInsn(SWAP);
        }
        else {
            super.visitInsn(DUP_X2);
            super.visitInsn(POP);
        }
        super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", descriptor, false);
    }

    private void dupAndPrintValueOnStack(Type t) {
        dup(t);
        printValueOnStack(t);
    }
}

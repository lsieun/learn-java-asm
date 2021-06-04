package lsieun.asm.template;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

public class MethodFinallyConverter extends AdviceAdapter {
    private final String methodName;
    private final Label tryLabel = new Label();
    private final Label finallyLabel = new Label();

    protected MethodFinallyConverter(int api, MethodVisitor mv, int access, String name, String descriptor) {
        super(api, mv, access, name, descriptor);
        this.methodName = name;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        super.visitLabel(tryLabel);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {

        super.visitLabel(finallyLabel);
        super.visitTryCatchBlock(tryLabel, finallyLabel, finallyLabel, null);

        onFinally();
        super.visitInsn(Opcodes.ATHROW);

        super.visitMaxs(maxStack, maxLocals);
    }

    @Override
    protected void onMethodExit(int opcode) {
        if (opcode != Opcodes.ATHROW) {
            onFinally();
        }
    }

    private void onFinally() {
        super.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
        super.visitLdcInsn("Exiting " + methodName + " Method");
        super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }
}

package lsieun.asm.template;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

public class MethodExitingConverter extends AdviceAdapter {
    private final String methodName;

    protected MethodExitingConverter(int api, MethodVisitor mv, int access, String name, String descriptor) {
        super(api, mv, access, name, descriptor);
        this.methodName = name;
    }

    @Override
    protected void onMethodExit(int opcode) {
        super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        if (opcode == Opcodes.ATHROW) {
            super.visitLdcInsn("Exiting on exception " + methodName + " Method");
        }
        else {
            super.visitLdcInsn("Exiting " + methodName + " Method");
        }

        super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }
}

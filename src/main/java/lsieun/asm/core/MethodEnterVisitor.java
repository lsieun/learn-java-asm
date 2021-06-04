package lsieun.asm.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.*;

public class MethodEnterVisitor extends ClassVisitor {
    public MethodEnterVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null && !"<init>".equals(name) && "parseURL".equals(name)) {
            mv = new MethodEnterAdapter(api, mv);
        }
        return mv;
    }

    private class MethodEnterAdapter extends MethodVisitor {
        public MethodEnterAdapter(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitCode() {
            // 首先，处理自己的代码逻辑
//            super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            super.visitLdcInsn("Method Enter...");
//            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            super.visitVarInsn(ALOAD, 2);
            super.visitLdcInsn("validateKey.action");
            super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z", false);
            Label elseLabel = new Label();
            super.visitJumpInsn(IFEQ, elseLabel);
            super.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
            super.visitInsn(DUP);
            super.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "()V", false);
            super.visitInsn(ATHROW);
            super.visitLabel(elseLabel);


            // 其次，调用父类的方法实现
            super.visitCode();
        }
    }
}

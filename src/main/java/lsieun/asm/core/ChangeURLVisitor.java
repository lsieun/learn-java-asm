package lsieun.asm.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

public class ChangeURLVisitor extends ClassVisitor {
    public ChangeURLVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM9, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null && "<init>".equals(name) && "(Ljava/net/URL;Ljava/lang/String;Ljava/net/URLStreamHandler;)V".equals(descriptor)) {
            mv = new ChangeURLAdapter(mv, access, name, descriptor);
        }
        return mv;
    }

    public class ChangeURLAdapter extends AdviceAdapter {
        protected ChangeURLAdapter(MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(Opcodes.ASM9, methodVisitor, access, name, descriptor);
        }

        @Override
        protected void onMethodEnter() {
            super.visitVarInsn(ALOAD, 2);
            Label elseLabel = new Label();
            super.visitJumpInsn(IFNULL, elseLabel);
            super.visitVarInsn(ALOAD, 2);
            super.visitLdcInsn("/lservice/rpc/validateKey.action");
            super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z", false);
            super.visitJumpInsn(IFEQ, elseLabel);
            super.visitTypeInsn(NEW, "java/net/MalformedURLException");
            super.visitInsn(DUP);
            super.visitMethodInsn(INVOKESPECIAL, "java/net/MalformedURLException", "<init>", "()V", false);
            super.visitInsn(ATHROW);
            super.visitLabel(elseLabel);
        }
    }
}

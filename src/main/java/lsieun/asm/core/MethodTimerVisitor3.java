package lsieun.asm.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import static org.objectweb.asm.Opcodes.*;

public class MethodTimerVisitor3 extends ClassVisitor {
    public MethodTimerVisitor3(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

        if (mv != null && !"<init>".equals(name) && !"<clinit>".equals(name)) {
            boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;
            boolean isNativeMethod = (access & ACC_NATIVE) != 0;
            if (!isAbstractMethod && !isNativeMethod) {
                mv = new MethodTimerAdapter3(api, access, name, descriptor, mv);
            }
        }
        return mv;
    }

    private static class MethodTimerAdapter3 extends LocalVariablesSorter {
        private final String methodName;
        private final String methodDesc;
        private int slotIndex;

        public MethodTimerAdapter3(int api, int access, String name, String descriptor, MethodVisitor methodVisitor) {
            super(api, access, descriptor, methodVisitor);
            this.methodName = name;
            this.methodDesc = descriptor;
        }

        @Override
        public void visitCode() {
            // 首先，实现自己的逻辑
            slotIndex = newLocal(Type.LONG_TYPE);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            mv.visitVarInsn(LSTORE, slotIndex);

            // 其次，调用父类的实现
            super.visitCode();
        }

        @Override
        public void visitInsn(int opcode) {
            // 首先，实现自己的逻辑
            if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                mv.visitVarInsn(LLOAD, slotIndex);
                mv.visitInsn(LSUB);
                mv.visitVarInsn(LSTORE, slotIndex);
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
                mv.visitInsn(DUP);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                mv.visitLdcInsn(methodName + methodDesc + " method execute: ");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitVarInsn(LLOAD, slotIndex);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            }

            // 其次，调用父类的实现
            super.visitInsn(opcode);
        }
    }
}
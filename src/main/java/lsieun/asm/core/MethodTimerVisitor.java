package lsieun.asm.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class MethodTimerVisitor extends ClassVisitor {
    private String owner;
    private boolean isInterface;

    public MethodTimerVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        owner = name;
        isInterface = (access & ACC_INTERFACE) != 0;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (!isInterface && mv != null && !"<init>".equals(name) && !"<clinit>".equals(name)) {
            boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;
            boolean isNativeMethod = (access & ACC_NATIVE) != 0;
            if (!isAbstractMethod && !isNativeMethod) {
                mv = new MethodTimerAdapter(api, mv, owner);
            }
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        if (!isInterface) {
            FieldVisitor fv = super.visitField(ACC_PUBLIC | ACC_STATIC, "timer", "J", null, null);
            if (fv != null) {
                fv.visitEnd();
            }
        }
        super.visitEnd();
    }

    private static class MethodTimerAdapter extends MethodVisitor {
        private final String owner;

        public MethodTimerAdapter(int api, MethodVisitor mv, String owner) {
            super(api, mv);
            this.owner = owner;
        }

        @Override
        public void visitCode() {
            // 首先，处理自己的代码逻辑
            super.visitFieldInsn(GETSTATIC, owner, "timer", "J");
            super.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            super.visitInsn(LSUB);
            super.visitFieldInsn(PUTSTATIC, owner, "timer", "J");

            // 其次，调用父类的方法实现
            super.visitCode();
        }

        @Override
        public void visitInsn(int opcode) {
            // 首先，处理自己的代码逻辑
            if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
                super.visitFieldInsn(GETSTATIC, owner, "timer", "J");
                super.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                super.visitInsn(LADD);
                super.visitFieldInsn(PUTSTATIC, owner, "timer", "J");
            }

            // 其次，调用父类的方法实现
            super.visitInsn(opcode);
        }
    }
}
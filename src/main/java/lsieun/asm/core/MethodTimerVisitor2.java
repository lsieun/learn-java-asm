package lsieun.asm.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class MethodTimerVisitor2 extends ClassVisitor {
    private String owner;
    private boolean isInterface;

    public MethodTimerVisitor2(int api, ClassVisitor classVisitor) {
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
                // 每遇到一个合适的方法，就添加一个相应的字段
                FieldVisitor fv = super.visitField(ACC_PUBLIC | ACC_STATIC, getFieldName(name), "J", null, null);
                if (fv != null) {
                    fv.visitEnd();
                }

                mv = new MethodTimerAdapter2(api, mv, owner, name);
            }

        }
        return mv;
    }


    private String getFieldName(String methodName) {
        return "timer_" + methodName;
    }

    private class MethodTimerAdapter2 extends MethodVisitor {
        private final String owner;
        private final String methodName;

        public MethodTimerAdapter2(int api, MethodVisitor mv, String owner, String methodName) {
            super(api, mv);
            this.owner = owner;
            this.methodName = methodName;
        }

        @Override
        public void visitCode() {
            // 首先，处理自己的代码逻辑
            super.visitFieldInsn(GETSTATIC, owner, getFieldName(methodName), "J"); // 注意，字段名字要对应
            super.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            super.visitInsn(LSUB);
            super.visitFieldInsn(PUTSTATIC, owner, getFieldName(methodName), "J"); // 注意，字段名字要对应

            // 其次，调用父类的方法实现
            super.visitCode();
        }

        @Override
        public void visitInsn(int opcode) {
            // 首先，处理自己的代码逻辑
            if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
                super.visitFieldInsn(GETSTATIC, owner, getFieldName(methodName), "J"); // 注意，字段名字要对应
                super.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                super.visitInsn(LADD);
                super.visitFieldInsn(PUTSTATIC, owner, getFieldName(methodName), "J"); // 注意，字段名字要对应
            }

            // 其次，调用父类的方法实现
            super.visitInsn(opcode);
        }
    }
}

package lsieun.asm.template;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.*;

public class MethodWithWholeTryCatchVisitor extends ClassVisitor {
    private final String methodName;
    private final String methodDesc;

    public MethodWithWholeTryCatchVisitor(int api, ClassVisitor classVisitor, String methodName, String methodDesc) {
        super(api, classVisitor);
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null && !"<init>".equals(name) && methodName.equals(name) && methodDesc.equals(descriptor)) {
            boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;
            boolean isNativeMethod = (access & ACC_NATIVE) != 0;
            if (!isAbstractMethod && !isNativeMethod) {
                mv = new MethodWithWholeTryCatchAdapter(api, mv);
            }
        }
        return mv;
    }

    private static class MethodWithWholeTryCatchAdapter extends MethodVisitor {
        private Label startLabel = new Label();
        private Label endLabel = new Label();
        private Label handlerLabel = new Label();

        public MethodWithWholeTryCatchAdapter(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        public void visitCode() {
            // 首先，处理自己的代码逻辑
            // (1) startLabel
            super.visitLabel(startLabel);

            // 其次，调用父类的方法实现
            super.visitCode();
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            // 首先，处理自己的代码逻辑
            // (2) endLabel
            super.visitLabel(endLabel);

            // (3) handlerLabel
            super.visitLabel(handlerLabel);
            super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            super.visitLdcInsn("catch Exception");
            super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            super.visitInsn(Opcodes.ATHROW);

            // (4) visitTryCatchBlock
            super.visitTryCatchBlock(startLabel, endLabel, handlerLabel, "java/lang/Exception");

            // 其次，调用父类的方法实现
            super.visitMaxs(maxStack, maxLocals);
        }
    }
}

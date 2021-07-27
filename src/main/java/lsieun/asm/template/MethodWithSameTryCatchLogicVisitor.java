package lsieun.asm.template;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class MethodWithSameTryCatchLogicVisitor extends ClassVisitor {
    public MethodWithSameTryCatchLogicVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null) {
            boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;
            boolean isNativeMethod = (access & ACC_NATIVE) != 0;
            if (!isAbstractMethod && !isNativeMethod) {
                mv = new MethodWithSameTryCatchLogicAdapter(api, mv);
            }
        }
        return mv;
    }


    private static class MethodWithSameTryCatchLogicAdapter extends MethodVisitor {
        private final List<Label> handlerList = new ArrayList<>();

        public MethodWithSameTryCatchLogicAdapter(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
            // 首先，处理自己的代码逻辑
            if (!handlerList.contains(handler)) {
                handlerList.add(handler);
            }

            // 其次，调用父类的方法实现
            super.visitTryCatchBlock(start, end, handler, type);
        }

        @Override
        public void visitLabel(Label label) {
            // 首先，调用父类的方法实现
            super.visitLabel(label);

            // 其次，处理自己的代码逻辑
            // 需要注意：不要将operand stack上的异常给弄丢了。
            if (handlerList.contains(label)) {
                // 在这里，我们复制一份来使用
                super.visitInsn(DUP);
                super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "(Ljava/io/PrintStream;)V", false);
            }
        }
    }
}

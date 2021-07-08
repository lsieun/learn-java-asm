package lsieun.asm.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class MethodTryCatchVisitor2 extends ClassVisitor {
    public MethodTryCatchVisitor2(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null) {
            boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;
            boolean isNativeMethod = (access & ACC_NATIVE) != 0;
            if (!isAbstractMethod && !isNativeMethod) {
                mv = new MethodTryCatchAdapter2(api, mv, access, descriptor);
            }
        }
        return mv;
    }


    private static class MethodTryCatchAdapter2 extends MethodVisitor {
        private final int methodAccess;
        private final String methodDesc;

        private final Label allExceptionHandlerLabel = new Label();

        public MethodTryCatchAdapter2(int api, MethodVisitor methodVisitor, int methodAccess, String methodDesc) {
            super(api, methodVisitor);
            this.methodAccess = methodAccess;
            this.methodDesc = methodDesc;
        }

        @Override
        public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
            // 注意，将所有异常都交给同一个allExceptionHandlerLabel来进行处理
            super.visitTryCatchBlock(start, end, allExceptionHandlerLabel, type);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            // 首先，处理自己的代码逻辑
            // (1) 添加Label
            super.visitLabel(allExceptionHandlerLabel);

            // (2) 添加处理异常逻辑的代码
            Type t = Type.getType(methodDesc);
            Type[] argumentTypes = t.getArgumentTypes();
            Type returnType = t.getReturnType();

            boolean isStaticMethod = ((methodAccess & ACC_STATIC) != 0);
            int localIndex = isStaticMethod ? 0 : 1;
            for (Type argType : argumentTypes) {
                localIndex += argType.getSize();
            }

            super.visitVarInsn(ASTORE, localIndex);
            super.visitVarInsn(ALOAD, localIndex);
            super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false);


            // (3) 添加return语句或者替换成throw语句
            if (returnType.getSort() == Type.VOID) {
                super.visitInsn(RETURN);
            }
            else if (returnType.getSort() >= Type.BOOLEAN && returnType.getSort() <= Type.DOUBLE) {
                super.visitInsn(returnType.getOpcode(ICONST_1));
                super.visitInsn(returnType.getOpcode(IRETURN));
            }
            else {
                super.visitInsn(ACONST_NULL);
                super.visitInsn(ARETURN);
            }


            // 其次，调用父类的方法实现
            super.visitMaxs(maxStack, maxLocals);
        }
    }
}

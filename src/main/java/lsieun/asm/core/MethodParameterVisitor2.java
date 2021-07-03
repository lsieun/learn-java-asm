package lsieun.asm.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class MethodParameterVisitor2 extends ClassVisitor {
    public MethodParameterVisitor2(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null && !name.equals("<init>")) {
            boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;
            boolean isNativeMethod = (access & ACC_NATIVE) != 0;
            if (!isAbstractMethod && !isNativeMethod) {
                mv = new MethodParameterAdapter2(api, mv, access, name, descriptor);
            }
        }
        return mv;
    }

    private static class MethodParameterAdapter2 extends MethodVisitor {
        private final int methodAccess;
        private final String methodName;
        private final String methodDesc;

        public MethodParameterAdapter2(int api, MethodVisitor mv, int methodAccess, String methodName, String methodDesc) {
            super(api, mv);
            this.methodAccess = methodAccess;
            this.methodName = methodName;
            this.methodDesc = methodDesc;
        }

        @Override
        public void visitCode() {
            // 首先，处理自己的代码逻辑
            boolean isStatic = ((methodAccess & ACC_STATIC) != 0);
            int slotIndex = isStatic ? 0 : 1;

            printMessage("Method Enter: " + methodName + methodDesc);

            Type methodType = Type.getMethodType(methodDesc);
            Type[] argumentTypes = methodType.getArgumentTypes();
            for (Type t : argumentTypes) {
                int sort = t.getSort();
                int size = t.getSize();
                String descriptor = t.getDescriptor();
                int opcode = t.getOpcode(ILOAD);
                super.visitVarInsn(opcode, slotIndex);
                if (sort >= Type.BOOLEAN && sort <= Type.DOUBLE) {
                    String methodDesc = String.format("(%s)V", descriptor);
                    printValueOnStack(methodDesc);
                }
                else {
                    printValueOnStack("(Ljava/lang/Object;)V");
                }

                slotIndex += size;
            }

            // 其次，调用父类的方法实现
            super.visitCode();
        }

        @Override
        public void visitInsn(int opcode) {
            // 首先，处理自己的代码逻辑
            if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
                printMessage("Method Exit: " + methodName + methodDesc);
                if (opcode >= IRETURN && opcode <= DRETURN) {
                    Type methodType = Type.getMethodType(methodDesc);
                    Type returnType = methodType.getReturnType();
                    int size = returnType.getSize();
                    String descriptor = returnType.getDescriptor();

                    if (size == 1) {
                        super.visitInsn(DUP);
                    }
                    else {
                        super.visitInsn(DUP2);
                    }
                    String methodDesc = String.format("(%s)V", descriptor);
                    printValueOnStack(methodDesc);
                }
                else if (opcode == ARETURN) {
                    super.visitInsn(DUP);
                    printValueOnStack("(Ljava/lang/Object;)V");
                }
                else if (opcode == RETURN) {
                    printMessage("    return void");
                }
                else {
                    printMessage("    abnormal return");
                }
            }

            // 其次，调用父类的方法实现
            super.visitInsn(opcode);
        }

        private void printMessage(String str) {
            super.visitLdcInsn(str);
            super.visitMethodInsn(INVOKESTATIC, "sample/ParameterUtils", "printText", "(Ljava/lang/String;)V", false);
        }

        private void printValueOnStack(String descriptor) {
            super.visitMethodInsn(INVOKESTATIC, "sample/ParameterUtils", "printValueOnStack", descriptor, false);
        }
    }
}

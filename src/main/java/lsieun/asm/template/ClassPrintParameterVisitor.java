package lsieun.asm.template;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

public class ClassPrintParameterVisitor extends ClassVisitor {
    public ClassPrintParameterVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null) {
            mv = new MethodPrintParameterAdapter(api, mv, access, name, descriptor);
        }
        return mv;
    }

    public static class MethodPrintParameterAdapter extends AdviceAdapter {
        public MethodPrintParameterAdapter(int api, MethodVisitor mv, int access, String name, String descriptor) {
            super(api, mv, access, name, descriptor);
        }

        @Override
        protected void onMethodEnter() {
            int slotIndex = (methodAccess & ACC_STATIC) != 0 ? 0 : 1;

            printMessage("Method Enter: " + getName() + methodDesc);

            Type methodType = Type.getMethodType(methodDesc);
            Type[] argumentTypes = methodType.getArgumentTypes();
            for (Type t : argumentTypes) {
                int size = t.getSize();
                int opcode = t.getOpcode(ILOAD);
                super.visitVarInsn(opcode, slotIndex);
                box(t);
                printValueOnStack("(Ljava/lang/Object;)V");

                slotIndex += size;
            }
        }

        @Override
        protected void onMethodExit(int opcode) {
            printMessage("Method Exit: " + getName() + methodDesc);

            if (opcode == ATHROW) {
                super.visitLdcInsn("abnormal return");
            }
            else if (opcode == RETURN) {
                super.visitLdcInsn("return void");
            }
            else if (opcode == ARETURN) {
                dup();
            }
            else {
                if (opcode == LRETURN || opcode == DRETURN) {
                    dup2();
                }
                else {
                    dup();
                }
                box(Type.getReturnType(this.methodDesc));
            }
            printValueOnStack("(Ljava/lang/Object;)V");
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

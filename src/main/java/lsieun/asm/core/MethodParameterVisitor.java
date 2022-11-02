package lsieun.asm.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

public class MethodParameterVisitor extends ClassVisitor {
    public MethodParameterVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null && !name.equals("<init>")) {
            boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;
            boolean isNativeMethod = (access & ACC_NATIVE) != 0;
            if (!isAbstractMethod && !isNativeMethod) {
                mv = new MethodParameterAdapter(api, mv, access, name, descriptor);
            }
        }
        return mv;
    }

    private static class MethodParameterAdapter extends MethodVisitor {
        private final int methodAccess;
        private final String methodName;
        private final String methodDesc;

        public MethodParameterAdapter(int api, MethodVisitor mv, int methodAccess, String methodName, String methodDesc) {
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

                if (sort == Type.BOOLEAN) {
                    printBoolean();
                }
                else if (sort == Type.CHAR) {
                    printChar();
                }
                else if (sort == Type.BYTE || sort == Type.SHORT || sort == Type.INT) {
                    printInt();
                }
                else if (sort == Type.FLOAT) {
                    printFloat();
                }
                else if (sort == Type.LONG) {
                    printLong();
                }
                else if (sort == Type.DOUBLE) {
                    printDouble();
                }
                else if (sort == Type.OBJECT && "Ljava/lang/String;".equals(descriptor)) {
                    printString();
                }
                else if (sort == Type.OBJECT) {
                    printObject();
                }
                else {
                    printMessage("No Support");
                    if (size == 1) {
                        super.visitInsn(Opcodes.POP);
                    }
                    else {
                        super.visitInsn(Opcodes.POP2);
                    }
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
                printMessage("Method Exit:");
                if (opcode == IRETURN) {
                    super.visitInsn(DUP);
                    Type methodType = Type.getMethodType(methodDesc);
                    Type returnType = methodType.getReturnType();
                    if (returnType == Type.BOOLEAN_TYPE) {
                        printBoolean();
                    }
                    else if (returnType == Type.CHAR_TYPE) {
                        printChar();
                    }
                    else {
                        printInt();
                    }
                }
                else if (opcode == FRETURN) {
                    super.visitInsn(DUP);
                    printFloat();
                }
                else if (opcode == LRETURN) {
                    super.visitInsn(DUP2);
                    printLong();
                }
                else if (opcode == DRETURN) {
                    super.visitInsn(DUP2);
                    printDouble();
                }
                else if (opcode == ARETURN) {
                    super.visitInsn(DUP);
                    printObject();
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

        private void printBoolean() {
            super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            super.visitInsn(SWAP);
            super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Z)V", false);
        }

        private void printChar() {
            super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            super.visitInsn(SWAP);
            super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(C)V", false);
        }

        private void printInt() {
            super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            super.visitInsn(SWAP);
            super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
        }

        private void printFloat() {
            super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            super.visitInsn(SWAP);
            super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(F)V", false);
        }

        private void printLong() {
            super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            super.visitInsn(DUP_X2);
            super.visitInsn(POP);
            super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V", false);
        }

        private void printDouble() {
            super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            super.visitInsn(DUP_X2);
            super.visitInsn(POP);
            super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(D)V", false);
        }

        private void printString() {
            super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            super.visitInsn(SWAP);
            super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }

        private void printObject() {
            super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            super.visitInsn(SWAP);
            super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false);
        }

        private void printMessage(String str) {
            super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            super.visitLdcInsn(str);
            super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
    }
}

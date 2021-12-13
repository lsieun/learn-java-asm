package lsieun.asm.commons;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AnalyzerAdapter;

import java.util.Arrays;
import java.util.List;

public class MethodStackMapFrameVisitor extends ClassVisitor {
    private String owner;

    public MethodStackMapFrameVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.owner = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new MethodStackMapFrameAdapter(api, owner, access, name, descriptor, mv);
    }

    private static class MethodStackMapFrameAdapter extends AnalyzerAdapter {
        private final String methodName;
        private final String methodDesc;

        public MethodStackMapFrameAdapter(int api, String owner, int access, String name, String descriptor, MethodVisitor methodVisitor) {
            super(api, owner, access, name, descriptor, methodVisitor);
            this.methodName = name;
            this.methodDesc = descriptor;
        }

        @Override
        public void visitCode() {
            super.visitCode();
            System.out.println();
            System.out.println(methodName + methodDesc);
            printStackMapFrame();
        }

        @Override
        public void visitInsn(int opcode) {
            super.visitInsn(opcode);
            printStackMapFrame();
        }

        @Override
        public void visitIntInsn(int opcode, int operand) {
            super.visitIntInsn(opcode, operand);
            printStackMapFrame();
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            super.visitVarInsn(opcode, var);
            printStackMapFrame();
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            super.visitTypeInsn(opcode, type);
            printStackMapFrame();
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            super.visitFieldInsn(opcode, owner, name, descriptor);
            printStackMapFrame();
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            printStackMapFrame();
        }

        @Override
        public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
            super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
            printStackMapFrame();
        }

        @Override
        public void visitJumpInsn(int opcode, Label label) {
            super.visitJumpInsn(opcode, label);
            printStackMapFrame();
        }

        @Override
        public void visitLdcInsn(Object value) {
            super.visitLdcInsn(value);
            printStackMapFrame();
        }

        @Override
        public void visitIincInsn(int var, int increment) {
            super.visitIincInsn(var, increment);
            printStackMapFrame();
        }

        @Override
        public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
            super.visitTableSwitchInsn(min, max, dflt, labels);
            printStackMapFrame();
        }

        @Override
        public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
            super.visitLookupSwitchInsn(dflt, keys, labels);
            printStackMapFrame();
        }

        @Override
        public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
            super.visitMultiANewArrayInsn(descriptor, numDimensions);
            printStackMapFrame();
        }

        private void printStackMapFrame() {
            String locals_str = locals == null ? "[]" : list2Str(locals);
            String stack_str = stack == null ? "[]" : list2Str(stack);
            String line = String.format("%s %s", locals_str, stack_str);
            System.out.println(line);
        }

        private String list2Str(List<Object> list) {
            if (list == null || list.size() == 0) return "[]";
            int size = list.size();
            String[] array = new String[size];
            for (int i = 0; i < size; i++) {
                Object item = list.get(i);
                array[i] = item2Str(item);
            }
            return Arrays.toString(array);
        }

        private String item2Str(Object obj) {
            if (obj == Opcodes.TOP) {
                return "top";
            }
            else if (obj == Opcodes.INTEGER) {
                return "int";
            }
            else if (obj == Opcodes.FLOAT) {
                return "float";
            }
            else if (obj == Opcodes.DOUBLE) {
                return "double";
            }
            else if (obj == Opcodes.LONG) {
                return "long";
            }
            else if (obj == Opcodes.NULL) {
                return "null";
            }
            else if (obj == Opcodes.UNINITIALIZED_THIS) {
                return "uninitialized_this";
            }
            else if (obj instanceof Label) {
                Object value = uninitializedTypes.get(obj);
                if (value == null) {
                    return obj.toString();
                }
                else {
                    return "uninitialized_" + value;
                }
            }
            else {
                return obj.toString();
            }
        }
    }
}

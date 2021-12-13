package lsieun.asm.commons;

import lsieun.classfile.ClassFile;
import lsieun.classfile.InsnRaw;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.AnalyzerAdapter;

import java.util.List;

public class MethodStackMapFrame02Visitor extends ClassVisitor {
    private String owner;
    private final ClassFile classFile;
    private int methodIndex = 0;

    public MethodStackMapFrame02Visitor(int api, ClassVisitor classVisitor, byte[] bytes) {
        super(api, classVisitor);
        this.classFile = new ClassFile(bytes);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.owner = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        byte[] code_bytes = classFile.getCode(methodIndex++);
        InsnRaw insnRaw = new InsnRaw(code_bytes);
        List<String> list = insnRaw.getList();
        mv = new MethodStackMapFrame02Adapter(api, owner, access, name, descriptor, mv, list);
        return mv;
    }

    private static class MethodStackMapFrame02Adapter extends AnalyzerAdapter {
        private static final String FORMAT = "%-30s // %s";
        private static final String START = "{";
        private static final String STOP = "}";
        private static final String EMPTY = "{}";
        private static final String SEPARATOR = "|";

        private final int methodAccess;
        private final String methodName;
        private final String methodDesc;
        private final List<String> insnList;
        private int insnIndex = 0;

        public MethodStackMapFrame02Adapter(int api, String owner, int access, String name, String descriptor, MethodVisitor methodVisitor, List<String> insnList) {
            super(api, owner, access, name, descriptor, methodVisitor);
            this.methodAccess = access;
            this.methodName = name;
            this.methodDesc = descriptor;
            this.insnList = insnList;
        }

        @Override
        public void visitCode() {
            super.visitCode();

            System.out.println(methodName + ":" + methodDesc);
            String frame = getStackFrame();
            String line = String.format(FORMAT, "", frame);
            System.out.println(line);
        }

        @Override
        public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
            super.visitFrame(type, numLocal, local, numStack, stack);

            String frame = getStackFrame();
            String line = String.format(FORMAT, "", frame);
            System.out.println(line);
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

            int insnSize = insnList.size();
            String insn;
            if (insnIndex < insnSize) {
                insn = insnList.get(insnIndex);
                insnIndex++;
            }
            else {
                insn = "";
            }

            String[] array = insn.split(System.lineSeparator(), 2);
            String frame = getStackFrame();
            String line = String.format(FORMAT, array[0], frame);
            System.out.println(line);
            System.out.println(array[1]);
        }

        @Override
        public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
            super.visitLookupSwitchInsn(dflt, keys, labels);

            int insnSize = insnList.size();
            String insn;
            if (insnIndex < insnSize) {
                insn = insnList.get(insnIndex);
                insnIndex++;
            }
            else {
                insn = "";
            }

            String[] array = insn.split(System.lineSeparator(), 2);
            String frame = getStackFrame();
            String line = String.format(FORMAT, array[0], frame);
            System.out.println(line);
            System.out.println(array[1]);
        }

        @Override
        public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
            super.visitMultiANewArrayInsn(descriptor, numDimensions);
            printStackMapFrame();
        }

        @Override
        public void visitEnd() {
            super.visitEnd();

            System.out.println();
            System.out.println();
        }

        private void printStackMapFrame() {
            int insnSize = insnList.size();
            String insn;
            if (insnIndex < insnSize) {
                insn = insnList.get(insnIndex);
                insnIndex++;
            }
            else {
                insn = "";
            }

            if (insn.contains(" wide ")) {
                System.out.println(insn);
                insn = insnList.get(insnIndex);
                insnIndex++;
            }

            String frame = getStackFrame();
            String line = String.format(FORMAT, insn, frame);
            System.out.println(line);
        }

        private String getStackFrame() {
            if (locals != null && locals.size() > 0) {
                boolean isStaticMethod = (methodAccess & Opcodes.ACC_STATIC) != 0;
                if (!isStaticMethod) {
                    Object obj = locals.get(0);
                    if (obj != Opcodes.UNINITIALIZED_THIS) {
                        locals.set(0, "this");
                    }
                }
            }
            String locals_str = locals == null ? EMPTY : list2Str(locals);
            String stack_str = stack == null ? EMPTY : list2Str(stack);
            return String.format("%s %s %s", locals_str, SEPARATOR, stack_str);
        }

        private String list2Str(List<Object> list) {
            if (list == null || list.size() == 0) return EMPTY;
            int size = list.size();
            String[] array = new String[size];
            for (int i = 0; i < size; i++) {
                Object item = list.get(i);
                array[i] = item2Str(item);
            }

            return array2Str(array);
        }

        private String array2Str(String[] array) {
            if (array == null || array.length == 0) return EMPTY;
            int length = array.length;

            StringBuilder sb = new StringBuilder();
            sb.append(START);
            for (int i = 0; i < length - 1; i++) {
                sb.append(array[i]).append(", ");
            }
            sb.append(array[length - 1]);
            sb.append(STOP);
            return sb.toString();
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
                return "uninitialized_" + getSimpleClassName(value);
            }
            else {
                return getSimpleClassName(obj);
            }
        }

        private String getSimpleClassName(Object obj) {
            if (obj == null) return "null";
            String descriptor = obj.toString();

            int squareIndex = descriptor.lastIndexOf("[");
            String prefix = descriptor.substring(0, squareIndex + 1);

            String simpleName = descriptor.substring(squareIndex + 1);
            if (simpleName.startsWith("L") && simpleName.endsWith(";")) {
                simpleName = simpleName.substring(1, simpleName.length() - 1);
            }

            int slashIndex = simpleName.lastIndexOf("/");
            simpleName = simpleName.substring(slashIndex + 1);

            return prefix + simpleName;
        }
    }
}

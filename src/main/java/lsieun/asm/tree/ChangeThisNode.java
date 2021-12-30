package lsieun.asm.tree;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import static org.objectweb.asm.Opcodes.*;

public class ChangeThisNode extends ClassNode {
    public ChangeThisNode(int api, ClassVisitor cv) {
        super(api);
        this.cv = cv;
    }

    @Override
    public void visitEnd() {
        // 首先，处理自己的代码逻辑
        int size = methods.size();
        for (int i = 0; i < size; i++) {
            MethodNode mn = methods.get(i);
            if ("<init>".equals(mn.name) || "<clinit>".equals(mn.name)) {
                continue;
            }
            InsnList instructions = mn.instructions;
            if (instructions.size() == 0) {
                continue;
            }

            int maxLocals = mn.maxLocals;

            int api = Opcodes.ASM9;
            MethodNode newMethodNode = new MethodNode(api, mn.access, mn.name, mn.desc, mn.signature, mn.exceptions.toArray(new String[0]));
            MethodVisitor mv = new ChangeThisAdapter(api, newMethodNode, mn.access, mn.name, mn.desc, maxLocals);
            mn.accept(mv);
            methods.set(i, newMethodNode);
        }

        // 其次，调用父类的方法实现（根据实际情况，选择保留，或删除）
        super.visitEnd();

        // 最后，向后续ClassVisitor传递
        if (cv != null) {
            accept(cv);
        }
    }

    private static class ChangeThisAdapter extends MethodVisitor {
        private final int methodAccess;
        private final String methodName;
        private final String methodDesc;
        private final int maxLocals;

        public ChangeThisAdapter(int api, MethodVisitor mv, int access, String methodName, String descriptor, int maxLocals) {
            super(api, mv);
            this.methodAccess = access;
            this.methodName = methodName;
            this.methodDesc = descriptor;
            this.maxLocals = maxLocals;
        }

        @Override
        public void visitCode() {
            // 首先，处理自己的代码逻辑
            boolean isStatic = (methodAccess & ACC_STATIC) != 0;

            // 第一步，考虑要不要复制this：
            // - 如果是static方法，就不复制了；
            // - 如果是non-static方法，就进行复制
            if (!isStatic) {
                //进入这里，说明是non-static方法，对this进行复制
                super.visitVarInsn(ALOAD, 0);
                super.visitVarInsn(ASTORE, getBackUpIndex(0));
            }

            // 第二步，考虑方法接收的参数
            // 根据方法描述符（methodDesc）获取各个参数的类型
            Type methodType = Type.getMethodType(methodDesc);
            Type[] argumentTypes = methodType.getArgumentTypes();

            // 对各个参数类型进行循环
            int localIndex = isStatic ? 0 : 1;
            for (Type t : argumentTypes) {
                // 将参数加载到栈上
                int load_opcode = t.getOpcode(ILOAD);
                super.visitVarInsn(load_opcode, localIndex);

                // 放到结尾目标位置
                int store_opcode = t.getOpcode(ISTORE);
                super.visitVarInsn(store_opcode, getBackUpIndex(localIndex));

                // 更新索引的位置
                localIndex += t.getSize();
            }

            // 其次，调用父类的方法实现
            super.visitCode();
        }

        @Override
        public void visitInsn(int opcode) {
            // 首先，处理自己的代码逻辑
            if (opcode == ATHROW || (opcode >= IRETURN && opcode <= RETURN)) {
                // 首先，处理自己的代码逻辑
                boolean isStatic = (methodAccess & ACC_STATIC) != 0;

                if (!isStatic) {
                    super.visitVarInsn(ALOAD, getBackUpIndex(0));
                    super.visitInsn(POP);
                }

                Type methodType = Type.getMethodType(methodDesc);
                Type[] argumentTypes = methodType.getArgumentTypes();
                int localIndex = isStatic ? 0 : 1;
                for (Type t : argumentTypes) {
                    int load_opcode = t.getOpcode(ILOAD);
                    super.visitVarInsn(load_opcode, getBackUpIndex(localIndex));
                    int size = t.getSize();
                    if (size == 1) {
                        super.visitInsn(POP);
                    }
                    else {
                        super.visitInsn(POP2);
                    }
                    localIndex += t.getSize();
                }

                super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                super.visitLdcInsn("%s %s:%s");
                super.visitInsn(ICONST_3);
                super.visitTypeInsn(ANEWARRAY, "java/lang/Object");
                super.visitInsn(DUP);
                super.visitInsn(ICONST_0);
                super.visitVarInsn(ALOAD, getBackUpIndex(0));
                super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
                super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
                super.visitInsn(AASTORE);
                super.visitInsn(DUP);
                super.visitInsn(ICONST_1);
                super.visitLdcInsn(methodName);
                super.visitInsn(AASTORE);
                super.visitInsn(DUP);
                super.visitInsn(ICONST_2);
                super.visitLdcInsn(methodDesc);
                super.visitInsn(AASTORE);
                super.visitMethodInsn(INVOKESTATIC, "java/lang/String", "format", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", false);
                super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            }

            // 其次，调用父类的方法实现
            super.visitInsn(opcode);
        }

        private int getBackUpIndex(int localIndex) {
            return maxLocals + localIndex;
        }
    }
}

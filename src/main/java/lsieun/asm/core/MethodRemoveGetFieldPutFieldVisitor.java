package lsieun.asm.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class MethodRemoveGetFieldPutFieldVisitor extends ClassVisitor {
    public MethodRemoveGetFieldPutFieldVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null && !"<init>".equals(name) && !"<clinit>".equals(name)) {
            boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;
            boolean isNativeMethod = (access & ACC_NATIVE) != 0;
            if (!isAbstractMethod && !isNativeMethod) {
                mv = new MethodRemoveGetFieldPutFieldAdapter(api, mv);
            }
        }
        return mv;
    }

    private class MethodRemoveGetFieldPutFieldAdapter extends MethodPatternAdapter {
        private final static int SEEN_ALOAD_0 = 1;
        private final static int SEEN_ALOAD_0_ALOAD_0 = 2;
        private final static int SEEN_ALOAD_0_ALOAD_0_GETFIELD = 3;

        private String fieldOwner;
        private String fieldName;
        private String fieldDesc;

        public MethodRemoveGetFieldPutFieldAdapter(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            // 第一，对于感兴趣的状态进行处理
            switch (state) {
                case SEEN_NOTHING:
                    if (opcode == ALOAD && var == 0) {
                        state = SEEN_ALOAD_0;
                        return;
                    }
                    break;
                case SEEN_ALOAD_0:
                    if (opcode == ALOAD && var == 0) {
                        state = SEEN_ALOAD_0_ALOAD_0;
                        return;
                    }
                    break;
                case SEEN_ALOAD_0_ALOAD_0:
                    if (opcode == ALOAD && var == 0) {
                        mv.visitVarInsn(opcode, var);
                        return;
                    }
                    break;
            }

            // 第二，对于不感兴趣的状态，交给父类进行处理
            super.visitVarInsn(opcode, var);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            // 第一，对于感兴趣的状态进行处理
            switch (state) {
                case SEEN_ALOAD_0_ALOAD_0:
                    if (opcode == GETFIELD) {
                        state = SEEN_ALOAD_0_ALOAD_0_GETFIELD;
                        fieldOwner = owner;
                        fieldName = name;
                        fieldDesc = descriptor;
                        return;
                    }
                    break;
                case SEEN_ALOAD_0_ALOAD_0_GETFIELD:
                    if (opcode == PUTFIELD && name.equals(fieldName)) {
                        state = SEEN_NOTHING;
                        return;
                    }
                    break;
            }

            // 第二，对于不感兴趣的状态，交给父类进行处理
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }

        @Override
        protected void visitInsn() {
            switch (state) {
                case SEEN_ALOAD_0:
                    mv.visitVarInsn(ALOAD, 0);
                    break;
                case SEEN_ALOAD_0_ALOAD_0:
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitVarInsn(ALOAD, 0);
                    break;
                case SEEN_ALOAD_0_ALOAD_0_GETFIELD:
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, fieldOwner, fieldName, fieldDesc);
                    break;
            }
            state = SEEN_NOTHING;
        }
    }
}

package lsieun.asm.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class MethodRemoveAddZeroVisitor extends ClassVisitor {
    public MethodRemoveAddZeroVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null && !"<init>".equals(name) && !"<clinit>".equals(name)) {
            boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;
            boolean isNativeMethod = (access & ACC_NATIVE) != 0;
            if (!isAbstractMethod && !isNativeMethod) {
                mv = new MethodRemoveAddZeroAdapter(api, mv);
            }
        }
        return mv;
    }

    private static class MethodRemoveAddZeroAdapter extends MethodPatternAdapter {
        private static final int SEEN_ICONST_0 = 1;

        public MethodRemoveAddZeroAdapter(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitInsn(int opcode) {
            // 第一，对于感兴趣的状态进行处理
            switch (state) {
                case SEEN_NOTHING:
                    if (opcode == ICONST_0) {
                        state = SEEN_ICONST_0;
                        return;
                    }
                    break;
                case SEEN_ICONST_0:
                    if (opcode == IADD) {
                        state = SEEN_NOTHING;
                        return;
                    }
                    else if (opcode == ICONST_0) {
                        mv.visitInsn(ICONST_0);
                        return;
                    }
                    break;
            }

            // 第二，对于不感兴趣的状态，交给父类进行处理
            super.visitInsn(opcode);
        }

        @Override
        protected void visitInsn() {
            if (state == SEEN_ICONST_0) {
                mv.visitInsn(ICONST_0);
            }
            state = SEEN_NOTHING;
        }
    }
}

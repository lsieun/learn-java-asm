package lsieun.asm.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_NATIVE;

public class MethodReplaceInvokeVisitor extends ClassVisitor {
    private final String oldOwner;
    private final String oldMethodName;
    private final String oldMethodDesc;

    private final int newOpcode;
    private final String newOwner;
    private final String newMethodName;
    private final String newMethodDesc;

    public MethodReplaceInvokeVisitor(int api, ClassVisitor classVisitor,
                                      String oldOwner, String oldMethodName, String oldMethodDesc,
                                      int newOpcode, String newOwner, String newMethodName, String newMethodDesc) {
        super(api, classVisitor);
        this.oldOwner = oldOwner;
        this.oldMethodName = oldMethodName;
        this.oldMethodDesc = oldMethodDesc;

        this.newOpcode = newOpcode;
        this.newOwner = newOwner;
        this.newMethodName = newMethodName;
        this.newMethodDesc = newMethodDesc;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null && !"<init>".equals(name) && !"<clinit>".equals(name)) {
            boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;
            boolean isNativeMethod = (access & ACC_NATIVE) != 0;
            if (!isAbstractMethod && !isNativeMethod) {
                mv = new MethodReplaceInvokeAdapter(api, mv);
            }
        }
        return mv;
    }

    private class MethodReplaceInvokeAdapter extends MethodVisitor {
        public MethodReplaceInvokeAdapter(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if (oldOwner.equals(owner) && oldMethodName.equals(name) && oldMethodDesc.equals(descriptor)) {
                // 注意，最后一个参数是false，会不会太武断呢？
                super.visitMethodInsn(newOpcode, newOwner, newMethodName, newMethodDesc, false);
            }
            else {
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            }
        }
    }
}

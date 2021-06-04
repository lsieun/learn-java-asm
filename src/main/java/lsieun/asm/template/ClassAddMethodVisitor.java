package lsieun.asm.template;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public abstract class ClassAddMethodVisitor extends ClassVisitor {
    private final int methodAccess;
    private final String methodName;
    private final String methodDesc;
    private final String methodSignature;
    private final String[] methodExceptions;
    private boolean isMethodPresent;

    public ClassAddMethodVisitor(int api, ClassVisitor cv, int methodAccess, String methodName, String methodDesc,
                                 String signature, String[] exceptions) {
        super(api, cv);
        this.methodAccess = methodAccess;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
        this.methodSignature = signature;
        this.methodExceptions = exceptions;
        this.isMethodPresent = false;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (name.equals(methodName) && descriptor.equals(methodDesc)) {
            isMethodPresent = true;
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        if (!isMethodPresent) {
            MethodVisitor mv = super.visitMethod(methodAccess, methodName, methodDesc, methodSignature, methodExceptions);
            if (mv != null) {
                // create method body
                generateMethodBody(mv);
            }
        }

        super.visitEnd();
    }

    protected abstract void generateMethodBody(MethodVisitor mv);
}

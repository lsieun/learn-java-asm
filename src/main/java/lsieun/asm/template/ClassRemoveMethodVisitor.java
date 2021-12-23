package lsieun.asm.template;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class ClassRemoveMethodVisitor extends ClassVisitor {
    private final String methodName;
    private final String methodDesc;

    public ClassRemoveMethodVisitor(int api, ClassVisitor cv, String methodName, String methodDesc) {
        super(api, cv);
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (name.equals(methodName) && descriptor.equals(methodDesc)) {
            return null;
        }
        else {
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
    }
}

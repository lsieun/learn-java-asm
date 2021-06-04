package lsieun.asm.tutorial.core;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class RemoveMethodAdapter extends ClassVisitor {
    private final String methodName;
    private final String methodDesc;

    public RemoveMethodAdapter(int api, ClassVisitor classVisitor, String methodName, String methodDesc) {
        super(api, classVisitor);
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if(name.equals(methodName) && descriptor.equals(methodDesc)) {
            // do not delegate to next visitor -> this removes the method
            return null;
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }
}

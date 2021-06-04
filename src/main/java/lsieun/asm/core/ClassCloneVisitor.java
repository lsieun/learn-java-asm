package lsieun.asm.core;

import org.objectweb.asm.ClassVisitor;

public class ClassCloneVisitor extends ClassVisitor {
    public ClassCloneVisitor(int api, ClassVisitor cw) {
        super(api, cw);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, new String[]{"java/lang/Cloneable"});
    }
}

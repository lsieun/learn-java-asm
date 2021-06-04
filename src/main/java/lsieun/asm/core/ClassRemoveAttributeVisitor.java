package lsieun.asm.core;

import org.objectweb.asm.ClassVisitor;

public class ClassRemoveAttributeVisitor extends ClassVisitor {
    public ClassRemoveAttributeVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visitSource(String source, String debug) {
        // do nothing
    }

    @Override
    public void visitOuterClass(String owner, String name, String descriptor) {
        // do nothing
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        // do nothing
    }
}

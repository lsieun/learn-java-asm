package lsieun.asm.analysis;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class MethodRemoveDeadCodeVisitor extends ClassVisitor {
    private String owner;

    public MethodRemoveDeadCodeVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.owner = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null) {
            mv = new MethodRemoveDeadCodeAdapter(api, owner, access, name, descriptor, mv);
        }
        return mv;
    }
}

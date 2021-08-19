package lsieun.asm.tree;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;

public class MyClassVisitor extends ClassVisitor {
    private final ClassVisitor next;
    public MyClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, new ClassNode());
        this.next = classVisitor;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        ClassNode cn = (ClassNode) cv;
        // put your transformation code here
        cn.accept(next);
    }
}

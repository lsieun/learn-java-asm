package lsieun.asm.tutorial.tree;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;

public class MyClassAdapter extends ClassVisitor {
    ClassVisitor next;
    public MyClassAdapter(int api, ClassVisitor cv) {
        super(api, new ClassNode());
        next = cv;
    }

    @Override
    public void visitEnd() {
        ClassNode cn = (ClassNode) cv;
        // put your transformation code here
        cn.accept(next);
    }
}

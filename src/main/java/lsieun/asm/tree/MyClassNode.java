package lsieun.asm.tree;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;

public class MyClassNode extends ClassNode {
    public MyClassNode(int api, ClassVisitor cv) {
        super(api);
        this.cv = cv;
    }

    @Override
    public void visitEnd() {
        // put your transformation code here
        super.visitEnd();

        accept(cv);
    }
}

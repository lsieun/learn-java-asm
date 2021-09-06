package lsieun.asm.tree.my;

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
        // 首先，处理自己的代码逻辑
        ClassNode cn = (ClassNode) cv;
        // put your transformation code here

        // 其次，调用父类的方法实现（根据实际情况，选择保留，或删除）
        super.visitEnd();

        // 最后，向后续ClassVisitor传递
        if (next != null) {
            cn.accept(next);
        }
    }
}

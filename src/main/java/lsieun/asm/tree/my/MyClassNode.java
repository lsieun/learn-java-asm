package lsieun.asm.tree.my;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;

public class MyClassNode extends ClassNode {
    public MyClassNode(int api, ClassVisitor cv) {
        super(api);
        this.cv = cv;
    }

    @Override
    public void visitEnd() {
        // 首先，处理自己的代码逻辑
        // put your transformation code here

        // 其次，调用父类的方法实现（根据实际情况，选择保留，或删除）
        super.visitEnd();

        // 最后，向后续ClassVisitor传递
        if (cv != null) {
            accept(cv);
        }
    }
}

package lsieun.asm.tree.my;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.MethodNode;

public class MyMethodNode extends MethodNode {
    public MyMethodNode(int access, String name, String descriptor,
                        String signature, String[] exceptions,
                        MethodVisitor mv) {
        super(access, name, descriptor, signature, exceptions);
        this.mv = mv;
    }

    @Override
    public void visitEnd() {
        // 首先，处理自己的代码逻辑
        // put your transformation code here

        // 其次，调用父类的方法实现（根据实际情况，选择保留，或删除）
        super.visitEnd();

        // 最后，向后续MethodVisitor传递
        if (mv != null) {
            accept(mv);
        }
    }
}

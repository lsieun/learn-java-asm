package lsieun.asm.tree.my;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.MethodNode;

public class MyMethodAdapter extends MethodVisitor {
    private final MethodVisitor next;

    public MyMethodAdapter(int api, int access, String name, String desc,
                           String signature, String[] exceptions, MethodVisitor mv) {
        super(api, new MethodNode(access, name, desc, signature, exceptions));
        this.next = mv;
    }

    @Override
    public void visitEnd() {
        // 首先，处理自己的代码逻辑
        MethodNode mn = (MethodNode) mv;
        // put your transformation code here

        // 其次，调用父类的方法实现（根据实际情况，选择保留，或删除）
        super.visitEnd();

        // 最后，向后续MethodVisitor传递
        if (next != null) {
            mn.accept(next);
        }
    }
}

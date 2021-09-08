package lsieun.asm.tree;

import lsieun.asm.template.MethodEnteringAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public class MixTree2CoreNode extends ClassNode {
    public MixTree2CoreNode(int api, ClassVisitor cv) {
        super(api);
        this.cv = cv;
    }

    @Override
    public void visitEnd() {
        // 首先，处理自己的代码逻辑
        int size = methods.size();
        for (int i = 0; i < size; i++) {
            MethodNode mn = methods.get(i);
            if ("<init>".equals(mn.name) || "<clinit>".equals(mn.name)) {
                continue;
            }
            InsnList instructions = mn.instructions;
            if (instructions.size() == 0) {
                continue;
            }

            int api = Opcodes.ASM9;
            MethodNode newMethodNode = new MethodNode(api, mn.access, mn.name, mn.desc, mn.signature, mn.exceptions.toArray(new String[0]));
            MethodVisitor mv = new MethodEnteringAdapter(api, newMethodNode, mn.access, mn.name, mn.desc);
            mn.accept(mv);
            methods.set(i, newMethodNode);
        }

        // 其次，调用父类的方法实现（根据实际情况，选择保留，或删除）
        super.visitEnd();

        // 最后，向后续ClassVisitor传递
        if (cv != null) {
            accept(cv);
        }
    }
}

package lsieun.asm.tree;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public class MethodOptimizeJumpTransformer extends ClassTransformer {
    public MethodOptimizeJumpTransformer(ClassTransformer ct) {
        super(ct);
    }

    @Override
    public void transform(ClassNode cn) {
        for (MethodNode mn : cn.methods) {
            if ("<init>".equals(mn.name) || "<clinit>".equals(mn.name)) {
                continue;
            }
            InsnList insns = mn.instructions;
            if (insns.size() == 0) {
                continue;
            }
            MethodTransformer mt = new MethodOptimizeJumpConverter(null);
            mt.transform(mn);
        }

        super.transform(cn);
    }
}

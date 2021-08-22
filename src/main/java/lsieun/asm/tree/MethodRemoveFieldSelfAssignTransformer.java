package lsieun.asm.tree;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public class MethodRemoveFieldSelfAssignTransformer extends ClassTransformer {

    public MethodRemoveFieldSelfAssignTransformer(ClassTransformer ct) {
        super(ct);
    }

    @Override
    public void transform(ClassNode cn) {
        for (MethodNode mn : cn.methods) {
            if ("<init>".equals(mn.name) || "<clinit>".equals(mn.name)) {
                continue;
            }
            InsnList instructions = mn.instructions;
            if (instructions.size() == 0) {
                continue;
            }
            MethodTransformer mc = new MethodRemoveFieldSelfAssignConverter(null);
            mc.transform(mn);
        }

        super.transform(cn);
    }
}

package lsieun.asm.tree;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class ClassAddMethodTransformer extends ClassTransformer {
    private final int methodAccess;
    private final String methodName;
    private final String methodDesc;

    public ClassAddMethodTransformer(ClassTransformer ct, int methodAccess, String methodName, String methodDesc) {
        super(ct);
        this.methodAccess = methodAccess;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    @Override
    public void transform(ClassNode cn) {
        boolean isPresent = false;
        for (MethodNode mn : cn.methods) {
            if (methodName.equals(mn.name) && methodDesc.equals(mn.desc)) {
                isPresent = true;
                break;
            }
        }
        if (!isPresent) {
            cn.methods.add(new MethodNode(methodAccess, methodName, methodDesc, null, null));
        }
        super.transform(cn);
    }
}

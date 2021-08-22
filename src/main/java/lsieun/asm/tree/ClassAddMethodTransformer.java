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
        // 首先，处理自己的代码逻辑
        boolean isPresent = false;
        for (MethodNode mn : cn.methods) {
            if (methodName.equals(mn.name) && methodDesc.equals(mn.desc)) {
                isPresent = true;
                break;
            }
        }
        if (!isPresent) {
            MethodNode mn = new MethodNode(methodAccess, methodName, methodDesc, null, null);
            cn.methods.add(mn);
            generateMethodBody(mn);
        }

        // 其次，调用父类的方法实现
        super.transform(cn);
    }

    protected void generateMethodBody(MethodNode mn) {
        // empty method
    }
}

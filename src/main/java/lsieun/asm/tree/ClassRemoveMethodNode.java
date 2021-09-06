package lsieun.asm.tree;

import lsieun.asm.tree.transformer.ClassTransformer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;

public class ClassRemoveMethodNode extends ClassNode {
    private final String methodName;
    private final String methodDesc;

    public ClassRemoveMethodNode(int api, ClassVisitor cv, String methodName, String methodDesc) {
        super(api);
        this.cv = cv;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
    }

    @Override
    public void visitEnd() {
        // 首先，处理自己的代码逻辑
        ClassTransformer ct = new ClassRemoveMethodTransformer(null, methodName, methodDesc);
        ct.transform(this);

        // 其次，调用父类的方法实现（根据实际情况，选择保留，或删除）
        super.visitEnd();

        // 最后，向后续ClassVisitor传递
        if (cv != null) {
            accept(cv);
        }
    }

    private static class ClassRemoveMethodTransformer extends ClassTransformer {
        private final String methodName;
        private final String methodDesc;

        public ClassRemoveMethodTransformer(ClassTransformer ct, String methodName, String methodDesc) {
            super(ct);
            this.methodName = methodName;
            this.methodDesc = methodDesc;
        }

        @Override
        public void transform(ClassNode cn) {
            // 首先，处理自己的代码逻辑
            cn.methods.removeIf(mn -> methodName.equals(mn.name) && methodDesc.equals(mn.desc));

            // 其次，调用父类的方法实现
            super.transform(cn);
        }
    }
}

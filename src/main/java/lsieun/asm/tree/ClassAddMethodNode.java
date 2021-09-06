package lsieun.asm.tree;

import lsieun.asm.tree.transformer.ClassTransformer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.function.Consumer;

public class ClassAddMethodNode extends ClassNode {
    private final int methodAccess;
    private final String methodName;
    private final String methodDesc;
    private final Consumer<MethodNode> methodBody;

    public ClassAddMethodNode(int api, ClassVisitor cv,
                              int methodAccess, String methodName, String methodDesc,
                              Consumer<MethodNode> methodBody) {
        super(api);
        this.cv = cv;
        this.methodAccess = methodAccess;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
        this.methodBody = methodBody;
    }

    @Override
    public void visitEnd() {
        // 首先，处理自己的代码逻辑
        ClassTransformer ct = new ClassAddMethodTransformer(null, methodAccess, methodName, methodDesc, methodBody);
        ct.transform(this);

        // 其次，调用父类的方法实现（根据实际情况，选择保留，或删除）
        super.visitEnd();

        // 最后，向后续ClassVisitor传递
        if (cv != null) {
            accept(cv);
        }
    }

    private static class ClassAddMethodTransformer extends ClassTransformer {
        private final int methodAccess;
        private final String methodName;
        private final String methodDesc;
        private final Consumer<MethodNode> methodBody;

        public ClassAddMethodTransformer(ClassTransformer ct,
                                         int methodAccess, String methodName, String methodDesc,
                                         Consumer<MethodNode> methodBody) {
            super(ct);
            this.methodAccess = methodAccess;
            this.methodName = methodName;
            this.methodDesc = methodDesc;
            this.methodBody = methodBody;
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

                if (methodBody != null) {
                    methodBody.accept(mn);
                }
            }

            // 其次，调用父类的方法实现
            super.transform(cn);
        }
    }
}

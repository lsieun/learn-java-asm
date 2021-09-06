package lsieun.asm.tree;

import lsieun.asm.tree.transformer.ClassTransformer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;

public class ClassRemoveFieldNode extends ClassNode {
    private final String fieldName;
    private final String fieldDesc;

    public ClassRemoveFieldNode(int api, ClassVisitor cv, String fieldName, String fieldDesc) {
        super(api);
        this.cv = cv;
        this.fieldName = fieldName;
        this.fieldDesc = fieldDesc;
    }

    @Override
    public void visitEnd() {
        // 首先，处理自己的代码逻辑
        ClassTransformer ct = new ClassRemoveFieldTransformer(null, fieldName, fieldDesc);
        ct.transform(this);

        // 其次，调用父类的方法实现（根据实际情况，选择保留，或删除）
        super.visitEnd();

        // 最后，向后续ClassVisitor传递
        if (cv != null) {
            accept(cv);
        }
    }

    private static class ClassRemoveFieldTransformer extends ClassTransformer {
        private final String fieldName;
        private final String fieldDesc;

        public ClassRemoveFieldTransformer(ClassTransformer ct, String fieldName, String fieldDesc) {
            super(ct);
            this.fieldName = fieldName;
            this.fieldDesc = fieldDesc;
        }

        @Override
        public void transform(ClassNode cn) {
            // 首先，处理自己的代码逻辑
            cn.fields.removeIf(fn -> fieldName.equals(fn.name) && fieldDesc.equals(fn.desc));

            // 其次，调用父类的方法实现
            super.transform(cn);
        }
    }
}

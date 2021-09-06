package lsieun.asm.tree;

import lsieun.asm.tree.transformer.ClassTransformer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class ClassAddFieldNode extends ClassNode {
    private final int fieldAccess;
    private final String fieldName;
    private final String fieldDesc;

    public ClassAddFieldNode(int api, ClassVisitor cv,
                             int fieldAccess, String fieldName, String fieldDesc) {
        super(api);
        this.cv = cv;
        this.fieldAccess = fieldAccess;
        this.fieldName = fieldName;
        this.fieldDesc = fieldDesc;
    }

    @Override
    public void visitEnd() {
        // 首先，处理自己的代码逻辑
        ClassTransformer ct = new ClassAddFieldTransformer(null, fieldAccess, fieldName, fieldDesc);
        ct.transform(this);

        // 其次，调用父类的方法实现（根据实际情况，选择保留，或删除）
        super.visitEnd();

        // 最后，向后续ClassVisitor传递
        if (cv != null) {
            accept(cv);
        }
    }

    private static class ClassAddFieldTransformer extends ClassTransformer {
        private final int fieldAccess;
        private final String fieldName;
        private final String fieldDesc;

        public ClassAddFieldTransformer(ClassTransformer ct, int fieldAccess, String fieldName, String fieldDesc) {
            super(ct);
            this.fieldAccess = fieldAccess;
            this.fieldName = fieldName;
            this.fieldDesc = fieldDesc;
        }

        @Override
        public void transform(ClassNode cn) {
            // 首先，处理自己的代码逻辑
            boolean isPresent = false;
            for (FieldNode fn : cn.fields) {
                if (fieldName.equals(fn.name)) {
                    isPresent = true;
                    break;
                }
            }
            if (!isPresent) {
                cn.fields.add(new FieldNode(fieldAccess, fieldName, fieldDesc, null, null));
            }

            // 其次，调用父类的方法实现
            super.transform(cn);
        }
    }
}

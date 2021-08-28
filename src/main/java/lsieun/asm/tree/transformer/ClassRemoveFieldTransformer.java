package lsieun.asm.tree.transformer;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.Iterator;

public class ClassRemoveFieldTransformer extends ClassTransformer {
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
        Iterator<FieldNode> it = cn.fields.iterator();
        while (it.hasNext()) {
            FieldNode fn = it.next();
            if (fieldName.equals(fn.name) && fieldDesc.equals(fn.desc)) {
                it.remove();
            }
        }

        // 其次，调用父类的方法实现
        super.transform(cn);
    }
}

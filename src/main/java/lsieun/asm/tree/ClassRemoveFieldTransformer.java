package lsieun.asm.tree;

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
        Iterator<FieldNode> it = cn.fields.iterator();
        while (it.hasNext()) {
            FieldNode fn = it.next();
            if (fieldName.equals(fn.name) && fieldDesc.equals(fn.desc)) {
                it.remove();
            }
        }
        super.transform(cn);
    }
}

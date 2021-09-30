package lsieun.asm.tree;

import lsieun.asm.template.CustomAttribute;
import lsieun.utils.ByteUtils;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;

public class ClassAddCustomAttributeNode extends ClassNode {
    private final String attrName;

    public ClassAddCustomAttributeNode(int api, ClassVisitor cv, String attrName) {
        super(api);
        this.cv = cv;
        this.attrName = attrName;
    }

    @Override
    public void visitEnd() {
        // 首先，处理自己的代码逻辑
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        for (FieldNode fn : fields) {
            sb.append(fn.name);
        }
        for (MethodNode mn : methods) {
            sb.append(mn.name);
        }
        int hashCode = sb.toString().hashCode();
        byte[] info = ByteUtils.intToByteArray(hashCode);
        Attribute customAttribute = new CustomAttribute(attrName, info);
        if (attrs == null) {
            attrs = new ArrayList<>();
        }
        attrs.add(customAttribute);

        // 其次，调用父类的方法实现（根据实际情况，选择保留，或删除）
        super.visitEnd();

        // 最后，向后续ClassVisitor传递
        if (cv != null) {
            accept(cv);
        }
    }
}

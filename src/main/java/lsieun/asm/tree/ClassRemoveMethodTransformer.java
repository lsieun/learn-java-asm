package lsieun.asm.tree;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;

public class ClassRemoveMethodTransformer extends ClassTransformer {
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
        Iterator<MethodNode> it = cn.methods.iterator();
        while (it.hasNext()) {
            MethodNode mn = it.next();
            if (methodName.equals(mn.name) && methodDesc.equals(mn.desc)) {
                it.remove();
            }
        }

        // 其次，调用父类的方法实现
        super.transform(cn);
    }
}

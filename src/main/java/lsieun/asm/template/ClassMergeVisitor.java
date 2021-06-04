package lsieun.asm.template;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

public class ClassMergeVisitor extends ClassVisitor {
    private final ClassNode anotherClass;

    public ClassMergeVisitor(int api, ClassVisitor classVisitor, ClassNode anotherClass) {
        super(api, classVisitor);
        this.anotherClass = anotherClass;
    }

    @Override
    public void visitEnd() {
        List<FieldNode> fields = anotherClass.fields;
        for (FieldNode fn : fields) {
            fn.accept(this);
        }

        List<MethodNode> methods = anotherClass.methods;
        for (MethodNode mn : methods) {
            String methodName = mn.name;
            if ("<init>".equals(methodName)) {
                continue;
            }
            mn.accept(this);
        }
        super.visitEnd();
    }
}

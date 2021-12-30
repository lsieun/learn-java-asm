package lsieun.asm.tree;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

public class ChangeMemberNameBasedOnAnnotationNode extends ClassNode {
    private final String annotationDescriptor;

    public ChangeMemberNameBasedOnAnnotationNode(int api, ClassVisitor cv, String annotationDescriptor) {
        super(api);
        this.annotationDescriptor = annotationDescriptor;
        this.cv = cv;
    }

    @Override
    public void visitEnd() {
        // 首先，处理自己的代码逻辑
        for (MethodNode mn : methods) {
            boolean flag = exists(annotationDescriptor, mn.visibleAnnotations);
            if (!flag) flag = exists(annotationDescriptor, mn.invisibleAnnotations);

            if (flag) {
                mn.name = "changed_" + mn.name;
            }
        }

        // 其次，调用父类的方法实现（根据实际情况，选择保留，或删除）
        super.visitEnd();

        // 最后，向后续ClassVisitor传递
        if (cv != null) {
            accept(cv);
        }
    }

    private boolean exists(String name, List<AnnotationNode> annotations) {
        if (annotations == null) return false;
        for (AnnotationNode an : annotations) {
            if (an.desc.equals(name)) {
                return true;
            }
        }
        return false;
    }
}

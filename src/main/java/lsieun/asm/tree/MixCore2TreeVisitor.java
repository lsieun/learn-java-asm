package lsieun.asm.tree;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class MixCore2TreeVisitor extends ClassVisitor {
    public MixCore2TreeVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null && !"<init>".equals(name) && !"<clinit>".equals(name)) {
            boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;
            boolean isNativeMethod = (access & ACC_NATIVE) != 0;
            if (!isAbstractMethod && !isNativeMethod) {
                mv = new MethodEnterNode(api, access, name, descriptor, signature, exceptions, mv);
            }
        }
        return mv;
    }

    private static class MethodEnterNode extends MethodNode {
        public MethodEnterNode(int api, int access, String name, String descriptor,
                               String signature, String[] exceptions,
                               MethodVisitor mv) {
            super(api, access, name, descriptor, signature, exceptions);
            this.mv = mv;
        }

        @Override
        public void visitEnd() {
            // 首先，处理自己的代码逻辑
            InsnList il = new InsnList();
            il.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
            il.add(new LdcInsnNode("Method Enter"));
            il.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
            instructions.insert(il);

            // 其次，调用父类的方法实现（根据实际情况，选择保留，或删除）
            super.visitEnd();

            // 最后，向后续MethodVisitor传递
            if (mv != null) {
                accept(mv);
            }
        }
    }
}

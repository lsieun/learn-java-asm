package lsieun.asm.analysis;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.*;

public class RemoveDeadCodeVisitor extends ClassVisitor {
    private String owner;

    public RemoveDeadCodeVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.owner = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null) {
            mv = new MethodRemoveDeadCodeAdapter(api, owner, access, name, descriptor, mv);
        }
        return mv;
    }

    private static class MethodRemoveDeadCodeAdapter extends MethodVisitor {
        private final String owner;
        private final MethodVisitor next;

        public MethodRemoveDeadCodeAdapter(int api, String owner, int access, String name, String desc, MethodVisitor next) {
            super(api, new MethodNode(access, name, desc, null, null));
            this.owner = owner;
            this.next = next;
        }

        @Override
        public void visitEnd() {
            // 首先，处理自己的代码逻辑
            MethodNode mn = (MethodNode) mv;
            Analyzer<BasicValue> analyzer = new Analyzer<>(new BasicInterpreter());
            try {
                analyzer.analyze(owner, mn);
                Frame<BasicValue>[] frames = analyzer.getFrames();
                AbstractInsnNode[] insnNodes = mn.instructions.toArray();
                for (int i = 0; i < frames.length; i++) {
                    if (frames[i] == null && !(insnNodes[i] instanceof LabelNode)) {
                        mn.instructions.remove(insnNodes[i]);
                    }
                }
            }
            catch (AnalyzerException ex) {
                ex.printStackTrace();
            }

            // 其次，调用父类的方法实现
            super.visitEnd();

            // 最后，向后续MethodVisitor传递
            if (next != null) {
                mn.accept(next);
            }
        }
    }
}

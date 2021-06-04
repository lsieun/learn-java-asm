package lsieun.asm.analysis;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.*;

public class MethodRemoveDeadCodeAdapter extends MethodVisitor {
    private final String owner;
    private final MethodVisitor next;

    public MethodRemoveDeadCodeAdapter(int api, String owner, int access, String name, String desc, MethodVisitor next) {
        super(api, new MethodNode(access, name, desc, null, null));
        this.owner = owner;
        this.next = next;
    }

    @Override
    public void visitEnd() {
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
        } catch (AnalyzerException e) {
            e.printStackTrace();
        }

        if (next != null) {
            mn.accept(next);
        }
    }
}

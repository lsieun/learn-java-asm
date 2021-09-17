package lsieun.asm.analysis;

import lsieun.asm.tree.transformer.MethodTransformer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;

import static org.objectweb.asm.Opcodes.CHECKCAST;

public class RemoveUnusedCastNode extends ClassNode {
    public RemoveUnusedCastNode(int api, ClassVisitor cv) {
        super(api);
        this.cv = cv;
    }

    @Override
    public void visitEnd() {
        // 首先，处理自己的代码逻辑
        MethodTransformer mt = new MethodRemoveUnusedCastTransformer(name, null);
        for (MethodNode mn : methods) {
            if ("<init>".equals(mn.name) || "<clinit>".equals(mn.name)) {
                continue;
            }
            InsnList insns = mn.instructions;
            if (insns.size() == 0) {
                continue;
            }
            mt.transform(mn);
        }

        // 其次，调用父类的方法实现
        super.visitEnd();

        // 最后，向后续ClassVisitor传递
        if (cv != null) {
            accept(cv);
        }
    }

    private static class MethodRemoveUnusedCastTransformer extends MethodTransformer {
        private final String owner;

        public MethodRemoveUnusedCastTransformer(String owner, MethodTransformer mt) {
            super(mt);
            this.owner = owner;
        }

        @Override
        public void transform(MethodNode mn) {
            // 首先，处理自己的代码逻辑
            Analyzer<BasicValue> analyzer = new Analyzer<>(new SimpleVerifier());
            try {
                Frame<BasicValue>[] frames = analyzer.analyze(owner, mn);
                AbstractInsnNode[] insnNodes = mn.instructions.toArray();
                for (int i = 0; i < insnNodes.length; i++) {
                    AbstractInsnNode insn = insnNodes[i];
                    if (insn.getOpcode() == CHECKCAST) {
                        Frame<BasicValue> f = frames[i];
                        if (f != null && f.getStackSize() > 0) {
                            BasicValue operand = f.getStack(f.getStackSize() - 1);
                            Class<?> to = getClass(((TypeInsnNode) insn).desc);
                            Class<?> from = getClass(operand.getType());
                            if (to.isAssignableFrom(from)) {
                                mn.instructions.remove(insn);
                            }
                        }
                    }
                }
            }
            catch (AnalyzerException ex) {
                ex.printStackTrace();
            }

            // 其次，调用父类的方法实现
            super.transform(mn);
        }

        private static Class<?> getClass(String desc) {
            try {
                return Class.forName(desc.replace('/', '.'));
            }
            catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex.toString());
            }
        }

        private static Class<?> getClass(Type t) {
            if (t.getSort() == Type.OBJECT) {
                return getClass(t.getInternalName());
            }
            return getClass(t.getDescriptor());
        }
    }
}

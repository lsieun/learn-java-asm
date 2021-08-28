package lsieun.asm.analysis.transformer;

import lsieun.asm.tree.transformer.MethodTransformer;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.analysis.*;

import static org.objectweb.asm.Opcodes.*;

public class MethodRemoveUnusedCastTransformer extends MethodTransformer {
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

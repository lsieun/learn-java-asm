package lsieun.asm.tree;

import lsieun.asm.tree.transformer.MethodTransformer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class OptimizeJumpNode extends ClassNode {
    public OptimizeJumpNode(int api, ClassVisitor cv) {
        super(api);
        this.cv = cv;
    }

    @Override
    public void visitEnd() {
        // 首先，处理自己的代码逻辑
        MethodTransformer mt = new MethodOptimizeJumpTransformer(null);
        for (MethodNode mn : methods) {
            if ("<init>".equals(mn.name) || "<clinit>".equals(mn.name)) {
                continue;
            }
            InsnList instructions = mn.instructions;
            if (instructions.size() == 0) {
                continue;
            }
            mt.transform(mn);
        }

        // 其次，调用父类的方法实现（根据实际情况，选择保留，或删除）
        super.visitEnd();

        // 最后，向后续ClassVisitor传递
        if (cv != null) {
            accept(cv);
        }
    }

    private static class MethodOptimizeJumpTransformer extends MethodTransformer {
        public MethodOptimizeJumpTransformer(MethodTransformer mt) {
            super(mt);
        }

        @Override
        public void transform(MethodNode mn) {
            // 首先，处理自己的代码逻辑
            InsnList instructions = mn.instructions;
            for (AbstractInsnNode insnNode : instructions) {
                if (insnNode instanceof JumpInsnNode) {
                    JumpInsnNode jumpInsnNode = (JumpInsnNode) insnNode;
                    LabelNode label = jumpInsnNode.label;
                    AbstractInsnNode target;
                    while (true) {
                        target = label;
                        while (target != null && target.getOpcode() < 0) {
                            target = target.getNext();
                        }

                        if (target != null && target.getOpcode() == GOTO) {
                            label = ((JumpInsnNode) target).label;
                        }
                        else {
                            break;
                        }
                    }

                    // update target
                    jumpInsnNode.label = label;
                    // if possible, replace jump with target instruction
                    if (insnNode.getOpcode() == GOTO && target != null) {
                        int opcode = target.getOpcode();
                        if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
                            instructions.set(insnNode, target.clone(null));
                        }
                    }
                }
            }

            // 其次，调用父类的方法实现
            super.transform(mn);
        }
    }
}

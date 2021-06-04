package lsieun.asm.tree;

import org.objectweb.asm.tree.*;

import java.util.ListIterator;

import static org.objectweb.asm.Opcodes.*;

public class MethodOptimizeJumpTransformer extends MethodTransformer {
    public MethodOptimizeJumpTransformer(MethodTransformer mt) {
        super(mt);
    }

    @Override
    public void transform(MethodNode mn) {
        InsnList instructions = mn.instructions;
        ListIterator<AbstractInsnNode> it = instructions.iterator();
        while (it.hasNext()) {
            AbstractInsnNode insnNode = it.next();
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
        super.transform(mn);
    }
}

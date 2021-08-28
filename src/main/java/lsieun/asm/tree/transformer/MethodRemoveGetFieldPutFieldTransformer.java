package lsieun.asm.tree.transformer;

import org.objectweb.asm.tree.*;

import java.util.ListIterator;

import static org.objectweb.asm.Opcodes.*;

public class MethodRemoveGetFieldPutFieldTransformer extends MethodTransformer {
    public MethodRemoveGetFieldPutFieldTransformer(MethodTransformer mt) {
        super(mt);
    }

    @Override
    public void transform(MethodNode mn) {
        InsnList instructions = mn.instructions;
        ListIterator<AbstractInsnNode> it = instructions.iterator();
        while (it.hasNext()) {
            AbstractInsnNode node1 = it.next();
            if (isALOAD0(node1)) {
                AbstractInsnNode node2 = getNext(node1);
                if (node2 != null && isALOAD0(node2)) {
                    AbstractInsnNode node3 = getNext(node2);
                    if (node3 != null && node3.getOpcode() == GETFIELD) {
                        AbstractInsnNode node4 = getNext(node3);
                        if (node4 != null && node4.getOpcode() == PUTFIELD) {
                            if (sameField(node3, node4)) {
                                while (it.next() != node4) {
                                }
                                instructions.remove(node1);
                                instructions.remove(node2);
                                instructions.remove(node3);
                                instructions.remove(node4);
                            }
                        }
                    }
                }
            }
        }

        super.transform(mn);
    }

    private static AbstractInsnNode getNext(AbstractInsnNode insn) {
        do {
            insn = insn.getNext();
            if (insn != null && !(insn instanceof LineNumberNode)) {
                break;
            }
        } while (insn != null);
        return insn;
    }

    private static boolean isALOAD0(AbstractInsnNode insnNode) {
        return insnNode.getOpcode() == ALOAD && ((VarInsnNode) insnNode).var == 0;
    }

    private static boolean sameField(AbstractInsnNode oneInsnNode, AbstractInsnNode anotherInsnNode) {
        if (!(oneInsnNode instanceof FieldInsnNode)) return false;
        if (!(anotherInsnNode instanceof FieldInsnNode)) return false;
        FieldInsnNode fieldInsnNode1 = (FieldInsnNode) oneInsnNode;
        FieldInsnNode fieldInsnNode2 = (FieldInsnNode) anotherInsnNode;
        String name1 = fieldInsnNode1.name;
        String name2 = fieldInsnNode2.name;
        return name1.equals(name2);
    }
}

package lsieun.asm.tree;

import lsieun.asm.tree.transformer.MethodTransformer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

import static org.objectweb.asm.Opcodes.*;

public class RemoveGetFieldPutFieldNode extends ClassNode {
    public RemoveGetFieldPutFieldNode(int api, ClassVisitor cv) {
        super(api);
        this.cv = cv;
    }

    @Override
    public void visitEnd() {
        // 首先，处理自己的代码逻辑
        MethodTransformer mt = new MethodRemoveGetFieldPutFieldTransformer(null);
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

    private static class MethodRemoveGetFieldPutFieldTransformer extends MethodTransformer {
        public MethodRemoveGetFieldPutFieldTransformer(MethodTransformer mt) {
            super(mt);
        }

        @Override
        public void transform(MethodNode mn) {
            // 首先，处理自己的代码逻辑
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

            // 其次，调用父类的方法实现
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
}

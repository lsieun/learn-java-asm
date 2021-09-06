package lsieun.asm.tree;

import lsieun.asm.tree.transformer.ClassTransformer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class ClassAddTimerNode extends ClassNode {
    public ClassAddTimerNode(int api, ClassVisitor cv) {
        super(api);
        this.cv = cv;
    }

    @Override
    public void visitEnd() {
        // 首先，处理自己的代码逻辑
        ClassTransformer ct = new ClassAddTimerTransformer(null);
        ct.transform(this);

        // 其次，调用父类的方法实现（根据实际情况，选择保留，或删除）
        super.visitEnd();

        // 最后，向后续ClassVisitor传递
        if (cv != null) {
            accept(cv);
        }
    }

    private static class ClassAddTimerTransformer extends ClassTransformer {
        public ClassAddTimerTransformer(ClassTransformer ct) {
            super(ct);
        }

        @Override
        public void transform(ClassNode cn) {
            for (MethodNode mn : cn.methods) {
                if ("<init>".equals(mn.name) || "<clinit>".equals(mn.name)) {
                    continue;
                }
                InsnList instructions = mn.instructions;
                if (instructions.size() == 0) {
                    continue;
                }
                for (AbstractInsnNode item : instructions) {
                    int opcode = item.getOpcode();
                    // 在方法退出之前，加上当前时间戳
                    if ((opcode >= IRETURN && opcode <= RETURN) || (opcode == ATHROW)) {
                        InsnList il = new InsnList();
                        il.add(new FieldInsnNode(GETSTATIC, cn.name, "timer", "J"));
                        il.add(new MethodInsnNode(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J"));
                        il.add(new InsnNode(LADD));
                        il.add(new FieldInsnNode(PUTSTATIC, cn.name, "timer", "J"));
                        instructions.insertBefore(item, il);
                    }
                }

                // 在方法刚进入之后，减去当前时间戳
                InsnList il = new InsnList();
                il.add(new FieldInsnNode(GETSTATIC, cn.name, "timer", "J"));
                il.add(new MethodInsnNode(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J"));
                il.add(new InsnNode(LSUB));
                il.add(new FieldInsnNode(PUTSTATIC, cn.name, "timer", "J"));
                instructions.insert(il);

                // local variables的大小，保持不变
                // mn.maxLocals = mn.maxLocals;
                // operand stack的大小，增加4个位置
                mn.maxStack += 4;
            }

            int acc = ACC_PUBLIC | ACC_STATIC;
            cn.fields.add(new FieldNode(acc, "timer", "J", null, null));
            super.transform(cn);
        }
    }
}

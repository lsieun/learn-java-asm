package run;

import lsieun.utils.FileUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

public class HelloWorldGenerateTree {
    public static void main(String[] args) throws Exception {
        String relative_path = "sample/HelloWorld.class";
        String filepath = FileUtils.getFilePath(relative_path);

        // (1) 生成byte[]内容
        byte[] bytes = dump();

        // (2) 保存byte[]到文件
        FileUtils.writeBytes(filepath, bytes);
    }

    public static byte[] dump() throws Exception {
        ClassNode cn = new ClassNode();
        cn.version = V1_8;
        cn.access = ACC_PUBLIC | ACC_SUPER;
        cn.name = "sample/HelloWorld";
        cn.superName = "java/lang/Object";

        {
            MethodNode mn1 = new MethodNode(ACC_PUBLIC, "<init>", "()V", null, null);
            cn.methods.add(mn1);

            InsnList il = mn1.instructions;
            il.add(new VarInsnNode(ALOAD, 0));
            il.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false));
            il.add(new InsnNode(RETURN));
            mn1.maxLocals = 0;
            mn1.maxStack = 0;
        }

        {
            MethodNode mn2 = new MethodNode(ACC_PUBLIC, "test", "()V", null, null);
            cn.methods.add(mn2);

            LabelNode startLabelNode = new LabelNode();
            LabelNode endLabelNode = new LabelNode();
            LabelNode handlerLabelNode = new LabelNode();
            LabelNode returnLabelNode = new LabelNode();
            TryCatchBlockNode tryCatchBlockNode = new TryCatchBlockNode(startLabelNode, endLabelNode, handlerLabelNode, "java/lang/InterruptedException");
            mn2.tryCatchBlocks.add(tryCatchBlockNode);


            InsnList il = mn2.instructions;
            il.add(startLabelNode);
            il.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
            il.add(new LdcInsnNode("Before Sleep"));
            il.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
            il.add(new LdcInsnNode(Long.valueOf(1000L)));
            il.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false));
            il.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
            il.add(new LdcInsnNode("After Sleep"));
            il.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));


            il.add(endLabelNode);
            il.add(new JumpInsnNode(GOTO, returnLabelNode)); // goto jump


            il.add(handlerLabelNode);
            il.add(new VarInsnNode(ASTORE, 1));
            il.add(new VarInsnNode(ALOAD, 1));
            il.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/InterruptedException", "printStackTrace", "()V", false));


            il.add(returnLabelNode); // return label
            il.add(new InsnNode(RETURN));


            mn2.maxLocals = 0;
            mn2.maxStack = 0;
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);
        return cw.toByteArray();
    }
}

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
        // (1) 使用ClassNode类收集数据
        ClassNode cn = new ClassNode();
        cn.version = V1_8;
        cn.access = ACC_PUBLIC | ACC_SUPER;
        cn.name = "sample/HelloWorld";
        cn.signature = null;
        cn.superName = "java/lang/Object";

        {
            MethodNode mn1 = new MethodNode(ACC_PUBLIC, "<init>", "()V", null, null);
            cn.methods.add(mn1);

            InsnList il = mn1.instructions;
            il.add(new VarInsnNode(ALOAD, 0));
            il.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false));
            il.add(new InsnNode(RETURN));

            mn1.maxStack = 1;
            mn1.maxLocals = 1;
        }

        {
            MethodNode mn2 = new MethodNode(ACC_PUBLIC, "test", "()V", null, null);
            cn.methods.add(mn2);

            InsnList il = mn2.instructions;
            il.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
            il.add(new LdcInsnNode("Hello World"));
            il.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
            il.add(new InsnNode(RETURN));

            mn2.maxStack = 2;
            mn2.maxLocals = 1;
        }

        // (2) 使用ClassWriter类生成字节码
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);
        return cw.toByteArray();
    }
}

package lsieun.asm.commons;

import lsieun.utils.FileUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;

import static org.objectweb.asm.Opcodes.*;

public class InstructionAdapterExample01 {
    public static void main(String[] args) throws Exception {
        String relative_path = "sample/HelloWorld.class";
        String filepath = FileUtils.getFilePath(relative_path);

        // (1) 生成byte[]内容
        byte[] bytes = dump();

        // (2) 保存byte[]到文件
        FileUtils.writeBytes(filepath, bytes);
    }

    public static byte[] dump() throws Exception {
        // (1) 创建ClassWriter对象
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        // (2) 调用visitXxx()方法
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, "sample/HelloWorld",
                null, "java/lang/Object", null);

        {
            MethodVisitor mv1 = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            InstructionAdapter ia = new InstructionAdapter(mv1);
            ia.visitCode();
            ia.load(0, InstructionAdapter.OBJECT_TYPE);
            ia.invokespecial("java/lang/Object", "<init>", "()V", false);
            ia.areturn(Type.VOID_TYPE);
            ia.visitMaxs(1, 1);
            ia.visitEnd();
        }

        {
            MethodVisitor mv2 = cw.visitMethod(ACC_PUBLIC, "test", "()V", null, null);
            InstructionAdapter ia = new InstructionAdapter(mv2);
            ia.visitCode();
            ia.getstatic("java/lang/System", "out", "Ljava/io/PrintStream;");
            ia.aconst("Hello World");
            ia.invokevirtual("java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            ia.areturn(Type.VOID_TYPE);
            ia.visitMaxs(2, 1);
            ia.visitEnd();
        }

        cw.visitEnd();

        // (3) 调用toByteArray()方法
        return cw.toByteArray();
    }
}

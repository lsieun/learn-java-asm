package lsieun.asm.util;

import lsieun.utils.FileUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;

import static org.objectweb.asm.Opcodes.*;

public class TraceClassVisitorExample01Generate {
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
        PrintWriter printWriter = new PrintWriter(System.out);
        TraceClassVisitor cv = new TraceClassVisitor(cw, printWriter);

        // (2) 调用visitXxx()方法
        cv.visit(V1_8, ACC_PUBLIC + ACC_SUPER, "sample/HelloWorld", null, "java/lang/Object", null);

        {
            FieldVisitor fv1 = cv.visitField(ACC_PRIVATE, "intValue", "I", null, null);
            fv1.visitEnd();
        }

        {
            FieldVisitor fv2 = cv.visitField(ACC_PRIVATE, "strValue", "Ljava/lang/String;", null, null);
            fv2.visitEnd();
        }

        {
            MethodVisitor mv1 = cv.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv1.visitCode();
            mv1.visitVarInsn(ALOAD, 0);
            mv1.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv1.visitInsn(RETURN);
            mv1.visitMaxs(0, 0);
            mv1.visitEnd();
        }

        {
            MethodVisitor mv2 = cv.visitMethod(ACC_PUBLIC, "test", "()V", null, null);
            mv2.visitCode();
            mv2.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv2.visitLdcInsn("Hello World");
            mv2.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            mv2.visitInsn(RETURN);
            mv2.visitMaxs(2, 1);
            mv2.visitEnd();
        }

        cv.visitEnd();

        // (3) 调用toByteArray()方法
        return cw.toByteArray();
    }
}
package lsieun.asm.commons;

import lsieun.utils.FileUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintStream;
import java.io.PrintWriter;

import static org.objectweb.asm.Opcodes.*;

public class GeneratorAdapterExample01 {
    public static void main(String[] args) throws Exception {
        String relative_path = "sample/HelloWorld.class";
        String filepath = FileUtils.getFilePath(relative_path);

        // (1) 生成byte[]内容
        byte[] bytes = dump();

        // (2) 保存byte[]到文件
        FileUtils.writeBytes(filepath, bytes);
    }

    public static byte[] dump() throws Exception {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        PrintWriter printWriter = new PrintWriter(System.out);
        TraceClassVisitor cv = new TraceClassVisitor(cw, printWriter);

        cv.visit(V1_8, ACC_PUBLIC + ACC_SUPER, "sample/HelloWorld", null, "java/lang/Object", null);

        {
            Method m1 = Method.getMethod("void <init> ()");
            GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC, m1, null, null, cv);
            mg.loadThis();
            mg.invokeConstructor(Type.getType(Object.class), m1);
            mg.returnValue();
            mg.endMethod();
        }

        {
            Method m2 = Method.getMethod("void main (String[])");
            GeneratorAdapter mg = new GeneratorAdapter(ACC_PUBLIC + ACC_STATIC, m2, null, null, cv);
            mg.getStatic(Type.getType(System.class), "out", Type.getType(PrintStream.class));
            mg.push("Hello world!");
            mg.invokeVirtual(Type.getType(PrintStream.class), Method.getMethod("void println (String)"));
            mg.returnValue();
            mg.endMethod();
        }

        cv.visitEnd();

        return cw.toByteArray();
    }
}

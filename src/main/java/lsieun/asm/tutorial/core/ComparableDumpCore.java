package lsieun.asm.tutorial.core;

import lsieun.utils.FileUtils;
import org.objectweb.asm.ClassWriter;

import static org.objectweb.asm.Opcodes.*;

public class ComparableDumpCore {
    public static void main(String[] args) {
        ClassWriter cw = new ClassWriter(0);
        cw.visit(V1_8, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, "sample/Comparable",
                null, "java/lang/Object", new String[]{"sample/Measurable"});
        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "LESS", "I",
                null, Integer.valueOf(-1)).visitEnd();
        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "EQUAL", "I",
                null, Integer.valueOf(0)).visitEnd();
        cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, "GREATER", "I",
                null, Integer.valueOf(1)).visitEnd();
        cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "compareTo", "(Ljava/lang/Object;)I",
                null, null).visitEnd();
        cw.visitEnd();
        byte[] bytes = cw.toByteArray();

        MyClassLoader myClassLoader = new MyClassLoader();
        Class<?> c = myClassLoader.defineClass("sample.Comparable", bytes);
        System.out.println(c.getName());

        String relative_path = "sample/Comparable.class";
        String filepath = FileUtils.getFilePath(relative_path);
        FileUtils.writeBytes(filepath, bytes);
    }
}

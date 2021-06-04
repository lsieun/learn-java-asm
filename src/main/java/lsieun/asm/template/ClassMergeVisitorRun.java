package lsieun.asm.template;

import lsieun.utils.FileUtils;
import lsieun.utils.ReadUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;

public class ClassMergeVisitorRun {
    public static void main(String[] args) {
        String first_class = "sample/HelloWorld";
        String second_class = "sample/GoodChild";
        String filepath = getFilePath(first_class);
        byte[] bytes1 = ReadUtils.readByPath(filepath);

        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes1);

        //（2）构建ClassWriter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        //（3）串连ClassVisitor
        byte[] second_class_bytes = rename(second_class, first_class);
        ClassNode cn = getClassNode(second_class_bytes);
        List<String> interface_list = cn.interfaces;
        int size = interface_list.size();
        String[] interfaces = new String[size];
        for (int i = 0; i < size; i++) {
            String item = interface_list.get(i);
            interfaces[i] = item;
        }
        ClassVisitor cv = new ClassMergeVisitor(Opcodes.ASM9, cw, cn);
        cv = new ClassAddInterfaceVisitor(Opcodes.ASM9, cv, interfaces);

        //（4）两者进行结合
        cr.accept(cv, 0);

        //（5）重新生成Class
        byte[] bytes2 = cw.toByteArray();

        FileUtils.writeBytes(filepath, bytes2);
    }

    public static ClassNode getClassNode(byte[] bytes) {
        ClassReader cr = new ClassReader(bytes);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        return cn;
    }

    public static byte[] rename(String origin_name, String target_name) {
        String origin_filepath = getFilePath(origin_name);
        byte[] bytes1 = ReadUtils.readByPath(origin_filepath);

        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes1);

        //（2）构建ClassWriter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        //（3）串连ClassVisitor
        ClassVisitor cv = new ClassRenameAdapter(Opcodes.ASM9, cw, origin_name, target_name);

        //（4）两者进行结合
        cr.accept(cv, 0);

        //（5）重新生成Class
        return cw.toByteArray();
    }

    public static String getFilePath(String internalName) {
        String relative_path = String.format("%s.class", internalName);
        return FileUtils.getFilePath(relative_path);
    }
}

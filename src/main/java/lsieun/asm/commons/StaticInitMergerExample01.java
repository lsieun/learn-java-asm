package lsieun.asm.commons;

import lsieun.asm.template.ClassAddInterfaceVisitor;
import lsieun.asm.template.ClassMergeVisitor;
import lsieun.utils.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.*;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;

public class StaticInitMergerExample01 {
    private static final int API_VERSION = Opcodes.ASM9;

    public static void main(String[] args) {
        // 第一步，读取两个类文件
        String first_class = "sample/HelloWorld";
        String second_class = "sample/GoodChild";

        String first_class_filepath = getFilePath(first_class);
        byte[] bytes1 = FileUtils.readBytes(first_class_filepath);

        String second_class_filepath = getFilePath(second_class);
        byte[] bytes2 = FileUtils.readBytes(second_class_filepath);

        // 第二步，将sample/GoodChild类重命名为sample/HelloWorld
        byte[] bytes3 = renameClass(second_class, first_class, bytes2);

        // 第三步，合并两个类
        byte[] bytes4 = mergeClass(bytes1, bytes3);

        // 第四步，处理重复的class initialization method
        byte[] bytes5 = removeDuplicateStaticInitMethod(bytes4);
        FileUtils.writeBytes(first_class_filepath, bytes5);
    }

    public static String getFilePath(String internalName) {
        String relative_path = String.format("%s.class", internalName);
        return FileUtils.getFilePath(relative_path);
    }

    public static byte[] renameClass(String origin_name, String target_name, byte[] bytes) {
        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）构建ClassWriter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        //（3）串连ClassVisitor
        Remapper remapper = new SimpleRemapper(origin_name, target_name);
        ClassVisitor cv = new ClassRemapper(cw, remapper);

        //（4）两者进行结合
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

        //（5）重新生成Class
        return cw.toByteArray();
    }

    public static byte[] mergeClass(byte[] bytes1, byte[] bytes2) {
        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes1);

        //（2）构建ClassWriter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        //（3）串连ClassVisitor
        ClassNode cn = getClassNode(bytes2);
        List<String> interface_list = cn.interfaces;
        int size = interface_list.size();
        String[] interfaces = new String[size];
        for (int i = 0; i < size; i++) {
            String item = interface_list.get(i);
            interfaces[i] = item;
        }
        ClassMergeVisitor cmv = new ClassMergeVisitor(API_VERSION, cw, cn);
        ClassAddInterfaceVisitor cv = new ClassAddInterfaceVisitor(API_VERSION, cmv, interfaces);

        //（4）两者进行结合
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

        //（5）重新生成Class
        return cw.toByteArray();
    }

    public static ClassNode getClassNode(byte[] bytes) {
        ClassReader cr = new ClassReader(bytes);
        ClassNode cn = new ClassNode();
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cn, parsingOptions);
        return cn;
    }

    public static byte[] removeDuplicateStaticInitMethod(byte[] bytes) {
        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）构建ClassWriter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        //（3）串连ClassVisitor
        ClassVisitor cv = new StaticInitMerger("class_init$", cw);

        //（4）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

        //（5）生成byte[]
        return cw.toByteArray();
    }
}

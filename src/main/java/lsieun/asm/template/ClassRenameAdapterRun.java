package lsieun.asm.template;

import lsieun.utils.FileUtils;
import lsieun.utils.ReadUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class ClassRenameAdapterRun {
    public static void main(String[] args) {
        String origin_name = "sample/HelloWorld";
        String target_name = "sample/GoodChild";
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
        byte[] bytes2 = cw.toByteArray();

        String target_filepath = getFilePath(target_name);
        FileUtils.writeBytes(target_filepath, bytes2);
    }

    public static String getFilePath(String internalName) {
        String relative_path = String.format("%s.class", internalName);
        return FileUtils.getFilePath(relative_path);
    }
}

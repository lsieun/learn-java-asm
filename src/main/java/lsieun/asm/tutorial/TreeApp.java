package lsieun.asm.tutorial;

import lsieun.asm.tree.ClassTransformer;
import lsieun.asm.tree.ClassAddFieldTransformer;
import lsieun.utils.FileUtils;
import lsieun.utils.ReadUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public class TreeApp {
    public static void main(String[] args) {
        String relative_path = "sample/HelloWorld.class";
        String filepath = FileUtils.getFilePath(relative_path);
        byte[] bytes1 = ReadUtils.readByPath(filepath);

        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes1);

        //（2）构建ClassNode
        ClassNode cn = new ClassNode(Opcodes.ASM9);
        cr.accept(cn, 0);

        //（3）对ClassNode进行处理
        ClassTransformer ct = new ClassAddFieldTransformer(null, Opcodes.ACC_PUBLIC,"objValue", "Ljava/lang/Object;");
        ct.transform(cn);

        //（4）构建ClassWriter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);

        //（5）重新生成Class
        byte[] bytes2 = cw.toByteArray();
        FileUtils.writeBytes(filepath, bytes2);
    }
}

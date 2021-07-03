package lsieun.asm.util;

import lsieun.utils.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.PrintWriter;

public class CheckClassAdapterExample02Generate {
    public static void main(String[] args) throws Exception {
        String relative_path = "sample/HelloWorld.class";
        String filepath = FileUtils.getFilePath(relative_path);

        // (1) 生成byte[]内容
        byte[] bytes = dump();

        // (2) 保存byte[]到文件
        FileUtils.writeBytes(filepath, bytes);

        // (3) 检查
        PrintWriter printWriter = new PrintWriter(System.out);
        CheckClassAdapter.verify(new ClassReader(bytes), false, printWriter);
    }

    public static byte[] dump() throws Exception {
        // (1) 创建ClassWriter对象
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        // (2) 调用visitXxx()方法
        CodeUtils.generate(cw);

        // (3) 调用toByteArray()方法
        return cw.toByteArray();
    }
}

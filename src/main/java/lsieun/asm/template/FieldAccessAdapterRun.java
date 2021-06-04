package lsieun.asm.template;

import lsieun.utils.FileUtils;
import lsieun.utils.ReadUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

public class FieldAccessAdapterRun {
    public static void main(String[] args) {
        String relative_path = "sample/HelloWorld.class";
        String filepath = FileUtils.getFilePath(relative_path);
        byte[] bytes1 = ReadUtils.readByPath(filepath);

        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes1);

        //（2）构建ClassWriter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        //（3）串连ClassVisitor
        Info info1 = new Info(Opcodes.GETFIELD, "sample/HelloWorld", "intValue", "I",
                Opcodes.INVOKEVIRTUAL, "sample/HelloWorld", "getHour", "()I");
        Info info2 = new Info(Opcodes.GETSTATIC, "sample/HelloWorld", "staticValue", "I",
                Opcodes.INVOKESTATIC, "sample/GoodChild", "getAge", "()I");
        List<Info> list = new ArrayList<>();
        list.add(info1);
        list.add(info2);
        ClassVisitor cv = new FieldAccessAdapter(Opcodes.ASM9, cw, list);

        //（4）两者进行结合
        cr.accept(cv, ClassReader.SKIP_FRAMES);

        //（5）重新生成Class
        byte[] bytes2 = cw.toByteArray();

        FileUtils.writeBytes(filepath, bytes2);
    }
}

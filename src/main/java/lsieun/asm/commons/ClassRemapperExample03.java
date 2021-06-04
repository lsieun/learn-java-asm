package lsieun.asm.commons;

import lsieun.utils.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.SimpleRemapper;

import java.util.HashMap;
import java.util.Map;

public class ClassRemapperExample03 {
    public static void main(String[] args) {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("sample/HelloWorld", "sample/AAA");
        mapping.put("sample/GoodChild", "sample/BBB");
        mapping.put("sample/HelloWorld.test()V", "a");
        mapping.put("sample/GoodChild.study()V", "b");
        obfuscate("sample/HelloWorld", "sample/AAA", mapping);
        obfuscate("sample/GoodChild", "sample/BBB", mapping);
    }

    public static void obfuscate(String origin_name, String target_name, Map<String, String> mapping) {
        String origin_filepath = getFilePath(origin_name);
        byte[] bytes1 = FileUtils.readBytes(origin_filepath);

        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes1);

        //（2）构建ClassWriter
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        //（3）串连ClassVisitor
        Remapper mapper = new SimpleRemapper(mapping);
        ClassVisitor cv = new ClassRemapper(cw, mapper);

        //（4）两者进行结合
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

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

package run;

import lsieun.asm.commons.MethodStackMapFrameAdvancedVisitor;
import lsieun.utils.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class HelloWorldFrameCoreAdvanced {
    public static void main(String[] args) {
        String relative_path = "sample/HelloWorld.class";
        String filepath = FileUtils.getFilePath(relative_path);
        byte[] bytes = FileUtils.readBytes(filepath);
        if (bytes == null) {
            throw new RuntimeException("bytes is null");
        }

        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）构建ClassVisitor
        int api = Opcodes.ASM9;
        ClassVisitor cv = new MethodStackMapFrameAdvancedVisitor(api, null, bytes);

        //（3）结合ClassReader和ClassVisitor
        int parsingOptions = ClassReader.EXPAND_FRAMES; // 注意，这里使用了EXPAND_FRAMES
        cr.accept(cv, parsingOptions);
    }
}

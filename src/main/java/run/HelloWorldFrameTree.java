package run;

import lsieun.utils.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.*;

import java.util.List;

public class HelloWorldFrameTree {
    public static void main(String[] args) {
        String relative_path = "sample/HelloWorld.class";
        String filepath = FileUtils.getFilePath(relative_path);
        byte[] bytes = FileUtils.readBytes(filepath);
        if (bytes == null) return;

        // (1)构建ClassReader
        ClassReader cr = new ClassReader(bytes);

        // (2) 构建ClassNode
        int api = Opcodes.ASM9;
        ClassNode cn = new ClassNode(api);
        cr.accept(cn, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);

        String owner = cn.name;
        List<MethodNode> methods = cn.methods;
        for (MethodNode mn : methods) {
            System.out.println(mn.name + ":" + mn.desc);
            Analyzer<BasicValue> analyzer = new Analyzer<>(new SimpleVerifier());
            try {
                analyzer.analyze(owner, mn);
                Frame<BasicValue>[] frames = analyzer.getFrames();
                for (Frame<?> frame : frames) {
                    System.out.println(frame);
                }
            } catch (AnalyzerException ex) {
                ex.printStackTrace();
            }
            System.out.println("====================================================");
            System.out.println();
        }
    }
}

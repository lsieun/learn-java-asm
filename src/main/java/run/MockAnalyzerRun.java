package run;

import lsieun.asm.analysis.MockAnalyzer;
import lsieun.cst.Const;
import lsieun.utils.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.*;

import java.util.List;

public class MockAnalyzerRun {
    public static void main(String[] args) throws Exception {
        String relative_path = "sample/HelloWorld.class";
        String filepath = FileUtils.getFilePath(relative_path);
        byte[] bytes = FileUtils.readBytes(filepath);

        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）分析ClassVisitor
        int api = Opcodes.ASM9;
        ClassNode cn = new ClassNode();
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cn, parsingOptions);

        //（3）分析ClassVisitor
        MockAnalyzer<BasicValue> analyzer = new MockAnalyzer<>(new SimpleVerifier());

        List<MethodNode> methods = cn.methods;
        for (MethodNode mn : methods) {
            System.out.println("Method Name: " + mn.name + ":" + mn.desc);
            Frame<BasicValue>[] frames = analyzer.analyze(cn.name, mn);
            for (Frame<?> f : frames) {
                System.out.println(f);
            }
            System.out.println(Const.DIVISION_LINE);
        }
    }
}

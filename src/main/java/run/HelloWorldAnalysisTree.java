package run;

import lsieun.asm.analysis.cc.CyclomaticComplexity;
import lsieun.utils.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

public class HelloWorldAnalysisTree {
    public static void main(String[] args) throws Exception {
        String relative_path = "sample/HelloWorld.class";
        String filepath = FileUtils.getFilePath(relative_path);
        byte[] bytes = FileUtils.readBytes(filepath);

        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）生成ClassNode
        int api = Opcodes.ASM9;
        ClassNode cn = new ClassNode(api);

        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cn, parsingOptions);

        //（3）进行分析
        String className = cn.name;
        List<MethodNode> methods = cn.methods;
        MethodNode mn = methods.get(1);
        int complexity = CyclomaticComplexity.getCyclomaticComplexity(className, mn);
        String line = String.format("%s:%s%n    complexity: %d", mn.name, mn.desc, complexity);
        System.out.println(line);
    }
}

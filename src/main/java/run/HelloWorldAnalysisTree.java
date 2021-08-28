package run;

import lsieun.asm.analysis.*;
import lsieun.utils.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.List;

public class HelloWorldAnalysisTree {
    public static void main(String[] args) throws Exception {
        String relative_path = "sample/HelloWorld.class";
        String filepath = FileUtils.getFilePath(relative_path);
        byte[] bytes = FileUtils.readBytes(filepath);
        if (bytes == null) {
            throw new RuntimeException("bytes is null");
        }

        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）生成ClassNode
        int api = Opcodes.ASM9;
        ClassNode cn = new ClassNode();

        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cn, parsingOptions);

        //（3）进行分析
        List<MethodNode> methods = cn.methods;
        NullDereferenceAnalyzer analyzer = new NullDereferenceAnalyzer();
        for (MethodNode mn : methods) {
            List<AbstractInsnNode> insnList = analyzer.findNullDereferences(cn.name, mn);
            if (insnList != null && insnList.size() > 0) {
                String line = String.format("Method: %s:%s", mn.name, mn.desc);
                System.out.println(line);
                for (AbstractInsnNode insnNode : insnList) {
                    if (insnNode instanceof MethodInsnNode) {
                        MethodInsnNode node = (MethodInsnNode) insnNode;
                        String item = String.format("    %s.%s:%s", node.owner, node.name, node.desc);
                        System.out.println(item);
                    }
                }
                System.out.println("=================================");
            }

        }
    }
}

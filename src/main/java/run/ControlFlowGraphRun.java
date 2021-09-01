package run;

import lsieun.asm.analysis.graph.InstructionGraph;
import lsieun.utils.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class ControlFlowGraphRun {
    public static void main(String[] args) {
        String relative_path = "sample/HelloWorld.class";
        String filepath = FileUtils.getFilePath(relative_path);
        byte[] bytes = FileUtils.readBytes(filepath);
        if (bytes == null) {
            throw new RuntimeException("bytes is null");
        }

        //（1）构建ClassReader
        ClassReader cr = new ClassReader(bytes);

        //（2）生成ClassNode
        ClassNode cn = new ClassNode();

        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cn, parsingOptions);

        //（3）查找方法
        String methodName = "test";
        MethodNode targetNode = null;
        for (MethodNode mn : cn.methods) {
            if (mn.name.equals(methodName)) {
                targetNode = mn;
                break;
            }
        }

        //（4）图形显示
        if (targetNode != null) {
            InstructionGraph graph = new InstructionGraph();
            graph.init(targetNode);
            graph.print();
            graph.draw();
        }
    }
}

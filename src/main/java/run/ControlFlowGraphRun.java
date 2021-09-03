package run;

import lsieun.asm.analysis.CyclomaticComplexity;
import lsieun.asm.analysis.graph.InstructionGraph;
import lsieun.utils.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class ControlFlowGraphRun {
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
        if (targetNode == null) {
            throw new RuntimeException("Can not find method: " + methodName);
        }

        //（4）图形显示
        InstructionGraph graph = new InstructionGraph();
        graph.init(targetNode);
        graph.print();
        graph.draw();

        //（5）打印复杂度
        CyclomaticComplexity cc = new CyclomaticComplexity();
        int complexity = cc.getCyclomaticComplexity(cn.name, targetNode);
        String line = String.format("%s:%s complexity: %d", targetNode.name, targetNode.desc, complexity);
        System.out.println(line);
    }
}

package run;

import lsieun.asm.analysis.ControlFlowEdgeAnalyzer;
import lsieun.asm.analysis.ControlFlowEdgeAnalyzer2;
import lsieun.asm.analysis.CyclomaticComplexity;
import lsieun.asm.analysis.graph.InsnBlock;
import lsieun.asm.analysis.ControlFlowGraphAnalyzer;
import lsieun.asm.analysis.graph.InsnGraph;
import lsieun.utils.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;

public class ControlFlowGraphRun {
    public static void main(String[] args) throws Exception {
        String relative_path = "sample/HelloWorld.class";
        String filepath = FileUtils.getFilePath(relative_path);
        byte[] bytes = FileUtils.readBytes(filepath);

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

        //（4）进行分析
        InsnBlock[] blocks;
        int kind = 2;
        switch (kind) {
            case 1: {
                ControlFlowEdgeAnalyzer<BasicValue> analyzer = new ControlFlowEdgeAnalyzer<>(new BasicInterpreter());
                analyzer.analyze(cn.name, targetNode);
                blocks = analyzer.getBlocks();
                break;
            }
            case 2: {
                ControlFlowEdgeAnalyzer<BasicValue> analyzer = new ControlFlowEdgeAnalyzer2<>(new BasicInterpreter());
                analyzer.analyze(cn.name, targetNode);
                blocks = analyzer.getBlocks();
                break;
            }
            default: {
                ControlFlowGraphAnalyzer analyzer = new ControlFlowGraphAnalyzer();
                analyzer.analyze(targetNode);
                blocks = analyzer.getBlocks();
            }
        }

        //（5）图形显示
        InsnGraph graph = new InsnGraph(blocks);
        graph.draw();

        //（6）打印复杂度
        CyclomaticComplexity cc = new CyclomaticComplexity();
        int complexity = cc.getCyclomaticComplexity(cn.name, targetNode);
        String line = String.format("%s:%s complexity: %d", targetNode.name, targetNode.desc, complexity);
        System.out.println(line);
    }
}

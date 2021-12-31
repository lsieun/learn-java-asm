package run;

import lsieun.asm.analysis.*;
import lsieun.asm.analysis.cc.CyclomaticComplexity;
import lsieun.asm.analysis.graph.InsnBlock;
import lsieun.asm.analysis.graph.TextGraph;
import lsieun.utils.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;

import java.util.List;

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
            System.out.println("Can not find method: " + methodName);
            return;
        }

        //（4）进行图形化显示
        System.out.println("Origin:");
        display(cn.name, targetNode, ControlFlowGraphType.NONE);
        System.out.println("Control Flow Graph:");
        display(cn.name, targetNode, ControlFlowGraphType.STANDARD);

        //（5）打印复杂度
        int complexity = CyclomaticComplexity.getCyclomaticComplexity(cn.name, targetNode);
        String line = String.format("%s:%s complexity: %d", targetNode.name, targetNode.desc, complexity);
        System.out.println(line);
    }

    private static void display(String owner, MethodNode mn, ControlFlowGraphType option) throws AnalyzerException {
        //（1）准备数据
        InsnBlock[] blocks;
        switch (option) {
            case NONE: {
                InsnText insnText = new InsnText();
                List<String> lines = insnText.toLines(mn.instructions.toArray());

                InsnBlock block = new InsnBlock();
                block.addLines(lines);

                blocks = new InsnBlock[1];
                blocks[0] = block;
                break;
            }
            case ONE_INSN_ONE_BOX: {
                ControlFlowEdgeAnalyzer<BasicValue> analyzer = new ControlFlowEdgeAnalyzer<>(new BasicInterpreter());
                analyzer.analyze(owner, mn);
                blocks = analyzer.getBlocks();
                break;
            }
            case MULTI_INSN_ONE_BOX:
            case STANDARD: {
                ControlFlowEdgeAnalyzer<BasicValue> analyzer = new ControlFlowEdgeAnalyzer2<>(new BasicInterpreter());
                analyzer.analyze(owner, mn);
                blocks = analyzer.getBlocks();
                break;
            }
            case EXPERIMENT_ONE_INSN_ONE_BOX: {
                ControlFlowAnalyzer2 analyzer = new ControlFlowAnalyzer2();
                analyzer.analyze(owner, mn);
                blocks = analyzer.getBlocks();
                break;
            }
            case EXPERIMENT_MULTI_INSN_ONE_BOX: {
                ControlFlowGraphAnalyzer analyzer = new ControlFlowGraphAnalyzer();
                analyzer.analyze(mn);
                blocks = analyzer.getBlocks();
                break;
            }
            default: {
                throw new RuntimeException("unknown type: " + option);
            }
        }

        //（2）图形显示
        TextGraph textGraph = new TextGraph(blocks);
        textGraph.draw(0, 0);
    }
}

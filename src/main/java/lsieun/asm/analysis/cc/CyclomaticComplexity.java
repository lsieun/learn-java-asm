package lsieun.asm.analysis.cc;

import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.*;

public class CyclomaticComplexity {
    public static int getCyclomaticComplexity(String owner, MethodNode mn) throws AnalyzerException {
        // 第一步，获取Frame信息
        Analyzer<BasicValue> analyzer = new CyclomaticComplexityAnalyzer<>(new BasicInterpreter());
        Frame<BasicValue>[] frames = analyzer.analyze(owner, mn);

        // 第二步，计算复杂度
        int edges = 0;
        int nodes = 0;
        for (Frame<BasicValue> frame : frames) {
            if (frame != null) {
                edges += ((CyclomaticComplexityFrame<BasicValue>) frame).successors.size();
                nodes += 1;
            }
        }
        return edges - nodes + 2;
    }
}

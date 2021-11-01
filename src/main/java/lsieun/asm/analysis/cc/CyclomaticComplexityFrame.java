package lsieun.asm.analysis.cc;

import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Value;

import java.util.HashSet;
import java.util.Set;

public class CyclomaticComplexityFrame<V extends Value> extends Frame<V> {
    public Set<CyclomaticComplexityFrame<V>> successors = new HashSet<>();

    public CyclomaticComplexityFrame(int numLocals, int numStack) {
        super(numLocals, numStack);
    }

    public CyclomaticComplexityFrame(Frame<? extends V> frame) {
        super(frame);
    }
}

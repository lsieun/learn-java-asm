package lsieun.asm.analysis;

import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Value;

import java.util.HashSet;
import java.util.Set;

public class Node<V extends Value> extends Frame<V> {
    public Set<Node<V>> successors = new HashSet<>();

    public Node(int numLocals, int numStack) {
        super(numLocals, numStack);
    }

    public Node(Frame<? extends V> frame) {
        super(frame);
    }
}

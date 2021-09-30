package lsieun.asm.analysis.state;

import org.objectweb.asm.tree.analysis.Value;

public class StateValue implements Value {
    public final int size;

    public StateValue(int size) {
        this.size = size;
    }

    @Override
    public int getSize() {
        return size;
    }
}

package lsieun.asm.analysis.cfg;

import java.util.Arrays;
import java.util.Formatter;

public final class ControlFlowGraph {
    public final int[][] transitions;
    public final int[][] errorTransitions;

    public ControlFlowGraph(int[][] transitions, int[][] errorTransitions) {
        this.transitions = transitions;
        this.errorTransitions = errorTransitions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb);
        fm.format("ControlFlowGraph%n");

        fm.format("Normal Transitions%n");
        int normalLength = transitions.length;
        for (int i = 0; i < normalLength; i++) {
            int[] array = transitions[i];
            fm.format("%03d    %s%n", i, Arrays.toString(array));
        }

        fm.format("Error Transitions%n");
        int errorLength = transitions.length;
        for (int i = 0; i < errorLength; i++) {
            int[] array = errorTransitions[i];
            fm.format("%03d    %s%n", i, Arrays.toString(array));
        }
        return sb.toString();
    }
}

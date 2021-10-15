package lsieun.asm.analysis.cfg;

import lsieun.trove.TIntArrayList;

public class SimpleEdgeCreator implements EdgeCreator {
    public final TIntArrayList[] transitions;
    public final TIntArrayList[] errorTransitions;

    public SimpleEdgeCreator(int size) {
        this.transitions = new TIntArrayList[size];
        this.errorTransitions = new TIntArrayList[size];
        for (int i = 0; i < transitions.length; i++) {
            this.transitions[i] = new TIntArrayList();
            this.errorTransitions[i] = new TIntArrayList();
        }
    }

    @Override
    public void newControlFlowEdge(int insn, int successor) {
        if (!transitions[insn].contains(successor)) {
            transitions[insn].add(successor);
        }
    }

    @Override
    public void newControlFlowExceptionEdge(int insn, int successor, boolean npe) {
        if (!errorTransitions[insn].contains(successor)) {
            errorTransitions[insn].add(successor);
        }
    }
}

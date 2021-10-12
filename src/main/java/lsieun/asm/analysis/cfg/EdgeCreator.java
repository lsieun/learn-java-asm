package lsieun.asm.analysis.cfg;

public interface EdgeCreator {
    void newControlFlowEdge(int insn, int successor);

    void newControlFlowExceptionEdge(int insn, int successor, boolean npe);
}
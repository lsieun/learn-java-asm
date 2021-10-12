package lsieun.asm.analysis.cfg;

import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.objectweb.asm.Opcodes.*;

/**
 * Specialized version of {@link org.objectweb.asm.tree.analysis.Analyzer}.
 * Calculation of fix-point of frames is removed, since frames are not needed to build control flow graph.
 * So, the main point here is handling of try-catch-finally blocks.
 */
public class FramelessAnalyzer {
    private static final Set<String> NPE_HANDLERS = new HashSet<>();

    static {
        NPE_HANDLERS.add("java/lang/Throwable");
        NPE_HANDLERS.add("java/lang/Exception");
        NPE_HANDLERS.add("java/lang/RuntimeException");
        NPE_HANDLERS.add("java/lang/NullPointerException");
    }

    protected boolean[] wasQueued;
    protected boolean[] queued;
    protected int[] queue;
    protected int top;
    protected final EdgeCreator myEdgeCreator;

    public FramelessAnalyzer(EdgeCreator creator) {
        myEdgeCreator = creator;
    }

    public void analyze(MethodNode m) throws AnalyzerException {
        int n = m.instructions.size();
        if ((m.access & (ACC_ABSTRACT | ACC_NATIVE)) != 0 || n == 0) {
            return;
        }
        InsnList insns = m.instructions;
        List<TryCatchBlockNode>[] handlers = newListArray(n);
        queued = new boolean[n];
        wasQueued = new boolean[n];
        queue = new int[n];
        top = 0;

        // computes exception handlers for each instruction
        for (TryCatchBlockNode tcb : m.tryCatchBlocks) {
            int begin = insns.indexOf(tcb.start);
            int end = insns.indexOf(tcb.end);
            for (int j = begin; j < end; ++j) {
                List<TryCatchBlockNode> insnHandlers = handlers[j];
                if (insnHandlers == null) {
                    insnHandlers = new ArrayList<>();
                    handlers[j] = insnHandlers;
                }
                insnHandlers.add(tcb);
            }
        }

        merge(0);
        // control flow analysis
        while (top > 0) {
            int insn = queue[--top];

            queued[insn] = false;

            AbstractInsnNode insnNode = null;
            try {
                insnNode = m.instructions.get(insn);
                int insnOpcode = insnNode.getOpcode();
                int insnType = insnNode.getType();

                if (insnType == AbstractInsnNode.LABEL || insnType == AbstractInsnNode.LINE || insnType == AbstractInsnNode.FRAME) {
                    merge(insn + 1);
                    myEdgeCreator.newControlFlowEdge(insn, insn + 1);
                }
                else {

                    if (insnNode instanceof JumpInsnNode) {
                        JumpInsnNode j = (JumpInsnNode) insnNode;
                        if (insnOpcode != GOTO && insnOpcode != JSR) {
                            merge(insn + 1);
                            myEdgeCreator.newControlFlowEdge(insn, insn + 1);
                        }
                        int jump = insns.indexOf(j.label);
                        if (insnOpcode == JSR) {
                            throw new AnalyzerException(insnNode, "ret is not supported");
                        }
                        else {
                            merge(jump);
                        }
                        myEdgeCreator.newControlFlowEdge(insn, jump);
                    }
                    else if (insnNode instanceof LookupSwitchInsnNode) {
                        LookupSwitchInsnNode lsi = (LookupSwitchInsnNode) insnNode;
                        int jump = insns.indexOf(lsi.dflt);
                        merge(jump);
                        myEdgeCreator.newControlFlowEdge(insn, jump);
                        for (int j = 0; j < lsi.labels.size(); ++j) {
                            LabelNode label = lsi.labels.get(j);
                            jump = insns.indexOf(label);
                            merge(jump);
                            myEdgeCreator.newControlFlowEdge(insn, jump);
                        }
                    }
                    else if (insnNode instanceof TableSwitchInsnNode) {
                        TableSwitchInsnNode tsi = (TableSwitchInsnNode) insnNode;
                        int jump = insns.indexOf(tsi.dflt);
                        merge(jump);
                        myEdgeCreator.newControlFlowEdge(insn, jump);
                        for (int j = 0; j < tsi.labels.size(); ++j) {
                            LabelNode label = tsi.labels.get(j);
                            jump = insns.indexOf(label);
                            merge(jump);
                            myEdgeCreator.newControlFlowEdge(insn, jump);
                        }
                    }
                    else if (insnOpcode == RET) {
                        throw new AnalyzerException(insnNode, "ret is not supported");
                    }
                    else if (insnOpcode != ATHROW && (insnOpcode < IRETURN || insnOpcode > RETURN)) {
                        merge(insn + 1);
                        myEdgeCreator.newControlFlowEdge(insn, insn + 1);
                    }
                }

                List<TryCatchBlockNode> insnHandlers = handlers[insn];
                if (insnHandlers != null) {
                    for (TryCatchBlockNode tcb : insnHandlers) {
                        myEdgeCreator.newControlFlowExceptionEdge(insn, insns.indexOf(tcb.handler), NPE_HANDLERS.contains(tcb.type));
                        merge(insns.indexOf(tcb.handler));
                    }
                }
            }
            catch (AnalyzerException e) {
                throw new AnalyzerException(e.node, "Error at instruction " + insn + ": " + e.getMessage(), e);
            }
            catch (Exception e) {
                throw new AnalyzerException(insnNode, "Error at instruction " + insn + ": " + e.getMessage(), e);
            }
        }
    }

    // -------------------------------------------------------------------------

    protected void merge(int insn) {
        boolean changes = false;

        if (!wasQueued[insn]) {
            wasQueued[insn] = true;
            changes = true;
        }

        if (changes && !queued[insn]) {
            queued[insn] = true;
            queue[top++] = insn;
        }
    }

    public static <V> List<V>[] newListArray(int size) {
        @SuppressWarnings("unchecked")
        List<V>[] a = (List<V>[]) new List[size];
        return a;
    }
}
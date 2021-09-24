package lsieun.asm.analysis;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import java.util.ArrayList;
import java.util.List;

/**
 * 将 {@link org.objectweb.asm.tree.analysis.Analyzer}类的control flow analysis功能抽取出来.
 *
 * NOTE: 当前类抛弃了所有泛型相关的信息(V)，抛弃了Frame、抛弃了Interpreter、抛弃了Value。
 *       但是，它仍然能够实现control flow analysis的功能。
 *
 * @see org.objectweb.asm.tree.analysis.Analyzer
 */
public class ControlFlowAnalyzer implements Opcodes {

    private InsnList insnList;
    private int insnListSize;
    private List<TryCatchBlockNode>[] handlers;

    /**
     * 记录需要处理的instructions.
     *
     * NOTE: 这三个字段为一组，应该一起处理，最好是放到同一个方法里来处理。
     * 因此，我就加了三个新方法。
     * {@link #initInstructionsToProcess()}、{@link #addInstructionsToProcess(int)}和
     * {@link #removeInstructionsToProcess()}
     *
     * @see #initInstructionsToProcess()
     * @see #addInstructionsToProcess(int)
     * @see #removeInstructionsToProcess()
     */
    private boolean[] inInstructionsToProcess;
    private int[] instructionsToProcess;
    private int numInstructionsToProcess;

    public ControlFlowAnalyzer() {
    }

    public List<TryCatchBlockNode> getHandlers(final int insnIndex) {
        return handlers[insnIndex];
    }


    @SuppressWarnings("unchecked")
    // NOTE: analyze方法的返回值类型变成了void类型。
    public void analyze(final String owner, final MethodNode method) throws AnalyzerException {
        if ((method.access & (ACC_ABSTRACT | ACC_NATIVE)) != 0) {
            return;
        }
        insnList = method.instructions;
        insnListSize = insnList.size();
        handlers = (List<TryCatchBlockNode>[]) new List<?>[insnListSize];

        initInstructionsToProcess();

        // For each exception handler, and each instruction within its range, record in 'handlers' the
        // fact that execution can flow from this instruction to the exception handler.
        for (int i = 0; i < method.tryCatchBlocks.size(); ++i) {
            TryCatchBlockNode tryCatchBlock = method.tryCatchBlocks.get(i);
            int startIndex = insnList.indexOf(tryCatchBlock.start);
            int endIndex = insnList.indexOf(tryCatchBlock.end);
            for (int j = startIndex; j < endIndex; ++j) {
                List<TryCatchBlockNode> insnHandlers = handlers[j];
                if (insnHandlers == null) {
                    insnHandlers = new ArrayList<>();
                    handlers[j] = insnHandlers;
                }
                insnHandlers.add(tryCatchBlock);
            }
        }


        // Initializes the data structures for the control flow analysis.
        // NOTE: 调用addInstructionsToProcess方法，传入参数0，启动整个循环过程。
        addInstructionsToProcess(0);
        init(owner, method);

        // Control flow analysis.
        while (numInstructionsToProcess > 0) {
            // Get and remove one instruction from the list of instructions to process.
            int insnIndex = removeInstructionsToProcess();

            // Simulate the execution of this instruction.
            AbstractInsnNode insnNode = method.instructions.get(insnIndex);
            int insnOpcode = insnNode.getOpcode();
            int insnType = insnNode.getType();

            if (insnType == AbstractInsnNode.LABEL
                    || insnType == AbstractInsnNode.LINE
                    || insnType == AbstractInsnNode.FRAME) {
                newControlFlowEdge(insnIndex, insnIndex + 1);
            }
            else {
                if (insnNode instanceof JumpInsnNode) {
                    JumpInsnNode jumpInsn = (JumpInsnNode) insnNode;
                    if (insnOpcode != GOTO && insnOpcode != JSR) {
                        newControlFlowEdge(insnIndex, insnIndex + 1);
                    }
                    int jumpInsnIndex = insnList.indexOf(jumpInsn.label);
                    newControlFlowEdge(insnIndex, jumpInsnIndex);
                }
                else if (insnNode instanceof LookupSwitchInsnNode) {
                    LookupSwitchInsnNode lookupSwitchInsn = (LookupSwitchInsnNode) insnNode;
                    int targetInsnIndex = insnList.indexOf(lookupSwitchInsn.dflt);
                    newControlFlowEdge(insnIndex, targetInsnIndex);
                    for (int i = 0; i < lookupSwitchInsn.labels.size(); ++i) {
                        LabelNode label = lookupSwitchInsn.labels.get(i);
                        targetInsnIndex = insnList.indexOf(label);
                        newControlFlowEdge(insnIndex, targetInsnIndex);
                    }
                }
                else if (insnNode instanceof TableSwitchInsnNode) {
                    TableSwitchInsnNode tableSwitchInsn = (TableSwitchInsnNode) insnNode;
                    int targetInsnIndex = insnList.indexOf(tableSwitchInsn.dflt);
                    newControlFlowEdge(insnIndex, targetInsnIndex);
                    for (int i = 0; i < tableSwitchInsn.labels.size(); ++i) {
                        LabelNode label = tableSwitchInsn.labels.get(i);
                        targetInsnIndex = insnList.indexOf(label);
                        newControlFlowEdge(insnIndex, targetInsnIndex);
                    }
                }
                else if (insnOpcode != ATHROW && (insnOpcode < IRETURN || insnOpcode > RETURN)) {
                    newControlFlowEdge(insnIndex, insnIndex + 1);
                }
            }

            List<TryCatchBlockNode> insnHandlers = handlers[insnIndex];
            if (insnHandlers != null) {
                for (TryCatchBlockNode tryCatchBlock : insnHandlers) {
                    newControlFlowExceptionEdge(insnIndex, tryCatchBlock);
                }
            }

        }
    }


    protected void init(final String owner, final MethodNode method) throws AnalyzerException {
        // Nothing to do.
    }

    // NOTE: 这是一个新添加的方法
    private void initInstructionsToProcess() {
        inInstructionsToProcess = new boolean[insnListSize];
        instructionsToProcess = new int[insnListSize];
        numInstructionsToProcess = 0;
    }

    // NOTE: 这是一个新添加的方法
    private int removeInstructionsToProcess() {
        int insnIndex = this.instructionsToProcess[--numInstructionsToProcess];
        inInstructionsToProcess[insnIndex] = false;
        return insnIndex;
    }

    // NOTE: 这是一个新添加的方法
    private void addInstructionsToProcess(final int insnIndex) {
        if (!inInstructionsToProcess[insnIndex]) {
            inInstructionsToProcess[insnIndex] = true;
            instructionsToProcess[numInstructionsToProcess++] = insnIndex;
        }
    }

    protected void newControlFlowEdge(final int insnIndex, final int successorIndex) {
        // Nothing to do.
        addInstructionsToProcess(successorIndex);
    }

    protected void newControlFlowExceptionEdge(final int insnIndex, final TryCatchBlockNode tryCatchBlock) {
        newControlFlowExceptionEdge(insnIndex, insnList.indexOf(tryCatchBlock.handler));
    }

    protected void newControlFlowExceptionEdge(final int insnIndex, final int successorIndex) {
        // Nothing to do.
        addInstructionsToProcess(successorIndex);
    }
}

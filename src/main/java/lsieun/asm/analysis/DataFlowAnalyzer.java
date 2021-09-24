package lsieun.asm.analysis;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.Value;

import java.util.ArrayList;
import java.util.List;

public class DataFlowAnalyzer<V extends Value> implements Opcodes {

    private final Interpreter<V> interpreter;

    // 数据输入：指令集和异常处理
    private InsnList insnList;
    private int insnListSize;
    private List<TryCatchBlockNode>[] handlers;

    // 中间状态：记录需要哪一个指令需要处理
    private boolean[] inInstructionsToProcess;
    private int[] instructionsToProcess;
    private int numInstructionsToProcess;

    // 数据输出：最终的返回结果
    private Frame<V>[] frames;

    public DataFlowAnalyzer(final Interpreter<V> interpreter) {
        this.interpreter = interpreter;
    }

    public List<TryCatchBlockNode> getHandlers(final int insnIndex) {
        return handlers[insnIndex];
    }

    public Frame<V>[] getFrames() {
        return frames;
    }


    @SuppressWarnings("unchecked")
    public Frame<V>[] analyze(final String owner, final MethodNode method) throws AnalyzerException {
        if ((method.access & (ACC_ABSTRACT | ACC_NATIVE)) != 0) {
            frames = (Frame<V>[]) new Frame<?>[0];
            return frames;
        }
        insnList = method.instructions;
        insnListSize = insnList.size();
        handlers = (List<TryCatchBlockNode>[]) new List<?>[insnListSize];
        frames = (Frame<V>[]) new Frame<?>[insnListSize];
        inInstructionsToProcess = new boolean[insnListSize];
        instructionsToProcess = new int[insnListSize];
        numInstructionsToProcess = 0;

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
        Frame<V> currentFrame = computeInitialFrame(owner, method);
        merge(0, currentFrame);
        init(owner, method);

        // Control flow analysis.
        while (numInstructionsToProcess > 0) {
            // Get and remove one instruction from the list of instructions to process.
            int insnIndex = instructionsToProcess[--numInstructionsToProcess];
            Frame<V> oldFrame = frames[insnIndex];
            inInstructionsToProcess[insnIndex] = false;

            // Simulate the execution of this instruction.
            AbstractInsnNode insnNode = null;
            try {
                insnNode = method.instructions.get(insnIndex);
                int insnOpcode = insnNode.getOpcode();
                int insnType = insnNode.getType();

                if (insnType == AbstractInsnNode.LABEL
                        || insnType == AbstractInsnNode.LINE
                        || insnType == AbstractInsnNode.FRAME) {
                    merge(insnIndex + 1, oldFrame);
                    newControlFlowEdge(insnIndex, insnIndex + 1);
                }
                else {
                    currentFrame.init(oldFrame).execute(insnNode, interpreter);

                    if (insnNode instanceof JumpInsnNode) {
                        JumpInsnNode jumpInsn = (JumpInsnNode) insnNode;
                        if (insnOpcode != GOTO && insnOpcode != JSR) {
                            currentFrame.initJumpTarget(insnOpcode, /* target = */ null);
                            merge(insnIndex + 1, currentFrame);
                            newControlFlowEdge(insnIndex, insnIndex + 1);
                        }
                        int jumpInsnIndex = insnList.indexOf(jumpInsn.label);
                        currentFrame.initJumpTarget(insnOpcode, jumpInsn.label);
                        merge(jumpInsnIndex, currentFrame);
                        newControlFlowEdge(insnIndex, jumpInsnIndex);
                    }
                    else if (insnNode instanceof LookupSwitchInsnNode) {
                        LookupSwitchInsnNode lookupSwitchInsn = (LookupSwitchInsnNode) insnNode;
                        int targetInsnIndex = insnList.indexOf(lookupSwitchInsn.dflt);
                        currentFrame.initJumpTarget(insnOpcode, lookupSwitchInsn.dflt);
                        merge(targetInsnIndex, currentFrame);
                        newControlFlowEdge(insnIndex, targetInsnIndex);
                        for (int i = 0; i < lookupSwitchInsn.labels.size(); ++i) {
                            LabelNode label = lookupSwitchInsn.labels.get(i);
                            targetInsnIndex = insnList.indexOf(label);
                            currentFrame.initJumpTarget(insnOpcode, label);
                            merge(targetInsnIndex, currentFrame);
                            newControlFlowEdge(insnIndex, targetInsnIndex);
                        }
                    }
                    else if (insnNode instanceof TableSwitchInsnNode) {
                        TableSwitchInsnNode tableSwitchInsn = (TableSwitchInsnNode) insnNode;
                        int targetInsnIndex = insnList.indexOf(tableSwitchInsn.dflt);
                        currentFrame.initJumpTarget(insnOpcode, tableSwitchInsn.dflt);
                        merge(targetInsnIndex, currentFrame);
                        newControlFlowEdge(insnIndex, targetInsnIndex);
                        for (int i = 0; i < tableSwitchInsn.labels.size(); ++i) {
                            LabelNode label = tableSwitchInsn.labels.get(i);
                            currentFrame.initJumpTarget(insnOpcode, label);
                            targetInsnIndex = insnList.indexOf(label);
                            merge(targetInsnIndex, currentFrame);
                            newControlFlowEdge(insnIndex, targetInsnIndex);
                        }
                    }
                    else if (insnOpcode != ATHROW && (insnOpcode < IRETURN || insnOpcode > RETURN)) {
                        merge(insnIndex + 1, currentFrame);
                        newControlFlowEdge(insnIndex, insnIndex + 1);
                    }
                }

                List<TryCatchBlockNode> insnHandlers = handlers[insnIndex];
                if (insnHandlers != null) {
                    for (TryCatchBlockNode tryCatchBlock : insnHandlers) {
                        Type catchType;
                        if (tryCatchBlock.type == null) {
                            catchType = Type.getObjectType("java/lang/Throwable");
                        }
                        else {
                            catchType = Type.getObjectType(tryCatchBlock.type);
                        }
                        if (newControlFlowExceptionEdge(insnIndex, tryCatchBlock)) {
                            Frame<V> handler = newFrame(oldFrame);
                            handler.clearStack();
                            handler.push(interpreter.newExceptionValue(tryCatchBlock, handler, catchType));
                            merge(insnList.indexOf(tryCatchBlock.handler), handler);
                        }
                    }
                }
            }
            catch (AnalyzerException e) {
                throw new AnalyzerException(e.node, "Error at instruction " + insnIndex + ": " + e.getMessage(), e);
            }
            catch (RuntimeException e) {
                // DontCheck(IllegalCatch): can't be fixed, for backward compatibility.
                throw new AnalyzerException(insnNode, "Error at instruction " + insnIndex + ": " + e.getMessage(), e);
            }
        }

        return frames;
    }

    private Frame<V> computeInitialFrame(final String owner, final MethodNode method) {
        Frame<V> frame = newFrame(method.maxLocals, method.maxStack);
        int currentLocal = 0;
        boolean isInstanceMethod = (method.access & ACC_STATIC) == 0;
        if (isInstanceMethod) {
            Type ownerType = Type.getObjectType(owner);
            frame.setLocal(currentLocal, interpreter.newParameterValue(isInstanceMethod, currentLocal, ownerType));
            currentLocal++;
        }
        Type[] argumentTypes = Type.getArgumentTypes(method.desc);
        for (Type argumentType : argumentTypes) {
            frame.setLocal(
                    currentLocal,
                    interpreter.newParameterValue(isInstanceMethod, currentLocal, argumentType));
            currentLocal++;
            if (argumentType.getSize() == 2) {
                frame.setLocal(currentLocal, interpreter.newEmptyValue(currentLocal));
                currentLocal++;
            }
        }
        while (currentLocal < method.maxLocals) {
            frame.setLocal(currentLocal, interpreter.newEmptyValue(currentLocal));
            currentLocal++;
        }
        frame.setReturn(interpreter.newReturnTypeValue(Type.getReturnType(method.desc)));
        return frame;
    }


    protected void init(final String owner, final MethodNode method) throws AnalyzerException {
        // Nothing to do.
    }

    protected Frame<V> newFrame(final int numLocals, final int numStack) {
        return new Frame<>(numLocals, numStack);
    }

    protected Frame<V> newFrame(final Frame<? extends V> frame) {
        return new Frame<>(frame);
    }

    protected void newControlFlowEdge(final int insnIndex, final int successorIndex) {
        // Nothing to do.
    }

    protected boolean newControlFlowExceptionEdge(final int insnIndex, final TryCatchBlockNode tryCatchBlock) {
        return newControlFlowExceptionEdge(insnIndex, insnList.indexOf(tryCatchBlock.handler));
    }

    protected boolean newControlFlowExceptionEdge(final int insnIndex, final int successorIndex) {
        return true;
    }

    // -----------------------------------------------------------------------------------------------

    private void merge(final int insnIndex, final Frame<V> frame)
            throws AnalyzerException {
        boolean changed;
        Frame<V> oldFrame = frames[insnIndex];
        if (oldFrame == null) {
            frames[insnIndex] = newFrame(frame);
            changed = true;
        }
        else {
            changed = oldFrame.merge(frame, interpreter);
        }

        if (changed && !inInstructionsToProcess[insnIndex]) {
            inInstructionsToProcess[insnIndex] = true;
            instructionsToProcess[numInstructionsToProcess++] = insnIndex;
        }
    }

}
